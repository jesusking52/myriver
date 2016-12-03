package com.riverauction.riverauction.api.service.teacher.params;

import com.google.common.base.Joiner;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.api.model.CGender;

import java.util.HashMap;
import java.util.List;

public class GetTeachersParams extends HashMap<String, String> {

    private GetTeachersParams(List<String> universities,
                              CGender gender,
                              List<Integer> subjects,
                              Integer moreThanPrice,
                              Integer lessThanPrice,
                              Integer maxZipCode,
                              Integer minZipCode,
                              Integer nextToken) {
        if (!Lists2.isNullOrEmpty(universities)) {
            put("universities", Joiner.on(",").join(universities));
        }

        if (gender != null) {
            put("gender", gender.toString());
        }

        if (subjects != null) {
            put("subjects", Joiner.on(",").join(subjects));
        }

        if (moreThanPrice != null) {
            put("more_than_price", String.valueOf(moreThanPrice));
        }

        if (lessThanPrice != null) {
            put("less_than_price", String.valueOf(lessThanPrice));
        }

        if (maxZipCode != null) {
            put("max_zip_code", String.valueOf(maxZipCode));
        }

        if (minZipCode != null) {
            put("min_zip_code", String.valueOf(minZipCode));
        }

        if (nextToken != null) {
            put("next_token", String.valueOf(nextToken));
        }
    }

    public static class Builder {
        private List<String> universities;
        private CGender gender;
        private List<Integer> subjects;
        private Integer moreThanPrice;
        private Integer lessThanPrice;
        private Integer maxZipCode;
        private Integer minZipCode;
        private Integer nextToken;

        public Builder setUniversities(List<String> universities) {
            this.universities = universities;
            return this;
        }

        public Builder setGender(CGender gender) {
            this.gender = gender;
            return this;
        }

        public Builder setSubjects(List<Integer> subjects) {
            this.subjects = subjects;
            return this;
        }

        public Builder setMoreThanPrice(Integer moreThanPrice) {
            this.moreThanPrice = moreThanPrice;
            return this;
        }

        public Builder setLessThanPrice(Integer lessThanPrice) {
            this.lessThanPrice = lessThanPrice;
            return this;
        }

        public Builder setMaxZipCode(Integer maxZipCode) {
            this.maxZipCode = maxZipCode;
            return this;
        }

        public Builder setMinZipCode(Integer minZipCode) {
            this.minZipCode = minZipCode;
            return this;
        }

        public Builder setNextToken(Integer nextToken) {
            this.nextToken = nextToken;
            return this;
        }

        public GetTeachersParams build() {
            return new GetTeachersParams(universities, gender, subjects, moreThanPrice, lessThanPrice, maxZipCode, minZipCode, nextToken);
        }
    }
}
