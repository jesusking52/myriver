package com.riverauction.riverauction.api.service.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class UserPreferencesRequest {

    public UserPreferencesRequest(Boolean hideOnSearching) {
        this.hideOnSearching = hideOnSearching;
    }

    @JsonProperty("hide_on_searching")
    private Boolean hideOnSearching;

    public static class Builder {
        private Boolean hideOnSearching;

        public Builder setHideOnSearching(Boolean hideOnSearching) {
            this.hideOnSearching = hideOnSearching;
            return this;
        }

        public UserPreferencesRequest build() {
            return new UserPreferencesRequest(hideOnSearching);
        }
    }
}
