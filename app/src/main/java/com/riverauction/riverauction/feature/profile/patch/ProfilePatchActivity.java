package com.riverauction.riverauction.feature.profile.patch;

import android.os.Bundle;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CErrorCause;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BaseActivity;

import javax.inject.Inject;

public class ProfilePatchActivity extends BaseActivity implements ProfilePatchMvpView {

    @Inject ProfilePatchPresenter presenter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_profile_patch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        presenter.attachView(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void successPatchUser(CUser user) {
        // TODO:
    }

    @Override
    public boolean failPatchUser(CErrorCause errorCause) {
        return false;
    }
}
