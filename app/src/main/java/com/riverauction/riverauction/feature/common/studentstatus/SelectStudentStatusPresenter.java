package com.riverauction.riverauction.feature.common.studentstatus;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectStudentStatusPresenter extends BasePresenter<SelectStudentStatusMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectStudentStatusPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectStudentStatusMvpView mvpView, Context context) {
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