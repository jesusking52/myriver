package com.riverauction.riverauction.feature.register.signup.teacher;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SignUpTeacherPresenter extends BasePresenter<SignUpTeacherMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SignUpTeacherPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SignUpTeacherMvpView mvpView, Context context) {
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
