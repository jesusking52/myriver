package com.riverauction.riverauction.api.service.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CStudentDepartmentType;
import com.riverauction.riverauction.api.model.CStudentStatus;
import com.riverauction.riverauction.api.model.CUserType;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class StudentBasicInformationRequest {

    public StudentBasicInformationRequest() {
    }

    public StudentBasicInformationRequest(CUserType type, String name, CGender gender, String phoneNumber,
                                          Integer birthYear, CLocation location, CStudentStatus studentStatus,
                                          Integer grade, CStudentDepartmentType department) {
        this.type = type;
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birthYear = birthYear;
        this.location = location;
        this.studentStatus = studentStatus;
        this.grade = grade;
        this.department = department;
    }

    @JsonProperty("type")
    private CUserType type;

    // 이름
    @JsonProperty("name")
    private String name;

    // 사진
    // TODO: 사진

    // 연락처
    @JsonProperty("phone_number")
    private String phoneNumber;

    // 성별
    @JsonProperty("gender")
    private CGender gender;

    // 나이
    @JsonProperty("birth_year")
    private Integer birthYear;

    // 주소
    @JsonProperty("location")
    private CLocation location;

    // 학교 (유치원/초/중/고/재수/일반인)
    @JsonProperty("student_status")
    private CStudentStatus studentStatus;

    // 학년 (유치원/초/중/고/재수/일반인)
    @JsonProperty("grade")
    private Integer grade;

    // 계열 (문과/이과/예체능/실업계/미정)
    @JsonProperty("department")
    private CStudentDepartmentType department;

    public static class Builder {
        private CUserType type;
        private String name;
        private CGender gender;
        private String phoneNumber;
        private Integer birthYear;
        private CLocation location;
        private CStudentStatus studentStatus;
        private Integer grade;
        private CStudentDepartmentType department;

        public Builder setType(CUserType type) {
            this.type = type;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setGender(CGender gender) {
            this.gender = gender;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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

        public Builder setStudentStatus(CStudentStatus studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public Builder setGrade(Integer grade) {
            this.grade = grade;
            return this;
        }

        public Builder setDepartment(CStudentDepartmentType department) {
            this.department = department;
            return this;
        }

        public StudentBasicInformationRequest build() {
            return new StudentBasicInformationRequest(type, name, gender, phoneNumber, birthYear, location, studentStatus, grade, department);
        }
    }
}
