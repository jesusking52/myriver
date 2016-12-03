package com.riverauction.riverauction.feature.mylesson.detail;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface MyLessonDetailMvpView extends MvpView{
    void successGetLesson(CLesson lesson);
    boolean failGetLesson(CErrorCause errorCause);
    void successGetLessonBiddings(List<CLessonBidding> lessonBiddingList, Integer nextToken);
    boolean failGetLessonBiddings(CErrorCause errorCause);
}
