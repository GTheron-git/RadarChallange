package nl.gt.space.invaders.service;

import nl.gt.space.invaders.entity.Image;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class ImageTestService implements ImageService {

    private static final int[][] invaderOne = new int[][]{
            { 0, 0, 1, 1, 0 },
            { 0, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 0 },
            { 1, 1, 1, 1, 0 },
            { 0, 1, 0, 0, 1 }
    };

    private static final int[][] invaderTwo = new int[][]{
            { 0, 0, 0, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 1, 0, 0 },
            { 0, 0, 1, 0, 1, 0, 0 },
            { 1, 1, 0, 0, 0, 1, 1 },
            { 1, 1, 0, 0, 0, 1, 1 },
            { 0, 0, 1, 0, 1, 0, 0 },
            { 0, 0, 1, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0 }
    };

    private static final int testRadarImageHeight = 40;
    private static final int testRadarImageWidth = 50;
    private List<Image> knownInvaders;
    private List<Image> radarImages;

    @PostConstruct
    public void setup() {
        // set up two invaders
        knownInvaders = new ArrayList<>();

        int rows = invaderOne.length;
        int cols = invaderOne[0].length;
        Image invaderOneImage = new Image(rows, cols);
        invaderOneImage.setId(0);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (invaderOne[i][j] == 1) {
                    invaderOneImage.getPointData()[i][j].setMagnitude(1.0f);
                    invaderOneImage.getPointData()[i][j].setPrintchar('o');
                } else {
                    invaderOneImage.getPointData()[i][j].setPrintchar('-');
                }
            }
        }

        rows = invaderTwo.length;
        cols = invaderTwo[0].length;
        Image invaderTwoImage = new Image(rows, cols);
        invaderTwoImage.setId(1);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (invaderTwo[i][j] == 1) {
                    invaderTwoImage.getPointData()[i][j].setMagnitude(1.0f);
                    invaderTwoImage.getPointData()[i][j].setPrintchar('o');
                } else {
                    invaderTwoImage.getPointData()[i][j].setPrintchar('-');
                }
            }
        }

        knownInvaders.add(invaderOneImage);
        knownInvaders.add(invaderTwoImage);

        // set up one radar Image
        radarImages = new ArrayList<>();
        radarImages.add(new Image(testRadarImageHeight, testRadarImageWidth));
    }

    @Override
    public List<Image> getKnownInvaders() {
        return knownInvaders;
    }

    @Override
    public List<Image> getRadarImages() {
        return radarImages;
    }
}
