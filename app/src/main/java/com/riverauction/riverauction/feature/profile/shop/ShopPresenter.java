package com.riverauction.riverauction.feature.profile.shop;

import android.content.Context;

import com.riverauction.riverauction.api.model.CReceipt;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ShopPresenter extends BasePresenter<ShopMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    ShopPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(ShopMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getUser(Integer userId) {
        checkViewAttached();

        subscription = dataManager.getUser(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successGetUser(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetUser(getErrorCause(e));
                    }
                }, context));
    }

    public void purchaseCoin(CReceipt receipt) {
        checkViewAttached();

        subscription = dataManager.purchaseCoin(receipt)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<Boolean>() {

                    @Override
                    public void onNext(Boolean result) {
                        super.onNext(result);
                        if (getMvpView() != null) {
                            getMvpView().successPurchaseCoin(result);
                        }
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPurchaseCoin(getErrorCause(e));
                    }
                }, context));
    }
}
