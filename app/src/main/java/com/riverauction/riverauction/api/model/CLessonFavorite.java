package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CLessonFavorite {

    @JsonProperty("user")
    private CUser user;

    @JsonProperty("lesson")
    private CLesson lesson;

    @JsonProperty("created_at")
    private Long createdAt;

    public CUser getUser() {
        return user;
    }

    public void setUser(CUser user) {
        this.user = user;
    }

    public CLesson getLesson() {
        return lesson;
    }

    public void setLesson(CLesson lesson) {
        this.lesson = lesson;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
