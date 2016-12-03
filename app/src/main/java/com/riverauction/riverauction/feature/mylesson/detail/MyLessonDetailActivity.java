package com.riverauction.riverauction.feature.mylesson.detail;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLesson;
import com.riverauction.riverauction.api.model.CLessonBidding;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

public class MyLessonDetailActivity extends BaseActivity implements MyLessonDetailMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.mylesson.detail.MyLessonDetailActivity.";
    public static final String EXTRA_LESSON_ID = EXTRA_PREFIX + "extra_lesson_id";

    @Inject MyLessonDetailPresenter presenter;

    private Integer lessonId;
    // 로그인한 사용자
    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_my_lesson_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromBundle(getIntent().getExtras());

        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        presenter.getLesson(lessonId);
        presenter.getLessonBiddings(lessonId, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_lesson_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_my_lesson_cancel_button) {
            // 입찰 취소 or 경매 취소
            // TODO: text change
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void getDataFromBundle(Bundle bundle) {
        if (bundle != null) {
            lessonId = bundle.getInt(EXTRA_LESSON_ID, -1);
            if (lessonId == -1) {
                throw new IllegalStateException("lessonId must be exist");
            }
        }
    }

    @Override
    public void successGetLesson(CLesson lesson) {

    }

    @Override
    public boolean failGetLesson(CErrorCause errorCause) {
        return false;
    }

    @Override
    public void successGetLessonBiddings(List<CLessonBidding> lessonBiddingList, Integer nextToken) {

    }

    @Override
    public boolean failGetLessonBiddings(CErrorCause errorCause) {
        return false;
    }
}
