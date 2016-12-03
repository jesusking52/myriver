package com.riverauction.riverauction.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jhcompany.android.libs.utils.DisplayUtils;
import com.riverauction.riverauction.R;

public class LoadingAnimationLayout extends FrameLayout {
    public static final int LARGE = 0;
    public static final int SMALL = 1;

    private static final float LARGE_OUTER_SIZE_DP = 75.f;
    private static final float SMALL_OUTER_SIZE_DP = 44.f;

    private static final float LARGE_INNER_SIZE_DP = 56.f;
    private static final float SMALL_INNER_SIZE_DP = 33.f;

    private ImageView outerView;
    private ImageView innerView;
    private ImageView rotatingArrowImageView;
    private ImageView errorImageView;

    protected int size = LARGE;

    public LoadingAnimationLayout(Context context) {
        super(context);
        init(context);
    }

    public LoadingAnimationLayout(Context context, int size) {
        super(context);
        this.size = size;
        init(context);
    }

    public LoadingAnimationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingAnimationLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // extract attrs
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingAnimationLayout);
        size = attributes.getInt(R.styleable.LoadingAnimationLayout_size, LARGE);
        attributes.recycle();
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.loading_animation_layout, this);

        outerView = (ImageView) findViewById(R.id.loading_animation_outer_view);
        innerView = (ImageView) findViewById(R.id.loading_animation_inner_view);
        rotatingArrowImageView = (ImageView) findViewById(R.id.loading_animation_rotating_arrow);
        errorImageView = (ImageView) findViewById(R.id.loading_animation_error_x);

        ViewGroup.LayoutParams outerParams = outerView.getLayoutParams();
        if (size == LARGE) {
            outerParams.width = DisplayUtils.getPixelFromDP(getContext(), LARGE_OUTER_SIZE_DP);
            outerParams.height = DisplayUtils.getPixelFromDP(getContext(), LARGE_OUTER_SIZE_DP);
        } else {
            outerParams.width = DisplayUtils.getPixelFromDP(getContext(), SMALL_OUTER_SIZE_DP);
            outerParams.height = DisplayUtils.getPixelFromDP(getContext(), SMALL_OUTER_SIZE_DP);
        }
        outerView.setLayoutParams(outerParams);

        ViewGroup.LayoutParams innerParams = innerView.getLayoutParams();
        if (size == LARGE) {
            innerParams.width = DisplayUtils.getPixelFromDP(getContext(), LARGE_INNER_SIZE_DP);
            innerParams.height = DisplayUtils.getPixelFromDP(getContext(), LARGE_INNER_SIZE_DP);
        } else {
            innerParams.width = DisplayUtils.getPixelFromDP(getContext(), SMALL_INNER_SIZE_DP);
            innerParams.height = DisplayUtils.getPixelFromDP(getContext(), SMALL_INNER_SIZE_DP);
        }
        innerView.setLayoutParams(innerParams);

        rotatingArrowImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotation));
    }

    public void showProgressImage() {
        rotatingArrowImageView.clearAnimation();
        rotatingArrowImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotation));
        rotatingArrowImageView.setVisibility(View.VISIBLE);

        errorImageView.clearAnimation();
        errorImageView.setVisibility(View.GONE);
    }

    public void showErrorImage() {
        rotatingArrowImageView.clearAnimation();
        rotatingArrowImageView.setVisibility(View.GONE);

        errorImageView.setVisibility(View.VISIBLE);
        errorImageView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.loading_shake));
    }

    public void stopAnimation() {
        rotatingArrowImageView.clearAnimation();
        errorImageView.clearAnimation();
    }
}
