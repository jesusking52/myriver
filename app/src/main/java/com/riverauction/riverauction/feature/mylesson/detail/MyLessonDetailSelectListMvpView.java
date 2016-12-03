package com.riverauction.riverauction.feature.mylesson.detail;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface MyLessonDetailSelectListMvpView extends MvpView {
    void successGetLessonBiddings(List<CLessonBidding> lessonBiddingList, Integer nextToken);
    boolean failGetLessonBiddings(CErrorCause errorCause);

    void successPostSelectTeacher(CUser user);
    boolean failPostSelectTeacher(CErrorCause errorCause);
}
