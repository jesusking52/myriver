package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CertifyPhoneNumberRequest {

    public CertifyPhoneNumberRequest() {
    }

    public CertifyPhoneNumberRequest(String phoneNumber, Integer authNumber) {
        this.phoneNumber = phoneNumber;
        this.authNumber = authNumber;
    }

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("auth_number")
    private Integer authNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Integer getAuthNumber() {
        return authNumber;
    }

    public static class Builder {
        private String phoneNumber;
        private Integer authNumber;

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setAuthNumber(Integer authNumber) {
            this.authNumber = authNumber;
            return this;
        }

        public CertifyPhoneNumberRequest build() {
            return new CertifyPhoneNumberRequest(phoneNumber, authNumber);
        }
    }
}
