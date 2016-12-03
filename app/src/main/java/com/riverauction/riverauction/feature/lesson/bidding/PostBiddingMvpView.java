package com.riverauction.riverauction.feature.lesson.bidding;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.base.MvpView;

public interface PostBiddingMvpView extends MvpView {
    void successPostLessonBiddings(CLessonBidding lessonBidding);
    boolean failPostLessonBiddings(CErrorCause errorCause);
}
