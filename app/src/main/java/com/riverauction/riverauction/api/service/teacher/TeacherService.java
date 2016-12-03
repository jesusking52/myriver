package com.riverauction.riverauction.api.service.teacher;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

public interface TeacherService {

    @GET("/api/teachers/{userId}")
    Observable<APISuccessResponse<List<CUser>>> getTeachers(@Path("userId") Integer userId, @QueryMap GetTeachersParams params);
}
