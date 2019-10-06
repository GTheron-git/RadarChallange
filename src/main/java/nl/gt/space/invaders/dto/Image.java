package nl.gt.space.invaders.dto;

import lombok.Data;

import java.util.List;

@Data
public class Image {
    private int[][] data;
    private int rows;
    private int cols;

    public Image(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new int[rows][cols];
    }

    public Image(List<String> imageStreamList) {
        this.rows = imageStreamList.size();
        this.cols = imageStreamList.get(0).length();
        data = new int[rows][cols];
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                data[i][j] = (imageStreamList.get(i).charAt(j) == 'o' ? 1 : 0);
            }
        }
    }

    public int[][] get() {
        return data;
    }

    public int[] getRow(int row) {
        return data[row];
    }

    public String print() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rows; ++i) {
            for (int j=0; j < cols; ++j) {
                sb.append((data[i][j] == 0 ? '-': 'o'));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
