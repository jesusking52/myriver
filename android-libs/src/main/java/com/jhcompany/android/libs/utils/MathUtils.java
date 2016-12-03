package com.jhcompany.android.libs.utils;

@SuppressWarnings("unused")
public final class MathUtils {

    private MathUtils() {
    }

    /**
     * Saturation clamping
     *
     * @param min   하한값
     * @param value 대상값
     * @param max   상한값
     * @return Math.min(Math.max(min, value), max)
     */
    public static double clamp(double min, double value, double max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Saturation clamping
     *
     * @param min   하한값
     * @param value 대상값
     * @param max   상한값
     * @return Math.min(Math.max(min, value), max)
     */
    public static float clamp(float min, float value, float max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Saturation clamping
     *
     * @param min   하한값
     * @param value 대상값
     * @param max   상한값
     * @return Math.min(Math.max(min, value), max)
     */
    public static int clamp(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Saturation clamping
     *
     * @param min   하한값
     * @param value 대상값
     * @param max   상한값
     * @return Math.min(Math.max(min, value), max)
     */
    public static long clamp(long min, long value, long max) {
        return Math.min(Math.max(min, value), max);
    }
}
