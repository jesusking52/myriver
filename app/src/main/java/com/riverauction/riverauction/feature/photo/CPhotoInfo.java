package com.riverauction.riverauction.feature.photo;

import android.text.format.Time;

public class CPhotoInfo {

    private String id;
    private long dateMillis; // 시간 정보는 없고, 날짜 정보만 있다.
    private long dateTimeMillis;
    private String path;
    private int orientation;
    private boolean isTemporaryFile; // 피카사의 경우 잠시 다운받아놨다가 업로드 성공 후 지워야한다.
    private int bucketId;

    // suggest theme 에 image view size 위해서 사용한다.
    private int width;
    private int height;

    public CPhotoInfo() {
    }

    public CPhotoInfo(String path) {
        this.path = path;
    }

    public CPhotoInfo(String path, int bucketId) {
        this.path = path;
        this.bucketId = bucketId;
    }

    public CPhotoInfo(String id, long dateTimeMillis, String path, int orientation, int bucketId) {
        this.id = id;
        this.dateTimeMillis = dateTimeMillis;

        Time date = new Time();
        date.set(dateTimeMillis);
        date.hour = 0;
        date.minute = 0;
        date.second = 0;
        dateMillis = date.toMillis(true);

        this.path = path;
        this.orientation = orientation;
        this.bucketId = bucketId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String data) {
        this.path = data;
    }

    public String getId() {
        return id;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
        Time date = new Time();
        date.set(dateTimeMillis);
        date.hour = 0;
        date.minute = 0;
        date.second = 0;
        dateMillis = date.toMillis(true);
    }

    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    public String getPath() {
        return path;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isTemporaryFile() {
        return isTemporaryFile;
    }

    public void setIsTemporaryFile(boolean isTemporaryFile) {
        this.isTemporaryFile = isTemporaryFile;
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().isInstance(this)) {
            return false;
        }

        if (this.path.equals(((CPhotoInfo) o).getPath())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : super.hashCode();
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean hasSize() {
        return height != 0 && width != 0;
    }
}
