package com.riverauction.riverauction.feature.teacher;

import android.content.Context;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class TeacherDetailPresenter extends BasePresenter<TeacherDetailMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    TeacherDetailPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(TeacherDetailMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getUserProfile(Integer userId, Boolean phoneNumber) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.getUserProfile(userId, phoneNumber)
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

    public void postUserFavorites(Integer userId) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.postUserFavorites(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUserFavorite>() {

                    @Override
                    public void onNext(CUserFavorite favorite) {
                        super.onNext(favorite);
                        getMvpView().successPostUserFavorites(favorite);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostUserFavorites(getErrorCause(e));
                    }
                }, context));
    }

    public void deleteUserFavorites(Integer userId) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.deleteUserFavorites(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<Void>() {

                    @Override
                    public void onNext(Void result) {
                        super.onNext(result);
                        getMvpView().successDeleteUserFavorites();
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failDeleteUserFavorites(getErrorCause(e));
                    }
                }, context));
    }

    public void checkPhoneNumber(Integer userId) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.checkPhoneNumber(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser result) {
                        super.onNext(result);
                        getMvpView().successCheckPhoneNumber(result);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failCheckPhoneNumber(getErrorCause(e));
                    }
                }, context));
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
