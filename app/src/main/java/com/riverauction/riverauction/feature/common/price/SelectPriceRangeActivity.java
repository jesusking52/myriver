package com.riverauction.riverauction.feature.common.price;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Strings;
import com.jhcompany.android.libs.wrapper.TextWatcherWrapper;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.base.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;

public class SelectPriceRangeActivity extends BaseActivity implements SelectPriceRangeMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.common.price.SelectPriceRangeActivity";
    public static final String EXTRA_LOW_PRICE = EXTRA_PREFIX + "extra_low_price";
    public static final String EXTRA_HIGH_PRICE = EXTRA_PREFIX + "extra_high_price";

    @Inject SelectPriceRangePresenter presenter;

    @Bind(R.id.select_price_range_low_price_edit_text) EditText lowPriceEditText;
    @Bind(R.id.select_price_range_low_price_button) View lowPriceIcon;
    @Bind(R.id.select_price_range_high_price_edit_text) EditText highPriceEditText;
    @Bind(R.id.select_price_range_high_price_button) View highPriceIcon;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_select_price_range;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.lesson_detail_preferred_price);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lowPriceEditText.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                String price = s.toString();
                if (Strings.isNullOrEmpty(price)) {
                    lowPriceIcon.setSelected(false);
                } else {
                    lowPriceIcon.setSelected(true);
                }
            }
        });

        highPriceEditText.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                String price = s.toString();
                if (Strings.isNullOrEmpty(price)) {
                    highPriceIcon.setSelected(false);
                } else {
                    highPriceIcon.setSelected(true);
                }
            }
        });
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
            String highPriceString = highPriceEditText.getText().toString();
            String lowPriceString = lowPriceEditText.getText().toString();

            if (Strings.isNullOrEmpty(highPriceString) && Strings.isNullOrEmpty(lowPriceString)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.select_price_range_description)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return false;
            }

            if (!Strings.isNullOrEmpty(highPriceString) && !Strings.isNullOrEmpty(lowPriceString)) {
                int lowPrice = Integer.parseInt(lowPriceString);
                int highPrice = Integer.parseInt(highPriceString);

                if (lowPrice > highPrice) {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.common_error_title)
                            .setMessage(R.string.select_price_range_error_message)
                            .setPositiveButton(R.string.common_button_confirm, null)
                            .show();
                    return false;
                }
            }

            if (!Strings.isNullOrEmpty(highPriceString)) {
                int highPrice = Integer.parseInt(highPriceString);
                intent.putExtra(EXTRA_HIGH_PRICE, highPrice);
            }

            if (!Strings.isNullOrEmpty(lowPriceString)) {
                int lowPrice = Integer.parseInt(lowPriceString);
                intent.putExtra(EXTRA_LOW_PRICE, lowPrice);
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
}
