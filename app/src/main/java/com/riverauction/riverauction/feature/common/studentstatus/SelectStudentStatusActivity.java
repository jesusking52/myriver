package com.riverauction.riverauction.feature.common.studentstatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CStudentStatus;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;

import javax.inject.Inject;

import butterknife.Bind;

public class SelectStudentStatusActivity extends BaseActivity implements SelectStudentStatusMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.studentstatus.SelectStudentStatusActivity";
    public static final String EXTRA_STUDENT_STATUS = EXTRA_PREFIX + "extra_student_status";
    public static final String EXTRA_STUDENT_GRADE = EXTRA_PREFIX + "extra_student_grade";

    @Inject SelectStudentStatusPresenter presenter;

    @Bind(R.id.select_student_status_spinner) Spinner studentStatusSpinner;
    @Bind(R.id.select_student_grade_spinner) Spinner studentGradeSpinner;

    private boolean isFirstLoad = true;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_student_status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.select_student_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeStudentStatusSpinner();
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
            Intent intent = new Intent();

            if (studentStatusSpinner.getSelectedItemPosition() == 0) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.select_student_status_description)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return false;
            }

            intent.putExtra(EXTRA_STUDENT_STATUS, getStudentStatus());
            if (studentGradeSpinner.getSelectedItemPosition() != 0 && isExistGrade()) {
                intent.putExtra(EXTRA_STUDENT_GRADE, (int) getGrade());
            }

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

    private void initializeStudentStatusSpinner() {
        SpinnerAdapter studentStatusSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_hint));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_kindergarten));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_elementary_school));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_middle_school));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_high_school));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_university));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_retry_university));
        studentStatusSpinnerAdapter.addItem(getString(R.string.sign_up_student_status_ordinary));
        studentStatusSpinner.setAdapter(studentStatusSpinnerAdapter);

        // student status 선택에 따라서 gradeSpinner 가 변경된다
        studentStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView titleView = (TextView) view.findViewById(R.id.item_spinner_title);
                if (position == 0) {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish));
                } else {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                }

                if (!isExistGrade()) {
                    // 선택하세요, 유치원, 재수, 일반인
                    studentGradeSpinner.setVisibility(View.GONE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 2) {
                    // 초등학교
                    isFirstLoad = true;
                    initializeGradeSpinner(6);
                    studentGradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 3) {
                    // 중학교
                    isFirstLoad = true;
                    initializeGradeSpinner(3);
                    studentGradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 4) {
                    // 고등학교
                    isFirstLoad = true;
                    initializeGradeSpinner(3);
                    studentGradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 5) {
                    // 대학생
                    isFirstLoad = true;
                    initializeGradeSpinner(4);
                    studentGradeSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initializeGradeSpinner(int maxGrade) {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        spinnerAdapter.addItem(getString(R.string.sign_up_grade_hint));
        for (int i = 1; i <= maxGrade; i++) {
            spinnerAdapter.addItem(getString(R.string.common_grade_unit, i));
        }
        studentGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }
                TextView titleView = (TextView) view.findViewById(R.id.item_spinner_title);
                if (position == 0) {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish));
                } else {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        studentGradeSpinner.setAdapter(spinnerAdapter);
    }

    private boolean isExistGrade() {
        return studentStatusSpinner.getSelectedItemPosition() == 2 ||
                studentStatusSpinner.getSelectedItemPosition() == 3 ||
                studentStatusSpinner.getSelectedItemPosition() == 4 ||
                studentStatusSpinner.getSelectedItemPosition() == 5;
    }

    private CStudentStatus getStudentStatus() {
        switch (studentStatusSpinner.getSelectedItemPosition()) {
            case 1 : return CStudentStatus.KINDERGARTEN;
            case 2 : return CStudentStatus.ELEMENTARY_SCHOOL;
            case 3 : return CStudentStatus.MIDDLE_SCHOOL;
            case 4 : return CStudentStatus.HIGH_SCHOOL;
            case 5 : return CStudentStatus.UNIVERSITY;
            case 6 : return CStudentStatus.RETRY_UNIVERSITY;
            case 7 : return CStudentStatus.ORDINARY;
        }

        return null;
    }

    private Integer getGrade() {
        if (isExistGrade()) {
            return studentGradeSpinner.getSelectedItemPosition();
        }
        return null;
    }
}
