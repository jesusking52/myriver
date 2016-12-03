package com.riverauction.riverauction.injection.module;

import android.app.Application;
import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ApplicationContext;
import com.riverauction.riverauction.base.BaseApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    protected final BaseApplication application;

    public ApplicationModule(BaseApplication application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }
}
