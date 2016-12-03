package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CNotification {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("deep_link")
    private String deepLink;

    @JsonProperty("skeleton")
    private String skeleton;

    @JsonProperty("words")
    private Map<String, String> words;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("type")
    private CNotificationType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(String skeleton) {
        this.skeleton = skeleton;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public CNotificationType getType() {
        return type;
    }

    public void setType(CNotificationType type) {
        this.type = type;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public void setDeepLink(String deepLink) {
        this.deepLink = deepLink;
    }

    public Map<String, String> getWords() {
        return words;
    }

    public void setWords(Map<String, String> words) {
        this.words = words;
    }
}
