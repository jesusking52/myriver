package com.riverauction.riverauction.feature.notification;

import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.model.CNotification;
import com.riverauction.riverauction.base.MvpView;

import java.util.List;

public interface NotificationMvpView extends MvpView {
    void successGetNotifications(List<CNotification> newNotifications, Integer newNextToken);
    boolean failGetNotifications(CErrorCause errorCause);
    // 학생이 호출함
    void successGetUserFavorites(List<CUserFavorite> newFavorite, Integer newNextToken);
    boolean failGetUserFavorites(CErrorCause errorCause);
    // 선생님이 호출함
    void successGetLessonFavorites(List<CLessonFavorite> newFavorite, Integer newNextToken);
    boolean failGetLessonFavorites(CErrorCause errorCause);
}
