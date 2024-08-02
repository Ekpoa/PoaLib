package poa.poalib.Math;

public class Euler {
    public static float radiansToDegrees(float radians) {
        return (float) ((radians * 180) / Math.PI);
    }

    public static float degreesToRadians(float degrees) {
        return (float) ((degrees * Math.PI) / 180);
    }

}

