package com.riverauction.riverauction.deeplink;

import android.os.Bundle;

import com.riverauction.riverauction.feature.main.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class MainActivityRedirectRequest extends StartActivityRequest {

    private Bundle bundle = new Bundle();

    private Class<?> targetActivity;
    private int redirectCode;

    public MainActivityRedirectRequest(int redirectCode, boolean shouldAppUpdate) {
        this.redirectCode = redirectCode;
        bundle.putBoolean(MainActivity.EXTRA_SHOULD_APP_UPDATE, shouldAppUpdate);
        targetActivity = MainActivity.class;
//        switch (redirectCode) {
//        case EXTRA_ACTIVITY_REDIRECT_HOME:
//        case EXTRA_ACTIVITY_REDIRECT_PLACE:
//        case EXTRA_ACTIVITY_REDIRECT_THEME:
//        case EXTRA_ACTIVITY_REDIRECT_PROFILE: {
//            targetActivity = MainActivity.class;
//            break;
//        }
//        }
    }

    @Override
    public Class getTargetActivityClass() {
        return targetActivity;
    }

    @Override
    public Bundle getData() {
        return bundle;
    }

    @Override
    public Class<?> getDataClassType() {
        return null;
    }

    @Override
    public int getFlag() {
        return FLAG_ACTIVITY_NO_ANIMATION | FLAG_ACTIVITY_CLEAR_TOP;
    }

    @Override
    public int getMainActivityRedirectCode() {
        return redirectCode;
    }
}
