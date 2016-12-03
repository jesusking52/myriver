package com.riverauction.riverauction.glide;

import android.graphics.Bitmap;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * {@link RequestListener} 를 편리하게 사용하기 위한 Adapter 클래스
 * @param <T> Request Model (String, GlideImage ..)
 */
public class GlideRequestAdapter<T> implements RequestListener<T, Bitmap> {

    @Override
    public boolean onException(Exception e, T model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, T model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }
}
