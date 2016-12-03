package com.riverauction.riverauction.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.common.base.Strings;
import com.jhcompany.android.libs.log.Logger;
import com.jhcompany.android.libs.log.LoggerFactory;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.api.service.user.request.UserGCMAddRequest;
import com.riverauction.riverauction.data.DataManager;
import com.riverauction.riverauction.rxjava.APISubscriber;
import com.riverauction.riverauction.states.PushStates;
import com.riverauction.riverauction.states.UserStates;

import rx.android.schedulers.AndroidSchedulers;

public class GCMTokenRegistrationService extends IntentService {
    private static final Logger LOGGER = LoggerFactory.getLogger("GCMTokenRegistrationService");

    private DataManager dataManager;

    public GCMTokenRegistrationService() {
        super("GCMTokenRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StateCtx stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        dataManager = RiverAuctionApplication.getApplication().getComponent().dataManager();
        CUser user = UserStates.USER.get(stateCtx);
        if (user == null) {
            LOGGER.debug("User should exist");
            return;
        }

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            if (Strings.isNullOrEmpty(token)) {
                LOGGER.debug("no GCM Id");
                return;
            }

            String savedToken = PushStates.GCM_REGISTRATION_ID.get(stateCtx);
            if (!token.equals(savedToken)) {
                // 토큰이 바뀌면 서버에 다시 업로드 한다
                PushStates.GCM_REGISTERED_ON_SERVER.set(stateCtx, false);
            }

            if (PushStates.GCM_REGISTERED_ON_SERVER.get(stateCtx)) {
                LOGGER.debug("GCM id already register");
                return;
            }

            PushStates.GCM_REGISTRATION_ID.set(stateCtx, token);
            LOGGER.debug("GCM token : " + token);

            UserGCMAddRequest request = new UserGCMAddRequest.Builder()
                    .setKey(token)
                    .build();

            dataManager.postGCM(user.getId(), request)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new APISubscriber<Boolean>() {

                        @Override
                        public void onNext(Boolean result) {
                            super.onNext(result);
                            if (result != null && result) {
                                PushStates.GCM_REGISTERED_ON_SERVER.set(stateCtx, true);
                            }
                        }

                        @Override
                        public boolean onErrors(Throwable e) {
                            return super.onErrors(e);
                        }
                    });
            LOGGER.debug("GCM token upload success");
        } catch (Exception e) {
            LOGGER.debug("GCM token upload failed");
        }

    }
}
