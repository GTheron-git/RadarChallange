package nl.gt.space.invaders.service;

import nl.gt.space.invaders.dto.Image;
import nl.gt.space.invaders.dto.Point;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RadarMatcher {

    public static Image matchKnownInvaders(Image radarImage, List<Image> knownInvaders) {

        Image resultImage = new Image(radarImage.getRows(), radarImage.getCols());

        for (Image invader : knownInvaders) {
             Map<Float, Point> pointScores = new TreeMap<>(Collections.reverseOrder());

            for (int row_ref = 0; row_ref < (radarImage.getRows() - invader.getRows()); ++row_ref) {
                for (int col_ref = 0; col_ref < (radarImage.getCols() - invader.getCols()); ++col_ref) {

                    float pointScore = sumOverlappedImage(row_ref, col_ref, radarImage, invader);
                    resultImage.get()[row_ref][col_ref] = pointScore;
                    pointScores.put(pointScore, new Point(row_ref, col_ref, pointScore));
                }
            }

            System.out.println(resultImage.print());

            for (Map.Entry<Float, Point> e : pointScores.entrySet()) {
                System.out.println(e);
            }
        }

        return radarImage;
    }

    private static float sumOverlappedImage(int row_ref, int col_ref, Image radarImage, Image invader) {
        int totalHits = 0;
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                if (invader.get()[i][j] == 1L && radarImage.get()[row_ref + i][col_ref + j] == 1L) {
                    totalHits += 1L;
                }
            }
        }

        return totalHits;
    }
}
