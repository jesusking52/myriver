package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.jhcompany.android.libs.utils.Lists2;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CLesson {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("status")
    private CLessonStatus status;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("expired_at")
    private Long expiredAt;

    @JsonProperty("profile_photos")
    private List<CImage> profilePhotos;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private CGender gender;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("birth_year")
    private Integer birthYear;

    @JsonProperty("location")
    private CLocation location;

    @JsonProperty("student_status")
    private CStudentStatus studentStatus;

    @JsonProperty("grade")
    private Integer grade;

    @JsonProperty("department")
    private CStudentDepartmentType department;

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

    @JsonProperty("biddings_count")
    private Integer biddingsCount;

    @JsonProperty("description")
    private String description;

    // 경매 주인
    @JsonProperty("owner")
    private CUser owner;

    // 선택한 선생님
    @JsonProperty("selected_user")
    private CUser selectedTeacher;

    // 내가 이 경매를 찜했는지 여부
    @JsonProperty("is_favorited")
    private Boolean isFavorited;

    // 내가 이 경매를 입찰했는지 여부
    @JsonProperty("is_bid")
    private Boolean isBid;

    @JsonProperty("bidding")
    private CLessonBidding bidding;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CLessonStatus getStatus() {
        return status;
    }

    public void setStatus(CLessonStatus status) {
        this.status = status;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CGender getGender() {
        return gender;
    }

    public void setGender(CGender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public List<CImage> getProfilePhotos() {
        return profilePhotos;
    }

    public void setProfilePhotos(List<CImage> profilePhotos) {
        this.profilePhotos = profilePhotos;
    }

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

    public Integer getBiddingsCount() {
        return biddingsCount;
    }

    public void setBiddingsCount(Integer biddingsCount) {
        this.biddingsCount = biddingsCount;
    }

    public CLocation getLocation() {
        return location;
    }

    public void setLocation(CLocation location) {
        this.location = location;
    }

    public CUser getSelectedTeacher() {
        return selectedTeacher;
    }

    public void setSelectedTeacher(CUser selectedTeacher) {
        this.selectedTeacher = selectedTeacher;
    }

    public boolean hasProfilePhotos() {
        return !Lists2.isNullOrEmpty(profilePhotos);
    }

    public boolean hasAddress() {
        return location != null && !Strings.isNullOrEmpty(location.getAddress());
    }

    public CUser getOwner() {
        return owner;
    }

    public void setOwner(CUser owner) {
        this.owner = owner;
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public Boolean getIsBid() {
        return isBid;
    }

    public void setIsBid(Boolean isBid) {
        this.isBid = isBid;
    }

    public CLessonBidding getBidding() {
        return bidding;
    }

    public void setBidding(CLessonBidding bidding) {
        this.bidding = bidding;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
