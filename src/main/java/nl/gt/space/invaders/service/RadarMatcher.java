package nl.gt.space.invaders.service;

import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.config.constants.Constants;
import nl.gt.space.invaders.util.ClusterUtil;
import nl.gt.space.invaders.util.CorrelationUtil;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;
import nl.gt.space.invaders.util.ImageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RadarMatcher {

    private static final int maxInvaderPerCluster = 3;

    private final ImageService imageService;
    private final Constants constants;

    @PostConstruct
    public void runMatches() {
        List<Image> matchedRadarImages = imageService.getRadarImages().stream().map(i -> matchKnownInvaders(i))
                .collect(Collectors.toList());
        matchedRadarImages.stream().forEach(
                i -> log.info("Radar Image [{}] scanned and result is:\n{}", i.getId(), ImageUtil.printChars(i)));
    }

    public Image matchKnownInvaders(Image radarImage) {
        List<Image> knownInvaders = imageService.getKnownInvaders();

        // Get a list of images showing significant areas of correlation between each invader and the radar image
        List<Image> correlationImageList = knownInvaders.stream()
                .map(i -> CorrelationUtil.getCorrelationImage(radarImage, i, constants.getCutoffMatchDensity()))
                .collect(Collectors.toList());

        // Overlay these images on top of another to get a singe image representing greatest areas of correlation
        // between all invaders and rader image
        Image mergedImage = ImageUtil.mergeImages(correlationImageList);

        // get the max row and col to determine window size for clustering
        Pair<Integer, Integer> maxRowAndCol = ImageUtil.getMaxRowAndCol(knownInvaders);

        // Perform Mean Average Clustering on correlation image
        int maxClusterId = ClusterUtil
                .movingMeanSquareCluster(mergedImage, maxRowAndCol.getKey(), maxRowAndCol.getValue());

        // Group all points from the same cluster as clusterList and put all groups in list called clusterLists
        List<List<Point>> clusterLists = new ArrayList<>();
        for (int i = 0; i <= maxClusterId; ++i) {
            final int targetCluster = i;
            clusterLists.add(ImageUtil.asList(mergedImage).stream().filter(p -> p.getCluster() == targetCluster)
                    .collect(Collectors.toList()));
        }

        // get all points in cluster and their related invader image id that are good matches
        List<Pair<Point, Integer>> pointHits = new ArrayList<>(); // this contains all the real hits
        for (List<Point> clusterList : clusterLists) {
            pointHits.addAll(getClusterHits(clusterList, radarImage));
        }

        // add each invader that matched back onto the original radar image, substituting 'o' for the invader id
        Image resultImage = radarImage.clone();
        pointHits.stream().forEach(pair -> {
            Image overlayImage = ImageUtil.getFromList(pair.getValue(), knownInvaders);
            Point p = pair.getKey();
            CorrelationUtil.recorrelateInvaderWithId(p.getRow(), p.getCol(), resultImage, overlayImage);
        });

        return resultImage;
    }

    private List<Pair<Point, Integer>> getClusterHits(List<Point> clusterList, Image radarImage) {
        List<Pair<Point, Integer>> clusterHits = new ArrayList<>();
        Image copyOfRadarImage = radarImage.clone();
        int maxIterations = maxInvaderPerCluster;
        while (maxIterations-- > 0) {
            Pair<Point, Integer> hit = getBestInvaderMatchInCLuster(clusterList, copyOfRadarImage,
                    imageService.getKnownInvaders());

            if (hit.getKey().getMagnitude() < constants.getAcceptedMatchRatioForHit()) {
                break;
            } else {
                clusterHits.add(hit);
                Image bestMatchedInvader = ImageUtil.getFromList(hit.getValue(), imageService.getKnownInvaders());
                CorrelationUtil.decorrelateInvader(hit.getKey().getRow(), hit.getKey().getCol(), copyOfRadarImage,
                        bestMatchedInvader);

                log.info("Invader [{}] scored [{}] at [{}]", hit.getValue(), hit.getKey().getMagnitude(),
                        hit.getKey().printLocation());
            }
        }

        return clusterHits;
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
