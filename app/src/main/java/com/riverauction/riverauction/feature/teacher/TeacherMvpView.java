package com.riverauction.riverauction.feature.teacher;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface TeacherMvpView extends MvpView {
    void successGetTeachers(APISuccessResponse<List<CUser>> response);
    boolean failGetTeachers(CErrorCause errorCause);
}
