package com.riverauction.riverauction.base;

import android.content.Context;

/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {

    void attachView(V mvpView, Context context);

    void detachView();
}
