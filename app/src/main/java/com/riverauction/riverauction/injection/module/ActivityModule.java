package com.riverauction.riverauction.injection.module;

import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ActivityContext;
import com.riverauction.riverauction.base.BaseActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    protected final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    BaseActivity provideActivity() {
        return activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return activity;
    }
}
