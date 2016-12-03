package com.riverauction.riverauction.feature.common.university;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectUniversityPresenter extends BasePresenter<SelectUniversityMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectUniversityPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectUniversityMvpView mvpView, Context context) {
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