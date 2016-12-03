package com.riverauction.riverauction.feature.register.signup.phone;

import android.content.Context;

import com.riverauction.riverauction.api.service.auth.request.CertifyPhoneNumberRequest;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class EnterPhoneNumberPresenter extends BasePresenter<EnterPhoneNumberMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    EnterPhoneNumberPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(EnterPhoneNumberMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void requestAuthNumber(CertifyPhoneNumberRequest request) {
        checkViewAttached();
        if (request == null) {
            return;
        }

        subscription = dataManager.requestAuthNumber(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<Boolean>() {

                    @Override
                    public void onNext(Boolean result) {
                        super.onNext(result);
                        getMvpView().successRequestAuthNumber(request.getPhoneNumber());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failRequestAuthNumber(getErrorCause(e));
                    }
                }, context));
    }
}
