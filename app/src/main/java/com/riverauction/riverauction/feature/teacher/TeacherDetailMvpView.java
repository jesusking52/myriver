package com.riverauction.riverauction.feature.teacher;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.MvpView;

public interface TeacherDetailMvpView extends MvpView {
    void successGetUser(CUser user);
    boolean failGetUser(CErrorCause errorCause);
    void successPostUserFavorites(CUserFavorite favorite);
    boolean failPostUserFavorites(CErrorCause errorCause);
    void successDeleteUserFavorites();
    boolean failDeleteUserFavorites(CErrorCause errorCause);

    void successCheckPhoneNumber(CUser user);
    boolean failCheckPhoneNumber(CErrorCause errorCause);

    void successPostSelectTeacher(CUser user);
    boolean failPostSelectTeacher(CErrorCause errorCause);
}
