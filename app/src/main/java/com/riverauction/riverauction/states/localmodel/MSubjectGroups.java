package com.riverauction.riverauction.states.localmodel;

import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CSubjectGroup;

import java.util.List;

import proguard.annotation.Keep;
import proguard.annotation.KeepName;

@Keep
@KeepName
public class MSubjectGroups {
    private List<CSubjectGroup> subjectGroups;

    public MSubjectGroups() {
    }

    public MSubjectGroups(List<CSubjectGroup> subjectGroups) {
        this.subjectGroups = subjectGroups;
    }

    public List<CSubjectGroup> getSubjectGroups() {
        return subjectGroups;
    }

    public void setSubjectGroups(List<CSubjectGroup> subjectGroups) {
        this.subjectGroups = subjectGroups;
    }

    public String getSubjectGroupName(Integer groupId) {
        if (Lists2.isNullOrEmpty(subjectGroups) || groupId == null) {
            return null;
        }

        for (CSubjectGroup subjectGroup : subjectGroups) {
            if (subjectGroup.getId().equals(groupId)) {
                return subjectGroup.getName();
            }
        }

        return null;
    }
}
