package com.riverauction.riverauction.feature.notification;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CNotification;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.MakeBiddingEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.feature.main.MainTabTracker;
import com.riverauction.riverauction.feature.mylesson.MyLessonTabPagerItem;
import com.riverauction.riverauction.feature.notification.favorite.FavoriteStudentView;
import com.riverauction.riverauction.feature.notification.favorite.FavoriteTeacherView;
import com.riverauction.riverauction.feature.notification.message.MessageView;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.slidingtab.SlidingTabLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class NotificationActivity extends BaseActivity implements NotificationMvpView {

    @Inject NotificationPresenter presenter;

    @Bind(R.id.notification_sliding_tabs) SlidingTabLayout slidingTabLayout;
    @Bind(R.id.notification_view_pager) ViewPager viewPager;

    private NotificationAdapter adapter;

    // 로그인 한 유저
    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RiverAuctionEventBus.getEventBus().register(this);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        user = UserStates.USER.get(stateCtx);

        getSupportActionBar().setTitle(R.string.notification_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        makeViewPagerSlidingTabLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (RiverAuctionEventBus.getEventBus().isRegistered(this)) {
            RiverAuctionEventBus.getEventBus().unregister(this);
        }
    }

    private void makeViewPagerSlidingTabLayout() {
        adapter = new NotificationAdapter(makeTabPagerItems());
        viewPager.setAdapter(adapter);

        slidingTabLayout.setSelectedIndicatorColors(android.R.color.transparent);
        slidingTabLayout.setDividerColors(android.R.color.transparent);
        slidingTabLayout.setTabCustomViewProvider(adapter);
        slidingTabLayout.setTabCustomLayoutParamsProvider(adapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    private List<MyLessonTabPagerItem> makeTabPagerItems() {
        List<MyLessonTabPagerItem> tabPagerItems = Lists.newArrayList();
        tabPagerItems.add(new MyLessonTabPagerItem(R.string.notification_tab_message, 0));
        tabPagerItems.add(new MyLessonTabPagerItem(R.string.notification_tab_favorite, 1));
        return tabPagerItems;
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final MakeBiddingEvent event) {
        if (CUserType.STUDENT == user.getType()) {
            adapter.setStudentFavoriteViewLoading();
            presenter.getUserFavorites(user.getId(), null);
        } else {
            adapter.setTeacherFavoriteViewLoading();
            presenter.getLessonFavorites(user.getId(), null);
        }
    }

    @Override
    public void successGetNotifications(List<CNotification> newNotifications, Integer newNextToken) {
        adapter.setMessageViewResult(newNotifications, newNextToken);
    }

    @Override
    public boolean failGetNotifications(CErrorCause errorCause) {
        adapter.setMessageViewError();
        return false;
    }

    @Override
    public void successGetUserFavorites(List<CUserFavorite> newFavorite, Integer newNextToken) {
        adapter.setStudentFavoriteViewResult(newFavorite, newNextToken);
    }

    @Override
    public boolean failGetUserFavorites(CErrorCause errorCause) {
        adapter.setStudentFavoriteViewError();
        return false;
    }

    @Override
    public void successGetLessonFavorites(List<CLessonFavorite> newFavorite, Integer newNextToken) {
        adapter.setTeacherFavoriteViewResult(newFavorite, newNextToken);
    }

    @Override
    public boolean failGetLessonFavorites(CErrorCause errorCause) {
        adapter.setTeacherFavoriteViewError();
        return false;
    }

    /**
     * View Pager 의 Adapter
     */
    private class NotificationAdapter extends PagerAdapter implements SlidingTabLayout.TabCustomViewProvider, SlidingTabLayout.TabCustomLayoutParamsProvider {

        private MessageView messageView;
        private FavoriteStudentView favoriteStudentView;
        private FavoriteTeacherView favoriteTeacherView;
        private List<MyLessonTabPagerItem> tabPagerItems = Lists.newArrayList();

        public NotificationAdapter(List<MyLessonTabPagerItem> tabPagerItems) {
            this.tabPagerItems = tabPagerItems;
        }

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
                    messageView = new MessageView(NotificationActivity.this) {
                        @Override
                        public void loadMore(Integer nextToken) {
                            presenter.getNotifications(user.getId(), nextToken);
                        }
                    };
                    messageView.setLoading();
                    presenter.getNotifications(user.getId(), null);
                    view = messageView;
                    break;

                }
                case 1: {
                    if (CUserType.STUDENT == user.getType()) {
                        favoriteStudentView = new FavoriteStudentView(NotificationActivity.this) {
                            @Override
                            public void loadMore(Integer nextToken) {
                                presenter.getUserFavorites(user.getId(), nextToken);
                            }
                        };
                        presenter.getUserFavorites(user.getId(), null);
                        favoriteStudentView.setLoading();

                        view = favoriteStudentView;
                    } else {
                        favoriteTeacherView = new FavoriteTeacherView(NotificationActivity.this) {
                            @Override
                            public void loadMore(Integer nextToken) {
                                presenter.getLessonFavorites(user.getId(), nextToken);
                            }
                        };
                        favoriteTeacherView.setLoading();
                        presenter.getLessonFavorites(user.getId(), null);
                        view = favoriteTeacherView;
                    }
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
            return tabPagerItems.get(position).createTabView(context);
        }

        @Override
        public ViewGroup.LayoutParams getLayoutParams() {
            int tabViewWidth = DisplayUtils.getDisplayWidthRaw(context) / getCount() - DisplayUtils.getPixelFromDP(context, 6);
            return new ViewGroup.LayoutParams(tabViewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        // message
        public void setMessageViewLoading() {
            messageView.setLoading();
        }
        public void setMessageViewResult(List<CNotification> notifications, Integer nextToken) {
            messageView.setContent(notifications, nextToken);
        }
        public void setMessageViewError() {
            messageView.setError();
        }

        // notification - student
        public void setStudentFavoriteViewLoading() {
            favoriteStudentView.setLoading();
        }
        public void setStudentFavoriteViewResult(List<CUserFavorite> favorites, Integer nextToken) {
            favoriteStudentView.setContent(favorites, nextToken);
        }
        public void setStudentFavoriteViewError() {
            favoriteStudentView.setError();
        }

        // notification - teacher
        public void setTeacherFavoriteViewLoading() {
            favoriteTeacherView.setLoading();
        }
        public void setTeacherFavoriteViewResult(List<CLessonFavorite> favorites, Integer nextToken) {
            favoriteTeacherView.setContent(favorites, nextToken);
        }
        public void setTeacherFavoriteViewError() {
            favoriteTeacherView.setError();
        }
    }
}
