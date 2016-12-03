package com.riverauction.riverauction.feature.lesson.filter;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class StudentFilterPresenter extends BasePresenter<StudentFilterMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    StudentFilterPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(StudentFilterMvpView mvpView, Context context) {
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
