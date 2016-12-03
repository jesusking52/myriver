package com.riverauction.riverauction.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.widget.LoadingAnimationLayout;

public class LoadMoreFooter extends FrameLayout {

    private LoadingAnimationLayout loadingAnimationLayout;
    private Button retryButton;

    public LoadMoreFooter(Context context) {
        super(context);
    }

    public LoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadingAnimationLayout = (LoadingAnimationLayout) findViewById(R.id.item_load_more_footer_animation);
        retryButton = (Button) findViewById(R.id.item_load_more_footer_retry_button);
    }

    public void showRetryButton() {
        loadingAnimationLayout.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }

    public void showLoadingAnimation() {
        loadingAnimationLayout.showProgressImage();
        loadingAnimationLayout.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
    }

    public void setOnRetryButtonClickListener(OnClickListener listener) {
        retryButton.setOnClickListener(listener);
    }
}
