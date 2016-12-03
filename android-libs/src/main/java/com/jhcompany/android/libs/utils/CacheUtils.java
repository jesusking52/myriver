package com.jhcompany.android.libs.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

public final class CacheUtils {
    private CacheUtils() {}

    /**
     * Check if OS version has built-in external cache dir method.
     * SDK_INT 8 is Froyo
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= 8;
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise).
     * @param context The context to use 
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {

        File cacheDir;
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable()) {

            String externalCachePath = getExternalCacheDir(context).getPath();
            if (TextUtils.isEmpty(uniqueName)) {
                cacheDir = new File(externalCachePath);
            } else {
                cacheDir = new File(externalCachePath + File.separator + uniqueName);
            }
            if (cacheDir.mkdirs() || cacheDir.isDirectory()) {
                return cacheDir;
            }
        }

        String internalCachePath = context.getCacheDir().getPath();
        if (TextUtils.isEmpty(uniqueName)) {
            cacheDir = new File(internalCachePath);
        } else {
            cacheDir = new File(internalCachePath + File.separator + uniqueName);
        }

        cacheDir.mkdirs();
        return cacheDir;
    }

    /**
     * Check if external storage is built-in or removable.
     * @return True if external storage is removable (like an SD card), false
     * otherwise.
     */
    public static boolean isExternalStorageRemovable() {
        // Gingerbread
        return Build.VERSION.SDK_INT < 9 || Environment.isExternalStorageRemovable();
    }

    /**
     * Get the external app cache directory.<br>
     * For getting a Cache directory, Use {@link #getDiskCacheDir(Context, String)}.<br>
     * This method is used for special case (because some devices are not sdcard mounted)
     * @param context The context to use
     * @return The external cache dir
     */
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                return cacheDir;
            }
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * Check how much usable space is available at a given path.
     * @param path The path to check
     * @return The space available in bytes
     */
    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= 9) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Sets the memory cache size based on a percentage of the device memory class.
     * Eg. setting percent to 0.2 would set the memory cache to one fifth of the device memory
     * class. Throws {@link IllegalArgumentException} if percent is < 0.05 or > .8.
     *
     * This value should be chosen carefully based on a number of factors
     * Refer to the corresponding Android Training class for more discussion:
     * http://developer.android.com/training/displaying-bitmaps/
     * @param context Context to use to fetch memory class
     * @param percent Percent of memory class to use to size memory cache
     */
    public static int getMemCacheSizePercent(Context context, float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.05 and 0.8 (inclusive)");
        }
        return Math.round(percent * getMemoryClass(context) * 1024 * 1024);
    }

    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

}
