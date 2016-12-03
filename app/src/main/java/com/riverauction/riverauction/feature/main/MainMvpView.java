package com.riverauction.riverauction.feature.main;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.base.MvpView;

public interface MainMvpView extends MvpView {
    void successSignOut(Boolean result);
    boolean failSignOut(CErrorCause errorCause);
}
