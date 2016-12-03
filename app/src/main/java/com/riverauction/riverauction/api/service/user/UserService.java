package com.riverauction.riverauction.api.service.user;

import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CNotification;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.auth.request.TeacherReviewRequest;
import com.riverauction.riverauction.api.service.user.request.UserGCMAddRequest;
import com.riverauction.riverauction.api.service.user.request.UserPatchRequest;
import com.riverauction.riverauction.api.service.user.request.UserPreferencesRequest;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import rx.Observable;

public interface UserService {

    @GET("/api/users/{userId}")
    Observable<APISuccessResponse<CUser>> getUser(@Path("userId") Integer userId);

    @GET("/api/users/{userId}/profile")
    Observable<APISuccessResponse<CUser>> getUserProfile(@Path("userId") Integer userId, @Query("phone_number") Boolean phoneNumber);

    @GET("/api/users/{userId}/notifications")
    Observable<APISuccessResponse<List<CNotification>>> getNotifications(@Path("userId") Integer userId, @Query("next_token") Integer nextToken);

    @PATCH("/api/users/{userId}")
    Observable<APISuccessResponse<CUser>> patchUser(@Path("userId") Integer userId, @Body UserPatchRequest request);

    @GET("/api/users/{userId}/lessons/active")
    Observable<APISuccessResponse<List<CLesson>>> getActiveLessons(@Path("userId") Integer userId, @Query("next_token") Integer nextToken);

    @GET("/api/users/{userId}/lessons/history")
    Observable<APISuccessResponse<List<CLesson>>> getHistoryLessons(@Path("userId") Integer userId, @Query("next_token") Integer nextToken);

    @GET("/api/users/{userId}/favorites/users")
    Observable<APISuccessResponse<List<CUserFavorite>>> getUserFavorites(@Path("userId") Integer userId, @Query("next_token") Integer nextToken);

    @GET("/api/users/{userId}/favorites/lessons")
    Observable<APISuccessResponse<List<CLessonFavorite>>> getLessonsFavorites(@Path("userId") Integer userId, @Query("next_token") Integer nextToken);

    @POST("/api/users/{userId}/favorites")
    Observable<APISuccessResponse<CUserFavorite>> postUserFavorites(@Path("userId") Integer userId, @Body String emptyBody);

    @DELETE("/api/users/{userId}/favorites")
    Observable<APISuccessResponse<Void>> deleteUserFavorites(@Path("userId") Integer userId);

    @POST("/api/users/{userId}/gcms")
    Observable<APISuccessResponse<Boolean>> postGCM(@Path("userId") Integer userId, @Body UserGCMAddRequest request);

    @POST("/api/users/{userId}/check_phone_number")
    Observable<APISuccessResponse<CUser>> checkPhoneNumber(@Path("userId") Integer userId, @Body String emptyBody);

    @POST("/api/users/{userId}/preferences")
    Observable<APISuccessResponse<CUser>> postPreferences(@Path("userId") Integer userId, @Body UserPreferencesRequest request);

    @Multipart
    @POST("/api/users/{userId}/profile_photos")
    Observable<APISuccessResponse<CUser>> postProfilePhoto(@Path("userId") Integer userId, @Part("file") TypedFile file);

    @PATCH("/api/users/{userId}/review_write")
    Observable<APISuccessResponse<Boolean>> writeReview(@Path("userId") Integer userId, @Body TeacherReviewRequest request);

}
