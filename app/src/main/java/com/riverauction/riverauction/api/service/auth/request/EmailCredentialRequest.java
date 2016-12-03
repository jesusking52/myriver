package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class EmailCredentialRequest {

    public EmailCredentialRequest() {
    }

    public EmailCredentialRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public static class Builder {
        private String email;
        private String password;

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public EmailCredentialRequest build() {
            return new EmailCredentialRequest(email, password);
        }
    }
}
