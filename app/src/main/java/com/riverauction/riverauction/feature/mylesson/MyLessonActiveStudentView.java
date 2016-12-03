package com.riverauction.riverauction.feature.mylesson;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.preference.StateCtx;
import com.jhcompany.android.libs.utils.Lists2;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CLessonStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.feature.MyLessonActiveStudentLoadable;
import com.riverauction.riverauction.feature.lesson.LessonDetailActivity;
import com.riverauction.riverauction.feature.teacher.TeacherDetailActivity;
import com.riverauction.riverauction.feature.teacher.TeacherItemView;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class MyLessonActiveStudentView extends FrameLayout implements MyLessonActiveStudentLoadable {
    @Bind(R.id.my_lesson_active_student_status_view) StatusView statusView;

    private BiddingAdapter adapter;
    private OnMyLessonButtonClickListener onMyLessonButtonClickListener;
    // data
    private CLesson lesson;
    private int biddingCount;
    private List<CLessonBidding> biddings;
    private Integer nextToken;

    private StateCtx stateCtx;
    // 로그인한 사용자
    private CUser user;

    public MyLessonActiveStudentView(Context context) {
        super(context);
        initialize(context);
    }

    public MyLessonActiveStudentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public MyLessonActiveStudentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        user = UserStates.USER.get(stateCtx);
        biddings = Lists.newArrayList();

        inflater.inflate(R.layout.layout_my_lesson_active_student, this, true);
        ButterKnife.bind(this);

        statusView.setLoadingView(findViewById(R.id.loading_animation_layout));
        statusView.setEmptyView(findViewById(R.id.empty_view));
        statusView.setErrorView(findViewById(R.id.error_view));
        statusView.setResultView(findViewById(R.id.recycler_view));

        TextView emptyButton = (TextView) findViewById(R.id.my_lesson_active_empty_button);
        emptyButton.setText(R.string.my_lesson_ongoing_empty_student_button);
        emptyButton.setOnClickListener(v -> {
            if (onMyLessonButtonClickListener != null) {
                onMyLessonButtonClickListener.makeLesson();
            }
        });
        if (user.getType() == CUserType.STUDENT) {
            emptyButton.setVisibility(VISIBLE);
        } else {
            emptyButton.setVisibility(GONE);
        }

        findViewById(R.id.error_retry_button).setOnClickListener(v -> {
            setLoading();
            MyLessonActiveStudentView.this.firstLoad(user.getId());
        });

        adapter = new BiddingAdapter(biddings);
        RecyclerView recyclerView = (RecyclerView) statusView.getResultView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        setLoading();
    }

    public void setContent(CLesson lesson, List<CLessonBidding> newBiddings, Integer newNextToken, int totalCount) {
        this.lesson = lesson;
        this.biddingCount = totalCount;
        if (lesson == null) {
            statusView.showEmptyView();
        } else {
            statusView.showResultView();
        }
        setContent(newBiddings, newNextToken);
    }

    public void setContent(List<CLessonBidding> newBiddings, Integer newNextToken) {
        if (biddings.size() == 0 && Lists2.isNullOrEmpty(newBiddings)) {
            adapter.notifyDataSetChanged();
            return;
        }

        if (Lists2.isNullOrEmpty(newBiddings)) {
            biddings.clear();
        } else {
            biddings.addAll(newBiddings);
        }

        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    public void setError() {
        if (lesson == null) {
            statusView.showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(biddings.size() + 1);
        }
    }

    public void setLoading() {
        if (lesson == null) {
            statusView.showLoadingView();
        }
    }

    public void clear() {
        biddings.clear();
        lesson = null;
        nextToken = null;
    }

    public void setOnMyLessonButtonClickListener(OnMyLessonButtonClickListener onMyLessonButtonClickListener) {
        this.onMyLessonButtonClickListener = onMyLessonButtonClickListener;
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public View biddingStatusView;
        public View dealingStatusView;
        public View canceledStatusView;
        public View finishedStatusView;
        public TextView remainTimeView;
        public View goToLessonDetailButton;
        public TextView biddingCountTextView;

        public HeaderHolder(View itemView) {
            super(itemView);
            biddingStatusView = itemView.findViewById(R.id.lesson_detail_lesson_status_bidding);
            dealingStatusView = itemView.findViewById(R.id.lesson_detail_lesson_status_dealing);
            canceledStatusView = itemView.findViewById(R.id.lesson_detail_lesson_status_canceled);
            finishedStatusView = itemView.findViewById(R.id.lesson_detail_lesson_status_finished);
            remainTimeView = (TextView) itemView.findViewById(R.id.lesson_detail_remain_time_view);
            goToLessonDetailButton = itemView.findViewById(R.id.go_to_lesson_detail_button);
            biddingCountTextView = (TextView) itemView.findViewById(R.id.active_lesson_biddings_count_view);
        }
    }

    public static class BiddingItemHolder extends RecyclerView.ViewHolder {
        public TeacherItemView teacherItemView;

        public BiddingItemHolder (View itemView) {
            super(itemView);
            teacherItemView = (TeacherItemView) itemView;
        }
    }

    /**
     * Recycler View 의 Adapter
     */
    private class BiddingAdapter extends LoadMoreRecyclerViewAdapter {
        private static final int TYPE_HEADER = 1;
        private static final int TYPE_BIDDING = 2;

        public BiddingAdapter(List<CLessonBidding> biddings) {
            items = biddings;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_active_lesson_header, parent, false));
            } else if (viewType == TYPE_BIDDING) {
                return new BiddingItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            if (TYPE_HEADER == getItemViewType(position)) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.biddingStatusView.setVisibility(View.GONE);
                headerHolder.dealingStatusView.setVisibility(View.GONE);
                headerHolder.canceledStatusView.setVisibility(View.GONE);
                headerHolder.finishedStatusView.setVisibility(View.GONE);
                switch (lesson.getStatus()) {
                    case BIDDING: {
                        headerHolder.biddingStatusView.setVisibility(View.VISIBLE);
                        headerHolder.remainTimeView.setText(DataUtils.convertRemainTimeToString(getContext(), lesson.getExpiresIn()));
                        break;
                    }
                    case DEALING: {
                        headerHolder.dealingStatusView.setVisibility(View.VISIBLE);
                        headerHolder.remainTimeView.setText(DataUtils.convertRemainTimeToString(getContext(), lesson.getExpiresIn()));
                        break;
                    }
                    case CANCELED: {
                        headerHolder.canceledStatusView.setVisibility(View.VISIBLE);
                        break;
                    }
                    case FINISHED: {
                        headerHolder.finishedStatusView.setVisibility(View.VISIBLE);
                        break;
                    }
                }

                headerHolder.goToLessonDetailButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), LessonDetailActivity.class);
                    intent.putExtra(LessonDetailActivity.EXTRA_LESSON_ID, lesson.getId());
                    intent.putExtra(LessonDetailActivity.EXTRA_OWNER_ID, lesson.getOwner().getId());
                    getContext().startActivity(intent);
                });

                headerHolder.biddingCountTextView.setText(getContext().getString(R.string.my_lesson_active_bidding_count, biddingCount));
            } else if (TYPE_BIDDING == getItemViewType(position)) {
                final CLessonBidding bidding = (CLessonBidding) items.get(position - 1);
                BiddingItemHolder biddingItemHolder = ((BiddingItemHolder) holder);
                biddingItemHolder.teacherItemView.setContent(bidding);
                biddingItemHolder.teacherItemView.setOnClickListener(v -> {
                    if (lesson.getStatus() == CLessonStatus.BIDDING) {
                        Toast.makeText(getContext(), R.string.my_lesson_bidding_status_toast, Toast.LENGTH_SHORT).show();
                    } else if (lesson.getStatus() == CLessonStatus.DEALING) {
                        Intent intent = new Intent(getContext(), TeacherDetailActivity.class);
                        intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, bidding.getUser().getId());
                        intent.putExtra(TeacherDetailActivity.EXTRA_BIDDING_PRICE, bidding.getPrice());
                        intent.putExtra(TeacherDetailActivity.EXTRA_BOOLEAN_SELECT_TEACHER, true);
                        intent.putExtra(TeacherDetailActivity.EXTRA_LESSON_ID, lesson.getId());
                        getContext().startActivity(intent);
                    }
                });
            }
        }

        @Override
        public boolean isExistHeader() {
            return true;
        }

        @Override
        public int getItemViewItemType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                return TYPE_BIDDING;
            }
        }

        @Override
        public void loadMore(Integer nextToken) {
            MyLessonActiveStudentView.this.loadMore(lesson.getId(), nextToken);
        }
    }

    public interface OnMyLessonButtonClickListener {
        void makeLesson();
        void cancelLesson(CLesson lesson);
    }
}
