package com.riverauction.riverauction.feature.register;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.service.auth.response.IssueTokenResult;
import com.riverauction.riverauction.base.MvpView;

public interface IntroMvpView extends MvpView {
    void successIssueToken(IssueTokenResult issueTokenResult);
    boolean failIssueToken(CErrorCause errorCause);
}
