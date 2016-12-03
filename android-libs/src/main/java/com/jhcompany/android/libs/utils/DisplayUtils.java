package com.jhcompany.android.libs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

@SuppressLint("NewApi")
public final class DisplayUtils {
    private DisplayUtils() {}

    /**
     * @param context 호출자의 context
     * @param factor 퍼포먼스 낮은 단말 대응 등에 사용.
     * @return 단말기 폭. dp.
     */
    public static int getDisplayWidth(Context context, float factor) {
        return (int) getDPFromPixel(context, (int) (getDisplayWidthRaw(context) * factor));
    }

    /**
     * @param context 호출자의 context
     * @param factor 퍼포먼스 낮은 단말 대응 등에 사용.
     * @return 단말기 높이.
     */
    public static int getDisplayHeight(Context context, float factor) {
        return (int) getDPFromPixel(context, (int) (getDisplayHeightRaw(context) * factor));
    }

    public static int getDisplayWidthPixel(Context context, float factor) {
        return (int) (getDisplayWidthRaw(context) * factor);
    }

    public static int getDisplayHeightPixel(Context context, float factor) {
        return (int) (getDisplayHeightRaw(context) * factor);
    }

    @SuppressWarnings("deprecation")
    public static int getDisplayHeightRaw(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // JELLY_BEAN_MR1 이후 부터는 Display Size 를 구할 때 getRealSize 를 쓰지 않으면 제대로 된 값이 나오지 않는다.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final Point pointSize = new Point();
            display.getRealSize(pointSize);
            return pointSize.y;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            final Point pointSize = new Point();
            display.getSize(pointSize);
            return pointSize.y;
        } else {
            return display.getHeight();
        }
    }

    @SuppressWarnings("deprecation")
    public static int getDisplayWidthRaw(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // JELLY_BEAN_MR1 이후 부터는 Display Size 를 구할 때 getRealSize 를 쓰지 않으면 제대로 된 값이 나오지 않는다.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final Point pointSize = new Point();
            display.getRealSize(pointSize);
            return pointSize.x;
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            final Point pointSize = new Point();
            display.getSize(pointSize);
            return pointSize.x;
        } else {
            return display.getWidth();
        }
    }

    /**
     * 픽셀단위를 현재 디스플레이 화면에 비례한 크기로 반환합니다.
     *
     * @param pixel 픽셀
     * @return 변환된 값 (DP)
     */
    public static float getDPFromPixel(Context context, int pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        return pixel / scale;
    }

    /**
     * 현재 디스플레이 화면에 비례한 DP 단위를 픽셀 크기로 반환합니다.
     * @return 변환된 값 (pixel)
     */
    public static int getPixelFromDP(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f); // 반올림 (+0.5f)
    }

    /**
     * Scaling factor for the Density Independent Pixel unit<br>
     * (ex. 2 - for XHDPI)
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * Screen density expressed as dots-per-inch<br>
     * (ex. 320 - DENSITY_XHIGH)
     * @param context
     * @return
     */
    public static int getDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    /**
     * display 의 orientation 을 가져온다.
     * @return ORIENTATION_LANDSCAPE or ORIENTATION_PORTRAIT
     */
    public static int getCurrentOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    /**
     * @param context 호출자의 context
     * @param pivotDeviceWidthDp 피벗 디바이스, 기준이 되는 디바이스의 가로 길이(세로 모드 가로)
     * @return 피벗 디바이스 대비 현재 디바이스의 크기 비율.
     */
    public static float deviceRate(Context context, float pivotDeviceWidthDp) {
        if (pivotDeviceWidthDp <= 0) {
            throw new IllegalArgumentException("pivotDeviceWidthDp");
        }
        float currentDeviceWidthDp = DisplayUtils.getDisplayWidth(context, 1.f);
        return currentDeviceWidthDp / pivotDeviceWidthDp;
    }
}
