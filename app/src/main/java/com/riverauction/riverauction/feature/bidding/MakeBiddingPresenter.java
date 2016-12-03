package com.riverauction.riverauction.feature.bidding;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MakeBiddingPresenter extends BasePresenter<MakeBiddingMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    MakeBiddingPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MakeBiddingMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void postLesson() {
        checkViewAttached();

        subscription = dataManager.postLesson()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CLesson>() {

                    @Override
                    public void onNext(CLesson lesson) {
                        super.onNext(lesson);
                        getMvpView().successPostLesson(lesson);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostLesson(getErrorCause(e));
                    }
                }, context));
    }
}
