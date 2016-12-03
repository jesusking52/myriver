package com.riverauction.riverauction.api.service.lesson.params;

import com.google.common.base.Joiner;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CStudentStatus;

import java.util.HashMap;
import java.util.List;

public class GetLessonsParams extends HashMap<String, String> {

    private GetLessonsParams(CGender gender,
                             List<Integer> subjects,
                             Integer moreThanPrice,
                             Integer lessThanPrice,
                             CStudentStatus studentStatus,
                             Integer grade,
                             Integer maxZipCode,
                             Integer minZipCode,
                             Integer nextToken) {
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

        if (studentStatus != null) {
            put("student_status", studentStatus.toString());
        }

        if (grade != null) {
            put("grade", String.valueOf(grade));
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
        private CGender gender;
        private List<Integer> subjects;
        private Integer moreThanPrice;
        private Integer lessThanPrice;
        private CStudentStatus studentStatus;
        private Integer grade;
        private Integer maxZipCode;
        private Integer minZipCode;
        private Integer nextToken;

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

        public Builder setStudentStatus(CStudentStatus studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public Builder setGrade(Integer grade) {
            this.grade = grade;
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

        public GetLessonsParams build() {
            return new GetLessonsParams(gender, subjects, moreThanPrice, lessThanPrice, studentStatus, grade, maxZipCode, minZipCode, nextToken);
        }
    }
}
