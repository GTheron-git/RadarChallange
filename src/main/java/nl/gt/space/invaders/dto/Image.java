package nl.gt.space.invaders.dto;

import lombok.Data;

import java.util.List;

@Data
public class Image {
    private float[][] data;
    private int rows;
    private int cols;

    public Image(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new float[rows][cols];
    }

    public Image(List<String> imageStreamList) {
        this.rows = imageStreamList.size();
        this.cols = imageStreamList.get(0).length();
        data = new float[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                data[i][j] = (imageStreamList.get(i).charAt(j) == 'o' ? 1 : 0);
            }
        }
    }

    public float[][] get() {
        return data;
    }

    public float[] getRow(int row) {
        return data[row];
    }

    public String print() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                sb.append((data[i][j] == 0L ? '-': 'o'));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public float sumDots() {
        float sum = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                sum += data[i][j];
            }
        }
        return sum;
    }
}
