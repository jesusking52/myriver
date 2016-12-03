package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class TeacherReviewRequest {

    public TeacherReviewRequest() {
    }

    public TeacherReviewRequest(String rank, String review, String teacherid) {
        this.rank = rank;
        this.review = review;
        this.teacherid = teacherid;
    }

    @JsonProperty("rank")
    private String rank;

    @JsonProperty("review")
    private String review;

    @JsonProperty("teacherid")
    private String teacherid;

    public static class Builder {
        private String rank;
        private String review;
        private String teacherid;

        public Builder setRank(String rank) {
            this.rank = rank;
            return this;
        }

        public Builder setReview(String review) {
            this.review = review;
            return this;
        }
        public Builder setTeacherid(String teacherid) {
            this.teacherid = teacherid;
            return this;
        }

        public TeacherReviewRequest build() {
            return new TeacherReviewRequest(rank, review, teacherid);
        }
    }
}
