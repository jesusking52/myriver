package com.riverauction.riverauction.feature.common.location;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectLocationPresenter extends BasePresenter<SelectLocationMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectLocationPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectLocationMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
