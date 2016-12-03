package com.riverauction.riverauction.glide;


import com.riverauction.riverauction.api.model.CImage;

import java.util.Collections;
import java.util.List;

public class GlideImage {

    public static final float THUMBNAIL_QUALITY = 0.1f;

    private List<CImage> images;
    private String source;

    private GlideImage(List<CImage> images) {
        this.images = images;
        Collections.sort(images, (lhs, rhs) -> (int) (lhs.getWidth() - rhs.getWidth()));
        source = images.get(images.size() - 1).getSource();
    }

    private GlideImage(List<CImage> images, String source) {
        this.images = images;
        Collections.sort(images, (lhs, rhs) -> (int) (lhs.getWidth() - rhs.getWidth()));
        this.source = source;
    }

    public List<CImage> getImages() {
        return images;
    }

    public String getSource() {
        return source;
    }

    public static GlideImage from(List<CImage> images) {
        return new GlideImage(images);
    }
}
