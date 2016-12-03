package com.riverauction.riverauction.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Intent intent = new Intent(this, GCMTokenRegistrationService.class);
        startService(intent);
    }
}

