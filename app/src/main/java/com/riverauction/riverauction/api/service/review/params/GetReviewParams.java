package com.riverauction.riverauction.api.service.review.params;

import java.util.HashMap;

public class GetReviewParams extends HashMap<String, String> {

    private GetReviewParams(
                              Integer rank,
                              String review,
                              String username,
                              String insdate,
                              Integer nextToken
                              ) {

        if ( rank != null){
            put("rank", String.valueOf(rank));
        }

        if ( review != null){
            put("review", String.valueOf(review));
        }

        if ( username != null){
            put("username", String.valueOf(username));
        }

        if ( insdate != null){
            put("insdate", String.valueOf(insdate));
        }

        if (nextToken != null) {
            put("next_token", String.valueOf(nextToken));
        }
    }

    public static class Builder {

        private Integer rank;
        private String username;
        private String review;
        private String insdate;
        private Integer nextToken;

        public Builder setRank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setReview(String review) {
            this.review = review;
            return this;
        }

        public Builder setNextToken(Integer nextToken) {
            this.nextToken = nextToken;
            return this;
        }

        public Builder setInsdate(String insdate) {
            this.insdate = insdate;
            return this;
        }

        public GetReviewParams build() {
            return new GetReviewParams(rank, review, username, insdate, nextToken);
        }
    }
}
