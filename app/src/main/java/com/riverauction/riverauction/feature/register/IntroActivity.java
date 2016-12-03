package com.riverauction.riverauction.feature.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.service.auth.request.EmailCredentialRequest;
import com.riverauction.riverauction.api.service.auth.response.IssueTokenResult;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.feature.postcode.DaumPostCodeActivity;
import com.riverauction.riverauction.feature.register.signup.phone.EnterPhoneNumberActivity;
import com.riverauction.riverauction.feature.register.signup.student.SignUpStudentActivity;
import com.riverauction.riverauction.feature.register.signup.teacher.SignUpTeacherActivity;
import com.riverauction.riverauction.states.UserStates;

import javax.inject.Inject;

import butterknife.Bind;

public class IntroActivity extends BaseActivity implements IntroMvpView {
    private static final int REQUEST_CERTIFY_PHONE_NUMBER_FOR_STUDENT = 0x01;
    private static final int REQUEST_CERTIFY_PHONE_NUMBER_FOR_TEACHER = 0x02;
    private static final int REQUEST_SIGN_UP = 0x03;

    @Inject IntroPresenter presenter;

    @Bind(R.id.intro_email_edit_text) EditText emailEditText;
    @Bind(R.id.intro_password_edit_text) EditText passwordEditText;
    @Bind(R.id.intro_sign_in_button) View signInButton;
    @Bind(R.id.intro_sign_up_student_button) View signUpStudentButton;
    @Bind(R.id.intro_sign_up_teacher_button) View signUpTeacherButton;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_intro;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);

        signInButton.setOnClickListener(v -> {
            EmailCredentialRequest request = new EmailCredentialRequest.Builder()
                    .setEmail(emailEditText.getText().toString())
                    .setPassword(passwordEditText.getText().toString())
                    .build();
            presenter.issueToken(request);
        });

        signUpStudentButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EnterPhoneNumberActivity.class);
            startActivityForResult(intent, REQUEST_CERTIFY_PHONE_NUMBER_FOR_STUDENT);
        });

        signUpTeacherButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EnterPhoneNumberActivity.class);
            startActivityForResult(intent, REQUEST_CERTIFY_PHONE_NUMBER_FOR_TEACHER);
        });
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

        if (REQUEST_CERTIFY_PHONE_NUMBER_FOR_STUDENT == requestCode) {
            Intent intent = new Intent(context, SignUpStudentActivity.class);
            startActivityForResult(intent, REQUEST_SIGN_UP);
        } else if (REQUEST_CERTIFY_PHONE_NUMBER_FOR_TEACHER == requestCode) {
            Intent intent = new Intent(context, SignUpTeacherActivity.class);
            startActivityForResult(intent, REQUEST_SIGN_UP);
        } else if (REQUEST_SIGN_UP == requestCode) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void successIssueToken(IssueTokenResult issueTokenResult) {
        UserStates.USER.set(stateCtx, issueTokenResult.getUser());
        UserStates.ACCESS_TOKEN.set(stateCtx, issueTokenResult.getToken());
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean failIssueToken(CErrorCause errorCause) {
        switch (errorCause) {
            case PASSWORD_NOT_MATCHED:
            case NO_SUCH_ELEMENT: {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.intro_sign_in_error_dialog_title)
                        .setMessage(R.string.intro_sign_in_error_dialog_message)
                        .setPositiveButton(R.string.common_button_confirm, null)
                        .show();
                return true;
            }
        }
        return false;
    }
}
