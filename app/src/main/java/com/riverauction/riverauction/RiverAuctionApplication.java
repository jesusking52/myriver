package com.riverauction.riverauction;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.riverauction.riverauction.base.BaseApplication;

public class RiverAuctionApplication extends BaseApplication {

    private static RiverAuctionApplication APPLICATION;
    private static Context CONTEXT;

    @Override
    public void onCreate() {
        super.onCreate();

        APPLICATION = this;
        CONTEXT = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static RiverAuctionApplication getApplication() {
        return APPLICATION;
    }

    public static Context getContext() {
        return CONTEXT;
    }
}
