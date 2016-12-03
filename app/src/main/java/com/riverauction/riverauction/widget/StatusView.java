package com.riverauction.riverauction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Empty, Error, Loading, Result 4가지 상태를 가지는 View.
 * {@link android.widget.ListView} or {@link android.support.v7.widget.RecyclerView} 이런데 로딩할 때 사용된다.
 */
public class StatusView extends FrameLayout {

    private View emptyView;
    private View errorView;
    private View loadingView;
    private View resultView;

    public StatusView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    public void hideAllViews() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
        if (resultView != null) {
            resultView.setVisibility(GONE);
        }
    }

    public void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(VISIBLE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
        if (resultView != null) {
            resultView.setVisibility(GONE);
        }
    }

    public void showErrorView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(VISIBLE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
        if (resultView != null) {
            resultView.setVisibility(GONE);
        }
    }

    public void showLoadingView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(VISIBLE);
        }
        if (resultView != null) {
            resultView.setVisibility(GONE);
        }
    }

    public void showResultView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
        if (resultView != null) {
            resultView.setVisibility(VISIBLE);
        }
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        emptyView.setVisibility(GONE);
    }

    public View getErrorView() {
        return errorView;
    }

    public void setErrorView(View errorView) {
        this.errorView = errorView;
        errorView.setVisibility(GONE);
    }

    private View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
        loadingView.setVisibility(GONE);
    }

    public View getResultView() {
        return resultView;
    }

    public void setResultView(View resultView) {
        this.resultView = resultView;
    }

    public void addEmptyView(View emptyView) {
        addView(emptyView);
        setEmptyView(emptyView);
    }

    public void addErrorView(View errorView) {
        addView(errorView);
        setErrorView(errorView);
    }

    public void addLoadingView(View loadingView) {
        addView(loadingView);
        setLoadingView(loadingView);
    }

    public void addResultView(View resultView) {
        addView(resultView);
        setResultView(resultView);
    }
}
