package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

/**
 * Daum Post Code 웹에서 return 되는 객체
 */
@Keep
@KeepClassMembers
public class CDaumPostCode {

    // ex) 460-130
    @JsonProperty("postcode")
    private String postcode;

    // ex) 460
    @JsonProperty("postcode1")
    private Integer postcode1;

    // ex) 130
    @JsonProperty("postcode2")
    private Integer postcode2;

    @JsonProperty("zonecode")
    private Integer zonecode;

    @JsonProperty("address")
    private String address;

    @JsonProperty("jibunAddress")
    private String jibunAddress;

    @JsonProperty("autoJibunAddress")
    private String autoJibunAddress;

    @JsonProperty("sido")
    private String sido;

    @JsonProperty("sigungu")
    private String sigungu;

    @JsonProperty("bname")
    private String bname;

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJibunAddress() { return jibunAddress; }

    public String getAutoJibunAddress() { return autoJibunAddress; }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getSigungu() {
        return sigungu;
    }

    public void setSigungu(String sigungu) {
        this.sigungu = sigungu;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public Integer getPostcode1() {
        return postcode1;
    }

    public void setPostcode1(Integer postcode1) {
        this.postcode1 = postcode1;
    }

    public Integer getPostcode2() {
        return postcode2;
    }

    public void setPostcode2(Integer postcode2) {
        this.postcode2 = postcode2;
    }

    public Integer getZonecode() {
        return zonecode;
    }

    public void setZonecode(Integer zonecode) {
        this.zonecode = zonecode;
    }
}
