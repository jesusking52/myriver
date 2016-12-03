package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CLessonBidding {

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("user")
    private CUser user;

    @JsonProperty("create_at")
    private Long createdAt;

    @JsonProperty("lesson")
    private CLesson lesson;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public CUser getUser() {
        return user;
    }

    public void setUser(CUser user) {
        this.user = user;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public CLesson getLesson() {
        return lesson;
    }

    public void setLesson(CLesson lesson) {
        this.lesson = lesson;
    }
}
