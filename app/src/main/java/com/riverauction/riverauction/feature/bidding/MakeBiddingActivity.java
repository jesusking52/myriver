package com.riverauction.riverauction.feature.bidding;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.MakeBiddingEvent;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.feature.common.BasicInfoView;
import com.riverauction.riverauction.feature.common.LessonInfoView;
import com.riverauction.riverauction.feature.profile.patch.student.ProfileStudentLessonInfoPatchActivity;
import com.riverauction.riverauction.states.UserStates;

import javax.inject.Inject;

import butterknife.Bind;

public class MakeBiddingActivity extends BaseActivity implements MakeBiddingMvpView {
    private static final int REQUEST_PATCH_STUDENT_LESSON_INFO = 0x01;

    @Inject MakeBiddingPresenter presenter;

    @Bind(R.id.lesson_detail_basic_info_view) BasicInfoView basicInfoView;
    @Bind(R.id.lesson_detail_lesson_info_view) LessonInfoView lessonInfoView;
    @Bind(R.id.description_view) TextView descriptionView;
    @Bind(R.id.make_bidding_button) View makeBiddingButton;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_make_bidding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.make_bidding_actionbar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CUser user = UserStates.USER.get(stateCtx);
        if (CUserType.STUDENT == user.getType()) {
            setContent(user);
        } else {
            finish();
        }

        makeBiddingButton.setOnClickListener(v -> presenter.postLesson());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_change_lesson_info) {
            // 과외 조건 변경
            Intent intent = new Intent(context, ProfileStudentLessonInfoPatchActivity.class);
            startActivityForResult(intent, REQUEST_PATCH_STUDENT_LESSON_INFO);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        if (RESULT_OK != resultCode) {
            return;
        }

        if (REQUEST_PATCH_STUDENT_LESSON_INFO == requestCode) {
            CUser user = UserStates.USER.get(stateCtx);
            if (CUserType.STUDENT == user.getType()) {
                setContent(user);
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_change_lesson_info, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void setContent(CUser user) {
        basicInfoView.setContent(user, true);
        lessonInfoView.setContent(user);
        descriptionView.setText(user.getStudent().getDescription());
    }

    @Override
    public void successPostLesson(CLesson lesson) {
        RiverAuctionEventBus.getEventBus().post(new MakeBiddingEvent());
        finish();
    }

    @Override
    public boolean failPostLesson(CErrorCause errorCause) {
        // TODO: error handle
        return false;
    }
}
