package nl.gt.space.invaders.util;

import nl.gt.space.invaders.entity.Image;
import nl.gt.space.invaders.entity.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClusterUtil {
    private static final float maxStepSizeAsConverged = 0.5f;
    private static final float mergeClustersWhenCloserThan = 2.0f;

    public static int movingMeanSquareCluster(Image correlationImage, int maxInvaderHeight, int maxInvaderWidth) {
        // assume invader is always divisable by 2 XXX
        int clusterRowStep = maxInvaderHeight / 2;
        int clusterColStep = maxInvaderWidth / 2;
        List<Point> clusterCentroids = new ArrayList<>();
        int frameHeight = clusterRowStep - 1;
        int frameWidth  = clusterColStep - 1;
        // create a set of cluster points spaced apart by same dimentions as the size of the invader
        for (int i = clusterRowStep; i + clusterRowStep < correlationImage.getRows(); i += clusterRowStep) {
            for (int j = clusterColStep; j + clusterColStep < correlationImage.getCols(); j += clusterColStep) {
                clusterCentroids.add(new Point(i, j, 0f));
            }
        }

        // Step 1: calculate median point of density in frame centered by cluster centroid


        // calculate new cluster centroid positions

        boolean finishedClustering = false;
        while (!finishedClustering) {
            // do mean avergae sweep
            clusterCentroids.stream().forEach(p -> calculateClusterMedian(p, correlationImage, frameHeight, frameWidth));
            // remove centroids that simply wont move
            clusterCentroids = clusterCentroids.stream().filter(p -> !(p.getCluster() == -1))
                    .collect(Collectors.toList());
            // remove cluster centroid that lie in close proximity
            clusterCentroids = mergeCloseClusters(clusterCentroids);
            finishedClustering = isComplete(clusterCentroids);

        }

        // by now we should have all clusters. assign clusters
        int clusterId = 0;
        for ( Point p :clusterCentroids) {
            p.setCluster(clusterId++);
            p.setRow((int) p.getRow_float());
            p.setCol((int) p.getCol_float());
        }

        for (int i =  0; i < correlationImage.getRows(); ++i) {
            for (int j = 0; j < correlationImage.getCols(); ++j) {
                Point targetPoint = correlationImage.getPointData()[i][j];
                if (targetPoint.getMagnitude() == 0f) {
                    targetPoint.setCluster(-1);
                    continue;
                }
                float minDistance = (float) correlationImage.getCols(); // this is an assumption on longest possible distance
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

    private static boolean isComplete(List<Point>  clusterCentroids) {
        return clusterCentroids.stream().filter(p -> p.getMagnitude() >= maxStepSizeAsConverged).count() == 0;
    }

    private static List<Point> mergeCloseClusters(List<Point> clusterCentroids) {
        for (int i = 0; i < clusterCentroids.size(); ++i) {
            Point p = clusterCentroids.get(i);

            for (int j = i + 1; j < clusterCentroids.size(); ) {
                if(p.distanceFrom(clusterCentroids.get(j)) <  mergeClustersWhenCloserThan) {
                    clusterCentroids.remove(j);
                } else {
                    j++;
                }
            }
        }

        return clusterCentroids;
    }

    public static void calculateClusterMedian(Point p, Image targetimage, int frameHeight, int frameWidth) {
        float totalHitsInFrame = 0f;

        int currentRow = (int) p.getRow_float();
        int currentCol = (int) p.getCol_float();

        int maxFrameRow = currentRow + frameHeight;
        if (maxFrameRow > targetimage.getRows()) maxFrameRow = targetimage.getRows();
        int minFrameRow = currentRow - frameHeight;
        if (minFrameRow < 0) minFrameRow = 0;
        int maxFrameCol = currentCol + frameWidth;
        if (maxFrameCol > targetimage.getCols()) maxFrameCol = targetimage.getCols();
        int minFrameCol = currentCol - frameWidth;
        if (minFrameCol < 0) minFrameCol = 0;
        for (int i = minFrameRow; i < maxFrameRow; ++i) {
            for (int j = minFrameCol; j < maxFrameCol; ++j) {
                totalHitsInFrame += targetimage.getPointData()[i][j].getMagnitude();
            }
        }

        if (totalHitsInFrame == 0) {
            //indicates it will be deleted
            p.setCluster(-1);
            return;
        }

        p.setMagnitude(0); // this will hold the distance needed to travel to next median point
        float initialRowPosition = p.getRow_float();
        float initialColPosition = p.getCol_float();
        float median = totalHitsInFrame/2;

        float medianRowPercentile = 0;
        float topTotal = 0;
        float bottomTotal = 0;

        for (int i = minFrameRow; i < maxFrameRow; ++i) {
            float rowTotal = 0;
            for (int j = minFrameCol; j < maxFrameCol; ++j) {
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

        p.setRow_float(medianRowPercentile);

        float medianColPercentile = 0;
        float leftTotal = 0;
        float rightTotal = 0;
        for (int i = minFrameCol; i < maxFrameCol; ++i) {
            float colTotal = 0;
            for (int j = minFrameRow; j < maxFrameRow; ++j) {
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

        p.setCol_float(medianColPercentile);

        // distance = sqrt((x - x0)^2 + (y - y0)^2)
        float deltaCol = (p.getCol_float() - initialColPosition);
        float deltaRow = (p.getRow_float() - initialRowPosition);
        p.setMagnitude((float) Math.sqrt(deltaCol*deltaCol + deltaRow*deltaRow));
    }
}
