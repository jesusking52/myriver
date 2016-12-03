package com.riverauction.riverauction.injection.component;


import android.content.Context;

import com.jhcompany.android.libs.injection.qualifier.ActivityContext;
import com.jhcompany.android.libs.injection.scope.PerActivity;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.bidding.MakeBiddingActivity;
import com.riverauction.riverauction.feature.common.dayofweek.SelectDayOfWeeksActivity;
import com.riverauction.riverauction.feature.common.gender.SelectGenderActivity;
import com.riverauction.riverauction.feature.common.location.SelectLocationActivity;
import com.riverauction.riverauction.feature.common.price.SelectPriceRangeActivity;
import com.riverauction.riverauction.feature.common.studentstatus.SelectStudentStatusActivity;
import com.riverauction.riverauction.feature.common.subject.SelectSubjectsActivity;
import com.riverauction.riverauction.feature.common.university.SelectUniversityActivity;
import com.riverauction.riverauction.feature.lesson.LessonDetailActivity;
import com.riverauction.riverauction.feature.lesson.bidding.PostBiddingActivity;
import com.riverauction.riverauction.feature.lesson.filter.StudentFilterActivity;
import com.riverauction.riverauction.feature.main.MainActivity;
import com.riverauction.riverauction.feature.mylesson.detail.MyLessonDetailActivity;
import com.riverauction.riverauction.feature.mylesson.detail.MyLessonDetailSelectListActivity;
import com.riverauction.riverauction.feature.notification.NotificationActivity;
import com.riverauction.riverauction.feature.profile.ProfileActivity;
import com.riverauction.riverauction.feature.profile.patch.ProfilePatchActivity;
import com.riverauction.riverauction.feature.profile.patch.student.ProfileStudentBasicInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.student.ProfileStudentLessonInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.teacher.ProfileTeacherBasicInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.patch.teacher.ProfileTeacherLessonInfoPatchActivity;
import com.riverauction.riverauction.feature.profile.shop.ShopActivity;
import com.riverauction.riverauction.feature.register.IntroActivity;
import com.riverauction.riverauction.feature.register.signup.phone.EnterPhoneNumberActivity;
import com.riverauction.riverauction.feature.register.signup.phone.EnterPhoneNumberCodeActivity;
import com.riverauction.riverauction.feature.register.signup.student.SignUpStudentActivity;
import com.riverauction.riverauction.feature.register.signup.student.SignUpStudentLessonInfoActivity;
import com.riverauction.riverauction.feature.register.signup.teacher.SignUpTeacherActivity;
import com.riverauction.riverauction.feature.register.signup.teacher.SignUpTeacherLessonInfoActivity;
import com.riverauction.riverauction.feature.review.ReviewList;
import com.riverauction.riverauction.feature.review.ReviewWriteActivity;
import com.riverauction.riverauction.feature.teacher.TeacherDetailActivity;
import com.riverauction.riverauction.feature.teacher.filter.TeacherFilterActivity;
import com.riverauction.riverauction.feature.tutorial.TutorialActivity;
import com.riverauction.riverauction.injection.module.ActivityModule;

import dagger.Component;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ActivityModule.class
        }
)
public interface ActivityComponent {
        @ActivityContext Context context();
        BaseActivity activity();

        StateCtx stateCtx();

        void inject(IntroActivity activity);
        void inject(SignUpTeacherActivity activity);
        void inject(SignUpTeacherLessonInfoActivity activity);
        void inject(SignUpStudentActivity activity);
        void inject(SignUpStudentLessonInfoActivity activity);
        void inject(MainActivity activity);
        void inject(MakeBiddingActivity activity);
        void inject(LessonDetailActivity activity);
        void inject(TeacherDetailActivity activity);
        void inject(MyLessonDetailActivity activity);
        void inject(MyLessonDetailSelectListActivity activity);
        void inject(NotificationActivity activity);
        void inject(ProfileActivity activity);
        void inject(ProfilePatchActivity activity);
        void inject(PostBiddingActivity activity);
        void inject(SelectSubjectsActivity activity);
        void inject(TeacherFilterActivity activity);
        void inject(SelectPriceRangeActivity activity);
        void inject(SelectDayOfWeeksActivity activity);
        void inject(SelectGenderActivity activity);
        void inject(SelectUniversityActivity activity);
        void inject(SelectLocationActivity activity);
        void inject(SelectStudentStatusActivity activity);
        void inject(EnterPhoneNumberActivity activity);
        void inject(EnterPhoneNumberCodeActivity activity);
        void inject(StudentFilterActivity activity);
        void inject(ProfileStudentBasicInfoPatchActivity activity);
        void inject(ProfileStudentLessonInfoPatchActivity activity);
        void inject(ProfileTeacherBasicInfoPatchActivity activity);
        void inject(ProfileTeacherLessonInfoPatchActivity activity);
        void inject(ShopActivity activity);

        void inject(TutorialActivity activity);
        //by csh
        void inject(ReviewWriteActivity activity);
        void inject(ReviewList activity);

//        void inject(MapView mapView);
}
