package com.riverauction.riverauction.feature.profile.patch.teacher;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CLocation;
import com.riverauction.riverauction.api.model.CTeacher;
import com.riverauction.riverauction.api.model.CUniversityStatus;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.model.CUserType;
import com.riverauction.riverauction.api.service.auth.request.TeacherBasicInformationRequest;
import com.riverauction.riverauction.api.service.user.request.UserPatchRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.common.university.SelectUniversityActivity;
import com.riverauction.riverauction.feature.postcode.DaumPostCodeActivity;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.feature.utils.GeocodeUtils;
import com.riverauction.riverauction.states.UserStates;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;
import com.riverauction.riverauction.widget.spinner.SpinnerItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class ProfileTeacherBasicInfoPatchActivity extends BaseActivity implements ProfileTeacherBasicInfoPatchMvpView {
    private final static int REQUEST_SEARCH_LOCATION = 0x01;
    private final static int REQUEST_SEARCH_UNIVERSITY = 0x02;

    @Inject ProfileTeacherBasicInfoPatchPresenter presenter;

    @Bind(R.id.sign_up_university_container) View universityContainer;
    @Bind(R.id.sign_up_university_view) TextView universityTextView;
    @Bind(R.id.sign_up_university_status_spinner) Spinner universityStatusSpinner;
    @Bind(R.id.sign_up_major_spinner) Spinner majorSpinner;

    @Bind(R.id.sign_up_address_container) View addressContainer;
    @Bind(R.id.sign_up_address) TextView addressView;

    private List<String> majors;

    private CLocation location;
    private String university;
    private CUser user;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_profile_teacher_basic_info_patch;
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
        initializeAddress();
        initializeUniversity();
        initializeUniversityStatusSpinner();
        initializeMajorSpinner();

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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void successPatchUser(CUser user) {

    }

    @Override
    public boolean failPatchUser(CErrorCause errorCause) {
        return false;
    }

    private void setUser(CUser user) {
        CTeacher teacher = user.getTeacher();
        university = teacher.getUniversity();
        universityTextView.setText(university);

        CUniversityStatus universityStatus = teacher.getUniversityStatus();
        switch (universityStatus) {
            case IN_SCHOOL: {
                universityStatusSpinner.setSelection(1);
                break;
            }
            case LEAVE_OF_ABSENCE: {
                universityStatusSpinner.setSelection(2);
                break;
            }
            case GRADUATION: {
                universityStatusSpinner.setSelection(3);
                break;
            }
        }

        // 전공
        String major = teacher.getMajor();
        int index = majors.indexOf(major);
        if (index != -1) {
            majorSpinner.setSelection(index);
        }

        // 장소
        location = user.getLocation();
        addressView.setText(DataUtils.convertLocationToString(location));
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
        majors = FileUtils.readFromAssests(context, "major.txt");
        for (String university : majors) {
            majorSpinnerAdapter.addItem(university);
        }
        majorSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        majorSpinner.setAdapter(majorSpinnerAdapter);
    }

    private boolean isValidCheckBasic() {
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
                .setType(CUserType.TEACHER)
                .setTeacherBasicInformationRequest(buildTeacherBasicInformationRequest())
                .build();
    }

    private TeacherBasicInformationRequest buildTeacherBasicInformationRequest() {
        TeacherBasicInformationRequest.Builder builder = new TeacherBasicInformationRequest.Builder()
                .setType(CUserType.TEACHER)
                .setMajor(getMajor())
                .setUniversityStatus(getUniversityStatus());

        if (location != null) {
            builder.setLocation(location);
        }
        if (!Strings.isNullOrEmpty(university)) {
            builder.setUniversity(university);
            builder.setUniversityRank(DataUtils.getUniversityRank(context, university));
        }

        return builder.build();
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
}
