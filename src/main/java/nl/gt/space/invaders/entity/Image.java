package nl.gt.space.invaders.entity;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Image implements Cloneable {
    private Point[][] pointData;
    private int rows;
    private int cols;
    private int id = 0;

    public Image(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initializePoints(rows,cols);
    }

    @Override
    public Image clone() {
        Image p = new Image(this.rows, this.cols);
        p.setId(this.id);
        Point[][] p_pointData = p.getPointData();
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                p_pointData[i][j] = this.pointData[i][j].clone();
            }
        }

        return p;
    }

    public Image(List<String> imageStreamList) {
        this.rows = imageStreamList.size();
        this.cols = imageStreamList.get(0).length();
        initializePoints(rows,cols);

        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                char inputchar = imageStreamList.get(i).charAt(j);
                pointData[i][j].setPrintchar(inputchar);
                pointData[i][j].setMagnitude((inputchar == 'o' ? 1.0f : 0f));
            }
        }
    }

    private void initializePoints(int rows, int cols) {
        pointData = new Point[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                pointData[i][j] = new Point(i,j,0f);
            }
        }
    }

    public void overLayWith(Image other) {
        if (other.getRows() != this.getRows()
        || other.getCols() != this.getCols()) return;

        for (int i = 0; i < this.getRows(); ++i) {
            for (int j = 0; j < this.getCols(); ++j) {
                if (other.getPointData()[i][j].getMagnitude() > this.getPointData()[i][j].getMagnitude()) {
                    this.getPointData()[i][j].setMagnitude(other.getPointData()[i][j].getMagnitude());
                }
            }
        }
    }
    //public Point[][] getPoints() {
    //    return pointData;
    //}

    public String print() {
        StringBuffer sb = new StringBuffer();
        sb.append("Image ID: ").append(id).append('\n');
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                sb.append((pointData[i][j].getMagnitude() < 1.0f ? '-': 'o'));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String printChars() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                sb.append((pointData[i][j].getPrintchar()));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public List<Point> asList() {
        return Arrays.stream(this.getPointData())
                .flatMap(Arrays::stream)
                .collect(Collectors.toList());
    }

}
