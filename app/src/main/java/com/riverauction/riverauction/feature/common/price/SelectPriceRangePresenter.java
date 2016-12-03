package com.riverauction.riverauction.feature.common.price;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectPriceRangePresenter extends BasePresenter<SelectPriceRangeMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectPriceRangePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectPriceRangeMvpView mvpView, Context context) {
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
