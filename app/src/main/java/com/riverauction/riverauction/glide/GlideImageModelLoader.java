package com.riverauction.riverauction.glide;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.jhcompany.android.libs.utils.ImageUtils;
import com.riverauction.riverauction.api.model.CImage;

import java.io.InputStream;
import java.util.List;

public class GlideImageModelLoader extends BaseGlideUrlLoader<GlideImage> {

    public GlideImageModelLoader(Context context, ModelCache<GlideImage, GlideUrl> modelCache) {
        super(context, modelCache);
    }

    @Override
    protected String getUrl(GlideImage model, int width, int height) {
        List<CImage> images = model.getImages();

        int index = 0;
        for (; index < images.size(); index++) {
            CImage image = images.get(index);
            if (image.getWidth() >= width && image.getHeight() >= height) {
                break;
            }
        }

        if (index >= images.size()) {
            index = images.size() - 1;
        }

        for (; index >= 0; index--) {
            CImage image = images.get(index);
            if (image.getWidth() <= ImageUtils.getMaxTextureSize() && image.getHeight() < ImageUtils.getMaxTextureSize()) {
                return image.getSource();
            }
        }

        return null;
    }

    public static class GlideModelLoaderFactory implements ModelLoaderFactory<GlideImage, InputStream> {

        private final ModelCache<GlideImage, GlideUrl> modelCache = new ModelCache<>(250);

        @Override
        public ModelLoader<GlideImage, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new GlideImageModelLoader(context, modelCache);
        }

        @Override
        public void teardown() {}
    }
}
