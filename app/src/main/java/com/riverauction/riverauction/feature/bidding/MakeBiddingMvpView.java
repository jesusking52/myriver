package com.riverauction.riverauction.feature.bidding;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.base.MvpView;

public interface MakeBiddingMvpView extends MvpView {
    void successPostLesson(CLesson lesson);
    boolean failPostLesson(CErrorCause errorCause);
}
