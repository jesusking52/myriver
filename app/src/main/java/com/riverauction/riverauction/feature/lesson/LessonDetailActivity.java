package com.riverauction.riverauction.feature.lesson;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonFavorite;
import com.riverauction.riverauction.api.model.CLessonStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.CancelEvent;
import com.riverauction.riverauction.eventbus.FavoriteChangedEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.SelectTeacherEvent;
import com.riverauction.riverauction.feature.common.BasicInfoView;
import com.riverauction.riverauction.feature.common.LessonInfoView;
import com.riverauction.riverauction.feature.lesson.bidding.PostBiddingActivity;
import com.riverauction.riverauction.feature.mylesson.detail.MyLessonDetailSelectListActivity;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.states.UserStates;

import javax.inject.Inject;

import butterknife.Bind;

public class LessonDetailActivity extends BaseActivity implements LessonDetailMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.lesson.LessonDetailActivity.";
    public static final String EXTRA_LESSON_ID = EXTRA_PREFIX + "extra_lesson_id";
    public static final String EXTRA_OWNER_ID = EXTRA_PREFIX + "extra_owner_id";

    private static final int REQUEST_POST_BIDDING = 0x01;

    @Inject LessonDetailPresenter presenter;

    @Bind(R.id.lesson_detail_lesson_status_bidding) View biddingStatusView;
    @Bind(R.id.lesson_detail_lesson_status_dealing) View dealingStatusView;
    @Bind(R.id.lesson_detail_lesson_status_canceled) View canceledStatusView;
    @Bind(R.id.lesson_detail_lesson_status_canceled_created_at) TextView canceledCreatedAtView;
    @Bind(R.id.lesson_detail_lesson_status_finished) View finishedStatusView;
    @Bind(R.id.lesson_detail_lesson_status_finished_created_at) TextView finishedCreatedAtView;

    @Bind(R.id.lesson_detail_remain_time_view) TextView remainTimeView;
    @Bind(R.id.lesson_detail_bidding_count_view) TextView biddingCountView;
    @Bind(R.id.lesson_detail_bidding_count_container) View biddingCountContainer;

    @Bind(R.id.lesson_detail_basic_info_view) BasicInfoView basicInfoView;
    @Bind(R.id.lesson_detail_lesson_info_view) LessonInfoView lessonInfoView;
    @Bind(R.id.description_view) TextView descriptionView;

    @Bind(R.id.bidding_button_dummy_view) View biddingButtonDummyView;
    @Bind(R.id.bidding_button) View biddingButton;
    @Bind(R.id.bidding_cancel_button) View biddingCancelButton;

    // 찜하기
    private MenuItem likeMenuItem;

    private Integer lessonId;
    private Integer ownerId;
    // 로그인 된 유저
    private CUser me;
    private CLesson lesson;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_lesson_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RiverAuctionEventBus.getEventBus().register(this);
        getDataFromBundle(getIntent().getExtras());
        me = UserStates.USER.get(stateCtx);

        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.lesson_detail_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter.getLesson(lessonId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favorite, menu);
        likeMenuItem = menu.getItem(0);
        likeMenuItem.setActionView(R.layout.action_layout_favorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        super.onActivityResult(requestCode, resultCode, data, bundle);
        if (REQUEST_POST_BIDDING == requestCode && RESULT_OK == resultCode) {
            presenter.getLesson(lessonId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (RiverAuctionEventBus.getEventBus().isRegistered(this)) {
            RiverAuctionEventBus.getEventBus().unregister(this);
        }
    }

    // lessonId, ownerId 를 이전 화면에서 넘겨받는다
    private void getDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            lessonId = bundle.getInt(EXTRA_LESSON_ID, -1);
            if (lessonId == -1) {
                throw new IllegalStateException("lessonId must be exist");
            }

            ownerId = bundle.getInt(EXTRA_OWNER_ID, -1);
            if (ownerId == -1) {
                throw new IllegalStateException("ownerId must be exist");
            }
        }
    }

    /**
     * 찜하기
     */
    private void postFavoriteDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.favorite_title)
                .setMessage(R.string.favorite_post_message)
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    presenter.postLessonFavorites(lessonId);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    /**
     * 찜 해제
     */
    private void deleteFavoriteDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.favorite_title)
                .setMessage(R.string.favorite_delete_message)
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    presenter.deleteLessonFavorites(lessonId);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final SelectTeacherEvent event) {
        // 선생님이 선택됨
        lesson.setStatus(CLessonStatus.FINISHED);
        setContent(lesson);
    }

    private void setContent(CLesson lesson) {
        if (lesson == null) {
            return;
        }
        this.lesson = lesson;

        showOrHideButtons();

        setLessonStatus(lesson.getStatus());
        biddingCountView.setText(getString(R.string.common_person_count_unit, lesson.getBiddingsCount()));
        biddingCountContainer.setOnClickListener(v -> {
            if (me.equals(lesson.getOwner())) {
                Intent intent = new Intent(context, MyLessonDetailSelectListActivity.class);
                intent.putExtra(MyLessonDetailSelectListActivity.EXTRA_LESSON, ParcelableWrappers.wrap(lesson));
                startActivity(intent);
            }
        });

        basicInfoView.setContent(lesson);
        lessonInfoView.setContent(lesson);
        descriptionView.setText(lesson.getDescription());

        setLikeMenuItem();
    }

    private void setLessonStatus(CLessonStatus lessonStatus) {
        biddingStatusView.setVisibility(View.GONE);
        dealingStatusView.setVisibility(View.GONE);
        canceledStatusView.setVisibility(View.GONE);
        finishedStatusView.setVisibility(View.GONE);
        switch (lessonStatus) {
            case BIDDING: {
                biddingStatusView.setVisibility(View.VISIBLE);
                remainTimeView.setText(DataUtils.convertRemainTimeToString(context, lesson.getExpiresIn()));
                remainTimeView.setTextColor(getResources().getColor(R.color.river_auction_greyish_brown_two));
                break;
            }
            case DEALING: {
                dealingStatusView.setVisibility(View.VISIBLE);
                remainTimeView.setText(DataUtils.convertRemainTimeToString(context, lesson.getExpiresIn()));
                remainTimeView.setTextColor(getResources().getColor(R.color.river_auction_greyish_brown_two));
                break;
            }
            case CANCELED: {
                canceledStatusView.setVisibility(View.VISIBLE);
                canceledCreatedAtView.setText(DateUtils.getRelativeTimeSpanString(lesson.getCreatedAt()));
                remainTimeView.setText("00:00");
                remainTimeView.setTextColor(getResources().getColor(R.color.river_auction_greyish));
                break;
            }
            case FINISHED: {
                finishedStatusView.setVisibility(View.VISIBLE);
                finishedCreatedAtView.setText(DateUtils.getRelativeTimeSpanString(lesson.getCreatedAt()));
                remainTimeView.setText("00:00");
                remainTimeView.setTextColor(getResources().getColor(R.color.river_auction_greyish));
                break;
            }
        }
    }

    private void setLikeMenuItem() {
        if (likeMenuItem == null) {
            return;
        }
        if (CUserType.STUDENT == me.getType()) {
            likeMenuItem.setVisible(false);
        } else {
            likeMenuItem.setVisible(true);
            TextView favoriteTextView = (TextView) likeMenuItem.getActionView().findViewById(R.id.favorite_text_view);
            if (lesson.getIsFavorited() != null && lesson.getIsFavorited()) {
                favoriteTextView.setText(R.string.menu_favorite_cancel);
                likeMenuItem.getActionView().setOnClickListener(item -> {
                    // 찜 해제
                    deleteFavoriteDialog();
                });
            } else {
                favoriteTextView.setText(R.string.menu_favorite);
                likeMenuItem.getActionView().setOnClickListener(item -> {
                    // 찜 하기
                    postFavoriteDialog();
                });
            }
        }
    }

    private void showOrHideButtons() {
        if (CUserType.STUDENT == me.getType()) {
            // 학생일 경우
            if (lesson.getOwner().getId().equals(me.getId())) {
                // 내가 경매 만든 사람이면 취소하기 보여줌 (경매중 or 매칭중)
                if (lesson.getStatus() == CLessonStatus.BIDDING || lesson.getStatus() == CLessonStatus.DEALING) {
                    biddingButton.setVisibility(View.GONE);
                    biddingCancelButton.setVisibility(View.VISIBLE);
                    biddingButtonDummyView.setVisibility(View.VISIBLE);
                } else {
                    biddingButton.setVisibility(View.GONE);
                    biddingCancelButton.setVisibility(View.GONE);
                    biddingButtonDummyView.setVisibility(View.GONE);
                }
            } else {
                biddingButton.setVisibility(View.GONE);
                biddingCancelButton.setVisibility(View.GONE);
                biddingButtonDummyView.setVisibility(View.GONE);
            }
        } else {
            // 선생님일 경우
            // 해당 경매를 입찰했으면 액션바의 입찰하기를 안보여준다
            if (lesson.getIsBid() != null && lesson.getIsBid()) {
                biddingButton.setVisibility(View.GONE);
                biddingCancelButton.setVisibility(View.GONE);
                biddingButtonDummyView.setVisibility(View.GONE);
            } else {
                biddingCancelButton.setVisibility(View.GONE);
                biddingButton.setVisibility(View.VISIBLE);
                biddingButtonDummyView.setVisibility(View.VISIBLE);
            }
        }
        biddingButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostBiddingActivity.class);
            intent.putExtra(PostBiddingActivity.EXTRA_LESSON_PREFERRED_PRICE, (int) lesson.getPreferredPrice());
            intent.putExtra(PostBiddingActivity.EXTRA_LESSON_ID, lessonId);
            startActivityForResult(intent, REQUEST_POST_BIDDING);
        });
        biddingCancelButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.menu_lesson_cancel)
                    .setMessage(R.string.my_lesson_cancel_lesson_button)
                    .setPositiveButton(R.string.common_button_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            presenter.cancelLesson(lessonId);
                        }
                    })
                    .show();
        });
    }

    @Override
    public void successGetLesson(CLesson lesson) {
        setContent(lesson);
    }

    @Override
    public boolean failGetLesson(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successPostLessonFavorites(CLessonFavorite lessonFavorite) {
        Toast.makeText(this, R.string.favorite_post_success_toast, Toast.LENGTH_SHORT).show();
        lesson.setIsFavorited(true);
        setContent(lesson);
        RiverAuctionEventBus.getEventBus().post(new FavoriteChangedEvent());
    }

    @Override
    public boolean failPostLessonFavorites(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successDeleteLessonFavorites() {
        Toast.makeText(this, R.string.favorite_delete_success_toast, Toast.LENGTH_SHORT).show();
        lesson.setIsFavorited(false);
        setContent(lesson);
        RiverAuctionEventBus.getEventBus().post(new FavoriteChangedEvent());
    }

    @Override
    public boolean failDeleteLessonFavorites(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successCancelLesson(CLesson lesson) {
        Toast.makeText(this, R.string.lesson_detail_cancel_lesson_toast, Toast.LENGTH_SHORT).show();
        RiverAuctionEventBus.getEventBus().post(new CancelEvent());
        finish();
    }

    @Override
    public boolean failCancelLesson(CErrorCause errorCause) {
        return false;
    }
}
