package com.riverauction.riverauction.api.service.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CUser;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class IssueTokenResult {

    @JsonProperty("user")
    private CUser user;

    @JsonProperty("token")
    private String token;

    public CUser getUser() {
        return user;
    }

    public void setUser(CUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
