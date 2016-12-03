package com.riverauction.riverauction.feature.lesson;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface LessonMvpView extends MvpView {
    void successGetLessons(APISuccessResponse<List<CLesson>> response);
    boolean failGetLessons(CErrorCause errorCause);
}
