package nl.gt.space.invaders.util;

import nl.gt.space.invaders.entity.Frame;
import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClusterUtil {

    private static final float maxStepSizeAsConverged = 0.5f;
    private static final float mergeClustersWhenCloserThan = 2.0f;

    public static int movingMeanSquareCluster(Image correlationImage, int maxInvaderHeight, int maxInvaderWidth) {

        int clusterRowStep = maxInvaderHeight / 2;
        int clusterColStep = maxInvaderWidth / 2;
        List<Point> clusterCentroids = new ArrayList<>();
        int frameHeight = clusterRowStep - 1;
        int frameWidth = clusterColStep - 1;

        // create a set of cluster points spaced apart by same dimentions as the size of the invader
        for (int i = clusterRowStep; i + clusterRowStep < correlationImage.getRows(); i += clusterRowStep) {
            for (int j = clusterColStep; j + clusterColStep < correlationImage.getCols(); j += clusterColStep) {
                clusterCentroids.add(new Point(i, j, 0f));
            }
        }

        // calculate convergence series for valid cluster centroids
        boolean finishedClustering = false;
        while (!finishedClustering) {
            // do mean avergae sweep
            clusterCentroids.stream()
                    .forEach(p -> calculateClusterMedian(p, correlationImage, frameHeight, frameWidth));

            // remove centroids that simply wont move
            clusterCentroids = clusterCentroids.stream().filter(p -> !(p.getCluster() == -1))
                    .collect(Collectors.toList());

            // remove cluster centroid that lie in close proximity
            clusterCentroids = mergeCloseClusters(clusterCentroids);

            //check if there are more clusters which will move a significant distance
            finishedClustering = isComplete(clusterCentroids);
        }

        // by now we should have all clusters. assign clusters
        int clusterId = 0;
        for (Point p : clusterCentroids) {
            p.setCluster(clusterId++);
            p.setRow((int) p.getRow_float());
            p.setCol((int) p.getCol_float());
        }

        // determine in which cluster each point in the radar image belongs to and eliminate insignificant points
        for (int i = 0; i < correlationImage.getRows(); ++i) {
            for (int j = 0; j < correlationImage.getCols(); ++j) {
                Point targetPoint = correlationImage.getPointData()[i][j];
                if (targetPoint.getMagnitude() == 0f) {
                    targetPoint.setCluster(-1);
                    continue;
                }
                float minDistance = (float) correlationImage.getCols(); // cannot be more than image boundary
                for (Point p : clusterCentroids) {
                    float testDistance = targetPoint.distanceFrom(p);
                    if (testDistance < minDistance) {
                        minDistance = testDistance;
                        targetPoint.setCluster(p.getCluster());
                    }
                }
            }
        }

        return --clusterId;
    }

    private static boolean isComplete(List<Point> clusterCentroids) {
        return clusterCentroids.stream().filter(p -> p.getMagnitude() >= maxStepSizeAsConverged).count() == 0;
    }

    private static List<Point> mergeCloseClusters(List<Point> clusterCentroids) {
        for (int i = 0; i < clusterCentroids.size(); ++i) {
            Point p = clusterCentroids.get(i);

            for (int j = i + 1; j < clusterCentroids.size(); ) {
                if (p.distanceFrom(clusterCentroids.get(j)) < mergeClustersWhenCloserThan) {
                    clusterCentroids.remove(j);
                } else {
                    j++;
                }
            }
        }

        return clusterCentroids;
    }

    public static void calculateClusterMedian(Point p, Image targetimage, int halfOfFrameHeight, int halfOfFrameWidth) {
        float totalHitsInFrame = 0f;

        // define a valid window around the cluster centroid p
        Frame window = defineFrame(p, targetimage, halfOfFrameHeight, halfOfFrameWidth);

        // get total hits in window
        for (int i = window.getMinRow(); i < window.getMaxRow(); ++i) {
            for (int j = window.getMinCol(); j < window.getMaxCol(); ++j) {
                totalHitsInFrame += targetimage.getPointData()[i][j].getMagnitude();
            }
        }

        //if no points in window then this cluster point will be ignored
        if (totalHitsInFrame == 0) {
            p.setCluster(-1);
            return;
        }

        p.setMagnitude(0);
        // store initial position
        float initialRowPosition = p.getRow_float();
        float initialColPosition = p.getCol_float();

        //calculate new median position
        float medianRowPercentile = calcMedianRowPercentile(window,
                totalHitsInFrame, targetimage);
        float medianColPercentile = calcMedianColPercentile(window,
                totalHitsInFrame, targetimage);

        // store new position
        p.setRow_float(medianRowPercentile);
        p.setCol_float(medianColPercentile);

        // calculate distance from original position
        float deltaCol = (p.getCol_float() - initialColPosition);
        float deltaRow = (p.getRow_float() - initialRowPosition);
        p.setMagnitude((float) Math
                .sqrt(deltaCol * deltaCol + deltaRow * deltaRow)); // distance = sqrt((x - x0)^2 + (y - y0)^2)
    }

    private static Frame defineFrame(Point p, Image targetimage, int halfOfFrameHeight, int halfOfFrameWidth) {
        int currentRow = (int) p.getRow_float();
        int currentCol = (int) p.getCol_float();

        // determine window extremities
        int maxFrameRow = currentRow + halfOfFrameHeight;
        if (maxFrameRow > targetimage.getRows()) {
            maxFrameRow = targetimage.getRows();
        }
        int minFrameRow = currentRow - halfOfFrameHeight;
        if (minFrameRow < 0) {
            minFrameRow = 0;
        }
        int maxFrameCol = currentCol + halfOfFrameWidth;
        if (maxFrameCol > targetimage.getCols()) {
            maxFrameCol = targetimage.getCols();
        }
        int minFrameCol = currentCol - halfOfFrameWidth;
        if (minFrameCol < 0) {
            minFrameCol = 0;
        }

        return new Frame(minFrameRow, maxFrameRow, minFrameCol, maxFrameCol);
    }

    private static float calcMedianRowPercentile(Frame window, float totalHitsInFrame, Image targetimage) {
        float medianRowPercentile = 0;
        float median = totalHitsInFrame / 2;
        float topTotal = 0;
        float bottomTotal = 0;

        for (int i = window.getMinRow(); i < window.getMaxRow(); ++i) {
            float rowTotal = 0;
            for (int j = window.getMinCol(); j < window.getMaxCol(); ++j) {
                rowTotal += targetimage.getPointData()[i][j].getMagnitude();
            }

            float tempTotal = topTotal + rowTotal;
            if (tempTotal == median) {
                medianRowPercentile = (float) i;
                break;
            } else if (tempTotal < median) {
                topTotal = tempTotal;
            } else {
                bottomTotal = totalHitsInFrame - tempTotal;
                float difference = totalHitsInFrame - (topTotal + bottomTotal);
                float topDifference = median - topTotal;
                medianRowPercentile = ((float) i) + (topDifference / difference);
                break;
            }
        }
        return medianRowPercentile;
    }

    private static float calcMedianColPercentile(Frame window, float totalHitsInFrame, Image targetimage) {
        float medianColPercentile = 0;
        float median = totalHitsInFrame / 2;
        float leftTotal = 0;
        float rightTotal = 0;

        for (int i = window.getMinCol(); i < window.getMaxCol(); ++i) {
            float colTotal = 0;
            for (int j = window.getMinRow(); j < window.getMaxRow(); ++j) {
                colTotal += targetimage.getPointData()[j][i].getMagnitude();
            }

            float tempTotal = leftTotal + colTotal;
            if (tempTotal == median) {
                medianColPercentile = (float) i;
                break;
            } else if (tempTotal < median) {
                leftTotal = tempTotal;
            } else {
                rightTotal = totalHitsInFrame - tempTotal;
                float difference = totalHitsInFrame - (leftTotal + rightTotal);
                float leftDifference = median - leftTotal;
                medianColPercentile = ((float) i) + (leftDifference / difference);
                break;
            }
        }
        return medianColPercentile;
    }

}
