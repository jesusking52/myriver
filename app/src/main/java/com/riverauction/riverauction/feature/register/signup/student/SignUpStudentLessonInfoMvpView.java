package com.riverauction.riverauction.feature.register.signup.student;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.service.auth.response.SignUpResult;
import com.riverauction.riverauction.base.MvpView;

public interface SignUpStudentLessonInfoMvpView extends MvpView {
    void successSignUp(SignUpResult signUpResult);
    boolean failSignUp(CErrorCause errorCause);
}
