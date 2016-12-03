package com.riverauction.riverauction.feature.teacher.filter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.Lists2;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CGender;
import com.riverauction.riverauction.api.model.CSubject;
import com.riverauction.riverauction.api.service.teacher.params.GetTeachersParams;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.eventbus.RiverAuctionEventBus;
import com.riverauction.riverauction.eventbus.TeacherFilterEvent;
import com.riverauction.riverauction.feature.common.gender.SelectGenderActivity;
import com.riverauction.riverauction.feature.common.location.SelectLocationActivity;
import com.riverauction.riverauction.feature.common.price.SelectPriceRangeActivity;
import com.riverauction.riverauction.feature.common.subject.SelectSubjectsActivity;
import com.riverauction.riverauction.feature.common.university.SelectUniversityActivity;
import com.riverauction.riverauction.feature.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class TeacherFilterActivity extends BaseActivity implements TeacherFilterMvpView{
    private final static int REQUEST_FILTER_SUBJECTS = 0x01;
    private final static int REQUEST_FILTER_PRICE = 0x02;
    private final static int REQUEST_FILTER_GRADE = 0x03;
    private final static int REQUEST_FILTER_LOCATION = 0x04;
    private final static int REQUEST_FILTER_GENDER = 0x05;

    @Inject TeacherFilterPresenter presenter;

    @Bind(R.id.filter_available_subjects_container) View subjectsContainer;
    @Bind(R.id.filter_available_subjects_icon) View subjectsIconView;
    @Bind(R.id.filter_available_subjects_view) TextView subjectsView;
    private List<CSubject> selectedSubjects = Lists.newArrayList();

    @Bind(R.id.filter_price_container) View priceContainer;
    @Bind(R.id.filter_price_icon) View priceIconView;
    @Bind(R.id.filter_price_view) TextView priceView;
    private Integer highPrice;
    private Integer lowPrice;

    @Bind(R.id.filter_grade_container) View universityContainer;
    @Bind(R.id.filter_grade_icon) View universityIconView;
    @Bind(R.id.filter_grade_view) TextView universityView;
    private List<String> universities = Lists.newArrayList();

    @Bind(R.id.filter_location_container) View locationContainer;
    @Bind(R.id.filter_location_icon) View locationIconView;
    @Bind(R.id.filter_location_view) TextView locationView;
    private Integer minZoneCode;
    private Integer maxZoneCode;

    @Bind(R.id.filter_gender_container) View genderContainer;
    @Bind(R.id.filter_gender_icon) View genderIconView;
    @Bind(R.id.filter_gender_view) TextView genderView;
    private CGender preferredGender;

    @Bind(R.id.filter_apply_button) View applyButton;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_teacher_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.filter_action_bar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subjectsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectSubjectsActivity.class);
            if (!Lists2.isNullOrEmpty(selectedSubjects)) {
                intent.putParcelableArrayListExtra(SelectSubjectsActivity.EXTRA_SELECTED_SUBJECTS, ParcelableWrappers.wrap(selectedSubjects));
            }
            startActivityForResult(intent, REQUEST_FILTER_SUBJECTS);
        });

        priceContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectPriceRangeActivity.class);
            startActivityForResult(intent, REQUEST_FILTER_PRICE);
        });

        universityContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectUniversityActivity.class);
            intent.putExtra(SelectUniversityActivity.EXTRA_SELECT_ONLY_ONE, false);
            startActivityForResult(intent, REQUEST_FILTER_GRADE);
        });

        locationContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectLocationActivity.class);
            startActivityForResult(intent, REQUEST_FILTER_LOCATION);
        });

        genderContainer.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectGenderActivity.class);
            startActivityForResult(intent, REQUEST_FILTER_GENDER);
        });

        applyButton.setOnClickListener(v -> {
            search();
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter_initialization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_filter_initialization) {
            // 초기화
            if (!Lists2.isNullOrEmpty(selectedSubjects)) {
                selectedSubjects.clear();
            }
            subjectsIconView.setSelected(false);
            subjectsView.setText(R.string.filter_no_subject);

            // 가격
            highPrice = null;
            lowPrice = null;
            priceIconView.setSelected(false);
            priceView.setText(R.string.filter_no);

            // 대학
            if (!Lists2.isNullOrEmpty(universities)) {
                universities.clear();
            }
            universityIconView.setSelected(false);
            universityView.setText(R.string.filter_no);

            // 장소
            maxZoneCode = null;
            minZoneCode = null;
            locationIconView.setSelected(false);
            locationView.setText(R.string.filter_no);

            // 성별
            preferredGender = null;
            genderIconView.setSelected(false);
            genderView.setText(R.string.filter_no_gender);
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

        if (REQUEST_FILTER_SUBJECTS == requestCode) {
            ArrayList<Parcelable> parcelableArrayList = data.getParcelableArrayListExtra(SelectSubjectsActivity.EXTRA_SELECTED_SUBJECTS);
            if (parcelableArrayList != null) {
                selectedSubjects = ParcelableWrappers.unwrap(parcelableArrayList);
                if (Lists2.isNullOrEmpty(selectedSubjects)) {
                    subjectsIconView.setSelected(false);
                    subjectsView.setText(R.string.filter_no_subject);
                } else {
                    subjectsIconView.setSelected(true);
                    subjectsView.setText(DataUtils.convertSubjectToString(selectedSubjects));
                }
            }
        } else if (REQUEST_FILTER_PRICE == requestCode) {
            int lowPrice = data.getIntExtra(SelectPriceRangeActivity.EXTRA_LOW_PRICE, 0);
            int highPrice = data.getIntExtra(SelectPriceRangeActivity.EXTRA_HIGH_PRICE, 0);
            if (lowPrice != 0) {
                this.lowPrice = lowPrice;
            }
            if (highPrice != 0) {
                this.highPrice = highPrice;
            }

            priceIconView.setSelected(true);
            String priceRangeString = "";
            if (this.lowPrice != null && this.highPrice != null) {
                priceRangeString = getString(R.string.filter_price_low, lowPrice) + " " + getString(R.string.filter_price_high, highPrice);
            } else if (this.lowPrice != null && this.highPrice == null) {
                priceRangeString = getString(R.string.filter_price_low, lowPrice);
            } else if (this.lowPrice == null && this.highPrice != null) {
                priceRangeString = getString(R.string.filter_price_high, highPrice);
            }
            priceView.setText(priceRangeString);
        } else if (REQUEST_FILTER_GRADE == requestCode) {
            ArrayList<Parcelable> parcelableArrayList = data.getParcelableArrayListExtra(SelectUniversityActivity.EXTRA_UNIVERSITIES);
            if (parcelableArrayList != null) {
                universities = ParcelableWrappers.unwrap(parcelableArrayList);
            }
            universityView.setText(Joiner.on(" ").skipNulls().join(universities));
            universityIconView.setSelected(true);
        } else if (REQUEST_FILTER_LOCATION == requestCode) {
            int minZoneCode = data.getIntExtra(SelectLocationActivity.EXTRA_MIN_ZON_CODE, -1);
            int maxZoneCode = data.getIntExtra(SelectLocationActivity.EXTRA_MAX_ZON_CODE, -1);
            String sidoSigungu = data.getStringExtra(SelectLocationActivity.EXTRA_SELECTED_SIDO_SIGUNGU);
            if (minZoneCode != -1) {
                this.minZoneCode = minZoneCode;
            }
            if (maxZoneCode != -1) {
                this.maxZoneCode = maxZoneCode;
            }
            locationIconView.setSelected(true);
            locationView.setText(sidoSigungu);
        } else if (REQUEST_FILTER_GENDER == requestCode) {
            String genderString = data.getStringExtra(SelectGenderActivity.EXTRA_GENDER);
            if (Strings.isNullOrEmpty(genderString)) {
                return;
            }

            preferredGender = CGender.valueOf(genderString);
            if (CGender.MALE == preferredGender) {
                genderView.setText(R.string.sign_up_gender_man);
            } else if (CGender.FEMALE == preferredGender) {
                genderView.setText(R.string.sign_up_gender_woman);
            } else {
                genderView.setText(R.string.filter_no_gender);
            }
            genderIconView.setSelected(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    /**
     * filter option 이 하나라도 걸려있는지 확인
     */
    private boolean hasFilterOption() {
        if (Lists2.isNullOrEmpty(selectedSubjects) && highPrice == null && lowPrice == null &&
                universities == null && minZoneCode == null && maxZoneCode == null&& preferredGender == null) {
            // 필터 하나도 설정 안함
            return false;
        }

        return true;
    }

    private void search() {
        GetTeachersParams.Builder builder = new GetTeachersParams.Builder();
        builder.setSubjects(DataUtils.convertSubjectsToIds(selectedSubjects));
        builder.setLessThanPrice(highPrice);
        builder.setMoreThanPrice(lowPrice);
        builder.setUniversities(universities);
        builder.setMinZipCode(minZoneCode);
        builder.setMaxZipCode(maxZoneCode);
        builder.setGender(preferredGender);

        RiverAuctionEventBus.getEventBus().post(new TeacherFilterEvent(builder));
    }
}
