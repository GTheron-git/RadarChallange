package nl.gt.space.invaders.entity;

import lombok.Data;

import java.util.List;

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

    private void initializePoints(int rows, int cols) {
        pointData = new Point[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                pointData[i][j] = new Point(i,j,0f);
            }
        }
    }
}
