package com.riverauction.riverauction.feature.register;

import android.content.Context;

import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.response.IssueTokenResult;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class IntroPresenter extends BasePresenter<IntroMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    IntroPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(IntroMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void issueToken(EmailCredentialRequest request) {
        checkViewAttached();
        if (request == null) {
            return;
        }

        subscription = dataManager.issuseToken(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<IssueTokenResult>() {

                    @Override
                    public void onNext(IssueTokenResult tokenResult) {
                        super.onNext(tokenResult);
                        getMvpView().successIssueToken(tokenResult);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failIssueToken(getErrorCause(e));
                    }
                }, context));
    }
}
