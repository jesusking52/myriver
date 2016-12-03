package com.riverauction.riverauction.feature.mylesson;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface MyLessonMvpView extends MvpView {
    // student
    void successGetActiveLessonAndBiddings(CLesson lesson, List<CLessonBidding> lessonBiddingList, Integer nextToken, int totalCount);
    void successGetLessonBiddings(List<CLessonBidding> lessonBiddingList, Integer nextToken);
    boolean failGetLessonBiddings(CErrorCause errorCause);
    void loadingGetActiveLessonList();
    // active
    void successGetActiveList(List<CLesson> lessons, Integer nextToken);
    boolean failGetActiveList(CErrorCause errorCause);
    void loadingGetActiveList();
    // history
    void successGetHistoryList(List<CLesson> lessons, Integer nextToken);
    boolean failGetHistoryList(CErrorCause errorCause);
    void loadingGetHistoryList();

    void successCancelLesson(CLesson lesson);
    boolean failCancelLesson(CErrorCause errorCause);
}
