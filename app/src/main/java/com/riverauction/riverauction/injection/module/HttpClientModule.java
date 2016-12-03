package com.riverauction.riverauction.injection.module;

import com.riverauction.riverauction.injection.annotations.ForUpload;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HttpClientModule {

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        return okHttpClient;
    }

    @Provides
    @Singleton
    @ForUpload
    public OkHttpClient provideUploadHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(4, TimeUnit.MINUTES);
        return okHttpClient;
    }
}
