package nl.gt.space.invaders.util;

public class MathUtil {
    public static int generateRandomNumber(int scale) {
        return (int) Math.round((double) scale * Math.random());
    }

}
