package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CTeacher {

    @JsonProperty("university")
    private String university;

    @JsonProperty("university_rank")
    private Integer universityRank;

    // 재학 휴학 졸업
    @JsonProperty("university_status")
    private CUniversityStatus universityStatus;

    @JsonProperty("major")
    private String major;

    @JsonProperty("career")
    private Integer career;

    // 횟수
    @JsonProperty("class_available_count")
    private Integer classAvailableCount;

    // 시간
    @JsonProperty("class_time")
    private Integer classTime;

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

    @JsonProperty("hide_on_searching")
    private Boolean hideOnSearching;
    //by csh 내가 추가
    @JsonProperty("rank")
    private String rank;

    @JsonProperty("rankcount")
    private String rankcount;

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Integer getUniversityRank() { return universityRank; }

    public void setUniversityRank(Integer universityRank) { this.universityRank = universityRank; }

    public CUniversityStatus getUniversityStatus() {
        return universityStatus;
    }

    public void setUniversityStatus(CUniversityStatus universityStatus) {
        this.universityStatus = universityStatus;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getCareer() {
        return career;
    }

    public void setCareer(Integer career) {
        this.career = career;
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

    public Boolean getHideOnSearching() {
        return hideOnSearching;
    }

    public void setHideOnSearching(Boolean hideOnSearching) {
        this.hideOnSearching = hideOnSearching;
    }


    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getRankcount() {
        return rankcount;
    }

    public void setRankcount(String rankcount) {
        this.rankcount = rankcount;
    }
}
