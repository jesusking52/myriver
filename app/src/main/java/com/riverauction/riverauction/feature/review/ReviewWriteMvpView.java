package com.riverauction.riverauction.feature.review;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.MvpView;

public interface ReviewWriteMvpView extends MvpView {
    void successPatchUser(CUser user);
    boolean failPatchUser(CErrorCause errorCause);
    void successGetUser(CUser user);
    boolean failGetUser(CErrorCause errorCause);
}
