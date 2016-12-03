package com.jhcompany.android.libs.utils;

import android.content.Context;
import android.media.ExifInterface;
import android.os.Environment;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtils {
    private FileUtils() {
    }

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");

    public static final String EXT_SEPARATOR = ".";
    public static final String EXT_ATTACHOR = "_";

    private static final String FILE_NAME_REX = "[^a-zA-Z0-9.-]";
    private static final String UNDERSCORE = "_";

    public static String getExt(String fileName) {
        String ext = "";
        int extIndex = fileName.lastIndexOf(EXT_SEPARATOR);
        if (extIndex > 0) {
            ext = fileName.substring(extIndex + 1);
        }
        return ext;
    }

    public static String getRandomFile(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(MD5Utils.getMd5Hex(path + RandomValues.randomAbsInt()));
        return new File(path, sb.toString()).getAbsolutePath();
    }

    /**
     * file path 를 주면 해당 file 이 있는 path 에 random file 을 생성해준다.
     *
     * @param originalFile
     * @return
     */
    public static String getRandomFileFromFile(String originalFile) {
        StringBuilder sb = new StringBuilder();
        sb.append(originalFile);
        sb.append(EXT_SEPARATOR);
        sb.append(MD5Utils.getMd5Hex(originalFile));
        sb.append(EXT_SEPARATOR);
        sb.append(String.valueOf(RandomValues.randomAbsInt()));
        return sb.toString();
    }

    public static String addExt(String fileName, String ext) {
        return fileName.concat(EXT_SEPARATOR).concat(ext);
    }

    public static boolean hasExt(String fileName) {
        int extIndex = fileName.lastIndexOf(EXT_SEPARATOR);
        return extIndex > 0;
    }

    public static String removeExt(String fileName) {
        if (hasExt(fileName)) {
            int extIndex = fileName.lastIndexOf(EXT_SEPARATOR);
            return fileName.substring(0, extIndex - 1);
        }
        return fileName;
    }

    public static void touchFile(File target) {
        try {
            target.setLastModified(System.currentTimeMillis());
        } catch (Exception e) {
        }
    }

    public static void copyFile(File target, File source) throws IOException {
        if (!target.exists()) {
            target.createNewFile();
        }

        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);

            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            IOUtils.closeSilently(in);
            IOUtils.closeSilently(out);
        }
    }

    public static void copyAsset(Context context, String fileName, File dst) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            if (dst.exists()) {
                dst.delete();
            }
            dst.createNewFile();

            in = context.getAssets().open(fileName);
            out = new FileOutputStream(dst);

            byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            IOUtils.closeSilently(in);
            IOUtils.closeSilently(out);
        }
    }

    public static String getIgnoredMediaFiledName(String fileName) {
        return fileName + EXT_ATTACHOR;
    }

    public static String getIgnoredMediaFiledName(String fileName, String ext) {
        StringBuilder sb = new StringBuilder();
        sb.append(removeExt(fileName));
        sb.append(EXT_SEPARATOR);
        sb.append(ext);
        sb.append(EXT_ATTACHOR);
        return sb.toString();
    }

    public static File getPhotoFile() {
        if (!PHOTO_DIR.exists()) {
            PHOTO_DIR.mkdirs();
        }
        return new File(PHOTO_DIR, getPhotoFileName());
    }

    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.US);
        return dateFormat.format(date) + ".jpg";
    }

    public static boolean deleteDir(File dir) {
        FileUtils.iterFiles(dir, true, new FileVisitor() {
            @Override
            public void visit(File file) {
                file.delete();
            }
        });
        return true;
    }

    public static void iterFiles(File root, boolean recursive, FileVisitor visitor) {
        if (root.exists()) {
            final File[] list = root.listFiles();
            if (root.isDirectory()) {
                if (recursive) {
                    for (File item : list) {
                        iterFiles(item, recursive, visitor);
                    }
                }
            } else {
                visitor.visit(root);
            }
        }
    }

    public void copyExif(File targetfile, File originalFile) throws IOException {
        /* Copy EXIF information */
        final ExifInterface oldexif = new ExifInterface(originalFile.getAbsolutePath());
        final ExifInterface newexif = new ExifInterface(targetfile.getAbsolutePath());

        copyAttribute(newexif, oldexif, "FNumber");
        copyAttribute(newexif, oldexif, "ExposureTime");
        copyAttribute(newexif, oldexif, "ISOSpeedRatings");
        copyAttribute(newexif, oldexif, "GPSAltitude");
        copyAttribute(newexif, oldexif, "GPSAltitudeRef");
        copyAttribute(newexif, oldexif, "FocalLength");
        copyAttribute(newexif, oldexif, "GPSDateStamp");
        copyAttribute(newexif, oldexif, "GPSProcessingMethod");
        copyAttribute(newexif, oldexif, "GPSTimeStamp");

        copyAttribute(newexif, oldexif, "DateTime");
        copyAttribute(newexif, oldexif, "Flash");
        copyAttribute(newexif, oldexif, "GPSLatitude");
        copyAttribute(newexif, oldexif, "GPSLatitudeRef");
        copyAttribute(newexif, oldexif, "GPSLongitude");
        copyAttribute(newexif, oldexif, "GPSLongitudeRef");
        copyAttribute(newexif, oldexif, "GPSTimeStamp");
        copyAttribute(newexif, oldexif, "GPSTimeStamp");

        // copyAttribute(newexif, oldexif, "ImageLength");
        // copyAttribute(newexif, oldexif, "ImageWidth");
        copyAttribute(newexif, oldexif, "Make");
        copyAttribute(newexif, oldexif, "Model");
        copyAttribute(newexif, oldexif, "Orientation");
        copyAttribute(newexif, oldexif, "WhiteBalance");
        copyAttribute(newexif, oldexif, "DateTimeOriginal", "DateTime");

        newexif.saveAttributes();
    }

    private static void copyAttribute(ExifInterface target, ExifInterface source, String attrName) {
        String value = source.getAttribute(attrName);
        if (!Strings.isNullOrEmpty(value)) {
            target.setAttribute(attrName, value);
        }
    }

    private static void copyAttribute(ExifInterface target, ExifInterface source, String targetAttrName, String sourceAttrName) {
        String value = source.getAttribute(sourceAttrName);
        if (!Strings.isNullOrEmpty(value)) {
            target.setAttribute(targetAttrName, value);
        }
    }

    public interface FileVisitor {
        public void visit(File file);
    }

    public static String getName(String cachePath) {
        return new File(cachePath).getName();
    }

    /**
     * Scheme name(file://) 을 제거해서 실제 File 객체 Path 로 사용할 수 있도록 만들어준다.
     *
     * @param path
     * @return
     */
    public static String nomalize(String path) {
        if (path == null) {
            return path;
        }
        String result = path;
        if (result.startsWith("file://")) {
            result = result.substring(7);
        }
        return result;
    }

    /**
     * Path 가 file:// (scheme name) 으로 시작하는 경우, 실제 File Path 로 변경하기 위해서는 file:// 을 제거해줘야 한다.
     *
     * @param path
     * @return
     */
    public static boolean needNormalize(String path) {
        return path.startsWith("file://");
    }

    public static void saveFromInputStream(File targetFile, InputStream is) {
        File tempFile = null;

        try {
            // save to tempfile and rename to targetFile after succeed, bacause
            // sometime download can be failed
            tempFile = new File(FileUtils.getRandomFileFromFile(targetFile.getAbsolutePath()));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                // FlushedInputStream is required becuase of google issues
                // http://code.google.com/p/android/issues/detail?id=6066
                inputStream = new FlushedInputStream(new BufferedInputStream(is, DEFAULT_BUFFER_SIZE));
                outputStream = new BufferedOutputStream(new FileOutputStream(tempFile), DEFAULT_BUFFER_SIZE);

                int readCnt = 0;
                byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];

                while ((readCnt = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, readCnt);
                }
                // flush output stream
                outputStream.flush();
            } finally {
                // close all of streams
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            // rename dowloaded file to target file if success
            tempFile.renameTo(targetFile);
        } catch (IOException e) {
        } finally {
            try {
                tempFile.delete();
            } catch (Exception e) {
            }
        }
    }

    public static void unZipFile(File source, File outputFolder) throws IOException {
        Preconditions.checkNotNull(source);

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(source));
        ZipEntry zipEntry;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(outputFolder, zipEntry.getName()));
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
                IOUtils.closeSilently(outputStream);
            } catch (FileNotFoundException e) {
                continue;
            }
        }
        IOUtils.closeSilently(zipInputStream);
    }

    /**
     * 파일 이름에 semicolon 등과 같은 특수문자가 들어가 있으면 HttpClient#execute() 에서 에러가 발생한다.
     * 따라서 파일이름을 execute() 가능 하도록 치환한다.
     * ex) 23;45;123.png -> 23_45_123.png
     *
     * @param originFileName 원래 파일 이름.
     * @return 치환된 파일 이름.
     */
    public static String replaceFileName(String originFileName) {
        return originFileName.replaceAll(FILE_NAME_REX, UNDERSCORE);
    }

    public static boolean isValidFile(File file) {
        if (file == null) {
            return false;
        }

        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }

        if (!file.canRead()) {
            return false;
        }

        return true;
    }

    public static List<String> readFromAssests(Context context, String filename) {
        InputStream is;
        InputStreamReader isr;
        BufferedReader br;

        String line;
        List<String> stringList = Lists.newArrayList();
        try {
            is = context.getAssets().open(filename);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                stringList.add(line);
            }

            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            // TODO: error handle
        }

        return stringList;
    }

    public static String readString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 300);
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(NEW_LINE);
            }
            return sb.toString();
        } finally {
            try {
                reader.close();
            } catch (Exception ignored) {}
        }
    }
}
