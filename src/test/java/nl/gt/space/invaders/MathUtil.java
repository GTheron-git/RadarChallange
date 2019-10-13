package nl.gt.space.invaders;

public class MathUtil {
    public static int generateRandomNumber(int scale) {
        return (int) Math.round((double) scale * Math.random());
    }

}
