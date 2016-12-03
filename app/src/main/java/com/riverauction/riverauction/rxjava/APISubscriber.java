package com.riverauction.riverauction.rxjava;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Toast;

import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.jhcompany.android.libs.rxjava.BasicSubscriber;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.service.APIErrorResponse;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.model.CError;
import com.riverauction.riverauction.api.model.CErrorCause;

import java.util.concurrent.atomic.AtomicLong;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class APISubscriber<T> extends BasicSubscriber<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger("APISubscriber");
    /**
     * State 에러 (e.g. Network Error) 인 경우, 최근 5초 동안 Dialog 가 표시된적 있다면 보여주지 않는다.
     */
    private static final long STATE_ERROR_TOAST_INTERVAL = 5 * 1000;

    /**
     * State 에러 (e.g. Network Error) Toast 가 언제 마지막으로 표시됐는지 시간을 저장한다.
     */
    private static final AtomicLong LAST_STATE_ERROR_SHOWN_MILLIS = new AtomicLong(0);

    private final Context context;
    private final boolean suppressToast;

    public APISubscriber() {
        this(false);
    }

    public APISubscriber(boolean suppressToast) {
        this.context = RiverAuctionApplication.getContext();
        this.suppressToast = suppressToast;
    }

    @Override
    public final void onError(Throwable e) {
        super.onError(e);

        try {
            if (onErrors(e)) {
                return;
            }
        } catch (Throwable throwable) {
            LOGGER.error(throwable.getMessage(), throwable);
        }

        if (e instanceof RetrofitError) {
            RetrofitError retrofitError = (RetrofitError) e;
            if (retrofitError.getKind() == RetrofitError.Kind.HTTP) {
                handleCommonError(getErrorCause(retrofitError));
            }
        }
    }

    private boolean isNetworkError(Throwable e) {
        if (e instanceof RetrofitError) {
            RetrofitError retrofitError = (RetrofitError) e;
            return retrofitError.getKind() == RetrofitError.Kind.NETWORK;
        }
        return false;
    }

    private void handleCommonError(CErrorCause errorCause) {
        switch (errorCause) {
            case INVALID_TOKEN:
            case REVOKED_TOKEN:
            case EXPIRED_TOKEN: {
//                handleExpiredToken(activity);
                break;
            }
            case REISSUE_TOKEN: {
                // 1.2.0 버전부터 REISSUE_TOKEN 이 에러코드로 떨어진다.
//                handleReissueToken(activity);
                break;
            }
            case NETWORK_ERROR: {
                showStateErrorToast(context, context.getResources().getString(R.string.common_error_message_network));
                break;
            }
            case UNKNOWN:
            default:{
                showUnknownToast();
                break;
            }
        }
    }

    public CErrorCause getErrorCause(Throwable e) {
        try {
            if (isNetworkError(e)) {
                return CErrorCause.NETWORK_ERROR;
            }
            if (e instanceof RetrofitError) {
                RetrofitError retrofitError = (RetrofitError) e;
                APIErrorResponse errorResponse = (APIErrorResponse) retrofitError.getBodyAs(APIErrorResponse.class);
                CError error = errorResponse.getError();
                if (error == null || error.getCause() == null) {
                    return CErrorCause.UNKNOWN;
                } else {
                    return error.getCause();
                }
            }
        } catch (Throwable throwable) {
            LOGGER.error(throwable.getMessage(), throwable);
        }
        return CErrorCause.UNKNOWN;
    }

    private void showUnknownToast() {
        if (suppressToast || context == null) {
            return;
        }
        showStateErrorToast(context, context.getResources().getString(R.string.common_error_message_unknown));
    }

    private void showStateErrorToast(final Context context, final String message) {
        final long currentMillis = SystemClock.elapsedRealtime();
        long elapsed = currentMillis - LAST_STATE_ERROR_SHOWN_MILLIS.get();
        if (elapsed < STATE_ERROR_TOAST_INTERVAL) {
            return;
        }

        Observable.empty()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BasicSubscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        LAST_STATE_ERROR_SHOWN_MILLIS.set(SystemClock.elapsedRealtime());
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean onErrors(Throwable e) {
        return false;
    }
}
