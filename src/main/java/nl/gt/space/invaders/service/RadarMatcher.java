package nl.gt.space.invaders.service;

import jdk.management.resource.internal.TotalResourceContext;
import nl.gt.space.invaders.dto.Image;
import nl.gt.space.invaders.dto.Point;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RadarMatcher {

    public static Image matchKnownInvaders(Image radarImage, List<Image> knownInvaders) {

        Image resultImage = new Image(radarImage.getRows(), radarImage.getCols());
        Map<Integer, Point> pointScores = new TreeMap<>(Collections.reverseOrder());

        Image invader = knownInvaders.get(0);
        //invader.print();
        for (int row_ref = 0; row_ref < (radarImage.getRows() - invader.getRows()); ++row_ref) {
            for (int col_ref = 0; col_ref < (radarImage.getCols() - invader.getCols()); ++col_ref) {
                int pointScore = sumOverlappedImage(row_ref, col_ref, radarImage, invader);
                resultImage.get()[row_ref][col_ref] = pointScore;
                pointScores.put(pointScore, new Point(row_ref, col_ref, pointScore));
            }
        }

        int maxMAtches = 30;
        for (Map.Entry<Integer,Point>  e : pointScores.entrySet()) {
            System.out.println(e);
        }

        return radarImage;
    }

    private static int sumOverlappedImage(int row_ref, int col_ref, Image radarImage, Image invader) {
        int totalHits = 0;
        for (int i = 0; i < invader.getRows(); ++i) {
            for (int j = 0; j < invader.getCols(); ++j) {
                if (invader.get()[i][j] == 1 && radarImage.get()[row_ref + i][col_ref + j] == 1) {
                    totalHits += 1;
                }
            }
        }

        return totalHits;
    }

    /*
    four corner cases

    invader bottom right and radar top left
    - invader[rows-1][cols-1] : radar[0][0]

    invader bottom left and radar top right
    - invader[rows-1][0] : radar[0][cols-1]

    invader top right and radar bottom left
    - invader[0][cols-1] : radar[rows-1][0]

    invader top left and radar bottom right
    - invader[0][0] : radar[rows-1][cols-1]

     */
}
