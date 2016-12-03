package com.riverauction.riverauction.feature.teacher.filter;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class TeacherFilterPresenter extends BasePresenter<TeacherFilterMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    TeacherFilterPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(TeacherFilterMvpView mvpView, Context context) {
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
