package com.riverauction.riverauction.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.states.UserStates;

public final class GCMRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger("GCMRegister");
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GCMRegister() {
    }

    public static synchronized void registerGCM(Context context) {
        LOGGER.debug("registerGCM -- start");

        StateCtx stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();

        // USER 가 없다면 더이상 진행할 수 없다.
        if (UserStates.USER.get(stateCtx) == null) {
            LOGGER.debug("registerGCM -- no RELATIONSHIP or USER");
            return;
        }

        // GooglePlayService 가 정상적으로 연결되지 않는다면 끝
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            LOGGER.debug("registerGCM -- no google play service");
            return;
        }

        LOGGER.debug("registerGCM -- startService");
        Intent intent = new Intent(context, GCMTokenRegistrationService.class);
        context.startService(intent);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                LOGGER.info("This device is not google play service supported.");
            }
            return false;
        }
        return true;
    }
}
