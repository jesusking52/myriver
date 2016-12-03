package com.riverauction.riverauction.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import com.jhcompany.android.libs.activity.ActivityResultHandler;
import com.jhcompany.android.libs.injection.DaggerService;
import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.gcm.GCMRegister;
import com.riverauction.riverauction.injection.component.ActivityComponent;
import com.riverauction.riverauction.injection.component.DaggerActivityComponent;
import com.riverauction.riverauction.injection.module.ActivityModule;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String INSTANCE_STATE_ACTIVITY_RESULT = ".BaseActivity.INSTANCE_STATE_ACTIVITY_RESULT";
    public final Handler handler = new Handler(Looper.getMainLooper());
    protected ActivityComponent activityComponent;
    protected Context context;
    protected StateCtx stateCtx;
    protected ActivityResultHandler activityResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.getActivityManager().createActivity(this);
        context = this;
        if (getLayoutResId() != -1) {
            setContentView(getLayoutResId());
        }
        activityResultHandler = new ActivityResultHandler();
        ButterKnife.bind(this);
        stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();

        if (savedInstanceState != null) {
            activityResultHandler.restoreInstanceState(savedInstanceState.getBundle(INSTANCE_STATE_ACTIVITY_RESULT));
        }

        // GCM ID 등록
        if (GCMRegister.checkPlayServices(this)) {
            GCMRegister.registerGCM(context);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseApplication.getActivityManager().startActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApplication.getActivityManager().resumeActivity(this);
        LOGGER.debug("onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApplication.getActivityManager().pauseActivity(this);
        LOGGER.debug("onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseApplication.getActivityManager().stopActivity(this);
        LOGGER.debug("onStop()");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getActivityManager().destroyActivity(this);
    }

    @Override
    public Object getSystemService(String name) {
        if (DaggerService.SERVICE_NAME.equals(name)) {
            return getActivityComponent();
        }
        return super.getSystemService(name);
    }

    /**
     * 일반 Activity 의 <code>startActivityForResult</code>와 다른 점을 마지막 파라메터로 Bundle 을 줄 수 있다는 것이다.
     * 마지막에 Bundle 을 넘기면, <code>onActivityResult</code>에서 던진 Bundle 을 받아서 처리할 수 있다.
     * 이는, startActivity 에 대한 결과를 받을때 필요한 정보를 요청에서 전달 할 수 있다는 점에서 편리하다.
     * Activity 가 백그라운드로 빠져도 이때 Bundle 을 그대로 유지되므로 안전한다.
     */
    public void startActivityForResultWithBundle(Intent intent, int requestCode, Bundle bundle) {
        activityResultHandler.putRequestData(requestCode, bundle);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = activityResultHandler.getRequestData(requestCode);
        onActivityResult(requestCode, resultCode, data, bundle);
        LOGGER.debug("onActivityResult {} {} {}", requestCode, resultCode, data);
    }

    /**
     * BaseActivity 의 <code>startActivityForResultWithBundle</code> 메서드를 이용하면 Bundle 을 주고 받을
     * 수 있다. startActivityForResultWithBundle 때 넘긴 Bundle 을 다시 넘겨주는 역할을 한다.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, Bundle bundle) {
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(RiverAuctionApplication.getApplication().getComponent())
                    .build();
        }
        return activityComponent;
    }

    public abstract int getLayoutResId();
}
