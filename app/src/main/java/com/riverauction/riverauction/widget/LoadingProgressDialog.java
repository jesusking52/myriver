package com.riverauction.riverauction.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.riverauction.riverauction.R;

public class LoadingProgressDialog extends AlertDialog {
    private static final int DISMISS_ANIMATION_DURATION = 300;
    private static final int FAIL_ANIMATION_DURATION = 500;
    private static final int FIFTEEN_SECONDS = 15000;

    private LoadingAnimationLayout mView;
    private Runnable mRunAfterDismiss;

    private final Handler mHandler = new Handler();

    public LoadingProgressDialog(Context context) {
        super(context, R.style.Transparent);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new LoadingAnimationLayout(getContext());
        setContentView(mView);
    }

    @Override
    public void show() {
        super.show();
        mView.showProgressImage();
    }

    public void dismissWithSuccess() {
        try {
            dismiss();
        } catch (Exception e) {
            // catch IllegalArgumentException,
            // thrown when activity has already finished before dismiss dialog
        }

        if (null != mRunAfterDismiss) {
            mHandler.postDelayed(mRunAfterDismiss, DISMISS_ANIMATION_DURATION);
        }
    }

    public void dismissWithFail() {
        if (mView != null) {
            mView.showErrorImage();
        }

        mHandler.postDelayed(() -> {
            try {
                dismiss();
            } catch (Exception e) {
                // catch IllegalArgumentException,
                // thrown when activity has already finished before dismiss dialog
            }
            if (null != mRunAfterDismiss) {
                mHandler.post(mRunAfterDismiss);
            }
        }, FAIL_ANIMATION_DURATION + DISMISS_ANIMATION_DURATION);
    }

    public void runAfterDismiss(Runnable runnable) {
        mRunAfterDismiss = runnable;
    }
}
