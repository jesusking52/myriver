package com.riverauction.riverauction.feature.lesson;

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
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.APISuccessResponse;
import com.riverauction.riverauction.api.service.lesson.params.GetLessonsParams;
import com.riverauction.riverauction.base.BaseFrameLayout;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.eventbus.MakeBiddingEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.StudentFilterEvent;
import com.riverauction.riverauction.feature.lesson.filter.StudentFilterActivity;
import com.riverauction.riverauction.feature.main.MainTabTracker;
import com.riverauction.riverauction.feature.main.MainTabTrackerListener;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;
import com.riverauction.riverauction.widget.recyclerview.DividerUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class LessonView extends BaseFrameLayout implements LessonMvpView, MainTabTrackerListener {

    @Inject LessonPresenter presenter;

    @Bind(R.id.filter_button) View filterButton;
    @Bind(R.id.status_view) StatusView statusView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private GetLessonsParams.Builder builder;
    private List<CLesson> lessons;
    private Integer nextToken;
    // 로그인 한 유저
    private CUser user;

    private LessonAdapter adapter;

    public LessonView(Context context) {
        super(context);
    }

    public LessonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LessonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_lesson;
    }

    @Override
    protected void initialize(Context context) {
        super.initialize(context);
        RiverAuctionApplication.getApplication().getComponent().inject(this);
        user = UserStates.USER.get(stateCtx);
        statusView.setLoadingView(findViewById(R.id.loading_animation_layout));
        statusView.setEmptyView(findViewById(R.id.empty_view));
        statusView.setErrorView(findViewById(R.id.error_view));
        statusView.setResultView(swipeRefreshLayout);

        TextView emptyView = (TextView) statusView.getEmptyView();
        emptyView.setText(R.string.lesson_empty_view);

        builder = createGetLessonsParamsBuilder();

        lessons = Lists.newArrayList();
        adapter = new LessonAdapter(lessons);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = DividerUtils.getHorizontalDividerItemDecoration(context);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StudentFilterActivity.class);
            getContext().startActivity(intent);
        });

        MainTabTracker.registerTabCallback(this, 2, getContext());
        statusView.showLoadingView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        RiverAuctionEventBus.getEventBus().register(this);
        presenter.attachView(this, getContext());
        presenter.getLessons(builder.build());

        // retry API call
        findViewById(R.id.error_retry_button).setOnClickListener(v -> presenter.getLessons(builder.build()));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lessons.clear();
            nextToken = null;
            builder.setNextToken(null);
            presenter.getLessons(builder.build());
        });
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
    public void onEventMainThread(final MakeBiddingEvent event) {
        lessons.clear();
        nextToken = null;
        builder.setNextToken(null);
        presenter.getLessons(builder.build());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final StudentFilterEvent event) {
        builder = event.getBuilder();
        lessons.clear();
        nextToken = null;
        statusView.showLoadingView();
        presenter.getLessons(builder.build());
    }

    @Override
    public void onEnterTab() {
    }

    @Override
    public void onLeaveTab() {
    }

    @Override
    public void successGetLessons(APISuccessResponse<List<CLesson>> response) {
        swipeRefreshLayout.setRefreshing(false);

        List<CLesson> newLessons = response.getResult();
        Integer newNextToken = response.getNextToken();
        if (lessons.size() == 0 && newLessons.size() == 0) {
            statusView.showEmptyView();
            return;
        }
        statusView.showResultView();

        lessons.addAll(newLessons);
        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean failGetLessons(CErrorCause errorCause) {
        if (lessons.size() == 0) {
            statusView.showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(lessons.size());
        }
        return true;
    }

    /**
     * 비어있는 GetLessonsParams.Builder 를 만든다
     */
    private GetLessonsParams.Builder createGetLessonsParamsBuilder() {
        return new GetLessonsParams.Builder();
    }

    /**
     * Recycler View 의 Holder
     */
    public static class LessonItemHolder extends RecyclerView.ViewHolder {

        public LessonItemView lessonItemView;

        public LessonItemHolder(View itemView) {
            super(itemView);
            lessonItemView = (LessonItemView) itemView;
        }
    }

    /**
     * Recycler View 의 Adapter
     */
    private class LessonAdapter extends LoadMoreRecyclerViewAdapter {
        private static final int TYPE_LESSON = 1;

        public LessonAdapter(List<CLesson> lessons) {
            items = lessons;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LESSON) {
                return new LessonItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            final CLesson lesson = (CLesson) items.get(position);
            LessonItemHolder lessonItemHolder = ((LessonItemHolder) holder);
            lessonItemHolder.lessonItemView.setContent(lesson);
            lessonItemHolder.lessonItemView.setOnClickListener(v -> {
                // 선생님일 경우
                Intent intent = new Intent(getContext(), LessonDetailActivity.class);
                intent.putExtra(LessonDetailActivity.EXTRA_LESSON_ID, lesson.getId());
                intent.putExtra(LessonDetailActivity.EXTRA_OWNER_ID, lesson.getOwner().getId());
                getContext().startActivity(intent);
//                if (CUserType.STUDENT == user.getType()) {
//                    // 학생일 경우
//                    Intent intent = new Intent(getContext(), MyLessonDetailActivity.class);
//                    intent.putExtra(MyLessonDetailActivity.EXTRA_LESSON_ID, lesson.getId());
//                    getContext().startActivity(intent);
//                } else {
//                    // 선생님일 경우
//                    Intent intent = new Intent(getContext(), LessonDetailActivity.class);
//                    intent.putExtra(LessonDetailActivity.EXTRA_LESSON_ID, lesson.getId());
//                    intent.putExtra(LessonDetailActivity.EXTRA_OWNER_ID, lesson.getOwner().getId());
//                    getContext().startActivity(intent);
//                }
            });
        }

        @Override
        public int getItemViewItemType(int position) {
            return TYPE_LESSON;
        }

        @Override
        public void loadMore(Integer nextToken) {
            builder.setNextToken(nextToken);
            presenter.getLessons(builder.build());
        }
    }
}
