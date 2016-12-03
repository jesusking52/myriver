package com.jhcompany.android.libs.utils;

import android.opengl.GLES10;

import javax.microedition.khronos.opengles.GL10;

public final class ImageUtils {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;

    private static int MAX_TEXTURE_SIZE;

    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        MAX_TEXTURE_SIZE = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
    }

    private ImageUtils() {
    }

    public static int getMaxTextureSize() {
        return MAX_TEXTURE_SIZE;
    }
}
