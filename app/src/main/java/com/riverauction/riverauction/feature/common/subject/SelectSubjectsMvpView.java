package com.riverauction.riverauction.feature.common.subject;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CSubjectGroup;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface SelectSubjectsMvpView extends MvpView {
    void successSubjectGroups(List<CSubjectGroup> subjectGroups);
    boolean failSubjectGroups(CErrorCause errorCause);
}
