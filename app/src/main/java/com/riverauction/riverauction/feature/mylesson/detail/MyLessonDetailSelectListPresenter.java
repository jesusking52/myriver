package com.riverauction.riverauction.feature.mylesson.detail;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MyLessonDetailSelectListPresenter extends BasePresenter<MyLessonDetailSelectListMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    MyLessonDetailSelectListPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MyLessonDetailSelectListMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getLessonBiddings(Integer lessonId, Integer nextToken) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.getLessonBiddings(lessonId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLessonBidding>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CLessonBidding>> response) {
                        super.onNext(response);
                        getMvpView().successGetLessonBiddings(response.getResult(), response.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLessonBiddings(getErrorCause(e));
                    }
                });
    }

    public void postSelectTeacher(Integer lessonId, Integer teacherId) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.postSelectTeacher(lessonId, teacherId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successPostSelectTeacher(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostSelectTeacher(getErrorCause(e));
                    }
                }, context));
    }
}
