package com.jhcompany.android.libs.injection;

import android.content.Context;

public class DaggerService {

    public static final String SERVICE_NAME = DaggerService.class.getName();

    private DaggerService() {}

    /**
     * Caller is required to know the type of the component for this context.
     */
    @SuppressWarnings("unchecked") //
    public static <T> T getDaggerComponent(Context context) {
        //noinspection ResourceType
        return (T) context.getSystemService(SERVICE_NAME);
    }
}
