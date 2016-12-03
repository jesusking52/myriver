package com.riverauction.riverauction.feature.profile.patch.student;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jhcompany.android.libs.jackson.Jackson;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDaumPostCode;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CStudent;
import com.riverauction.riverauction.api.model.CStudentDepartmentType;
import com.riverauction.riverauction.api.model.CStudentStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.StudentBasicInformationRequest;
import com.riverauction.riverauction.api.service.user.request.UserPatchRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.postcode.DaumPostCodeActivity;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.GeocodeUtils;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;

public class ProfileStudentBasicInfoPatchActivity extends BaseActivity implements ProfileStudentBasicInfoPatchMvpView {
    private final static int REQUEST_SEARCH_LOCATION = 0x01;

    @Inject
    ProfileStudentBasicInfoPatchPresenter presenter;

    // basic
    @Bind(R.id.sign_up_student_status_spinner) Spinner studentStatusSpinner;
    @Bind(R.id.sign_up_grade_spinner) Spinner gradeSpinner;
    @Bind(R.id.sign_up_department_spinner) Spinner departmentTypeSpinner;

    @Bind(R.id.sign_up_address_container) View addressContainer;
    @Bind(R.id.sign_up_address) TextView addressView;

    // address 정보
    private CLocation location;

    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_student_basic_info_patch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        user = UserStates.USER.get(stateCtx);

        getSupportActionBar().setTitle(R.string.sign_up_basic_info_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // basic
        initializeStudentStatusSpinner();
        initializeDepartmentTypeSpinner();
        initializeAddress();

        setUser(user);
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
            if (isValidCheckBasic()) {
                presenter.patchUser(user.getId(), buildUserPatchRequest());
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                    CDaumPostCode daumPostCode = Jackson.stringToObject(response, CDaumPostCode.class);
                    location = getLocation(daumPostCode);
                    addressView.setText(daumPostCode.getAddress());
                    addressView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    /**
     * User data 로 초기화
     * @param user
     */
    private void setUser(CUser user) {
        CStudent student = user.getStudent();
        CStudentStatus studentStatus = student.getStudentStatus();
        switch (studentStatus) {
            case KINDERGARTEN: {
                studentStatusSpinner.setSelection(1);
                break;
            }
            case ELEMENTARY_SCHOOL: {
                studentStatusSpinner.setSelection(2);
                break;
            }
            case MIDDLE_SCHOOL: {
                studentStatusSpinner.setSelection(3);
                break;
            }
            case HIGH_SCHOOL: {
                studentStatusSpinner.setSelection(4);
                break;
            }
            case UNIVERSITY: {
                studentStatusSpinner.setSelection(5);
                break;
            }
            case RETRY_UNIVERSITY: {
                studentStatusSpinner.setSelection(6);
                break;
            }
            case ORDINARY: {
                studentStatusSpinner.setSelection(7);
                break;
            }
        }

        handler.postDelayed(() -> {
            Integer grade = student.getGrade();
            if (grade != null && grade > 0) {
                gradeSpinner.setSelection(grade);
            }
        }, 500);

        CStudentDepartmentType studentDepartmentType = student.getDepartment();
        switch (studentDepartmentType) {
            case LIBERAL_ARTS: {
                departmentTypeSpinner.setSelection(1);
                break;
            }
            case NATURAL_SCIENCES: {
                departmentTypeSpinner.setSelection(2);
                break;
            }
            case ART_MUSIC_PHYSICAL: {
                departmentTypeSpinner.setSelection(3);
                break;
            }
            case COMMERCIAL_AND_TECHNICAL: {
                departmentTypeSpinner.setSelection(4);
                break;
            }
            case NONE: {
                departmentTypeSpinner.setSelection(5);
                break;
            }
        }

        location = user.getLocation();
        addressView.setText(location.getSido() + " " + location.getSigungu() + " " + location.getBname());
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

        if (location == null) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sign_up_error_dialog_title)
                    .setMessage(R.string.sign_up_error_dialog_check_address_message)
                    .setPositiveButton(R.string.common_button_confirm, null)
                    .show();
            return false;
        }

        return true;
    }

    private UserPatchRequest buildUserPatchRequest() {
        return new UserPatchRequest.Builder()
                .setType(CUserType.STUDENT)
                .setStudentBasicInformationRequest(buildStudentBasicInformationRequest())
                .build();
    }

    private StudentBasicInformationRequest buildStudentBasicInformationRequest() {
        StudentBasicInformationRequest.Builder builder = new StudentBasicInformationRequest.Builder()
                .setType(CUserType.STUDENT)
                .setStudentStatus(getStudentStatus())
                .setGrade(getGrade())
                .setDepartment(getDepartmentType());

        if (location != null) {
            builder.setLocation(location);
        }

        return builder.build();
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

    private CLocation getLocation(CDaumPostCode daumPostCode) {
        if (daumPostCode == null) {
            return null;
        }

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

    @Override
    public void successPatchUser(CUser user) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean failPatchUser(CErrorCause errorCause) {
        return false;
    }
}
