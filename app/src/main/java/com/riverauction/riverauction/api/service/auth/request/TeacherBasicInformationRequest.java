package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CUniversityStatus;
import com.riverauction.riverauction.api.model.CUserType;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class TeacherBasicInformationRequest {

    public TeacherBasicInformationRequest() {
    }

    public TeacherBasicInformationRequest(CUserType type, String name, String phoneNumber,
                                          CGender gender, Integer birthYear,
                                          CLocation location, String university, Integer universityRank,
                                          String major, CUniversityStatus universityStatus) {
        this.type = type;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthYear = birthYear;
        this.location = location;
        this.university = university;
        this.universityRank = universityRank;
        this.major = major;
        this.universityStatus = universityStatus;
    }

    @JsonProperty("type")
    private CUserType type;

    // 이름
    @JsonProperty("name")
    private String name;

    // 사진

    // 연락처
    @JsonProperty("phone_number")
    private String phoneNumber;

    // 성별
    @JsonProperty("gender")
    private CGender gender;

    // 나이
    @JsonProperty("birth_year")
    private Integer birthYear;

    @JsonProperty("location")
    private CLocation location;

    // 대학교
    @JsonProperty("university")
    private String university;

    @JsonProperty("university_rank")
    private Integer universityRank;

    // 학과
    @JsonProperty("major")
    private String major;

    // 학년 (재학/휴학/졸업)
    @JsonProperty("university_status")
    private CUniversityStatus universityStatus;

    public static class Builder {
        private CUserType type;
        private String name;
        private String phoneNumber;
        private CGender gender;
        private Integer birthYear;
        private CLocation location;
        private String university;
        private Integer universityRank;
        private String major;
        private CUniversityStatus universityStatus;

        public Builder setType(CUserType type) {
            this.type = type;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setGender(CGender gender) {
            this.gender = gender;
            return this;
        }

        public Builder setBirthYear(Integer birthYear) {
            this.birthYear = birthYear;
            return this;
        }

        public Builder setLocation(CLocation location) {
            this.location = location;
            return this;
        }

        public Builder setUniversity(String university) {
            this.university = university;
            return this;
        }

        public Builder setUniversityRank(Integer universityRank) {
            this.universityRank = universityRank;
            return this;
        }

        public Builder setMajor(String major) {
            this.major = major;
            return this;
        }

        public Builder setUniversityStatus(CUniversityStatus universityStatus) {
            this.universityStatus = universityStatus;
            return this;
        }

        public TeacherBasicInformationRequest build() {
            return new TeacherBasicInformationRequest(type, name, phoneNumber, gender, birthYear, location, university, universityRank, major, universityStatus);
        }
    }
}
