package com.riverauction.riverauction.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepClassMembers;

@Keep
@KeepClassMembers
public class CSidoSigungu {

    @JsonProperty("sidoSigungu")
    private List<CSido> sidoSigungu;

    public List<CSido> getSidoSigungu() {
        return sidoSigungu;
    }

    public void setSidoSigungu(List<CSido> sidoSigungu) {
        this.sidoSigungu = sidoSigungu;
    }
}
