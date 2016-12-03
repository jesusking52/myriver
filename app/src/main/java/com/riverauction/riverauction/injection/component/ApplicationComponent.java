package com.riverauction.riverauction.injection.component;

import android.app.Application;
import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ApplicationContext;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.feature.lesson.LessonView;
import com.riverauction.riverauction.feature.mylesson.MyLessonView;
import com.riverauction.riverauction.feature.teacher.TeacherView;
import com.riverauction.riverauction.injection.module.APIModule;
import com.riverauction.riverauction.injection.module.ApplicationModule;
import com.riverauction.riverauction.injection.module.HttpClientModule;
import com.riverauction.riverauction.injection.module.StatesModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        HttpClientModule.class,
        StatesModule.class,
        APIModule.class,
})
public interface ApplicationComponent {

    @ApplicationContext Context context();

    Application application();
    DataManager dataManager();
    StateCtx stateCtx();

    void inject(TeacherView view);
    void inject(LessonView view);
    void inject(MyLessonView view);
}
