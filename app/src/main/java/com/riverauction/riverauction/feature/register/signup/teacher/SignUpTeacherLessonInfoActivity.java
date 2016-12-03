package com.riverauction.riverauction.feature.register.signup.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.Lists2;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDayOfWeekType;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.request.SignUpRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherBasicInformationRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherLessonInformationRequest;
import com.riverauction.riverauction.api.service.auth.response.SignUpResult;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.common.dayofweek.SelectDayOfWeeksDialog;
import com.riverauction.riverauction.feature.common.subject.SelectSubjectsActivity;
import com.riverauction.riverauction.feature.common.widget.SelectedSubjectsView;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.states.DeviceStates;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class SignUpTeacherLessonInfoActivity extends BaseActivity implements SignUpTeacherLessonInfoMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.register.signup.teacher.SignUpTeacherLessonInfoActivity.";
    public static final String EXTRA_BASIC_REQUEST = EXTRA_PREFIX + "extra_basic_request";
    public static final String EXTRA_EMAIL_CREDENTIAL_REQUEST = EXTRA_PREFIX + "extra_email_credential_request";
    private final static int REQUEST_SELECT_SUBJECTS = 0x01;

    private EmailCredentialRequest emailCredentialRequest;
    private TeacherBasicInformationRequest teacherBasicInformationRequest;

    @Inject SignUpTeacherLessonInfoPresenter presenter;

    @Bind(R.id.sign_up_next_button) View nextButton;

    // lesson info
    @Bind(R.id.sign_up_career_spinner) Spinner careerSpinner;
    @Bind(R.id.sign_up_available_subjects_container) SelectedSubjectsView selectedSubjectsView;
    @Bind(R.id.sign_up_class_available_count_spinner) Spinner classAvailableCountSpinner;
    @Bind(R.id.sign_up_available_days_of_week_container) View availableDaysOfWeekContainer;
    @Bind(R.id.sign_up_available_days_of_week_view) TextView availableDaysOfWeekView;
    @Bind(R.id.sign_up_class_time_spinner) Spinner classTimeSpinner;
    @Bind(R.id.sign_up_preferred_gender_spinner) Spinner preferredGenderSpinner;

    @Bind(R.id.sign_up_preferred_price) EditText preferredPriceEditText;
    @Bind(R.id.sign_up_description) EditText descriptionEditText;

    private List<CSubject> selectedSubjects = Lists.newArrayList();
    private List<CDayOfWeekType> selectedDayOfWeeks = Lists.newArrayList();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_sign_up_teacher_lesson_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        getDataFromBundle(getIntent().getExtras());

        getSupportActionBar().setTitle(R.string.sign_up_lesson_info_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // lesson
        initializeCareerSpinner();
        initializeClassAvailableCountSpinner();
        initializeClassTimeSpinner();
        initializePreferredGenderSpinner();

        selectedSubjectsView.setOnClickSelectSubjectViewListener(selectedSubjects1 -> {
            Intent intent = new Intent(context, SelectSubjectsActivity.class);
            if (!Lists2.isNullOrEmpty(selectedSubjects1)) {
                intent.putParcelableArrayListExtra(SelectSubjectsActivity.EXTRA_SELECTED_SUBJECTS, ParcelableWrappers.wrap(selectedSubjects1));
            }
            startActivityForResult(intent, REQUEST_SELECT_SUBJECTS);
        });

        availableDaysOfWeekContainer.setOnClickListener(v -> {
            SelectDayOfWeeksDialog dialog = new SelectDayOfWeeksDialog(context, selectedDayOfWeekTypes -> {
                selectedDayOfWeeks = selectedDayOfWeekTypes;
                availableDaysOfWeekView.setText(DataUtils.convertAvailableDayOfWeekToString(context, selectedDayOfWeekTypes));
            });
            dialog.show();
        });

        nextButton.setOnClickListener(v -> {
            if (isValidCheckLesson()) {
                presenter.signUp(buildSignUpRequest());
            }
        });
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
        if (REQUEST_SELECT_SUBJECTS == requestCode && RESULT_OK == resultCode) {
            ArrayList<Parcelable> parcelableArrayList = data.getParcelableArrayListExtra(SelectSubjectsActivity.EXTRA_SELECTED_SUBJECTS);
            if (parcelableArrayList != null) {
                selectedSubjects = ParcelableWrappers.unwrap(parcelableArrayList);
                selectedSubjectsView.setSelectedSubjects(selectedSubjects);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void getDataFromBundle(Bundle bundle) {
        Parcelable parcelable = bundle.getParcelable(EXTRA_BASIC_REQUEST);
        if (parcelable != null) {
            teacherBasicInformationRequest = ParcelableWrappers.unwrap(parcelable);
        } else {
            throw new IllegalStateException("teacherBasicInformationRequest must be exist");
        }

        Parcelable parcelable2 = bundle.getParcelable(EXTRA_EMAIL_CREDENTIAL_REQUEST);
        if (parcelable2 != null) {
            emailCredentialRequest = ParcelableWrappers.unwrap(parcelable2);
        } else {
            throw new IllegalStateException("emailCredentialRequest must be exist");
        }
    }

    private void initializeCareerSpinner() {
        SpinnerAdapter careerSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        careerSpinnerAdapter.addItem(getString(R.string.common_career_choose));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_0));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_1));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_2));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_3));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_4));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_5));
        careerSpinnerAdapter.addItem(getString(R.string.sign_up_career_6));
        careerSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        careerSpinner.setAdapter(careerSpinnerAdapter);
    }

    private void initializeClassAvailableCountSpinner() {
        SpinnerAdapter classAvailableCountSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        classAvailableCountSpinnerAdapter.addItem(getString(R.string.common_class_available_count_choose));
        for (int i = 1; i <= 7; i++) {
            classAvailableCountSpinnerAdapter.addItem(getString(R.string.common_class_available_count_unit, i));
        }
        classAvailableCountSpinnerAdapter.addItem(getString(R.string.common_anything));
        classAvailableCountSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        classAvailableCountSpinner.setAdapter(classAvailableCountSpinnerAdapter);
    }

    private void initializeClassTimeSpinner() {
        SpinnerAdapter classTimeSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        classTimeSpinnerAdapter.addItem(getString(R.string.common_class_time_choose));
        for (int i = 2; i <= 8; i++) {
            float classTime = (float) i / 2;
            classTimeSpinnerAdapter.addItem(getString(R.string.common_count_time_unit, classTime));
        }
        classTimeSpinnerAdapter.addItem(getString(R.string.common_anything));
        classTimeSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        classTimeSpinner.setAdapter(classTimeSpinnerAdapter);
    }

    private void initializePreferredGenderSpinner() {
        SpinnerAdapter preferredGenderSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        preferredGenderSpinnerAdapter.addItem(getString(R.string.common_preferred_gender_choose));
        preferredGenderSpinnerAdapter.addItem(getString(R.string.sign_up_gender_man));
        preferredGenderSpinnerAdapter.addItem(getString(R.string.sign_up_gender_woman));
        preferredGenderSpinnerAdapter.addItem(getString(R.string.filter_no_gender));
        preferredGenderSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        preferredGenderSpinner.setAdapter(preferredGenderSpinnerAdapter);
    }

    private boolean isValidCheckLesson() {
        if (careerSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_career_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }
        if (selectedSubjects.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_subject_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (classAvailableCountSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_class_available_count_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }
        if (selectedDayOfWeeks.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_day_of_week_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (classTimeSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_class_time_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (preferredGenderSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_preferred_gender_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (Strings.isNullOrEmpty(preferredPriceEditText.getText().toString())) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_preferred_price_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (Strings.isNullOrEmpty(descriptionEditText.getText().toString())) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_description_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }
        return true;
    }

    /**
     * classTime 은 1.5 시간이면 2 곱해서 3 을 서버로 보낸다 (Integer 로 처리하기 위해
     */
    private Integer getClassTime() {
        int selectedPosition = classTimeSpinner.getSelectedItemPosition();
        // index 0 은 "선택하세요" 문구임으로 getSelectedItemPosition + 1 하면 선택한 시간 * 2 이다
        return selectedPosition + 1;
    }

    private CGender getPreferredGender() {
        if (preferredGenderSpinner.getSelectedItemPosition() == 1) {
            return CGender.MALE;
        } else if (preferredGenderSpinner.getSelectedItemPosition() == 2) {
            return CGender.FEMALE;
        } else if (preferredGenderSpinner.getSelectedItemPosition() == 3) {
            return CGender.NONE;
        }

        return null;
    }

    private List<Integer> getSubjects() {
        return Lists.newArrayList(Iterables.transform(selectedSubjects, input -> input.getId()));
    }

    /**
     * API call 에 사용될 {@link SignUpRequest} 를 만든다
     * @return {@link SignUpRequest}
     */
    private SignUpRequest buildSignUpRequest() {
        return new SignUpRequest.Builder()
                .setType(CUserType.TEACHER)
                .setEmailCredentialRequest(emailCredentialRequest)
                .setTeacherBasicInformationRequest(teacherBasicInformationRequest)
                .setTeacherLessonInformationRequest(buildTeacherLessonInformationRequest())
                .build();
    }

    private TeacherLessonInformationRequest buildTeacherLessonInformationRequest() {
        return new TeacherLessonInformationRequest.Builder()
                .setSubjects(getSubjects())
                .setCareer(careerSpinner.getSelectedItemPosition() - 1) // index 1번째가 career 0.
                .setDaysOfWeek(selectedDayOfWeeks)
                .setClassAvailableCount(classAvailableCountSpinner.getSelectedItemPosition())
                .setClassTime(getClassTime())
                .setPreferredGender(getPreferredGender())
                .setPreferredPrice(Integer.valueOf(preferredPriceEditText.getText().toString()))
                .setDescription(descriptionEditText.getText().toString())
                .build();
    }

    @Override
    public void successSignUp(SignUpResult signUpResult) {
        if (signUpResult == null) {
            return;
        }
        UserStates.USER.set(stateCtx, signUpResult.getUser());
        UserStates.ACCESS_TOKEN.set(stateCtx, signUpResult.getToken());
        DeviceStates.PHONE_NUMBER.clear(stateCtx);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean failSignUp(CErrorCause errorCause) {
        switch (errorCause) {
            case INVALID_ARGUMENT: {
                Toast.makeText(context, R.string.sign_up_error_invalid_argument_toast, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
}
