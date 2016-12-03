package com.riverauction.riverauction.feature.profile;

import android.content.Context;

import com.jhcompany.android.libs.injection.DaggerService;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.user.request.UserPreferencesRequest;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.injection.component.ApplicationComponent;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.rxjava.DialogSubscriber;
import com.riverauction.riverauction.states.PushStates;
import com.riverauction.riverauction.states.UserStates;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ProfilePresenter extends BasePresenter<ProfileMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    ProfilePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(ProfileMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void signOut() {
        checkViewAttached();

        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                StateCtx stateCtx = DaggerService.<ApplicationComponent>getDaggerComponent(RiverAuctionApplication.getContext()).stateCtx();
                UserStates.USER.clear(stateCtx);
                PushStates.clear(stateCtx);

                try {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        }) .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<Boolean>() {

                    @Override
                    public void onNext(Boolean result) {
                        super.onNext(result);
                        getMvpView().successSignOut(result);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failSignOut(getErrorCause(e));
                    }
                });
    }

    public void postPreferences(Integer userId, UserPreferencesRequest request) {
        checkViewAttached();

        subscription = dataManager.postPreferences(userId, request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successPostPreferences(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostPreferences(getErrorCause(e));
                    }
                }, context));
    }

    public void postProfilePhoto(Integer userId, File localFile) {
        checkViewAttached();

        subscription = dataManager.postProfilePhoto(userId, localFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DialogSubscriber<>(new APISubscriber<CUser>() {

                    @Override
                    public void onNext(CUser user) {
                        super.onNext(user);
                        getMvpView().successPostProfilePhoto(user);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failPostPreferences(getErrorCause(e));
                    }
                }, context));
    }
}
