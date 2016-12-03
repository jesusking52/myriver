package com.riverauction.riverauction.feature.register.signup.student;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.jhcompany.android.libs.jackson.Jackson;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDaumPostCode;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CStudentDepartmentType;
import com.riverauction.riverauction.api.model.CStudentStatus;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.request.StudentBasicInformationRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.postcode.DaumPostCodeActivity;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.GeocodeUtils;
import com.riverauction.riverauction.states.DeviceStates;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;
import com.riverauction.riverauction.widget.spinner.SpinnerItem;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;

public class SignUpStudentActivity extends BaseActivity implements SignUpStudentMvpView {
    private final static int REQUEST_SEARCH_LOCATION = 0x01;
    private final static int REQUEST_LESSON_INFO = 0x02;

    @Bind(R.id.sign_up_next_button) View nextButton;

    // basic
    @Bind(R.id.sign_up_name) EditText nameEditText;
    @Bind(R.id.sign_up_age_spinner) Spinner ageSpinner;
    @Bind(R.id.sign_up_gender_spinner) Spinner genderSpinner;

    @Bind(R.id.sign_up_email) EditText emailEditText;
    @Bind(R.id.sign_up_password) EditText passwordEditText;
    @Bind(R.id.sign_up_password_check) EditText passwordCheckEditText;

    @Bind(R.id.sign_up_student_status_spinner) Spinner studentStatusSpinner;
    @Bind(R.id.sign_up_grade_spinner) Spinner gradeSpinner;
    @Bind(R.id.sign_up_department_spinner) Spinner departmentTypeSpinner;

    @Bind(R.id.sign_up_address_container) View addressContainer;
    @Bind(R.id.sign_up_address) TextView addressView;

    @Inject SignUpStudentPresenter presenter;

    // address 정보
    private CDaumPostCode daumPostCode;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_sign_up_student;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.sign_up_basic_info_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // basic
        initializeAgeSpinner();
        initializeGender();
        initializeStudentStatusSpinner();
        initializeDepartmentTypeSpinner();
        initializeAddress();

        nextButton.setOnClickListener(v -> {
            if (isValidCheckBasic()) {
                Intent intent = new Intent(context, SignUpStudentLessonInfoActivity.class);
                intent.putExtra(SignUpStudentLessonInfoActivity.EXTRA_EMAIL_CREDENTIAL_REQUEST, ParcelableWrappers.wrap(buildEmailCredentialRequest()));
                intent.putExtra(SignUpStudentLessonInfoActivity.EXTRA_BASIC_REQUEST, ParcelableWrappers.wrap(buildStudentBasicInformationRequest()));
                startActivityForResult(intent, REQUEST_LESSON_INFO);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        if (RESULT_OK != resultCode) {
            return;
        }

        if (REQUEST_SEARCH_LOCATION == requestCode) {
            String response = data.getExtras().getString("data");
            if (response != null) {
                try {
                    daumPostCode = Jackson.stringToObject(response, CDaumPostCode.class);
                    addressView.setText(daumPostCode.getAddress());
                    addressView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (REQUEST_LESSON_INFO == requestCode) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void initializeAgeSpinner() {
        SpinnerAdapter ageSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        ageSpinnerAdapter.addItem(getString(R.string.common_age_choose));
        // 1900년 생 부터 2016년 생까지 입력가능
        for (int i = 2016; i >= 1900; i--) {
            ageSpinnerAdapter.addItem(String.valueOf(i) + getString(R.string.sign_up_age_year));
        }
        ageSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        ageSpinner.setAdapter(ageSpinnerAdapter);
    }

    private void initializeGender() {
        SpinnerAdapter genderSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        genderSpinnerAdapter.addItem(getString(R.string.common_gender_choose));
        genderSpinnerAdapter.addItem(getString(R.string.sign_up_gender_man));
        genderSpinnerAdapter.addItem(getString(R.string.sign_up_gender_woman));
        genderSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        genderSpinner.setAdapter(genderSpinnerAdapter);
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
                    gradeSpinner.setVisibility(View.GONE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 2) {
                    // 초등학교
                    initializeGradeSpinner(6);
                    gradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 3) {
                    // 중학교
                    initializeGradeSpinner(3);
                    gradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 4) {
                    // 고등학교
                    initializeGradeSpinner(3);
                    gradeSpinner.setVisibility(View.VISIBLE);
                } else if (studentStatusSpinner.getSelectedItemPosition() == 5) {
                    // 대학생
                    initializeGradeSpinner(4);
                    gradeSpinner.setVisibility(View.VISIBLE);
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
        gradeSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        gradeSpinner.setAdapter(spinnerAdapter);
    }

    private void initializeDepartmentTypeSpinner() {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        spinnerAdapter.addItem(getString(R.string.sign_up_department_hint));
        spinnerAdapter.addItem(getString(R.string.sign_up_department_type_liberal_arts));
        spinnerAdapter.addItem(getString(R.string.sign_up_department_type_natural_sciences));
        spinnerAdapter.addItem(getString(R.string.sign_up_department_type_art_music_physical));
        spinnerAdapter.addItem(getString(R.string.sign_up_department_type_commercial_and_technical));
        spinnerAdapter.addItem(getString(R.string.sign_up_department_type_none));
        departmentTypeSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        departmentTypeSpinner.setAdapter(spinnerAdapter);
    }

    private void initializeAddress() {
        addressContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, DaumPostCodeActivity.class);
            startActivityForResult(intent, REQUEST_SEARCH_LOCATION);
        });
    }

    private boolean isValidCheckBasic() {
        if (Strings.isNullOrEmpty(nameEditText.getText().toString())) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_name_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (ageSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_age_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (genderSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_gender_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (Strings.isNullOrEmpty(emailEditText.getText().toString())) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_email_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        String password = passwordEditText.getText().toString();
        if (Strings.isNullOrEmpty(password)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_password_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        String passwordCheck = passwordCheckEditText.getText().toString();
        if (!password.equals(passwordCheck)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_password_check_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (studentStatusSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_student_status_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (isExistGrade()) {
            // 초등학교 중학교 고등학교 대학교 일 때 만 gradeSpinner
            if (gradeSpinner.getSelectedItemPosition() == 0) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.sign_up_error_dialog_title)
                        .setMessage(R.string.sign_up_error_dialog_check_grade_message)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return false;
            }
        }

        if (departmentTypeSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_department_type_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (daumPostCode == null) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_address_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        return true;
    }

    private EmailCredentialRequest buildEmailCredentialRequest() {
        return new EmailCredentialRequest.Builder()
                .setEmail(emailEditText.getText().toString())
                .setPassword(passwordEditText.getText().toString())
                .build();
    }

    private StudentBasicInformationRequest buildStudentBasicInformationRequest() {
        return new StudentBasicInformationRequest.Builder()
                .setType(CUserType.STUDENT)
                .setName(nameEditText.getText().toString())
                .setPhoneNumber(DeviceStates.PHONE_NUMBER.get(stateCtx))
                .setGender(getGender())
                .setBirthYear(getBirthYear())
                .setLocation(getLocation())
                .setStudentStatus(getStudentStatus())
                .setGrade(getGrade())
                .setDepartment(getDepartmentType())
                .build();
    }

    private CGender getGender() {
        int selectedItemPosition = genderSpinner.getSelectedItemPosition();
        if (selectedItemPosition == 1) {
            return CGender.MALE;
        } else if (selectedItemPosition == 2){
            return CGender.FEMALE;
        }

        return null;
    }

    private Integer getBirthYear() {
        SpinnerItem spinnerItem = (SpinnerItem) ageSpinner.getSelectedItem();
        String selectedBirthYear = spinnerItem.getTitle();
        // 앞에 4자리 년도만 자른다
        return Integer.valueOf(selectedBirthYear.substring(0, 4));
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
            return gradeSpinner.getSelectedItemPosition();
        }
        return null;
    }

    private CStudentDepartmentType getDepartmentType() {
        switch (departmentTypeSpinner.getSelectedItemPosition()) {
            case 1 : return CStudentDepartmentType.LIBERAL_ARTS;
            case 2 : return CStudentDepartmentType.NATURAL_SCIENCES;
            case 3 : return CStudentDepartmentType.ART_MUSIC_PHYSICAL;
            case 4 : return CStudentDepartmentType.COMMERCIAL_AND_TECHNICAL;
            case 5 : return CStudentDepartmentType.NONE;
        }

        return null;
    }

    private CLocation getLocation() {
        CLocation location = new CLocation();
        location.setAddress(daumPostCode.getAddress());
        location.setSido(daumPostCode.getSido());
        location.setSigungu(daumPostCode.getSigungu());
        location.setZipCode(daumPostCode.getZonecode());
        location.setBname(daumPostCode.getBname());

        // Geocoding: Retrieve GPS location from Address name
        GeocodeUtils geocodeUtils = new GeocodeUtils(getApplicationContext());
        Address gpsAddr = null;

        // R: Road Address, J: Ji-bun Address
        // AutoJibunAddress: Refer to Daum POST-CODE API Document
        if(daumPostCode.getAutoJibunAddress().isEmpty()){
            gpsAddr = geocodeUtils.getGPSfromAddress(daumPostCode.getJibunAddress());
        }
        else {
            gpsAddr = geocodeUtils.getGPSfromAddress(daumPostCode.getAutoJibunAddress());
        }

        if(gpsAddr!= null){
            location.setLatitude(gpsAddr.getLatitude());
            location.setLongitude(gpsAddr.getLongitude());
        }
        else{
            Toast.makeText(context, R.string.common_error_message_network, Toast.LENGTH_LONG).show();
        }

        return location;
    }

    private boolean isExistGrade() {
        return studentStatusSpinner.getSelectedItemPosition() == 2 ||
                studentStatusSpinner.getSelectedItemPosition() == 3 ||
                studentStatusSpinner.getSelectedItemPosition() == 4 ||
                studentStatusSpinner.getSelectedItemPosition() == 5;
    }
}
