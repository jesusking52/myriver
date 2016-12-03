package com.riverauction.riverauction.feature.tutorial;

public class TutorialItem {
    private int imageResId;
    private int textImageResId;

    public TutorialItem(int imageResId, int textImageResId) {
        this.imageResId = imageResId;
        this.textImageResId = textImageResId;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getTextImageResId() {
        return textImageResId;
    }

    public void setTextImageResId(int textImageResId) {
        this.textImageResId = textImageResId;
    }
}
