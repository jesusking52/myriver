package com.riverauction.riverauction.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StatusViewBuilder {

    private int emptyViewResId;
    private View emptyView;

    private int errorViewResId;
    private View errorView;

    private int loadingViewResId;
    private View loadingView;

    private int resultViewResId;
    private View resultView;

    public StatusViewBuilder setEmptyViewResId(int emptyViewResId) {
        this.emptyViewResId = emptyViewResId;
        return this;
    }

    public StatusViewBuilder setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        return this;
    }

    public StatusViewBuilder setErrorViewResId(int errorViewResId) {
        this.errorViewResId = errorViewResId;
        return this;
    }

    public StatusViewBuilder setErrorView(View errorView) {
        this.errorView = errorView;
        return this;
    }

    public StatusViewBuilder setLoadingViewResId(int loadingViewResId) {
        this.loadingViewResId = loadingViewResId;
        return this;
    }

    public StatusViewBuilder setLoadingView(View loadingView) {
        this.loadingView = loadingView;
        return this;
    }

    public StatusViewBuilder setResultViewResId(int resultViewResId) {
        this.resultViewResId = resultViewResId;
        return this;
    }

    public StatusViewBuilder setResultView(View resultView) {
        this.resultView = resultView;
        return this;
    }

    public StatusView createView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        StatusView rootLayout = new StatusView(context);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if (emptyView == null && emptyViewResId != 0) {
            emptyView = inflater.inflate(emptyViewResId, rootLayout, false);
        }

        if (errorView == null && errorViewResId != 0) {
            errorView = inflater.inflate(errorViewResId, rootLayout, false);
        }

        if (loadingView == null && loadingViewResId != 0) {
            loadingView = inflater.inflate(loadingViewResId, rootLayout, false);
        }

        if (resultView == null && resultViewResId != 0) {
            resultView = inflater.inflate(resultViewResId, rootLayout, false);
        }

        if (emptyView != null) {
            rootLayout.addView(emptyView);
            rootLayout.setEmptyView(emptyView);
        }

        if (errorView != null) {
            rootLayout.addView(errorView);
            rootLayout.setErrorView(errorView);
        }

        if (loadingView != null) {
            rootLayout.addView(loadingView);
            rootLayout.setLoadingView(loadingView);
        }

        if (resultView != null) {
            rootLayout.addView(resultView);
            rootLayout.setResultView(resultView);
        }

        return rootLayout;
    }
}
