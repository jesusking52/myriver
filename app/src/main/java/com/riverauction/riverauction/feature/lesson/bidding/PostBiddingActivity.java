package com.riverauction.riverauction.feature.lesson.bidding;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionConstant;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.service.lesson.request.LessonBiddingRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.PostBiddingEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.inapppurchase.util.InAppPurchaseUtils;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * 선생님이 학생 경매에 참여하기 위해 금액을 쓰는 화면
 */
public class PostBiddingActivity extends BaseActivity implements PostBiddingMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.lesson.bidding.PostBiddingActivity.";
    public static final String EXTRA_LESSON_PREFERRED_PRICE = EXTRA_PREFIX + "extra_lesson_preferred_price";
    public static final String EXTRA_LESSON_ID = EXTRA_PREFIX + "extra_lesson_id";

    @Bind(R.id.lesson_preferred_price_view) TextView lessonPreferredPriceView;
    @Bind(R.id.bidding_price_view) EditText biddingPriceView;

    @Inject PostBiddingPresenter presenter;

    private Integer lessonPreferredPrice;
    private Integer lessonId;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_post_bidding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromBundle(getIntent().getExtras());

        getSupportActionBar().setTitle(R.string.post_bidding_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        lessonPreferredPriceView.setText(getString(R.string.common_price_big_unit, lessonPreferredPrice));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_common_confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_common_confirm) {
            if (isValid()) {
                postLessonBiddingsDialog();

                return true;
            }

            return false;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            lessonPreferredPrice = bundle.getInt(EXTRA_LESSON_PREFERRED_PRICE, -1);
            if (lessonPreferredPrice == -1) {
                throw new IllegalStateException("lessonPreferredPrice must be exist");
            }

            lessonId = bundle.getInt(EXTRA_LESSON_ID, -1);
            if (lessonId == -1) {
                throw new IllegalStateException("lessonId must be exist");
            }
        }
    }

    private boolean isValid() {
        String biddingPrice = biddingPriceView.getText().toString();
        if (Strings.isNullOrEmpty(biddingPrice)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.post_bidding_dialog_title)
                    .setMessage(R.string.post_bidding_hint)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        return true;
    }

    /**
     * 입찰하기
     */
    private void postLessonBiddingsDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.post_bidding_dialog_title)
                .setMessage(getResources().getString(R.string.lesson_detail_bidding_dialog_message, RiverAuctionConstant.PRICE_POST_BIDDING))
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    Integer price = Integer.valueOf(biddingPriceView.getText().toString());
                    LessonBiddingRequest request = new LessonBiddingRequest.Builder().setPrice(price).build();
                    presenter.postLessonBiddings(lessonId, request);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    @Override
    public void successPostLessonBiddings(CLessonBidding lessonBidding) {
        if (lessonBidding != null) {
            RiverAuctionEventBus.getEventBus().post(new PostBiddingEvent());
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean failPostLessonBiddings(CErrorCause errorCause) {
        if (CErrorCause.EXPIRED_LESSON_BIDDING == errorCause) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.post_bidding_dialog_title)
                    .setMessage(R.string.lesson_detail_expired_bidding_dialog_message)
                    .setPositiveButton(R.string.common_button_ok, null)
                    .show();
            return true;
        } else if (CErrorCause.INSUFFICIENT_PERMISSION == errorCause || CErrorCause.INSUFFICIENT_COINS == errorCause) {
            InAppPurchaseUtils.showInsufficientCoinDialog(context);
            return true;
        }
        return false;
    }
}
