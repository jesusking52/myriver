package com.riverauction.riverauction.feature.review;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CReview;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface ReviewListMvpView extends MvpView {
    void successGetReviews(APISuccessResponse<List<CReview>> response);
    boolean failGetReviews(CErrorCause errorCause);
}
