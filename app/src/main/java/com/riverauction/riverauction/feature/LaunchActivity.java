package com.riverauction.riverauction.feature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.base.BaseActivity;
import com.riverauction.riverauction.deeplink.DeepLinkParser;
import com.riverauction.riverauction.deeplink.StartActivityRequest;
import com.riverauction.riverauction.feature.main.MainActivity;
import com.riverauction.riverauction.feature.register.IntroActivity;
import com.riverauction.riverauction.states.UserStates;

public class LaunchActivity extends BaseActivity {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final int ACTIVITY_FOR_RESULT_REGISTER = 0;

    private static final String PREFIX = "com.riverauction.riverauction.feature.LaunchActivity.";
    private static final String KEY_REGISTER_HANDLED = PREFIX + ".register_handled";
    private static final String KEY_MAIN_ACTIVITY_HANDLED = PREFIX + ".main_activity_handled";

    private boolean isRegisterHandled;
    private boolean isMainActivityHandled;

    @Override
    public int getLayoutResId() {
        return -1;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isRegisterHandled = savedInstanceState.getBoolean(KEY_REGISTER_HANDLED, false);
            isMainActivityHandled = savedInstanceState.getBoolean(KEY_MAIN_ACTIVITY_HANDLED, false);
        }

        startOnCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_REGISTER_HANDLED, isRegisterHandled);
        outState.putBoolean(KEY_MAIN_ACTIVITY_HANDLED, isMainActivityHandled);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
        super.onActivityResult(requestCode, resultCode, data, bundle);

        if (resultCode == Activity.RESULT_CANCELED) {
            setResult(resultCode);
            finish();
            return;
        }

        switch (requestCode) {
            case ACTIVITY_FOR_RESULT_REGISTER:
                redirectToNext();
                break;
        }
    }

    /**
     * {@link #onCreate(Bundle)} 에서 알맞은 Activity 로 이동시키는 함수이다. {@link #onActivityResult(int, int, Intent)} 에서 사용하는
     * {@link #redirectToNext()} 와는 조금 동작이 다르다. (이미 처리된 동작이 있다면, 다음으로 넘어가지 않고 return 시킨다.)
     * 액티비티 회수되었을 때 이미 뜬 Activity 를 안띄우기 위해서 코드가 조금 다르다
     */
    private void startOnCreate() {
        CUser user = UserStates.USER.get(stateCtx);
        if (user == null) {
            if (!isRegisterHandled) {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_REGISTER);
            }
            isRegisterHandled = true;
            return;
        }

        if (isRegisterHandled) {
            return;
        }

        // 메인 화면
        if (!isMainActivityHandled) {
            Intent outGoingIntent = createRedirectNextIntent(getIntent());
            outGoingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(outGoingIntent);
            finish();

            isMainActivityHandled = true;
        }
    }

    private void redirectToNext() {
        CUser user = UserStates.USER.get(stateCtx);
        if (user == null) {
            if (!isRegisterHandled) {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_REGISTER);
            }

            isRegisterHandled = true;
            isMainActivityHandled = false;
            return;
        }

        if (!isMainActivityHandled) {
            Intent outGoingIntent = createRedirectNextIntent(getIntent());
            outGoingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(outGoingIntent);
            finish();

            isRegisterHandled = false;
            isMainActivityHandled = true;
        } else {
            finish();
        }
    }

    private Intent createRedirectNextIntent(Intent incomingIntent){
        if (incomingIntent == null){
            return new Intent(this, MainActivity.class);
        }

        final StartActivityRequest startActivityRequest = DeepLinkParser.createRequest(this, incomingIntent);
        if (RiverAuctionApplication.getActivityManager().isMainActivityInStack()) {
            return StartActivityRequest.createMainActivityInStackActivity(this, startActivityRequest);
        } else {
            return StartActivityRequest.createMainActivityInStackActivity(this, startActivityRequest);
        }
    }

    /**
     * LaunchActivity 를 향하는 Intent 를 생성한다. (+ CLEAR_TOP, NO_ANIMATION, NEW_TASK flag 와 함께)
     */
    public static void startLaunchActivity(Context context) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
