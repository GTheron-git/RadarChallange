package nl.gt.space.invaders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {
    private int row;
    private int col;
    private int magnitude;
}
