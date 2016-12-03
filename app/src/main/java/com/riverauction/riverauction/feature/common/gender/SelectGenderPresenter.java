package com.riverauction.riverauction.feature.common.gender;

import android.content.Context;

import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;

import javax.inject.Inject;

import rx.Subscription;

public class SelectGenderPresenter extends BasePresenter<SelectGenderMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectGenderPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectGenderMvpView mvpView, Context context) {
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
