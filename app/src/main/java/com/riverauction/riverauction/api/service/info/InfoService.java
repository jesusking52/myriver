package com.riverauction.riverauction.api.service.info;

import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.api.service.APISuccessResponse;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

public interface InfoService {

    @GET("/api/info/subjects")
    Observable<APISuccessResponse<List<CSubjectGroup>>> getSubjectGroups();
}
