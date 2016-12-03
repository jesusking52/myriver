package com.riverauction.riverauction.feature.register.signup.phone;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.base.MvpView;

public interface EnterPhoneNumberCodeMvpView extends MvpView {
    void successCertifyAuthNumber();
    boolean failCertifyAuthNumber(CErrorCause errorCause);
}
