package com.riverauction.riverauction.feature.review;

import android.content.Context;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.auth.request.TeacherReviewRequest;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ReviewWritePresenter extends BasePresenter<ReviewWriteMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    ReviewWritePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(ReviewWriteMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void writeReview(Integer userId, TeacherReviewRequest request) {
        checkViewAttached();
        if (userId == null) {
            return;
        }
        /*
        subscription = dataManager.writeReview(userId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successPatchUser(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPatchUser(getErrorCause(e));
                    }
                }, context));
                */
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

}
