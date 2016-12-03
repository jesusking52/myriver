package com.riverauction.riverauction.feature.common.dayofweek;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.Lists2;
import com.jhcompany.android.libs.utils.ParcelableWrappers;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDayOfWeekType;
import com.riverauction.riverauction.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

//TODO: 안쓴다
public class SelectDayOfWeeksActivity extends BaseActivity implements SelectDayOfWeeksMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.dayofweek.price.SelectDayOfWeeksActivity";
    public static final String EXTRA_SELECTED_DAY_OF_WEEKS = EXTRA_PREFIX + "extra_selected_day_of_weeks";

    @Inject SelectDayOfWeeksPresenter presenter;

    @Bind(R.id.select_day_of_week_view) SelectDayOfWeeksView selectDayOfWeeksView;

    private List<CDayOfWeekType> selectedDayOfWeeks = Lists.newArrayList();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_day_of_weeks;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

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
            selectedDayOfWeeks = selectDayOfWeeksView.getSelectedDayOfWeeks();
            if (!Lists2.isNullOrEmpty(selectedDayOfWeeks)) {
                intent.putParcelableArrayListExtra(EXTRA_SELECTED_DAY_OF_WEEKS, ParcelableWrappers.wrap(selectedDayOfWeeks));
            }
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
