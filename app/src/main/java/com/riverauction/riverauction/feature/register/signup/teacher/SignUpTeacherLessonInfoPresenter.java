package com.riverauction.riverauction.feature.register.signup.teacher;

import android.content.Context;

import com.riverauction.riverauction.api.service.auth.request.SignUpRequest;
import com.riverauction.riverauction.api.service.auth.response.SignUpResult;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SignUpTeacherLessonInfoPresenter extends BasePresenter<SignUpTeacherLessonInfoMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SignUpTeacherLessonInfoPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SignUpTeacherLessonInfoMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void signUp(SignUpRequest request) {
        checkViewAttached();
        if (request == null) {
            return;
        }

        subscription = dataManager.signUp(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<SignUpResult>() {

                    @Override
                    public void onNext(SignUpResult accessTokenResult) {
                        super.onNext(accessTokenResult);
                        getMvpView().successSignUp(accessTokenResult);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failSignUp(getErrorCause(e));
                    }
                });
    }
}
