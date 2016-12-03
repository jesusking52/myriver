package com.riverauction.riverauction.feature.common.subject;

import android.content.Context;

import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SelectSubjectsPresenter extends BasePresenter<SelectSubjectsMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    SelectSubjectsPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(SelectSubjectsMvpView mvpView, Context context) {
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
                .subscribe(new DialogSubscriber<>(new APISubscriber<List<CSubjectGroup>>() {

                    @Override
                    public void onNext(List<CSubjectGroup> subjectGroups) {
                        super.onNext(subjectGroups);
                        getMvpView().successSubjectGroups(subjectGroups);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failSubjectGroups(getErrorCause(e));
                    }
                }, context));
    }
}