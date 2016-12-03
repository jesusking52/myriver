package com.riverauction.riverauction.feature.register.signup.student;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SignUpStudentPresenter extends BasePresenter<SignUpStudentMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SignUpStudentPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SignUpStudentMvpView mvpView, Context context) {
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
