package com.riverauction.riverauction.api.service.lesson.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class LessonBiddingRequest {

    LessonBiddingRequest(Integer price) {
        this.price = price;
    }

    @JsonProperty("price")
    private Integer price;

    public static class Builder {
        private Integer price;

        public Builder setPrice(Integer price) {
            this.price = price;
            return this;
        }

        public LessonBiddingRequest build() {
            return new LessonBiddingRequest(price);
        }
    }
}
