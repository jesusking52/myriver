package com.riverauction.riverauction.api.service.lesson;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.lesson.params.GetLessonsParams;
import com.riverauction.riverauction.api.service.lesson.request.LessonBiddingRequest;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

public interface LessonService {

    @GET("/api/lessons")
    Observable<APISuccessResponse<List<CLesson>>> getLessons(@QueryMap GetLessonsParams params);

    @GET("/api/lessons/{lessonId}")
    Observable<APISuccessResponse<CLesson>> getLesson(@Path("lessonId") Integer lessonId);

    @POST("/api/lessons")
    Observable<APISuccessResponse<CLesson>> postLesson(@Body String emptyBody);

    @POST("/api/lessons/{lessonId}/cancel")
    Observable<APISuccessResponse<CLesson>> cancelLesson(@Path("lessonId") Integer lessonId, @Body String emptyBody);

    @GET("/api/lessons/{lessonId}/biddings")
    Observable<APISuccessResponse<List<CLessonBidding>>> getLessonBiddings(@Path("lessonId") Integer lessonId, @Query("next_token") Integer nextToken);

    @POST("/api/lessons/{lessonId}/biddings")
    Observable<APISuccessResponse<CLessonBidding>> postLessonBiddings(@Path("lessonId") Integer lessonId, @Body LessonBiddingRequest request);

    @POST("/api/lessons/{lessonId}/favorites")
    Observable<APISuccessResponse<CLessonFavorite>> postLessonFavorites(@Path("lessonId") Integer lessonId, @Body String emptyBody);

    @DELETE("/api/lessons/{lessonId}/favorites")
    Observable<APISuccessResponse<Void>> deleteLessonFavorites(@Path("lessonId") Integer lessonId);

    @POST("/api/lessons/{lessonId}/select/{teacherId}")
    Observable<APISuccessResponse<CUser>> postSelectTeacher(@Path("lessonId") Integer lessonId, @Path("teacherId") Integer teacherId, @Body String emptyBody);
}
