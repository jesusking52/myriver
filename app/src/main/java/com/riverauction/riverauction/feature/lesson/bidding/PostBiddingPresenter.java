package com.riverauction.riverauction.feature.lesson.bidding;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.service.lesson.request.LessonBiddingRequest;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.feature.lesson.LessonDetailMvpView;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class PostBiddingPresenter extends BasePresenter<PostBiddingMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    PostBiddingPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(PostBiddingMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void postLessonBiddings(Integer lessonId, LessonBiddingRequest request) {
        checkViewAttached();
        if (lessonId == null) {
            return;
        }

        subscription = dataManager.postLessonBiddings(lessonId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CLessonBidding>() {

                    @Override
                    public void onNext(CLessonBidding bidding) {
                        super.onNext(bidding);
                        getMvpView().successPostLessonBiddings(bidding);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostLessonBiddings(getErrorCause(e));
                    }
                }, context));
    }
}
