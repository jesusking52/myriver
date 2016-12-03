package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CStudent {

    @JsonProperty("student_status")
    private CStudentStatus studentStatus;

    @JsonProperty("grade")
    private Integer grade;

    @JsonProperty("department")
    private CStudentDepartmentType department;

    // 상 / 중 / 하
    @JsonProperty("level")
    private CStudentLevel level;

    @JsonProperty("class_available_count")
    private Integer classAvailableCount;

    @JsonProperty("class_time")
    private Integer classTime;

    @JsonProperty("class_type")
    private CClassType classType;

    @JsonProperty("preferred_gender")
    private CGender preferredGender;

    @JsonProperty("preferred_price")
    private Integer preferredPrice;

    @JsonProperty("available_subjects")
    private List<CSubject> availableSubjects;

    @JsonProperty("available_days_of_week")
    private List<CDayOfWeekType> availableDaysOfWeek;

    @JsonProperty("description")
    private String description;

    public CStudentStatus getStudentStatus() {
        return studentStatus;
    }

    public void setStudentStatus(CStudentStatus studentStatus) {
        this.studentStatus = studentStatus;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public CStudentDepartmentType getDepartment() {
        return department;
    }

    public void setDepartment(CStudentDepartmentType department) {
        this.department = department;
    }

    public CStudentLevel getLevel() {
        return level;
    }

    public void setLevel(CStudentLevel level) {
        this.level = level;
    }

    public Integer getClassAvailableCount() {
        return classAvailableCount;
    }

    public void setClassAvailableCount(Integer classAvailableCount) {
        this.classAvailableCount = classAvailableCount;
    }

    public Integer getClassTime() {
        return classTime;
    }

    public void setClassTime(Integer classTime) {
        this.classTime = classTime;
    }

    public CClassType getClassType() {
        return classType;
    }

    public void setClassType(CClassType classType) {
        this.classType = classType;
    }

    public CGender getPreferredGender() {
        return preferredGender;
    }

    public void setPreferredGender(CGender preferredGender) {
        this.preferredGender = preferredGender;
    }

    public Integer getPreferredPrice() {
        return preferredPrice;
    }

    public void setPreferredPrice(Integer preferredPrice) {
        this.preferredPrice = preferredPrice;
    }

    public List<CSubject> getAvailableSubjects() {
        return availableSubjects;
    }

    public void setAvailableSubjects(List<CSubject> availableSubjects) {
        this.availableSubjects = availableSubjects;
    }

    public List<CDayOfWeekType> getAvailableDaysOfWeek() {
        return availableDaysOfWeek;
    }

    public void setAvailableDaysOfWeek(List<CDayOfWeekType> availableDaysOfWeek) {
        this.availableDaysOfWeek = availableDaysOfWeek;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
