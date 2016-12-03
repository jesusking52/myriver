package com.riverauction.riverauction.feature.profile.patch.teacher;

import android.content.Context;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.user.request.UserPatchRequest;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ProfileTeacherBasicInfoPatchPresenter extends BasePresenter<ProfileTeacherBasicInfoPatchMvpView> {
    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    ProfileTeacherBasicInfoPatchPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(ProfileTeacherBasicInfoPatchMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void patchUser(Integer userId, UserPatchRequest request) {
        checkViewAttached();
        if (userId == null) {
            return;
        }

        subscription = dataManager.patchUser(userId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successPatchUser(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPatchUser(getErrorCause(e));
                    }
                }, context));
    }
}
