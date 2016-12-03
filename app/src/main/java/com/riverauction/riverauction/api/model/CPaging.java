package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CPaging {

    @JsonProperty("next_token")
    private Integer nextToken;

    @JsonProperty("total_count")
    private Integer totalCount;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNextToken() {
        return nextToken;
    }

    public void setNextToken(Integer nextToken) {
        this.nextToken = nextToken;
    }
}
