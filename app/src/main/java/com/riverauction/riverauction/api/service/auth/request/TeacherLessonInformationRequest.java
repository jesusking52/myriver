package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CDayOfWeekType;
import com.riverauction.riverauction.api.model.CGender;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class TeacherLessonInformationRequest {

    public TeacherLessonInformationRequest() {
    }

    public TeacherLessonInformationRequest(List<Integer> subjects, Integer career, List<CDayOfWeekType> daysOfWeek,
                                           Integer classAvailableCount, Integer classTime, CGender preferredGender,
                                           Integer preferredPrice, String description) {
        this.subjects = subjects;
        this.career = career;
        this.daysOfWeek = daysOfWeek;
        this.classAvailableCount = classAvailableCount;
        this.classTime = classTime;
        this.preferredGender = preferredGender;
        this.preferredPrice = preferredPrice;
        this.description = description;
    }

    // 가능 과목
    @JsonProperty("subjects")
    private List<Integer> subjects;

    // 과외 경력
    @JsonProperty("career")
    private Integer career;

    // 수업 가능 요일
    @JsonProperty("days_of_week")
    private List<CDayOfWeekType> daysOfWeek;

    // 수업 가능 횟수
    @JsonProperty("class_available_count")
    private Integer classAvailableCount;

    // 수업 시간
    @JsonProperty("class_time")
    private Integer classTime;

    // 희망 성별
    @JsonProperty("preferred_gender")
    private CGender preferredGender;

    // 희망 금액
    @JsonProperty("preferred_price")
    private Integer preferredPrice;

    // 경력 소개, 수업 방식
    @JsonProperty("description")
    private String description;

    public static class Builder {
        private List<Integer> subjects;
        private Integer career;
        private List<CDayOfWeekType> daysOfWeek;
        private Integer classAvailableCount;
        private Integer classTime;
        private CGender preferredGender;
        private Integer preferredPrice;
        private String description;

        public Builder setSubjects(List<Integer> subjects) {
            this.subjects = subjects;
            return this;
        }

        public Builder setCareer(Integer career) {
            this.career = career;
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

        public Builder setPreferredGender(CGender preferredGender) {
            this.preferredGender = preferredGender;
            return this;
        }

        public Builder setPreferredPrice(Integer preferredPrice) {
            this.preferredPrice = preferredPrice;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public TeacherLessonInformationRequest build() {
            return new TeacherLessonInformationRequest(subjects, career, daysOfWeek, classAvailableCount, classTime, preferredGender, preferredPrice, description);
        }
    }
}
