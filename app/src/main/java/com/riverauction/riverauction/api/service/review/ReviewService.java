package com.riverauction.riverauction.api.service.review;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.review.params.GetReviewParams;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

public interface ReviewService {

    @GET("/api/teachers/{teacherId}/reviews")
    Observable<APISuccessResponse<List<CUser>>> getReviews(@Path("teacherId") Integer teacherId, @QueryMap GetReviewParams params);
}
