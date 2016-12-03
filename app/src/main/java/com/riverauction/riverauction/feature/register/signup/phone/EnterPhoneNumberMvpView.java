package com.riverauction.riverauction.feature.register.signup.phone;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.base.MvpView;

public interface EnterPhoneNumberMvpView extends MvpView {
    void successRequestAuthNumber(String phoneNumber);
    boolean failRequestAuthNumber(CErrorCause errorCause);
}
