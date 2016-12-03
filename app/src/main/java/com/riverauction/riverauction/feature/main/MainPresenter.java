package com.riverauction.riverauction.feature.main;

import android.content.Context;

import com.jhcompany.android.libs.injection.DaggerService;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.injection.component.ApplicationComponent;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;
import com.riverauction.riverauction.states.UserStates;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainPresenter extends BasePresenter<MainMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    MainPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MainMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getSubjectGroups() {
        checkViewAttached();

        subscription = dataManager.getSubjectGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<List<CSubjectGroup>>() {

                    @Override
                    public void onNext(List<CSubjectGroup> subjectGroups) {
                        super.onNext(subjectGroups);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        // 무시한다
                        return true;
                    }
                });
    }
}
