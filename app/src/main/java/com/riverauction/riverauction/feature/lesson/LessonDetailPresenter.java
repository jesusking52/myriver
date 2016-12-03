package com.riverauction.riverauction.feature.lesson;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class LessonDetailPresenter extends BasePresenter<LessonDetailMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    LessonDetailPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(LessonDetailMvpView mvpView, Context context) {
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

    public void postLessonFavorites(Integer lessonId) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.postLessonFavorites(lessonId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CLessonFavorite>() {

                    @Override
                    public void onNext(CLessonFavorite lessonFavorite) {
                        super.onNext(lessonFavorite);
                        getMvpView().successPostLessonFavorites(lessonFavorite);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostLessonFavorites(getErrorCause(e));
                    }
                }, context));
    }

    public void deleteLessonFavorites(Integer lessonId) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.deleteLessonFavorites(lessonId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<Void>() {

                    @Override
                    public void onNext(Void result) {
                        super.onNext(result);
                        getMvpView().successDeleteLessonFavorites();
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failDeleteLessonFavorites(getErrorCause(e));
                    }
                }, context));
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
