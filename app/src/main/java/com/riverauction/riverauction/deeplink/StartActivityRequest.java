package com.riverauction.riverauction.deeplink;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.riverauction.riverauction.feature.main.MainActivity;

public abstract class StartActivityRequest implements Parcelable {

    private static final String PREFIX = "com.riverauction.riverauction.external.StartActivityRequest.";
    private static final String KEY_CLASS_NAME = PREFIX + "className";
    private static final String KEY_DATA = PREFIX + "bundle";
    private static final String KEY_DATA_CLASS_NAME = PREFIX + "dataClassName";
    private static final String KEY_FLAG = PREFIX + "flag";
    private static final String KEY_MAIN_ACTIVITY_REDIRECT_CODE = PREFIX + "activityRedirectCode";

    public StartActivityRequest() {
    }

    public abstract Class<?> getTargetActivityClass();
    public abstract Bundle getData();
    public abstract Class<?> getDataClassType();
    public abstract int getFlag();
    public abstract int getMainActivityRedirectCode();

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(getTargetActivityClass());
        dest.writeBundle(getData());
        dest.writeValue(getDataClassType());
        dest.writeInt(getFlag());
        dest.writeInt(getMainActivityRedirectCode());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StartActivityRequest> CREATOR = new Creator<StartActivityRequest>() {
        public StartActivityRequest createFromParcel(final Parcel in) {
            final Class<?> targetActivityClass = (Class<?>) in.readValue(null);
            final Bundle data = in.readBundle();
            final Class<?> targetDataClassType = (Class<?>) in.readValue(null);
            final int flag = in.readInt();
            final int redirectCode = in.readInt();

            return new DefaultStartActivityRequest(targetActivityClass, data, targetDataClassType, flag, redirectCode);
        }

        public StartActivityRequest[] newArray(int size) {
            return new StartActivityRequest[size];
        }
    };

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CLASS_NAME, getTargetActivityClass().getName());

        if (getData() != null) {
            bundle.putBundle(KEY_DATA, getData());
        }

        if (getDataClassType() != null) {
            bundle.putString(KEY_DATA_CLASS_NAME, getDataClassType().getName());
        }

        if (getFlag() != 0) {
            bundle.putInt(KEY_FLAG, getFlag());
        }

        bundle.putInt(KEY_MAIN_ACTIVITY_REDIRECT_CODE, getMainActivityRedirectCode());
        return bundle;
    }

    public static StartActivityRequest fromBundle(final Bundle bundle) {
        // 다른 Field 는 없을 수 있다.
        if (bundle == null || !bundle.containsKey(KEY_CLASS_NAME)) {
            return null;
        }

        Class<?> targetActivityClass;
        try {
            targetActivityClass = Class.forName(bundle.getString(KEY_CLASS_NAME));
        } catch (ClassNotFoundException ignored) {
            return null;
        }

        final Bundle data = bundle.getBundle(KEY_DATA);

        Class<?> targetDataClassType = null;
        if (bundle.containsKey(KEY_DATA_CLASS_NAME)) {
            try {
                targetDataClassType =  Class.forName(bundle.getString(KEY_DATA_CLASS_NAME));
            } catch (ClassNotFoundException ignored) {
                return null;
            }
        }

        int flag = bundle.getInt(KEY_FLAG, 0);
        int redirectCode = bundle.getInt(KEY_MAIN_ACTIVITY_REDIRECT_CODE);

        return new DefaultStartActivityRequest(targetActivityClass, data, targetDataClassType, flag, redirectCode);
    }

    /**
     * Stack 에 MainActivity 가 존재하는 경우, {@link StartActivityRequest} 를 알맞은 형태의 Intent 로 변환하는 함수이다.
     */
    public static Intent createMainActivityInStackActivity(Context context, StartActivityRequest startActivityRequest) {
        Bundle extras = startActivityRequest.getData();
        int flag = startActivityRequest.getFlag();
        int redirectCode = startActivityRequest.getMainActivityRedirectCode();

        final Intent intent = new Intent(context, startActivityRequest.getTargetActivityClass());
        if (flag != 0) {
            intent.addFlags(flag);
        }

        if (extras != null) {
            if (startActivityRequest.getDataClassType() != null) {
                extras.setClassLoader(startActivityRequest.getDataClassType().getClassLoader());
            }
            intent.putExtras(extras);
        }

        if (redirectCode > 0) {
            intent.putExtra(MainActivity.EXTRA_ACTIVITY_REDIRECT, redirectCode);
        }

        return intent;
    }

    /**
     * {@link DeepLinkParser} 는 요청을 받아서 {@link StartActivityRequest} 생성한다. <br>
     * 하지만 Intent extra 로 {@link StartActivityRequest} 를 직접 넘겨줄 수 있다.
     * StartActivityRequest 를 Intent 이 설정하는 작업을 한다. (Parcelable 을 직접 넘길 수 없는 이유는 Class 형태라면 다른 Application 에서는 우리의 ClassLoader 를 알 수 없기 때문이다.)
     */
    public static Intent setDirectRequest(Intent intent, StartActivityRequest request) {
        intent.putExtras(request.toBundle());
        return intent;
    }
}
