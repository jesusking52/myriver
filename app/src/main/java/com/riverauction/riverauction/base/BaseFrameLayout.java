package com.riverauction.riverauction.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.injection.component.ActivityComponent;

import butterknife.ButterKnife;

public abstract class BaseFrameLayout extends FrameLayout {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    protected View rootView;
    protected StateCtx stateCtx;
    private ActivityComponent activityComponent;

    public BaseFrameLayout(Context context) {
        super(context);
        initialize(context);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public BaseFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    protected void initialize(Context context) {
        LOGGER.debug("initialize()");
        rootView = inflate(context, getLayoutResId(), this);
        ButterKnife.bind(this);

        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LOGGER.debug("onAttachedToWindow()");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LOGGER.debug("onDetachedFromWindow()");
    }

    public abstract int getLayoutResId();
}
