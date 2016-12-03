package com.riverauction.riverauction.api.service.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class UserGCMAddRequest {
    public UserGCMAddRequest(String key) {
        this.key = key;
    }

    @JsonProperty("key")
    private String key;

    public static class Builder {
        private String key;

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public UserGCMAddRequest build() {
            return new UserGCMAddRequest(key);
        }
    }
}
