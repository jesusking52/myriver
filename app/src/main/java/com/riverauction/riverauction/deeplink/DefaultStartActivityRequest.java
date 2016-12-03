package com.riverauction.riverauction.deeplink;

import android.os.Bundle;

public class DefaultStartActivityRequest extends StartActivityRequest{

    private final Class<?> targetClass;
    private final Bundle bundle;
    private final Class<?> dataClassType;
    private final int flag;
    private final int mainActivityRedirectCode;

    public DefaultStartActivityRequest(Class<?> targetClass, Bundle bundle, Class<?> dataClassType, int flag, int mainActivityRedirectCode) {
        this.targetClass = targetClass;
        this.bundle = bundle;
        this.dataClassType = dataClassType;
        this.flag = flag;
        this.mainActivityRedirectCode = mainActivityRedirectCode;
    }

    @Override
    public Class<?> getTargetActivityClass() {
        return targetClass;
    }

    @Override
    public Bundle getData() {
        return bundle;
    }

    @Override
    public Class<?> getDataClassType() {
        return dataClassType;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public int getMainActivityRedirectCode() {
        return mainActivityRedirectCode;
    }
}
