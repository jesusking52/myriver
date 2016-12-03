package com.riverauction.riverauction.feature.profile;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CImage;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface ProfileMvpView extends MvpView {
    void successSignOut(Boolean result);
    boolean failSignOut(CErrorCause errorCause);

    void successPostPreferences(CUser user);
    boolean failPostPreferences(CErrorCause errorCause);

    void successPostProfilePhoto(CUser user);
    boolean failPostProfilePhoto(CErrorCause errorCause);
}
