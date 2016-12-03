package com.riverauction.riverauction.feature.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.LaunchActivity;
import com.riverauction.riverauction.feature.lesson.LessonView;
import com.riverauction.riverauction.feature.mylesson.MyLessonView;
import com.riverauction.riverauction.feature.notification.NotificationActivity;
import com.riverauction.riverauction.feature.profile.ProfileActivity;
import com.riverauction.riverauction.feature.teacher.TeacherView;
import com.riverauction.riverauction.feature.tutorial.TutorialActivity;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.slidingtab.SlidingTabLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements MainMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.main.MainActivity.";
    /**
     * true 이면 app 을 업데이트 하라는 팝업 다이얼로그가 뜬다.
     */
    public static final String EXTRA_SHOULD_APP_UPDATE = EXTRA_PREFIX + "extra_should_app_update";

    public static final String EXTRA_ACTIVITY_REDIRECT = EXTRA_PREFIX + "extra_activity_redirect";
    public static final int EXTRA_ACTIVITY_REDIRECT_HOME = 0x01;
    public static final String EXTRA_ACTIVITY_REDIRECT_NOTIFICATION = EXTRA_PREFIX + "EXTRA_ACTIVITY_REDIRECT_NOTIFICATION";

    /**
     * MainActivity 위에 모든 Activity 를 finish 시키고, MainActivity 를 finish 하고 LaunchActivity 를 실행시키는 코드
     */
    private static final int EXTRA_ACTIVITY_REDIRECT_RESTART = 0xFF;

    @Inject MainPresenter presenter;

    @Bind(R.id.main_view_pager) ViewPager viewPager;
    @Bind(R.id.main_sliding_tabs) SlidingTabLayout slidingTabLayout;

    private List<OnBackPressListener> onBackPressListeners = Lists.newCopyOnWriteArrayList();

    private MainPagerAdapter adapter;
    // Sliding Tabs 을 표현하는 tabPagerItems 의 List.
    private List<MainTabPagerItem> tabPagerItems = Lists.newArrayList();

    private CUser me;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        SpannableString ss = new SpannableString("abc");
        Drawable d = getResources().getDrawable(R.drawable.logo_part);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(ss);
        me = UserStates.USER.get(stateCtx);
        boolean tutorialShown = UserStates.TUTORIAL_SHOWN.get(stateCtx);
        if (!tutorialShown) {
            // tutorial 이 보인적 없다면 tutorial 을 보여준다
            Intent intent = new Intent(context, TutorialActivity.class);
            startActivity(intent);
            UserStates.TUTORIAL_SHOWN.set(stateCtx, true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int redirectCode = extras.getInt(EXTRA_ACTIVITY_REDIRECT);
            if (handleExtraRedirectCode(redirectCode)) {
                return;
            }
        }

        makeTabPagerItems();
        makeViewPagerSlidingTabLayout();

        redirectIntent(getIntent());

        if (me.getType() == CUserType.STUDENT) {
            // "선생님 목록" default
            viewPager.setCurrentItem(0);
        } else {
            // "경매목록" default
            viewPager.setCurrentItem(2);
        }

        // 서버에서 변경될수 있으니 수시로 가져온다
        presenter.getSubjectGroups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            Intent intent = new Intent(context, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_notification) {
            Intent intent = new Intent(context, NotificationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int redirectCode = extras.getInt(EXTRA_ACTIVITY_REDIRECT);
            if (handleExtraRedirectCode(redirectCode)) {
                return;
            }
        }

        redirectIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        for (OnBackPressListener onBackPressListener : onBackPressListeners) {
            if (onBackPressListener.onBackPressed()) {
                return;
            }
        }

        super.onBackPressed();
    }

    private void addOnBackPressListener(OnBackPressListener onBackPressListener) {
        onBackPressListeners.add(onBackPressListener);
    }

    private void redirectIntent(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }

        boolean shouldGoNotification = extras.getBoolean(EXTRA_ACTIVITY_REDIRECT_NOTIFICATION, false);
        if (!shouldGoNotification) {
            return;
        } else {
            Intent notificationIntent = new Intent(this, NotificationActivity.class);
            startActivity(notificationIntent);
        }

        // TODO: scheme
//        StartActivityRequest redirectRequest = extras.getParcelable(EXTRA_ACTIVITY_REQUEST);
//        if (redirectRequest != null) {
//            Intent outIntent = StartActivityRequest.createMainActivityInStackActivity(this, redirectRequest);
//            startActivity(outIntent);
//            return;
//        }
//
//        int redirectCode = extras.getInt(EXTRA_ACTIVITY_REDIRECT, EXTRA_ACTIVITY_REDIRECT_HOME);
//
//        if (redirectCode > 0) {
//            switch (redirectCode){
//                case EXTRA_ACTIVITY_REDIRECT_HOME:
//                    mViewPager.setCurrentItem(INDEX_TAB_HOME);
//                    break;
//                case EXTRA_ACTIVITY_REDIRECT_PLACE:
//                    mViewPager.setCurrentItem(INDEX_TAB_NEARBY);
//                    if (nearbyView != null) {
//                        nearbyView.refreshIntent();
//                    }
//                    break;
//                case EXTRA_ACTIVITY_REDIRECT_THEME:
//                    mViewPager.setCurrentItem(INDEX_TAB_THEME);
//                    break;
//                case EXTRA_ACTIVITY_REDIRECT_PROFILE:
//                    mViewPager.setCurrentItem(INDEX_TAB_PROFILE);
//                    break;
//                default:
//            }
//            return;
//        }
    }

    private void makeTabPagerItems() {
        tabPagerItems.add(new MainTabPagerItem(R.string.main_tab_teacher, 0));
        tabPagerItems.add(new MainTabPagerItem(R.string.main_tab_my_lesson, 1));
        tabPagerItems.add(new MainTabPagerItem(R.string.main_tab_lesson, 2));
    }

    private void makeViewPagerSlidingTabLayout() {
        adapter = new MainPagerAdapter();
        viewPager.setAdapter(adapter);
        // 미리 모두 로딩해놓으면 view pager 로 이동할 때 새로 view 를 만들지 않기 때문에 버벅이지 않는다.
        viewPager.setOffscreenPageLimit(5);

        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setDividerColors(android.R.color.transparent);
        slidingTabLayout.setTabCustomViewProvider(adapter);
        slidingTabLayout.setTabCustomLayoutParamsProvider(adapter);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                MainTabTracker.notifyTabSelected(position, MainActivity.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        slidingTabLayout.setViewPager(viewPager);
    }

    /**
     * MainActivity 가 표시되고 있을 때, {@link #createRestartIntent(android.content.Context)} 를 실행시키면 onNewIntent -> onPause -> onResume 이 순서대로 실행된다.
     * @param context Context
     */
    public static void restartApplication(Context context) {
        context.startActivity(createRestartIntent(context));
    }

    /**
     * Stack 가장 아래에 존재하는 {@link MainActivity} 를 {@link android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP} Flag 로 이동하는 Intent 를 생성한다.
     * 따라서 Stack 에 있는 모든 Activity 는 종료되고 MainActivity 가 호출된다. MainActivity 에서는 {@link #finish()} 하면서 {@link LaunchActivity} 를 실행시킨다.
     */
    private static Intent createRestartIntent(Context context) {
        return new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                .putExtra(EXTRA_ACTIVITY_REDIRECT, EXTRA_ACTIVITY_REDIRECT_RESTART);
    }

    private boolean handleExtraRedirectCode(int code) {
        switch (code) {
            case EXTRA_ACTIVITY_REDIRECT_RESTART : {
                finish();
                LaunchActivity.startLaunchActivity(context);
                return true;
            }
        }

        return false;
    }

    @Override
    public void successSignOut(Boolean result) {
        MainActivity.restartApplication(context);
        // TODO:
    }

    @Override
    public boolean failSignOut(CErrorCause errorCause) {
        // TODO:
        return false;
    }

    /**
     * View Pager 의 Adapter
     */
    private class MainPagerAdapter extends PagerAdapter implements SlidingTabLayout.TabCustomViewProvider, SlidingTabLayout.TabCustomLayoutParamsProvider {

        private TeacherView teacherView;
        private LessonView lessonView;

        @Override
        public int getCount() {
            return tabPagerItems.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0: {
                    teacherView = new TeacherView(MainActivity.this);
                    view = teacherView;
                    break;
                }
                case 1: {
                    view = new MyLessonView(MainActivity.this);
                    break;
                }
                case 2: {
                    lessonView = new LessonView(MainActivity.this);
                    view = lessonView;
                    break;
                }
            }

            if (view != null) {
                container.addView(view);
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public View getTabCustomView(int position) {
            return tabPagerItems.get(position).createTabView(MainActivity.this);
        }

        @Override
        public ViewGroup.LayoutParams getLayoutParams() {
            int tabViewWidth = DisplayUtils.getDisplayWidthRaw(MainActivity.this) / getCount();
            return new ViewGroup.LayoutParams(tabViewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * {@link MainActivity#onBackPressed()} 가 눌렸을 때 발생하는 콜백
     */
    public interface OnBackPressListener {
        boolean onBackPressed();
    }
}
