package com.riverauction.riverauction.feature.teacher;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;
import com.riverauction.riverauction.base.BaseFrameLayout;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.TeacherFilterEvent;
import com.riverauction.riverauction.feature.main.MainTabTracker;
import com.riverauction.riverauction.feature.main.MainTabTrackerListener;
import com.riverauction.riverauction.feature.teacher.filter.TeacherFilterActivity;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class TeacherView extends BaseFrameLayout implements TeacherMvpView, MainTabTrackerListener {

    @Inject TeacherPresenter presenter;

    @Bind(R.id.filter_button) View filterButton;
    @Bind(R.id.status_view) StatusView statusView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private GetTeachersParams.Builder builder;
    private List<CUser> teachers;
    private Integer nextToken;

    private TeacherAdapter adapter;
    private CUser user;

    public TeacherView(Context context) {
        super(context);
    }

    public TeacherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeacherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_teacher;
    }

    @Override
    protected void initialize(Context context) {
        super.initialize(context);
        RiverAuctionApplication.getApplication().getComponent().inject(this);

        statusView.setLoadingView(findViewById(R.id.loading_animation_layout));
        statusView.setEmptyView(findViewById(R.id.empty_view));
        statusView.setErrorView(findViewById(R.id.error_view));
        statusView.setResultView(swipeRefreshLayout);

        TextView emptyView = (TextView) statusView.getEmptyView();
        emptyView.setText(R.string.teacher_empty_view);

        builder = createGetTeachersParamsBuilder();

        teachers = Lists.newArrayList();
        adapter = new TeacherAdapter(teachers);
        user = UserStates.USER.get(stateCtx);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = DividerUtils.getHorizontalDividerItemDecoration(context);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TeacherFilterActivity.class);
            getContext().startActivity(intent);
        });

        MainTabTracker.registerTabCallback(this, 0, getContext());
        statusView.showLoadingView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        RiverAuctionEventBus.getEventBus().register(this);
        presenter.attachView(this, getContext());
        presenter.getTeachers(user.getId(), builder.build());

        // retry API call
        findViewById(R.id.error_retry_button).setOnClickListener(v -> presenter.getTeachers(user.getId(), builder.build()));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            teachers.clear();
            nextToken = null;
            builder.setNextToken(null);
            presenter.getTeachers(user.getId(), builder.build());
        });
        MainTabTracker.registerTabCallback(this, 0, getContext());
        statusView.showLoadingView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.detachView();
        if (RiverAuctionEventBus.getEventBus().isRegistered(this)) {
            RiverAuctionEventBus.getEventBus().unregister(this);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final TeacherFilterEvent event) {
        builder = event.getBuilder();
        teachers.clear();
        nextToken = null;
        statusView.showLoadingView();
        presenter.getTeachers(user.getId(), builder.build());
    }

    @Override
    public void successGetTeachers(APISuccessResponse<List<CUser>> response) {
        swipeRefreshLayout.setRefreshing(false);

        List<CUser> newTeachers = response.getResult();
        Integer newNextToken = response.getNextToken();
        if (teachers.size() == 0 && newTeachers.size() == 0) {
            statusView.showEmptyView();
            return;
        }
        statusView.showResultView();

        teachers.addAll(newTeachers);
        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean failGetTeachers(CErrorCause errorCause) {
        if (teachers.size() == 0) {
            statusView.showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(teachers.size());
        }
        return false;
    }

    @Override
    public void onEnterTab() {

    }

    @Override
    public void onLeaveTab() {

    }

    /**
     * 비어있는 GetTeachersParams.Builder 를 만든다
     */
    private GetTeachersParams.Builder createGetTeachersParamsBuilder() {
        return new GetTeachersParams.Builder();
    }

    /**
     * Recycler View 의 Holder
     */
    public static class TeacherItemHolder extends RecyclerView.ViewHolder {

        public TeacherItemView teacherItemView;

        public TeacherItemHolder (View itemView) {
            super(itemView);
            teacherItemView = (TeacherItemView) itemView;
        }
    }

    /**
     * Recycler View 의 Adapter
     */
    private class TeacherAdapter extends LoadMoreRecyclerViewAdapter {
        private static final int TYPE_TEACHER = 1;

        public TeacherAdapter(List<CUser> teachers) {
            items = teachers;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_TEACHER) {
                return new TeacherItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            final CUser user = (CUser) items.get(position);
            TeacherItemHolder teacherItemHolder = ((TeacherItemHolder) holder);
            teacherItemHolder.teacherItemView.setContent(user);
            teacherItemHolder.teacherItemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TeacherDetailActivity.class);
                intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, user.getId());
                getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemViewItemType(int position) {
            return TYPE_TEACHER;
        }

        @Override
        public void loadMore(Integer nextToken) {
            builder.setNextToken(nextToken);
            presenter.getTeachers(user.getId(), builder.build());
        }
    }
}
