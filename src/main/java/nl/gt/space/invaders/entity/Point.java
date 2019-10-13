package nl.gt.space.invaders.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Point implements Cloneable {
    private int row;
    private int col;
    private float magnitude;
    private float row_float;
    private float col_float;
    private int cluster;
    private char printchar = '-';

    public Point(int row, int col, float magnitude) {
        this.row = row;
        this.col = col;
        this.magnitude = magnitude;
        this.cluster = 0;
        this.row_float = (float) row;
        this.col_float = (float) col;
    }

    @Override
    public Point clone() {
        Point p = new Point();
        p.setRow(this.row);
        p.setCol(this.col);
        p.setMagnitude(this.magnitude);
        p.setCluster(this.cluster);
        p.setRow_float(this.row_float);
        p.setCol_float(this.col_float);
        p.setPrintchar(this.printchar);
        return p;
    }

    public String printLocation() {
        return "<row,col>{" + row + "," + col + "}";
    }
    public float distanceFrom(Point neighbour) {
        float deltaCol = (this.getCol_float() - neighbour.getCol_float());
        float deltaRow = (this.getRow_float() - neighbour.getRow_float());
        return (float) Math.sqrt(deltaCol*deltaCol + deltaRow*deltaRow);
    }
}
