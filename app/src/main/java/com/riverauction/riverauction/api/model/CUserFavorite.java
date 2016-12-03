package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CUserFavorite {

    @JsonProperty("from_user")
    private CUser fromUser;

    @JsonProperty("to_user")
    private CUser toUser;

    @JsonProperty("created_at")
    private Long createdAt;

    public CUser getFromUser() {
        return fromUser;
    }

    public void setFromUser(CUser fromUser) {
        this.fromUser = fromUser;
    }

    public CUser getToUser() {
        return toUser;
    }

    public void setToUser(CUser toUser) {
        this.toUser = toUser;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
