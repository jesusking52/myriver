package com.jhcompany.android.libs.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.ParcelFileDescriptor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public final class IOUtils {
    private IOUtils() {}

    private static final String TAG = "IOUtils";
    private static final String NEW_LINE = System.getProperty("line.separator");

    public static String readString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 300);
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(NEW_LINE);
            }
            return sb.toString();
        } finally {
            closeSilently(reader);
        }
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 300);
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(NEW_LINE);
            }
            return sb.toString().getBytes();
        } finally {
            closeSilently(reader);
        }
    }

    public static BufferedReader asBufferedReader(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 300);
        return reader;
    }

    public static byte[] getBytes(ByteBuffer buf) {
        if (buf.isDirect()) {
            int position = buf.position();
            byte[] res = new byte[buf.limit()];
            buf.get(res).position(position);
            return res;
        } else {
            return buf.array();
        }
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static InputStream bitmapToByteInputStream(Bitmap bitmap) {
        /* application/octet-stream */
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return new ByteArrayInputStream(byteArray);
    }

    public static void closeSilently(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void closeSilently(Cursor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static void closeSilently(ParcelFileDescriptor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }
}
