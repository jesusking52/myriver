package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CSigungu {

    @JsonProperty("name")
    private String name;

    @JsonProperty("minZoneCode")
    private Integer minZoneCode;

    @JsonProperty("maxZoneCode")
    private Integer maxZoneCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
