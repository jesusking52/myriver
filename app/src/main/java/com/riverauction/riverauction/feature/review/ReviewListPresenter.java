package com.riverauction.riverauction.feature.review;

import android.content.Context;

import com.riverauction.riverauction.api.model.CReview;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.review.params.GetReviewParams;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ReviewListPresenter extends BasePresenter<ReviewListMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    ReviewListPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(ReviewListMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getReviews(Integer userId, GetReviewParams params) {
        checkViewAttached();
        if (params == null) {
            return;
        }

        subscription = dataManager.getReviews(userId, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CUser>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CReview>> response) {
                        super.onNext(response);
                        getMvpView().successGetReviews(response);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetReviews(getErrorCause(e));
                    }
                });
    }
}
