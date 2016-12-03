package com.riverauction.riverauction.injection.module;

import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ApplicationContext;
import com.jhcompany.android.libs.preference.PreferenceStateCtx;
import com.jhcompany.android.libs.preference.StateCtx;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StatesModule {

    @Provides
    @Singleton
    public StateCtx provideStateCtx(@ApplicationContext Context context) {
        return new PreferenceStateCtx(context);
    }
}
