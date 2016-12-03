package com.riverauction.riverauction.base;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.jhcompany.android.libs.activity.ActivityManager;
import com.jhcompany.android.libs.injection.DaggerService;
import com.jhcompany.android.libs.jackson.Jackson;
import com.jhcompany.android.libs.jackson.ObjectMapperFactory;
import com.riverauction.riverauction.feature.main.MainActivity;
import com.riverauction.riverauction.glide.GlideImage;
import com.riverauction.riverauction.glide.GlideImageModelLoader;
import com.riverauction.riverauction.injection.component.ApplicationComponent;
import com.riverauction.riverauction.injection.component.DaggerApplicationComponent;
import com.riverauction.riverauction.injection.module.APIModule;
import com.riverauction.riverauction.injection.module.ApplicationModule;
import com.riverauction.riverauction.injection.module.HttpClientModule;
import com.riverauction.riverauction.injection.module.StatesModule;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;


public abstract class BaseApplication extends Application {

    private static BaseApplication APPLICATION;
    private static Context CONTEXT;
    private static ActivityManager ACTIVITY_MANAGER;

    protected ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        CONTEXT = this;
        APPLICATION = this;
        ACTIVITY_MANAGER = new ActivityManager(MainActivity.class);

        Jackson.setMapper(ObjectMapperFactory.newMapper());
        setUpGlide();
    }

    @Override
    public Object getSystemService(String name) {
        if (DaggerService.SERVICE_NAME.equals(name)) {
            return getComponent();
        }
        return super.getSystemService(name);
    }

    public static Context getContext() {
        return CONTEXT;
    }

    public static BaseApplication getApplication() {
        return APPLICATION;
    }

    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .httpClientModule(new HttpClientModule())
                    .aPIModule(new APIModule())
                    .statesModule(new StatesModule())
                    .build();
        }
        return applicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        applicationComponent = applicationComponent;
    }

    public static ActivityManager getActivityManager() {
        return ACTIVITY_MANAGER;
    }

    private void setUpGlide() {
        Glide.setup(new GlideBuilder(this));
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
        Glide.get(this).register(GlideImage.class, InputStream.class, new GlideImageModelLoader.GlideModelLoaderFactory());
    }
}
