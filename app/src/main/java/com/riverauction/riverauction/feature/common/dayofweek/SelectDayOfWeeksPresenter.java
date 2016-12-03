package com.riverauction.riverauction.feature.common.dayofweek;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectDayOfWeeksPresenter extends BasePresenter<SelectDayOfWeeksMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectDayOfWeeksPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectDayOfWeeksMvpView mvpView, Context context) {
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
