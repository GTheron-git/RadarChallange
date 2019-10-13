package nl.gt.space.invaders.service;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import nl.gt.space.invaders.util.MathUtil;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;
import nl.gt.space.invaders.util.CorrelationUtil;
import nl.gt.space.invaders.util.ImageUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SpringBootTest()
@RunWith(SpringRunner.class)
@Slf4j
public class RadarMatcherTest {

    private static final float noiseDensity = 0.1f;
    private static final int numberOfInvaders = 4;
    private float minimumDistanceBetweenInvaders = 5f;
    private float requiredSuccessRate = 0.7f;
    private int numberOfTestRuns = 20;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RadarMatcher radarMatcher;

    @Before
    public void setUp() throws Exception {
        List<Image> spaceInvaders = imageService.getKnownInvaders();
        log.info("Loaded [{}] invaders", spaceInvaders.size());
        spaceInvaders.stream().forEach(invader -> log.info("{}", ImageUtil.printChars(invader)));
    }

    private float shortestDistanceToNeighbour(Point p, List<Point> neighbours) {
        return neighbours.stream().map(i -> i.distanceFrom(p)).min(Float::compare).get();
    }

    @Test
    public void matchKnownInvaders() {
        float successfulTests = 0.0f;
        for (int testRun = 0; testRun < numberOfTestRuns; ++testRun) {
            successfulTests += testScanPerfectMatch() == Boolean.TRUE ? 1.0f : 0.0f;
        }

        log.info("Number of test runs [{}], number of perfect matches [{}]", numberOfTestRuns, successfulTests);
        assertTrue((successfulTests / (float) numberOfTestRuns) > requiredSuccessRate);
    }

    private boolean testScanPerfectMatch() {
        Image radarImage = imageService.getRadarImages().get(0).clone();
        List<Pair<Point, Integer>> testHits = new ArrayList<>();
        for (int i = 0; i < numberOfInvaders; ++i) {
            int randomRow = MathUtil.generateRandomNumber(radarImage.getRows());
            int randomCol = MathUtil.generateRandomNumber(radarImage.getCols());
            int randomInvaderId = MathUtil.generateRandomNumber(1);
            Point testHitPoint = new Point(randomRow, randomCol, 1.0f);

            if (i > 0 && shortestDistanceToNeighbour(testHitPoint,
                    testHits.stream().map(p -> p.getKey()).collect(Collectors.toList()))
                         < minimumDistanceBetweenInvaders) {
                i--;
                continue;
            }

            testHits.add(new Pair(testHitPoint, randomInvaderId));
            CorrelationUtil.recorrelateInvader(randomRow, randomCol, radarImage,
                    ImageUtil.getFromList(randomInvaderId, imageService.getKnownInvaders()));
        }

        Image noisyImage = ImageUtil.applyNoise(noiseDensity, radarImage);

        log.info("originalNoisyImage: \n[{}]", ImageUtil.printChars(noisyImage));
        Image scannedRadarImage = radarMatcher.matchKnownInvaders(noisyImage);
        log.info("scannedNoisyImage: \n[{}]", ImageUtil.printChars(scannedRadarImage));

        Image testImage = noisyImage.clone();
        testHits.stream().forEach(pair -> {
            Image overlayImage = ImageUtil.getFromList(pair.getValue(), imageService.getKnownInvaders());
            Point p = pair.getKey();
            CorrelationUtil.recorrelateInvaderWithId(p.getRow(), p.getCol(), testImage, overlayImage);
        });

        log.info("Test Image: \n[{}]", ImageUtil.printChars(testImage));

        for (int i = 0; i < testImage.getRows(); ++i) {
            for (int j = 0; j < testImage.getCols(); ++j) {
                if (testImage.getPointData()[i][j].getPrintchar() != scannedRadarImage.getPointData()[i][j]
                        .getPrintchar()) {
                    return false;
                }
            }
        }
        return true;
    }
}