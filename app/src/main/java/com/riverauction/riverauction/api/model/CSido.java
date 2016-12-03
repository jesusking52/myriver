package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CSido {

    @JsonProperty("sido")
    private String sido;

    @JsonProperty("minZoneCode")
    private Integer minZoneCode;

    @JsonProperty("maxZoneCode")
    private Integer maxZoneCode;

    @JsonProperty("sigungu")
    private List<CSigungu> sigungu;

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public Integer getMinZoneCode() {
        return minZoneCode;
    }

    public void setMinZoneCode(Integer minZoneCode) {
        this.minZoneCode = minZoneCode;
    }

    public Integer getMaxZoneCode() {
        return maxZoneCode;
    }

    public void setMaxZoneCode(Integer maxZoneCode) {
        this.maxZoneCode = maxZoneCode;
    }

    public List<CSigungu> getSigungu() {
        return sigungu;
    }

    public void setSigungu(List<CSigungu> sigungu) {
        this.sigungu = sigungu;
    }
}
