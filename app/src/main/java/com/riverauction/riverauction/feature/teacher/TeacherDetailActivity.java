package com.riverauction.riverauction.feature.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionConstant;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserFavorite;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.FavoriteChangedEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.SelectTeacherEvent;
import com.riverauction.riverauction.feature.common.BasicInfoView;
import com.riverauction.riverauction.feature.common.LessonInfoView;
import com.riverauction.riverauction.feature.review.ReviewList;
import com.riverauction.riverauction.feature.review.ReviewWriteActivity;
import com.riverauction.riverauction.inapppurchase.util.InAppPurchaseUtils;
import com.riverauction.riverauction.states.UserStates;

import javax.inject.Inject;

import butterknife.Bind;

public class TeacherDetailActivity extends BaseActivity implements TeacherDetailMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.teacher.TeacherDetailActivity.";
    public static final String EXTRA_USER_ID = EXTRA_PREFIX + "extra_user_id";
    // 입찰금액을 보여줄꺼면 bidding_price 를 넘겨준다.
    public static final String EXTRA_BIDDING_PRICE = EXTRA_PREFIX + "extra_bidding_price";
    // true 이면 연락처 보기 버튼 대신 "입찰 선택" 버튼이 나온다
    public static final String EXTRA_BOOLEAN_SELECT_TEACHER = EXTRA_PREFIX + "extra_boolean_select_teacher";
    public static final String EXTRA_LESSON_ID= EXTRA_PREFIX + "extra_lesson_id";

    @Inject TeacherDetailPresenter presenter;

    @Bind(R.id.price_title_view) TextView priceTitleView;
    @Bind(R.id.price_view) TextView priceView;
    @Bind(R.id.basic_info_view) BasicInfoView basicInfoView;
    @Bind(R.id.lesson_info_view) LessonInfoView lessonInfoView;
    @Bind(R.id.description_view) TextView descriptionView;

    @Bind(R.id.phone_number_dummy_view) View phoneNumberDummyView;
    @Bind(R.id.phone_number_button) TextView phoneNumberButton;
    @Bind(R.id.riview_button) TextView riviewbutton;
    @Bind(R.id.review_list) TextView reviewlist;

    // 찜하기d
    private MenuItem likeMenuItem;

    private Integer userId;
    // 입찰금액
    private Integer biddingPrice;
    private CUser teacher;
    // 로그인 한 유저
    private CUser me;

    // 입찰 선택에 사용된다
    private boolean isSelectTeacherButton;
    private Integer lessonId;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_teacher_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromBundle(getIntent().getExtras());
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        me = UserStates.USER.get(stateCtx);

        getSupportActionBar().setTitle(R.string.teacher_detail_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (isSelectTeacherButton) {
            phoneNumberButton.setText(R.string.lesson_bidding_list_dialog_title);
            phoneNumberButton.setOnClickListener(v -> {
                if (lessonId != -1) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.lesson_bidding_list_dialog_negative_button)
                            .setPositiveButton(R.string.lesson_bidding_list_dialog_positive_button, (dialog, which) -> {
                                // 입찰 선택
                                presenter.postSelectTeacher(lessonId, userId);
                            })
                            .show();
                }
                //Toast.makeText(this, "test1", Toast.LENGTH_SHORT).show();
            });
            phoneNumberButton.setVisibility(View.VISIBLE);

        }else{
            //일단 따라 넣었음. 선생이 아닌 경우
            riviewbutton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ReviewWriteActivity.class);
                intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, userId);
                startActivity(intent);
                //Toast.makeText(this, "test2", Toast.LENGTH_SHORT).show();
            });
            riviewbutton.setVisibility(View.VISIBLE);

            //일단 따라 넣었음. 선생이 아닌 경우
            reviewlist.setOnClickListener(v -> {
                Intent intent = new Intent(context, ReviewList.class);
                intent.putExtra(TeacherDetailActivity.EXTRA_USER_ID, userId);
                startActivity(intent);
                //Toast.makeText(this, "test2", Toast.LENGTH_SHORT).show();
            });
            reviewlist.setVisibility(View.VISIBLE);
        }
        presenter.getUserProfile(userId, true);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    // userId 를 이전 화면에서 넘겨받는다
    private void getDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            userId = bundle.getInt(EXTRA_USER_ID, -1);
            if (userId == -1) {
                throw new IllegalStateException("userId must be exist");
            }

            biddingPrice = bundle.getInt(EXTRA_BIDDING_PRICE, -1);
            isSelectTeacherButton = bundle.getBoolean(EXTRA_BOOLEAN_SELECT_TEACHER, false);
            lessonId = bundle.getInt(EXTRA_LESSON_ID, -1);
        }
    }

    private void postFavoriteDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.favorite_title)
                .setMessage(R.string.favorite_post_message)
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    presenter.postUserFavorites(userId);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    private void deleteFavoriteDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.favorite_title)
                .setMessage(R.string.favorite_delete_message)
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    presenter.deleteUserFavorites(userId);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    private void showPhoneNumberDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.teacher_phone_number_dialog_title)
                .setMessage(getResources().getString(R.string.teacher_phone_number_dialog_message, RiverAuctionConstant.PRICE_SHOW_PHONE_NUMBER))
                .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                    presenter.checkPhoneNumber(userId);
                })
                .setNegativeButton(R.string.common_button_no, null)
                .show();
    }

    private void setContent(CUser user) {
        if (user == null) {
            return;
        }
        teacher = user;

        if (!isSelectTeacherButton) {
            setPhoneButton();
        }

        basicInfoView.setContent(user);
        lessonInfoView.setContent(user);
        setTeacher(user.getTeacher());

        descriptionView.setText(user.getTeacher().getDescription());
        setLikeMenuItem();
    }

    private void setLikeMenuItem() {
        if (likeMenuItem == null) {
            return;
        }
        if (CUserType.TEACHER == me.getType()) {
            likeMenuItem.setVisible(false);
        } else {
            likeMenuItem.setVisible(true);
            TextView favoriteTextView = (TextView) likeMenuItem.getActionView().findViewById(R.id.favorite_text_view);
            if (teacher.getIsFavorited() != null && teacher.getIsFavorited()) {
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

    private void setPhoneButton() {
        if (CUserType.TEACHER == me.getType()) {
            // 선생님일 경우
            phoneNumberButton.setVisibility(View.GONE);
            phoneNumberDummyView.setVisibility(View.GONE);
        } else {
            // 학생일 경우
            // 해당 선생님의 연락처 보기를 했으면 안보여준다
            if (teacher.getIsCheckedPhoneNumber() != null && teacher.getIsCheckedPhoneNumber()) {
                phoneNumberButton.setVisibility(View.GONE);
                phoneNumberDummyView.setVisibility(View.GONE);
            } else {
                phoneNumberButton.setVisibility(View.VISIBLE);
                phoneNumberDummyView.setVisibility(View.VISIBLE);
            }
        }
        phoneNumberButton.setOnClickListener(v -> showPhoneNumberDialog());
    }

    private void setTeacher(CTeacher teacher) {
        if (teacher == null) {
            return;
        }

        if (teacher.getDescription() != null) {
            descriptionView.setText(teacher.getDescription());
        }

        if (biddingPrice == -1) {
            // 희망 금액
            priceTitleView.setText(R.string.teacher_item_preferred_price);
            String price = getString(R.string.common_price_big_unit, teacher.getPreferredPrice());
            SpannableStringBuilder builder = new SpannableStringBuilder(price);
            builder.setSpan(new TextAppearanceSpan(context, R.style.PriceSmallText), price.length() - 2, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceView.setTextColor(getResources().getColor(R.color.river_auction_dodger_blue));
            priceView.setText(builder);
        } else {
            // 입찰 금액
            priceTitleView.setText(R.string.teacher_item_bidding_price);
            String price = getString(R.string.common_price_big_unit, biddingPrice);
            SpannableStringBuilder builder = new SpannableStringBuilder(price);
            builder.setSpan(new TextAppearanceSpan(context, R.style.PriceSmallText), price.length() - 2, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceView.setTextColor(getResources().getColor(R.color.river_auction_leafy_green));
            priceView.setText(builder);
        }
    }

    @Override
    public void successGetUser(CUser user) {
        setContent(user);
    }

    @Override
    public boolean failGetUser(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successPostUserFavorites(CUserFavorite favorite) {
        Toast.makeText(this, R.string.favorite_post_success_toast, Toast.LENGTH_SHORT).show();
        teacher.setIsFavorited(true);
        setContent(teacher);
        RiverAuctionEventBus.getEventBus().post(new FavoriteChangedEvent());
    }

    @Override
    public boolean failPostUserFavorites(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successDeleteUserFavorites() {
        Toast.makeText(this, R.string.favorite_delete_success_toast, Toast.LENGTH_SHORT).show();
        teacher.setIsFavorited(false);
        setContent(teacher);
        RiverAuctionEventBus.getEventBus().post(new FavoriteChangedEvent());
    }

    @Override
    public boolean failDeleteUserFavorites(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successCheckPhoneNumber(CUser user) {
        teacher.setIsCheckedPhoneNumber(true);
        teacher.setPhoneNumber(user.getPhoneNumber());
        setContent(teacher);
    }

    @Override
    public boolean failCheckPhoneNumber(CErrorCause errorCause) {
        if (CErrorCause.INSUFFICIENT_PERMISSION == errorCause || CErrorCause.INSUFFICIENT_COINS == errorCause) {
            InAppPurchaseUtils.showInsufficientCoinDialog(context);
            return true;
        }
        return false;
    }

    @Override
    public void successPostSelectTeacher(CUser user) {
        teacher.setIsCheckedPhoneNumber(true);
        teacher.setPhoneNumber(user.getPhoneNumber());
        phoneNumberButton.setVisibility(View.GONE);
        phoneNumberDummyView.setVisibility(View.GONE);
        setContent(teacher);
        RiverAuctionEventBus.getEventBus().post(new SelectTeacherEvent());
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
}
