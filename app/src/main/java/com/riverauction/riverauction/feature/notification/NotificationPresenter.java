package com.riverauction.riverauction.feature.notification;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CNotification;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class NotificationPresenter extends BasePresenter<NotificationMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    NotificationPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(NotificationMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getNotifications(Integer userId, Integer nextToken) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.getNotifications(userId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CNotification>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CNotification>> response) {
                        super.onNext(response);
                        getMvpView().successGetNotifications(response.getResult(), response.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetNotifications(getErrorCause(e));
                    }
                });
    }

    /**
     * 학생의 찜리스트를 가져온다
     */
    public void getUserFavorites(Integer userId, Integer nextToken) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.getUserFavorites(userId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CUserFavorite>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CUserFavorite>> response) {
                        super.onNext(response);
                        getMvpView().successGetUserFavorites(response.getResult(), response.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetUserFavorites(getErrorCause(e));
                    }
                });
    }

    /**
     * 선생님의 찜리스트를 가져온다
     */
    public void getLessonFavorites(Integer userId, Integer nextToken) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.getLessonFavorites(userId, nextToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLessonFavorite>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CLessonFavorite>> response) {
                        super.onNext(response);
                        getMvpView().successGetLessonFavorites(response.getResult(), response.getNextToken());
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLessonFavorites(getErrorCause(e));
                    }
                });
    }
}
