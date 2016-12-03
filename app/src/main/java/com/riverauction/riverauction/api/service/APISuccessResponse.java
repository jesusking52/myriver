package com.riverauction.riverauction.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CError;
import com.riverauction.riverauction.api.model.CPaging;
import com.riverauction.riverauction.api.model.CResponseStatus;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

import static com.riverauction.riverauction.api.APIConstant.KEY_ERROR;
import static com.riverauction.riverauction.api.APIConstant.KEY_PAGING;
import static com.riverauction.riverauction.api.APIConstant.KEY_RESULT;
import static com.riverauction.riverauction.api.APIConstant.KEY_STATUS;

@Keep
@KeepClassMembers
public class APISuccessResponse<T> {

    @JsonProperty(KEY_STATUS)
    private CResponseStatus status;

    @JsonProperty(KEY_RESULT)
    private T result;

    @JsonProperty(KEY_PAGING)
    private CPaging paging;

    @JsonProperty(KEY_ERROR)
    private CError error;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public CPaging getPaging() {
        return paging;
    }

    public void setPaging(CPaging paging) {
        this.paging = paging;
    }

    public Integer getNextToken() {
        if (getPaging() != null) {
            return getPaging().getNextToken();
        }
        return null;
    }

    public int getTotalCount() {
        if (getPaging() != null) {
            if (getPaging().getTotalCount() != null) {
                return getPaging().getTotalCount();
            }
        }
        return 0;
    }

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
