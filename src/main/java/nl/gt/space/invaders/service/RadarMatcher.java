package nl.gt.space.invaders.service;

import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.config.constants.Constants;
import nl.gt.space.invaders.util.ClusterUtil;
import nl.gt.space.invaders.util.CorrelationUtil;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadarMatcher {

    private final ImageService imageService;
    private final Constants constants;

    @PostConstruct
    public void runMatches() {
        List<Image> matchedRadarImages = imageService.getRadarImages().stream()
                .map(i -> matchKnownInvaders(i, imageService.getKnownInvaders())).collect(Collectors.toList());
        matchedRadarImages.stream()
                .forEach(i -> log.info("Radar Image [{}] scanned and result is:\n{}", i.getId(), i.printChars()));
    }

    public Image matchKnownInvaders(Image radarImage, List<Image> knownInvaders) {

        List<Image> correlationImageList = knownInvaders.stream()
                .map(i -> CorrelationUtil.getCorrelationImage(radarImage, i, constants.getCutoffMatchDensity()))
                .collect(Collectors.toList());

        Image mergedImage = new Image(radarImage.getRows(), radarImage.getCols());
        for (Image i : correlationImageList) {
            mergedImage.overLayWith(i);
        }

        int maxInvaderWidth = knownInvaders.stream().map(i -> i.getCols()).max(Integer::compare).get();
        int maxInvaderHeight = knownInvaders.stream().map(i -> i.getRows()).max(Integer::compare).get();

        int maxClusterId = ClusterUtil.movingMeanSquareCluster(mergedImage, maxInvaderHeight, maxInvaderWidth);

        List<List<Point>> clusterLists = new ArrayList<>();
        for (int i = 0; i <= maxClusterId; ++i) {
            final int targetCluster = i;
            clusterLists.add(mergedImage.asList().stream().filter(p -> p.getCluster() == targetCluster)
                    .collect(Collectors.toList()));
        }

        List<Pair<Point, Integer>> pointHits = new ArrayList<>(); // this contains all the real hits
        // XXX here I want to get the top most likely matches per cluster

        for (List<Point> clusterList : clusterLists) {

            //System.out.println("Cluster: " + clusterList.get(0).getCluster());

            Image copyOfRadarImage = radarImage.clone();
            boolean doneWithCluster = false;
            while (!doneWithCluster) {
                Pair<Point, Integer> hit = getBestInvaderMatchInCLuster(clusterList, copyOfRadarImage, knownInvaders);

                // XXX from constant please
                if (hit.getKey().getMagnitude() < constants.getAcceptedMatchRatioForHit()) {
                    doneWithCluster = true;
                } else {
                    pointHits.add(hit);
                    Image bestMatchedInvader = getFromList(hit.getValue(), knownInvaders);
                    CorrelationUtil.decorrelateInvader(hit.getKey().getRow(), hit.getKey().getCol(), copyOfRadarImage,
                            bestMatchedInvader);

                    System.out.println(
                            "Invader [" + hit.getValue() + "] scored [" + hit.getKey().getMagnitude() + "] at " + hit
                                    .getKey().printLocation());
                }
            }
        }

        Image resultImage = radarImage.clone();

        Map<Integer, Image> indexedInvaders = new LinkedHashMap<>();
        knownInvaders.stream().forEach(i -> indexedInvaders.put(i.getId(), i));

        pointHits.stream().forEach(pair -> {
            Image overlayImage = indexedInvaders.get(pair.getValue());
            Point p = pair.getKey();
            CorrelationUtil.recorrelateInvaderWithId(p.getRow(), p.getCol(), resultImage, overlayImage);
        });

        return resultImage;
    }

    private static Image getFromList(int id, List<Image> imageList) {
        return imageList.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    private static Pair<Point, Integer> getBestInvaderMatchInCLuster(List<Point> pointsInCluster, Image radarImage,
            List<Image> knownInvaders) {
        Point bestMatchedInvaderPoint = new Point();
        int bestMatchedInvaderId = -1;
        for (Image invader : knownInvaders) {
            Point maxMatchedPoint = getBestMatchedPointInCluster(radarImage, pointsInCluster, invader);

            if (maxMatchedPoint.getMagnitude() > bestMatchedInvaderPoint.getMagnitude()) {
                bestMatchedInvaderPoint = maxMatchedPoint.clone();
                bestMatchedInvaderId = invader.getId();
            }
        }

        return new Pair<>(bestMatchedInvaderPoint, bestMatchedInvaderId);
    }

    private static Point getBestMatchedPointInCluster(Image radarImage, List<Point> pointsInCluster, Image invader) {
        Point maxMatchedPoint = new Point();

        for (Point testPoint : pointsInCluster) {
            Pair<Float, Float> hitsRatio = CorrelationUtil
                    .calculateHitsRatio(testPoint.getRow(), testPoint.getCol(), radarImage, invader);
            float testRatio = hitsRatio.getKey() / hitsRatio.getValue();
            if (testRatio > maxMatchedPoint.getMagnitude()) {
                maxMatchedPoint = testPoint.clone();
                maxMatchedPoint.setMagnitude(testRatio);
            }
        }

        return maxMatchedPoint;
    }
}
