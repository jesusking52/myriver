package com.riverauction.riverauction.feature.register.signup.teacher;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
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
import com.jhcompany.android.libs.jackson.Jackson;
import com.jhcompany.android.libs.utils.FileUtils;
import com.jhcompany.android.libs.utils.Lists2;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDaumPostCode;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CUniversityStatus;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.request.TeacherBasicInformationRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.common.university.SelectUniversityActivity;
import com.riverauction.riverauction.feature.postcode.DaumPostCodeActivity;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.feature.utils.GeocodeUtils;
import com.riverauction.riverauction.states.DeviceStates;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;
import com.riverauction.riverauction.widget.spinner.SpinnerItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class SignUpTeacherActivity extends BaseActivity implements SignUpTeacherMvpView {
    private final static int REQUEST_SEARCH_LOCATION = 0x01;
    private final static int REQUEST_SEARCH_UNIVERSITY = 0x02;
    private final static int REQUEST_LESSON_INFO = 0x03;

    @Bind(R.id.sign_up_next_button) View nextButton;

    // basic
    @Bind(R.id.sign_up_teacher_name) EditText nameEditText;
    @Bind(R.id.sign_up_age_spinner) Spinner ageSpinner;
    @Bind(R.id.sign_up_gender_spinner) Spinner genderSpinner;

    @Bind(R.id.sign_up_email) EditText emailEditText;
    @Bind(R.id.sign_up_password) EditText passwordEditText;
    @Bind(R.id.sign_up_password_check) EditText passwordCheckEditText;

    @Bind(R.id.sign_up_university_container) View universityContainer;
    @Bind(R.id.sign_up_university_view) TextView universityTextView;
    @Bind(R.id.sign_up_university_status_spinner) Spinner universityStatusSpinner;
    @Bind(R.id.sign_up_major_spinner) Spinner majorSpinner;

    @Bind(R.id.sign_up_address_container) View addressContainer;
    @Bind(R.id.sign_up_address) TextView addressView;

    @Inject SignUpTeacherPresenter presenter;

    private CDaumPostCode daumPostCode;
    private String university;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_sign_up_teacher;
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

        initializeAddress();
        initializeUniversity();
        initializeUniversityStatusSpinner();
        initializeMajorSpinner();

        nextButton.setOnClickListener(v -> {
            if (isValidCheckBasic()) {
                Intent intent = new Intent(context, SignUpTeacherLessonInfoActivity.class);
                intent.putExtra(SignUpTeacherLessonInfoActivity.EXTRA_EMAIL_CREDENTIAL_REQUEST, ParcelableWrappers.wrap(buildEmailCredentialRequest()));
                intent.putExtra(SignUpTeacherLessonInfoActivity.EXTRA_BASIC_REQUEST, ParcelableWrappers.wrap(buildTeacherBasicInformationRequest()));
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
                    addressView.setTextColor(Color.BLACK);
                    addressView.setText(daumPostCode.getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (REQUEST_SEARCH_UNIVERSITY == requestCode) {
            ArrayList<Parcelable> parcelableArrayList = data.getParcelableArrayListExtra(SelectUniversityActivity.EXTRA_UNIVERSITIES);
            if (parcelableArrayList != null) {
                List<String> universities = ParcelableWrappers.unwrap(parcelableArrayList);
                if (!Lists2.isNullOrEmpty(universities)) {
                    university = universities.get(0);
                    universityTextView.setTextColor(Color.BLACK);
                    universityTextView.setText(university);
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

    private void initializeAddress() {
        addressContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, DaumPostCodeActivity.class);
            startActivityForResult(intent, REQUEST_SEARCH_LOCATION);
        });
    }

    private void initializeUniversity() {
        universityContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectUniversityActivity.class);
            intent.putExtra(SelectUniversityActivity.EXTRA_SELECT_ONLY_ONE, true);
            startActivityForResult(intent, REQUEST_SEARCH_UNIVERSITY);
        });
    }

    private void initializeUniversityStatusSpinner() {
        SpinnerAdapter universityStatusSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        universityStatusSpinnerAdapter.addItem(getString(R.string.common_university_status_choose));
        universityStatusSpinnerAdapter.addItem(getString(R.string.common_in_school));
        universityStatusSpinnerAdapter.addItem(getString(R.string.common_leave_of_absence));
        universityStatusSpinnerAdapter.addItem(getString(R.string.common_graduation));
        universityStatusSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        universityStatusSpinner.setAdapter(universityStatusSpinnerAdapter);
    }

    private void initializeMajorSpinner() {
        SpinnerAdapter majorSpinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        majorSpinnerAdapter.addItem(getString(R.string.common_major_choose));
        List<String> majors = FileUtils.readFromAssests(context, "major.txt");
        for (String university : majors) {
            majorSpinnerAdapter.addItem(university);
        }
        majorSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        majorSpinner.setAdapter(majorSpinnerAdapter);
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

        if (Strings.isNullOrEmpty(university)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_university_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (universityStatusSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_university_status_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        if (majorSpinner.getSelectedItemPosition() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_major_message)
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

    private TeacherBasicInformationRequest buildTeacherBasicInformationRequest() {
        return new TeacherBasicInformationRequest.Builder()
                .setType(CUserType.TEACHER)
                .setName(nameEditText.getText().toString())
                .setPhoneNumber(DeviceStates.PHONE_NUMBER.get(stateCtx))
                .setGender(getGender())
                .setBirthYear(getBirthYear())
                .setLocation(getLocation())
                .setUniversity(university)
                .setUniversityRank(DataUtils.getUniversityRank(context, university))
                .setMajor(getMajor())
                .setUniversityStatus(getUniversityStatus())
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

    private String getMajor() {
        SpinnerItem spinnerItem = (SpinnerItem) majorSpinner.getSelectedItem();
        return spinnerItem.getTitle();
    }

    private CUniversityStatus getUniversityStatus() {
        int selectedItemPosition = universityStatusSpinner.getSelectedItemPosition();

        if (selectedItemPosition == 1) {
            return CUniversityStatus.IN_SCHOOL;
        } else if (selectedItemPosition == 2) {
            return CUniversityStatus.LEAVE_OF_ABSENCE;
        } else if (selectedItemPosition == 3) {
            return CUniversityStatus.GRADUATION;
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
}
