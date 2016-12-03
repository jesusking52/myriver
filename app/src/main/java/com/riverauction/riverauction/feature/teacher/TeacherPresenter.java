package com.riverauction.riverauction.feature.teacher;

import android.content.Context;

import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class TeacherPresenter extends BasePresenter<TeacherMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    TeacherPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(TeacherMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getTeachers(Integer userId, GetTeachersParams params) {
        checkViewAttached();
        if (params == null) {
            return;
        }

        subscription = dataManager.getTeachers(userId, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CUser>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CUser>> response) {
                        super.onNext(response);
                        getMvpView().successGetTeachers(response);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetTeachers(getErrorCause(e));
                    }
                });
    }
}
