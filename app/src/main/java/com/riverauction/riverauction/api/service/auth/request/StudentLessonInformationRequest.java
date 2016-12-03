package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CClassType;
import com.riverauction.riverauction.api.model.CDayOfWeekType;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CStudentLevel;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class StudentLessonInformationRequest {

    public StudentLessonInformationRequest() {
    }

    public StudentLessonInformationRequest(List<Integer> subjects, CStudentLevel level,
                                           List<CDayOfWeekType> daysOfWeek, Integer classAvailableCount,
                                           Integer classTime, CClassType classType, Integer preferredPrice,
                                           CGender preferredGender, String description) {
        this.subjects = subjects;
        this.level = level;
        this.daysOfWeek = daysOfWeek;
        this.classAvailableCount = classAvailableCount;
        this.classTime = classTime;
        this.classType = classType;
        this.preferredPrice = preferredPrice;
        this.preferredGender = preferredGender;
        this.description = description;
    }

    // 과목 (복수선택)
    @JsonProperty("subjects")
    private List<Integer> subjects;

    // 학업 수준
    @JsonProperty("level")
    private CStudentLevel level;

    // 수업 가능 요일 (복수선택)
    @JsonProperty("days_of_week")
    private List<CDayOfWeekType> daysOfWeek;

    // 수업 가능 횟수
    @JsonProperty("class_available_count")
    private Integer classAvailableCount;

    // 수업 시간
    @JsonProperty("class_time")
    private Integer classTime;

    // 과외 형태
    @JsonProperty("class_type")
    private CClassType classType;

    // 희망 금액
    @JsonProperty("preferred_price")
    private Integer preferredPrice;

    // 희망 성별
    @JsonProperty("preferred_gender")
    private CGender preferredGender;

    // 선생님에게 바라는점
    @JsonProperty("description")
    private String description;

    public static class Builder {
        private List<Integer> subjects;
        private CStudentLevel level;
        private List<CDayOfWeekType> daysOfWeek;
        private Integer classAvailableCount;
        private Integer classTime;
        private CClassType classType;
        private Integer preferredPrice;
        private CGender preferredGender;
        private String description;

        public Builder setSubjects(List<Integer> subjects) {
            this.subjects = subjects;
            return this;
        }

        public Builder setLevel(CStudentLevel level) {
            this.level = level;
            return this;
        }

        public Builder setDaysOfWeek(List<CDayOfWeekType> daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
            return this;
        }

        public Builder setClassAvailableCount(Integer classAvailableCount) {
            this.classAvailableCount = classAvailableCount;
            return this;
        }

        public Builder setClassTime(Integer classTime) {
            this.classTime = classTime;
            return this;
        }

        public Builder setClassType(CClassType classType) {
            this.classType = classType;
            return this;
        }

        public Builder setPreferredPrice(Integer preferredPrice) {
            this.preferredPrice = preferredPrice;
            return this;
        }

        public Builder setPreferredGender(CGender preferredGender) {
            this.preferredGender = preferredGender;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public StudentLessonInformationRequest build() {
            return new StudentLessonInformationRequest(subjects, level, daysOfWeek, classAvailableCount, classTime, classType, preferredPrice, preferredGender, description);
        }
    }
}
