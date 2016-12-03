package com.riverauction.riverauction.gcm;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.jhcompany.android.libs.jackson.Jackson;
import com.jhcompany.android.libs.preference.StateCtx;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.RiverAuctionApplication;
import com.riverauction.riverauction.api.model.CGCMData;
import com.riverauction.riverauction.api.model.CGCMNotification;
import com.riverauction.riverauction.api.model.CUser;
import com.riverauction.riverauction.feature.main.MainActivity;
import com.riverauction.riverauction.states.UserStates;

public class RiverAuctionGCMListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final int AWAKE_TO_READ_ID = 0;
    private static final int CHECK_TO_DELETE_APP_ID = 1;
    private static final String KEY_DATA = "data";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NOTIFICATION = "notification";

    private StateCtx stateCtx;

    // push data
    private Integer userId;
    private String message;
    private CGCMData gcmData;
    private CGCMNotification gcmNotification;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        StateCtx stateCtx = RiverAuctionApplication.getApplication().getComponent().stateCtx();
        Log.d("RiverAuctionGCMListenerService", "push success!!");

        CUser user = UserStates.USER.get(stateCtx);

        if (user == null) {
            // user 가 없으면 푸시를 받지 않는다.
            return;
        }

        if (data != null && !data.isEmpty()) {
            try {
                String str = data.getString(KEY_USER_ID);
                userId = Integer.parseInt(str);
            } catch (Exception e) {
                return;
            }

            if (!user.getId().equals(userId)) {
                // push 를 받는 user 가 다르면 처리하지않는다
                return;
            }

            try {
                String str = data.getString(KEY_DATA);
                gcmData = Jackson.stringToObject(str, CGCMData.class);
            } catch (Exception e) {
                gcmData = null;
            }

            try {
                message = data.getString(KEY_MESSAGE);
                // android system notification
                showNotification(message);
            } catch (Exception e) {
                message = null;
            }
        }
    }

    private void showNotification(String notificationContentText) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACTIVITY_REDIRECT_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setContentText(notificationContentText)
                .setAutoCancel(true)
                .setVibrate(new long[]{300, 300})
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(getApplicationContext()).notify(AWAKE_TO_READ_ID, builder.build());
    }
}
