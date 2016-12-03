package com.riverauction.riverauction.feature.mylesson;

import android.content.Context;

import com.jhcompany.android.libs.utils.Lists2;
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

public class MyLessonPresenter extends BasePresenter<MyLessonMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    MyLessonPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(MyLessonMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getActiveLessonAndLessonBiddings(Integer userId) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        checkViewAttached();

        dataManager.getActiveLessons(userId, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLesson>>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        getMvpView().loadingGetActiveLessonList();
                    }

                    @Override
                    public void onNext(APISuccessResponse<List<CLesson>> result) {
                        super.onNext(result);
                        List<CLesson> lessons = result.getResult();
                        if (Lists2.isNullOrEmpty(lessons)) {
                            getMvpView().successGetActiveLessonAndBiddings(null, null, null, 0);
                        } else {
                            CLesson lesson = lessons.get(0);
                            dataManager.getLessonBiddings(lesson.getId(), null)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new APISubscriber<APISuccessResponse<List<CLessonBidding>>>() {

                                        @Override
                                        public void onNext(APISuccessResponse<List<CLessonBidding>> response) {
                                            super.onNext(response);
                                            getMvpView().successGetActiveLessonAndBiddings(lesson, response.getResult(), response.getNextToken(), response.getTotalCount());
                                        }

                                        @Override
                                        public boolean onErrors(Throwable e) {
                                            return getMvpView().failGetLessonBiddings(getErrorCause(e));
                                        }
                                    });
                        }
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLessonBiddings(getErrorCause(e));
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

    public void getActiveLessons(Integer userId, Integer nextToken) {
        if (userId == null) {
            return;
        }
        checkViewAttached();

        subscription = dataManager.getActiveLessons(userId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLesson>>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        getMvpView().loadingGetActiveList();
                    }

                    @Override
                    public void onNext(APISuccessResponse<List<CLesson>> result) {
                        super.onNext(result);
                        getMvpView().successGetActiveList(result.getResult(), result.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetActiveList(getErrorCause(e));
                    }
                });
    }

    public void getHistoryLessons(Integer userId, Integer nextToken) {
        if (userId == null) {
            return;
        }
        checkViewAttached();

        subscription = dataManager.getHistoryLessons(userId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLesson>>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        getMvpView().loadingGetHistoryList();
                    }

                    @Override
                    public void onNext(APISuccessResponse<List<CLesson>> result) {
                        super.onNext(result);
                        getMvpView().successGetHistoryList(result.getResult(), result.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetHistoryList(getErrorCause(e));
                    }
                });
    }

    public void cancelLesson(Integer lessonId) {
        checkViewAttached();

        subscription = dataManager.cancelLesson(lessonId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<CLesson>() {

                    @Override
                    public void onNext(CLesson lesson) {
                        super.onNext(lesson);
                        getMvpView().successCancelLesson(lesson);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failCancelLesson(getErrorCause(e));
                    }
                });
    }
}
