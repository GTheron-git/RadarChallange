package nl.gt.space.invaders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private int row;
    private int col;
    private float magnitude;
    private float row_float;
    private float col_float;
    private int cluster;

    public Point(int row, int col, float magnitude) {
        this.row = row;
        this.col = col;
        this.magnitude = magnitude;
        this.cluster = 0;
        this.row_float = (float) row;
        this.col_float = (float) col;
        this.cluster = 0;
    }
}
