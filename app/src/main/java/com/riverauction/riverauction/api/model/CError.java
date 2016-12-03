package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CError {

    @JsonProperty("cause")
    private CErrorCause cause;

    @JsonProperty("message")
    private String message;

    public CErrorCause getCause() {
        return cause;
    }

    public void setCause(CErrorCause cause) {
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
