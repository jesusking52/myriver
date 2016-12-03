package com.riverauction.riverauction.rxjava;

import android.content.Context;

import com.riverauction.riverauction.widget.LoadingProgressDialog;

import rx.Subscriber;

public class DialogSubscriber<T> extends Subscriber<T> {

    private Subscriber<T> subscriber;
    private final LoadingProgressDialog dialog;

    public DialogSubscriber(Subscriber<T> subscriber, Context context) {
        this.subscriber = subscriber;
        this.dialog = new LoadingProgressDialog(context);
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.show();

        if (subscriber != null) {
            subscriber.onStart();
        }
    }

    @Override
    public void onCompleted() {
        dialog.dismissWithSuccess();
        if (subscriber != null) {
            subscriber.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        dialog.dismissWithFail();
        if (subscriber != null) {
            subscriber.onError(e);
        }
    }

    @Override
    public void onNext(Object o) {
        if (subscriber != null) {
            subscriber.onNext((T) o);
        }
    }
}
