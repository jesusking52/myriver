package com.riverauction.riverauction.feature.register.signup.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.google.common.base.Strings;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.service.auth.request.CertifyPhoneNumberRequest;
import com.riverauction.riverauction.base.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;

public class EnterPhoneNumberActivity extends BaseActivity implements EnterPhoneNumberMvpView {
    private final static int REQUEST_CERTIFY_AUTH_NUMBER = 0x01;

    @Inject EnterPhoneNumberPresenter presenter;

    @Bind(R.id.enter_phone_number_edit_text) EditText phoneNumberEditText;
    @Bind(R.id.next_button) View nextButton;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_enter_phone_number;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        getSupportActionBar().setTitle(R.string.enter_phone_number_action_bar_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nextButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString();
            if (Strings.isNullOrEmpty(phoneNumber)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.common_error_title)
                        .setMessage(R.string.enter_phone_number_error_message)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return;
            }

            CertifyPhoneNumberRequest request = new CertifyPhoneNumberRequest.Builder()
                    .setPhoneNumber(phoneNumber)
                    .build();
            presenter.requestAuthNumber(request);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        if (REQUEST_CERTIFY_AUTH_NUMBER == requestCode && RESULT_OK == resultCode) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //        if (Strings.isNullOrEmpty(phoneNumberEditText.getText().toString())) {
//            new AlertDialog.Builder(context)
//                    .setTitle(R.string.sign_up_error_dialog_title)
//                    .setMessage(R.string.sign_up_error_dialog_check_phone_number_message)
//                    .setPositiveButton(R.string.common_button_confirm, null)
//                    .show();
//            return false;
//        }
//        try {
//            // editText 에 숫자이외에 특수문자를 입력하면 Integer 로 파싱이 안된다 그때 에러를 발생시킨다
//            Integer.valueOf(phoneNumberEditText.getText().toString().trim());
//        } catch (Exception e) {
//            new AlertDialog.Builder(context)
//                    .setTitle(R.string.sign_up_error_dialog_title)
//                    .setMessage(R.string.sign_up_error_dialog_check_phone_number_only_integer_message)
//                    .setPositiveButton(R.string.common_button_confirm, null)
//                    .show();
//            return false;
//        }

    @Override
    public void successRequestAuthNumber(String phoneNumber) {
        Intent intent = new Intent(context, EnterPhoneNumberCodeActivity.class);
        intent.putExtra(EnterPhoneNumberCodeActivity.EXTRA_PHONE_NUMBER, phoneNumber);
        startActivityForResult(intent, REQUEST_CERTIFY_AUTH_NUMBER);
    }

    @Override
    public boolean failRequestAuthNumber(CErrorCause errorCause) {
        return false;
    }
}
