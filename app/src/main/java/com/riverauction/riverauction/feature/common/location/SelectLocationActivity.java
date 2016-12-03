package com.riverauction.riverauction.feature.common.location;

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
import com.riverauction.riverauction.api.model.CSido;
import com.riverauction.riverauction.api.model.CSigungu;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.register.signup.SpinnerItemSelectedListener;
import com.riverauction.riverauction.feature.utils.DataUtils;
import com.riverauction.riverauction.widget.spinner.SpinnerAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class SelectLocationActivity extends BaseActivity implements SelectLocationMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.location.SelectLocationActivity";
    public static final String EXTRA_MIN_ZON_CODE = EXTRA_PREFIX + "extra_min_zon_code";
    public static final String EXTRA_MAX_ZON_CODE = EXTRA_PREFIX + "extra_max_zon_code";
    public static final String EXTRA_SELECTED_SIDO_SIGUNGU = EXTRA_PREFIX + "extra_selected_sido_sigungu";

    @Inject
    SelectLocationPresenter presenter;

    @Bind(R.id.select_location_sido_spinner) Spinner sidoSpinner;
    @Bind(R.id.select_location_sigungu_spinner) Spinner sigunguSpinner;

    // 선택한 도시의 최소, 최대 우편번호
    private Integer minZoneCode;
    private Integer maxZonCode;
    private CSido selectedSido;
    private CSigungu selectedSigungu;

    private boolean isFirstLoad = true;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.select_location_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<CSido> sidoList = DataUtils.getSidoSigunguForFilter(context);
        initializeSidoSpinner(sidoList);
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

            if (minZoneCode == null && maxZonCode == null) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.select_location_description)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return false;
            }

            intent.putExtra(EXTRA_SELECTED_SIDO_SIGUNGU, getSelectedSidoSigungu());
            intent.putExtra(EXTRA_MAX_ZON_CODE, maxZonCode);
            intent.putExtra(EXTRA_MIN_ZON_CODE, minZoneCode);
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

    private String getSelectedSidoSigungu() {
        String result = selectedSido.getSido();

        if (selectedSigungu != null) {
            result += " ";
            result += selectedSigungu.getName();
        }

        return result;
    }

    private void initializeSidoSpinner(List<CSido> sidos) {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        spinnerAdapter.addItem(getString(R.string.select_location_sido_hint));
        for (CSido sido : sidos) {
            spinnerAdapter.addItem(sido.getSido());
        }
        sidoSpinner.setAdapter(spinnerAdapter);

        // student status 선택에 따라서 gradeSpinner 가 변경된다
        sidoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView titleView = (TextView) view.findViewById(R.id.item_spinner_title);
                if (position == 0) {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish));
                    selectedSido = null;
                    minZoneCode = null;
                    maxZonCode = null;
                    sigunguSpinner.setVisibility(View.GONE);
                } else {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                    CSido sido = sidos.get(position - 1);
                    selectedSido = sido;
                    minZoneCode = sido.getMinZoneCode();
                    maxZonCode = sido.getMaxZoneCode();
                    isFirstLoad = true;
                    initializeSigunguSpinner(sido.getSigungu());
                    sigunguSpinner.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initializeSigunguSpinner(List<CSigungu> sigungus) {
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.item_spinner);
        spinnerAdapter.addItem(getString(R.string.select_location_sigungu_hint));
        for (CSigungu sigungu : sigungus) {
            spinnerAdapter.addItem(sigungu.getName());
        }
        sigunguSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(context));
        sigunguSpinner.setAdapter(spinnerAdapter);
        sigunguSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstLoad) {
                    isFirstLoad = false;
                    return;
                }
                TextView titleView = (TextView) view.findViewById(R.id.item_spinner_title);
                if (position == 0) {
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish));
                    selectedSigungu = null;
                    minZoneCode = null;
                    maxZonCode = null;
                } else {
                    CSigungu sigungu = sigungus.get(position - 1);
                    titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
                    selectedSigungu = sigungu;
                    minZoneCode = sigungu.getMinZoneCode();
                    maxZonCode = sigungu.getMaxZoneCode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
