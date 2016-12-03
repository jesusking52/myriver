package com.riverauction.riverauction.feature.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class RiverAuctionFileUtils {
    public static final File EXTERNAL_DIR = new File(Environment.getExternalStorageDirectory(), "/MatchingTutor");

    private RiverAuctionFileUtils() {}

    public static File createNewPhotoFile() {
        if (!EXTERNAL_DIR.exists()) {
            EXTERNAL_DIR.mkdirs();
        }
        return new File(EXTERNAL_DIR, createPhotoFileName());
    }

    public static String createDownloadFileName(String prefix, String extension) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'" + prefix + "'_yyyyMMdd_HHmmss", Locale.US);
        String nanoString = String.valueOf(System.nanoTime());
        return dateFormat.format(date) + "_" + nanoString.substring(nanoString.length() - 3) + "." + extension;
    }

    public static String createPhotoFileName() {
        return createDownloadFileName("IMG", "jpg");
    }

    public static int getBetweenDateBucketId() {
        return getBucketId(EXTERNAL_DIR.toString());
    }

    private static int getBucketId(String path) {
        return path.toLowerCase().hashCode();
    }
}
