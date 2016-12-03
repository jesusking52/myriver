package com.jhcompany.android.libs.rxjava;

import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;

import rx.Subscriber;

public class BasicSubscriber<T> extends Subscriber<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSubscriber.class);

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.error(e.getMessage(), e);
    }

    @Override
    public void onNext(T t) {
    }
}
