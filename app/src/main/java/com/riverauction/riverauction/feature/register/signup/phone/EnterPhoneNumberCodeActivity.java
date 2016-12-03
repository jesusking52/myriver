package com.riverauction.riverauction.feature.register.signup.phone;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.service.auth.request.CertifyPhoneNumberRequest;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.states.DeviceStates;

import javax.inject.Inject;

import butterknife.Bind;

public class EnterPhoneNumberCodeActivity extends BaseActivity implements EnterPhoneNumberCodeMvpView {
    private static final String EXTRA_PREFIX = "com.riverauction.riverauction.feature.register.signup.phone.EnterPhoneNumberCodeActivity";
    public static final String EXTRA_PHONE_NUMBER = EXTRA_PREFIX + "extra_phone_number";

    @Inject EnterPhoneNumberCodePresenter presenter;

    @Bind(R.id.enter_phone_number_code_description) TextView descriptionView;
    @Bind(R.id.enter_phone_number_code_edit_text) EditText codeEditText;
    @Bind(R.id.next_button) View nextButton;

    private String phoneNumber;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_enter_phone_number_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
        getDataFromBundle(getIntent().getExtras());

        getSupportActionBar().setTitle(R.string.enter_phone_number_code_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nextButton.setOnClickListener(v -> {
            String codeString = codeEditText.getText().toString();
            if (Strings.isNullOrEmpty(codeString)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.enter_phone_number_code_error_message)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return;
            }

            Integer code = Integer.valueOf(codeString);

            CertifyPhoneNumberRequest request = new CertifyPhoneNumberRequest.Builder()
                    .setPhoneNumber(phoneNumber)
                    .setAuthNumber(code)
                    .build();
            presenter.certifyAuthNumber(request);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void getDataFromBundle(Bundle bundle) {
        phoneNumber = bundle.getString(EXTRA_PHONE_NUMBER);
        if (Strings.isNullOrEmpty(phoneNumber)) {
            throw new IllegalStateException("phoneNumber must be exist");
        }

        descriptionView.setText(getString(R.string.enter_phone_number_code_description, phoneNumber));
    }

    @Override
    public void successCertifyAuthNumber() {
        DeviceStates.PHONE_NUMBER.set(stateCtx, phoneNumber);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean failCertifyAuthNumber(CErrorCause errorCause) {
        return false;
    }
}
