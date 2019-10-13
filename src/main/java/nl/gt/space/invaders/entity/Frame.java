package nl.gt.space.invaders.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Frame {
    int minRow;
    int maxRow;
    int minCol;
    int maxCol;
}
