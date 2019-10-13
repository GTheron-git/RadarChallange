package nl.gt.space.invaders.util;

import javafx.util.Pair;
import nl.gt.space.invaders.entity.Image;

import java.util.Objects;

public class CorrelationUtil {

    public static Image getCorrelationImage(Image radarImage, Image invaderImage, float hitCutoffRatio) {
        Image resultImage = new Image(radarImage.getRows(), radarImage.getCols());

        for (int row_ref = 0; row_ref < radarImage.getRows(); ++row_ref) {
            for (int col_ref = 0; col_ref < radarImage.getCols(); ++col_ref) {
                Pair<Float, Float> hitsRatioPair = calculateHitsRatio(row_ref, col_ref, radarImage, invaderImage);

                resultImage.getPointData()[row_ref][col_ref].setMagnitude(hitsRatioPair.getKey());

                if (hitsRatioPair.getKey()/hitsRatioPair.getValue() > hitCutoffRatio) {
                    resultImage.getPointData()[row_ref][col_ref].setMagnitude(hitsRatioPair.getKey());
                } else {
                    resultImage.getPointData()[row_ref][col_ref].setCluster(-1);
                    resultImage.getPointData()[row_ref][col_ref].setMagnitude(0);
                }
            }
        }

        return resultImage;
    }

    public static Pair<Float, Float> calculateHitsRatio(int row_ref, int col_ref, Image radarImage, Image invader) {
        float totalHits = 0f;
        float possibleHits = 0f;
        int rowOffset = -(invader.getRows()/2);
        int colOffset = -(invader.getCols()/2);
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                int radarRow = i + row_ref + rowOffset;
                int radarCol = j + col_ref + colOffset;
                if (!isValidCoordinate(radarRow, radarCol, radarImage)) continue;
                if (invader.getPointData()[i][j].getMagnitude() != 1.0f) continue;

                possibleHits += 1.0f;
                if (invader.getPointData()[i][j].getMagnitude() == 1.0f
                    && radarImage.getPointData()[radarRow][radarCol].getMagnitude() == 1.0f) {
                    totalHits += 1f;
                }
            }
        }
        return new Pair<>(totalHits, possibleHits);
    }

    public static void decorrelateInvader(int row_ref, int col_ref, Image radarImage, Image invader) {
        if (Objects.isNull(invader)) {
            return;
        }

        int rowOffset = -(invader.getRows()/2);
        int colOffset = -(invader.getCols()/2);
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                int radarRow = i + row_ref + rowOffset;
                int radarCol = j + col_ref + colOffset;
                if (!isValidCoordinate(radarRow, radarCol, radarImage)) continue;
                if (invader.getPointData()[i][j].getMagnitude() == 1.0f) {
                    radarImage.getPointData()[radarRow][radarCol].setMagnitude(0f);
                }
            }
        }
    }

    public static void recorrelateInvaderWithId(int row_ref, int col_ref, Image radarImage, Image invader) {
        if (Objects.isNull(invader)) {
            return;
        }

        int rowOffset = -(invader.getRows()/2);
        int colOffset = -(invader.getCols()/2);
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                int radarRow = i + row_ref + rowOffset;
                int radarCol = j + col_ref + colOffset;
                if (!isValidCoordinate(radarRow, radarCol, radarImage)) continue;

                if (invader.getPointData()[i][j].getMagnitude() == 1.0f) {
                    radarImage.getPointData()[radarRow][radarCol].setMagnitude(1.0f);
                    radarImage.getPointData()[radarRow][radarCol].setPrintchar(String.valueOf(invader.getId()).charAt(0));
                }
            }
        }
    }

    public static void recorrelateInvader(int row_ref, int col_ref, Image radarImage, Image invader) {
        if (Objects.isNull(invader)) {
            return;
        }

        int rowOffset = -(invader.getRows()/2);
        int colOffset = -(invader.getCols()/2);
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                int radarRow = i + row_ref + rowOffset;
                int radarCol = j + col_ref + colOffset;
                if (!isValidCoordinate(radarRow, radarCol, radarImage)) continue;

                if (invader.getPointData()[i][j].getMagnitude() == 1.0f) {
                    radarImage.getPointData()[radarRow][radarCol].setMagnitude(1.0f);
                    radarImage.getPointData()[radarRow][radarCol].setPrintchar('o');
                }
            }
        }
    }

    private static boolean isValidCoordinate(int row, int col, Image targetImage) {
        if (row < 0 || row >= targetImage.getRows()) return false;
        if (col < 0 || col >= targetImage.getCols()) return false;
        return true;
    }
}
