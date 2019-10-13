package nl.gt.space.invaders.util;

import javafx.util.Pair;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageUtil {

    public static Image mergeImages(List<Image> images) {
        Image resultImage = images.get(0);
        images.stream().forEach(i -> mergeLayers(resultImage, i));
        return resultImage;
    }

    public static void mergeLayers(Image baseLayer, Image topLayer) {
        if (topLayer.getRows() != baseLayer.getRows() || topLayer.getCols() != baseLayer.getCols()) {
            return;
        }

        for (int i = 0; i < baseLayer.getRows(); ++i) {
            for (int j = 0; j < baseLayer.getCols(); ++j) {
                float topMagnitude = topLayer.getPointData()[i][j].getMagnitude();
                if (topMagnitude > baseLayer.getPointData()[i][j].getMagnitude()) {
                    baseLayer.getPointData()[i][j].setMagnitude(topMagnitude);
                }
            }
        }
    }

    public static Image getFromList(int id, List<Image> imageList) {
        return imageList.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    public static Pair<Integer, Integer> getMaxRowAndCol(List<Image> imageList) {
        int maxInvaderHeight = imageList.stream().map(i -> i.getRows()).max(Integer::compare).get();
        int maxInvaderWidth = imageList.stream().map(i -> i.getCols()).max(Integer::compare).get();
        return new Pair(maxInvaderHeight, maxInvaderWidth);
    }

    public static String printChars(Image image) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < image.getRows(); ++i) {
            for (int j = 0; j < image.getCols(); ++j) {
                sb.append((image.getPointData()[i][j].getPrintchar()));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static List<Point> asList(Image image) {
        return Arrays.stream(image.getPointData()).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public static Image applyNoise(float noisePercentage, Image targetImage) {
        if (noisePercentage < 0.0f || noisePercentage > 1.0f) {
            throw new RuntimeException("Noise level [" + noisePercentage + "] invalid, expecting [0-1]");
        }

        Image resultImage = targetImage.clone();
        for (int i = 0; i < resultImage.getRows(); ++i) {
            for (int j = 0; j < resultImage.getCols(); ++j) {
                float randomFloat = (float) Math.random();
                if (randomFloat < noisePercentage) {
                    Point p = resultImage.getPointData()[i][j];
                    p.setMagnitude(Math.abs(Math.round(p.getMagnitude() - 1.0f)));
                    if (p.getMagnitude() == 0) {
                        p.setPrintchar('-');
                    } else {
                        p.setPrintchar('o');
                    }
                }
            }
        }
        System.out.println(ImageUtil.printChars(resultImage));
        return resultImage;

    }
}
