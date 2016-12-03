package com.riverauction.riverauction.feature.mylesson.detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CLessonStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.common.LoadMoreRecyclerViewAdapter;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.SelectTeacherEvent;
import com.riverauction.riverauction.feature.teacher.TeacherDetailActivity;
import com.riverauction.riverauction.feature.teacher.TeacherItemView;
import com.riverauction.riverauction.inapppurchase.util.InAppPurchaseUtils;
import com.riverauction.riverauction.widget.StatusView;
import com.riverauction.riverauction.widget.recyclerview.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class MyLessonDetailSelectListActivity extends BaseActivity implements MyLessonDetailSelectListMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.mylesson.detail.MyLessonDetailSelectListActivity.";
    public static final String EXTRA_LESSON = EXTRA_PREFIX + "extra_lesson";

    @Inject MyLessonDetailSelectListPresenter presenter;

    @Bind(R.id.my_lesson_detail_select_list_status_view) StatusView statusView;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private BiddingAdapter adapter;

    // data
    private List<CLessonBidding> biddings;
    private Integer nextToken;

    // from bundle
    private CLesson lesson;
    // 로그인한 사용자
    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_my_lesson_detail_select_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromBundle(getIntent().getExtras());

        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        RiverAuctionEventBus.getEventBus().register(this);
        getSupportActionBar().setTitle(R.string.lesson_detail_bidding_count);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        biddings = Lists.newArrayList();
        adapter = new BiddingAdapter(biddings);

        statusView.setLoadingView(findViewById(R.id.loading_animation_layout));
        statusView.setEmptyView(findViewById(R.id.empty_view));
        statusView.setErrorView(findViewById(R.id.error_view));
        statusView.setResultView(recyclerView);

        TextView emptyView = (TextView) statusView.getEmptyView();
        emptyView.setText(R.string.my_lesson_detail_select_empty_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // retry API call
        findViewById(R.id.error_retry_button).setOnClickListener(v -> {
            statusView.showLoadingView();
            presenter.getLessonBiddings(lesson.getId(), null);
        });

        statusView.showLoadingView();
        presenter.getLessonBiddings(lesson.getId(), null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (RiverAuctionEventBus.getEventBus().isRegistered(this)) {
            RiverAuctionEventBus.getEventBus().unregister(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            Parcelable parcelable = bundle.getParcelable(EXTRA_LESSON);
            if (parcelable == null) {
                throw new IllegalStateException("lesson must be exist");
            } else {
                lesson = ParcelableWrappers.unwrap(parcelable);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final SelectTeacherEvent event) {
        // 선생님이 선택됨
        lesson.setStatus(CLessonStatus.FINISHED);
    }

    @Override
    public void successGetLessonBiddings(List<CLessonBidding> newBiddings, Integer newNextToken) {
        if (biddings.size() == 0 && newBiddings.size() == 0) {
            statusView.showEmptyView();
            return;
        }
        statusView.showResultView();

        biddings.addAll(newBiddings);
        nextToken = newNextToken;

        adapter.setNextToken(nextToken);
        adapter.setErrorLoadMore(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean failGetLessonBiddings(CErrorCause errorCause) {
        if (biddings.size() == 0) {
            statusView.showErrorView();
        } else {
            // 더 불러오기 실패
            adapter.setErrorLoadMore(true);
            adapter.notifyItemChanged(biddings.size());
        }
        return false;
    }

    @Override
    public void successPostSelectTeacher(CUser user) {
        // 선생님 선택 성공
        Toast.makeText(context, R.string.lesson_bidding_select_teacher_success_toast, Toast.LENGTH_SHORT).show();
        RiverAuctionEventBus.getEventBus().post(new SelectTeacherEvent());
        lesson.setStatus(CLessonStatus.FINISHED);
    }

    @Override
    public boolean failPostSelectTeacher(CErrorCause errorCause) {
        if (CErrorCause.INSUFFICIENT_PERMISSION == errorCause || CErrorCause.INSUFFICIENT_COINS == errorCause) {
            InAppPurchaseUtils.showInsufficientCoinDialog(context);
            return true;
        } else if (CErrorCause.EXPIRED_LESSON_DEALING == errorCause) {
            Toast.makeText(context, R.string.lesson_bidding_select_fail_toast, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Recycler View 의 Holder
     */
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
        private static final int TYPE_BIDDING = 1;

        public BiddingAdapter(List<CLessonBidding> biddings) {
            items = biddings;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewItemHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_BIDDING) {
                return new BiddingItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_list, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewItemHolder(RecyclerView.ViewHolder holder, int position) {
            final CLessonBidding bidding = (CLessonBidding) items.get(position);
            BiddingItemHolder biddingItemHolder = ((BiddingItemHolder) holder);
            biddingItemHolder.teacherItemView.setContent(bidding);
            biddingItemHolder.teacherItemView.setOnClickListener(v -> {
                if (lesson.getStatus() == CLessonStatus.BIDDING) {
                  Toast.makeText(context, R.string.my_lesson_bidding_status_toast, Toast.LENGTH_SHORT).show();
                } else if (lesson.getStatus() == CLessonStatus.DEALING) {
                    // 선생님 상세보기
                    Intent intent = new Intent(context, TeacherDetailActivity.class);
                    intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, bidding.getUser().getId());
                    intent.putExtra(TeacherDetailActivity.EXTRA_BIDDING_PRICE, bidding.getPrice());
                    intent.putExtra(TeacherDetailActivity.EXTRA_BOOLEAN_SELECT_TEACHER, true);
                    intent.putExtra(TeacherDetailActivity.EXTRA_LESSON_ID, lesson.getId());
                    startActivity(intent);
                } else if (lesson.getStatus() == CLessonStatus.FINISHED) {
                    // 선생님 상세보기
                    Intent intent = new Intent(context, TeacherDetailActivity.class);
                    intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, bidding.getUser().getId());
                    intent.putExtra(TeacherDetailActivity.EXTRA_BIDDING_PRICE, bidding.getPrice());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemViewItemType(int position) {
            return TYPE_BIDDING;
        }

        @Override
        public void loadMore(Integer nextToken) {
            presenter.getLessonBiddings(lesson.getId(), nextToken);
        }
    }
}
