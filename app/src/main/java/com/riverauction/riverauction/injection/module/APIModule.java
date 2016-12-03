package com.riverauction.riverauction.injection.module;

import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ApplicationContext;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.BuildConfig;
import com.riverauction.riverauction.api.APIConstant;
import com.riverauction.riverauction.api.converter.JacksonApiConverter;
import com.riverauction.riverauction.api.service.auth.AuthService;
import com.riverauction.riverauction.api.service.info.InfoService;
import com.riverauction.riverauction.api.service.lesson.LessonService;
import com.riverauction.riverauction.api.service.payment.PaymentService;
import com.riverauction.riverauction.api.service.teacher.TeacherService;
import com.riverauction.riverauction.api.service.user.UserService;
import com.riverauction.riverauction.states.UserStates;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

@Module
public class APIModule {

    public static final String AUTHORIZATION = "Authorization";

    @Provides
    @Singleton
    public RestAdapter provideRestAdapter(@ApplicationContext Context context, StateCtx stateCtx, OkHttpClient okHttpClient) {
        RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;

        return new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint(APIConstant.API_ENDPOINT)
                .setConverter(new JacksonApiConverter())
                .setLogLevel(logLevel)
                .setRequestInterceptor(request -> {
                    if (UserStates.hasAccessToken(stateCtx)) {
                        String accessToken = UserStates.ACCESS_TOKEN.get(stateCtx);
                        request.addHeader(AUTHORIZATION, String.format("RiverAuction access_token=%s", accessToken));
                    }
                })
                .build();
    }

    @Provides
    @Singleton
    AuthService provideAuthService(RestAdapter restAdapter) {
        return restAdapter.create(AuthService.class);
    }

    @Provides
    @Singleton
    TeacherService provideTeacherService(RestAdapter restAdapter) {
        return restAdapter.create(TeacherService.class);
    }

    @Provides
    @Singleton
    LessonService provideLessonService(RestAdapter restAdapter) {
        return restAdapter.create(LessonService.class);
    }

    @Provides
    @Singleton
    UserService provideUserService(RestAdapter restAdapter) {
        return restAdapter.create(UserService.class);
    }

    @Provides
    @Singleton
    InfoService provideInfoService(RestAdapter restAdapter) {
        return restAdapter.create(InfoService.class);
    }

    @Provides
    @Singleton
    PaymentService providePaymentService(RestAdapter restAdapter) {
        return restAdapter.create(PaymentService.class);
    }
}
