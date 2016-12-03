package com.riverauction.riverauction.feature.mylesson.detail;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MyLessonDetailPresenter extends BasePresenter<MyLessonDetailMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    MyLessonDetailPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MyLessonDetailMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getLesson(Integer lessonId) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.getLesson(lessonId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<CLesson>() {

                    @Override
                    public void onNext(CLesson lesson) {
                        super.onNext(lesson);
                        getMvpView().successGetLesson(lesson);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLesson(getErrorCause(e));
                    }
                });
    }

    public void getLessonBiddings(Integer lessonId, Integer nextToken) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.getLessonBiddings(lessonId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<APISuccessResponse<List<CLessonBidding>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CLessonBidding>> response) {
                        super.onNext(response);
                        getMvpView().successGetLessonBiddings(response.getResult(), response.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLessonBiddings(getErrorCause(e));
                    }
                }, context));
    }
}
