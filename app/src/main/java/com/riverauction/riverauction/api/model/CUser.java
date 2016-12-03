package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.jhcompany.android.libs.utils.Lists2;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CUser {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("type")
    private CUserType type;

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

    @JsonProperty("profile_photos")
    private List<CImage> profilePhotos;

    @JsonProperty("location")
    private CLocation location;

    @JsonProperty("student")
    private CStudent student;

    @JsonProperty("teacher")
    private CTeacher teacher;

    @JsonProperty("coins")
    private Integer coins;

    // 내가 이 유저를 찜했는지 여부
    @JsonProperty("is_favorited")
    private Boolean isFavorited;

    // 내가 이 유저 연락처 보기를 했는지 여부
    @JsonProperty("is_checked_phone_number")
    private Boolean isCheckedPhoneNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CUserType getType() {
        return type;
    }

    public void setType(CUserType type) {
        this.type = type;
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

    public CStudent getStudent() {
        return student;
    }

    public void setStudent(CStudent student) {
        this.student = student;
    }

    public CTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(CTeacher teacher) {
        this.teacher = teacher;
    }

    public List<CImage> getProfilePhotos() {
        return profilePhotos;
    }

    public void setProfilePhotos(List<CImage> profilePhotos) {
        this.profilePhotos = profilePhotos;
    }

    public CLocation getLocation() {
        return location;
    }

    public void setLocation(CLocation location) {
        this.location = location;
    }

    public boolean hasProfilePhotos() {
        return !Lists2.isNullOrEmpty(profilePhotos);
    }

    public boolean hasAddress() {
        return location != null && !Strings.isNullOrEmpty(location.getAddress());
    }

    public Boolean getIsFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(Boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public Boolean getIsCheckedPhoneNumber() {
        return isCheckedPhoneNumber;
    }

    public void setIsCheckedPhoneNumber(Boolean isCheckedPhoneNumber) {
        this.isCheckedPhoneNumber = isCheckedPhoneNumber;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!o.getClass().isInstance(this)) {
            return false;
        }

        if (this.id.equals(((CUser) o).getId())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    public boolean isDefaultPhoto() {
        if (Lists2.isNullOrEmpty(profilePhotos)) {
            return true;
        }
        return false;
    }
}
