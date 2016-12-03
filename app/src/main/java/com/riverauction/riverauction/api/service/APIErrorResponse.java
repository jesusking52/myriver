package com.riverauction.riverauction.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CError;
import com.riverauction.riverauction.api.model.CResponseStatus;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

import static com.riverauction.riverauction.api.APIConstant.KEY_STATUS;
import static com.riverauction.riverauction.api.APIConstant.KEY_ERROR;

@Keep
@KeepClassMembers
public class APIErrorResponse {

    @JsonProperty(KEY_STATUS)
    private CResponseStatus status;

    @JsonProperty(KEY_ERROR)
    private CError error;

    public CResponseStatus getStatus() {
        return status;
    }

    public void setStatus(CResponseStatus status) {
        this.status = status;
    }

    public CError getError() {
        return error;
    }

    public void setError(CError error) {
        this.error = error;
    }
}
