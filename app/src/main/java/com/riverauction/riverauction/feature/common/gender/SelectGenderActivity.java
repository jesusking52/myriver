package com.riverauction.riverauction.feature.common.gender;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.base.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;

public class SelectGenderActivity extends BaseActivity implements SelectGenderMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.gender.SelectGenderActivity";
    public static final String EXTRA_GENDER = EXTRA_PREFIX + "extra_gender";

    @Inject SelectGenderPresenter presenter;

    @Bind(R.id.select_gender_man) View genderMan;
    @Bind(R.id.select_gender_woman) View genderWoman;
    @Bind(R.id.select_gender_none) View genderNone;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_gender;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.common_preferred_gender_choose);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        genderMan.setOnClickListener(v -> setOnClick(genderMan));
        genderWoman.setOnClickListener(v -> setOnClick(genderWoman));
        genderNone.setOnClickListener(v -> setOnClick(genderNone));
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
            View selectedView = getSelectedView();
            if (selectedView == null) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.select_gender_description)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return true;
            }

            CGender gender;
            if (selectedView == genderMan) {
                gender = CGender.MALE;
            } else if (selectedView == genderWoman) {
                gender = CGender.FEMALE;
            } else {
                gender = CGender.NONE;
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_GENDER, gender.toString());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void setOnClick(View genderView) {
        if (genderView.isSelected()) {
            genderView.setSelected(false);
        } else {
            View selectedView = getSelectedView();
            if (selectedView != null) {
                selectedView.setSelected(false);
            }
            genderView.setSelected(true);
        }
    }

    private View getSelectedView() {
        if (genderMan.isSelected()) {
            return genderMan;
        } else if (genderWoman.isSelected()) {
            return genderWoman;
        } else if (genderNone.isSelected()) {
            return genderNone;
        }

        return null;
    }

    private void clearSelect() {
        genderMan.setSelected(false);
        genderWoman.setSelected(false);
        genderNone.setSelected(false);
    }
}
