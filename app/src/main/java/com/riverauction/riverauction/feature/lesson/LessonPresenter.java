package com.riverauction.riverauction.feature.lesson;

import android.content.Context;

import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.lesson.params.GetLessonsParams;
import com.riverauction.riverauction.base.BasePresenter;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.feature.mylesson.MyLessonMvpView;
import com.riverauction.riverauction.rxjava.APISubscriber;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class LessonPresenter extends BasePresenter<LessonMvpView> {

    private final DataManager dataManager;
    private Subscription subscription;

    @Inject
    LessonPresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void attachView(LessonMvpView mvpView, Context context) {
        super.attachView(mvpView, context);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public void getLessons(GetLessonsParams params) {
        checkViewAttached();
        if (params == null) {
            return;
        }

        subscription = dataManager.getLessons(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new APISubscriber<APISuccessResponse<List<CLesson>>>() {

                    @Override
                    public void onNext(APISuccessResponse<List<CLesson>> response) {
                        super.onNext(response);
                        getMvpView().successGetLessons(response);
                    }

                    @Override
                    public boolean onErrors(Throwable e) {
                        return getMvpView().failGetLessons(getErrorCause(e));
                    }
                });
    }
}
