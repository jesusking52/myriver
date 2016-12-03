package com.riverauction.riverauction.feature.mylesson;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.feature.MoreLoadable;
import com.riverauction.riverauction.feature.lesson.LessonDetailActivity;
import com.riverauction.riverauction.feature.lesson.LessonItemView;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;

import java.util.List;

public abstract class MyLessonActiveTeacherView extends StatusView implements MoreLoadable {
    private OnFindLessonClickListener onFindLessonClickListener;
    private LessonsAdapter adapter;

    private List<CLesson> lessons = Lists.newArrayList();
    private Integer nextToken;
    private StateCtx stateCtx;
    // 로그인한 사용자
    private CUser user;

    public MyLessonActiveTeacherView(Context context) {
        super(context);
        initialize(context);
    }

    public MyLessonActiveTeacherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public MyLessonActiveTeacherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        user = UserStates.USER.get(stateCtx);

        View emptyView = inflater.inflate(R.layout.status_my_lesson_ongoing_empty_view, this, false);
        TextView emptyButton = (TextView) emptyView.findViewById(R.id.my_lesson_active_empty_button);
        emptyButton.setText(R.string.my_lesson_ongoing_empty_teacher_button);
        emptyButton.setOnClickListener(v -> {
            if (onFindLessonClickListener != null) {
                onFindLessonClickListener.findLesson();
            }
        });
        if (user.getType() == CUserType.STUDENT) {
            emptyButton.setVisibility(VISIBLE);
        } else {
            emptyButton.setVisibility(GONE);
        }
        addView(emptyView);
        setEmptyView(emptyView);

        View errorView = inflater.inflate(R.layout.status_error_view, this, false);
        addView(errorView);
        setErrorView(errorView);

        View loadingView = inflater.inflate(R.layout.status_loading_view, this, false);
        addView(loadingView);
        setLoadingView(loadingView);

        View recyclerView = inflater.inflate(R.layout.recycler_view, this, false);
        addView(recyclerView);
        setResultView(recyclerView);

        findViewById(R.id.error_retry_button).setOnClickListener(v -> MyLessonActiveTeacherView.this.loadMore(null));

        adapter = new LessonsAdapter(lessons);
        RecyclerView mRecyclerView = (RecyclerView) getResultView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    public void setContent(List<CLesson> newLessons, Integer newNextToken) {
        if (lessons.size() == 0 && newLessons.size() == 0) {
            showEmptyView();
            return;
        }
        showResultView();

        lessons.addAll(newLessons);
        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    public void setError() {
        if (lessons.size() == 0) {
            showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(lessons.size());
        }
    }

    public void setLoading() {
        if (lessons.size() == 0) {
            showLoadingView();
        }
    }

    public void clear() {
        lessons.clear();
        nextToken = null;
    }

    public void setOnFindLessonClickListener(OnFindLessonClickListener onFindLessonClickListener) {
        this.onFindLessonClickListener = onFindLessonClickListener;
    }

    public static class LessonHolder extends RecyclerView.ViewHolder {
        public LessonItemView lessonItemView;

        public LessonHolder(View itemView) {
            super(itemView);
            lessonItemView = (LessonItemView) itemView;
        }
    }

    private class LessonsAdapter extends LoadMoreRecyclerViewAdapter {
        private static final int TYPE_ITEM = 1;

        public LessonsAdapter(List<CLesson> lessons) {
            Preconditions.checkNotNull(lessons);
            this.items = lessons;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                return new LessonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            CLesson lesson = lessons.get(position);
            LessonHolder lessonHolder = ((LessonHolder) holder);
            lessonHolder.lessonItemView.setContent(lesson);
            lessonHolder.lessonItemView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), LessonDetailActivity.class);
                intent.putExtra(LessonDetailActivity.EXTRA_LESSON_ID, lesson.getId());
                intent.putExtra(LessonDetailActivity.EXTRA_OWNER_ID, lesson.getOwner().getId());
                getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemViewItemType(int position) {
            return TYPE_ITEM;
        }

        @Override
        public void loadMore(Integer nextToken) {
            MyLessonActiveTeacherView.this.loadMore(nextToken);
        }
    }

    public interface OnFindLessonClickListener {
        void findLesson();
    }
}
