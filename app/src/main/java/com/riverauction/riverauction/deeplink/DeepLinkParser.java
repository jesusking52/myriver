package com.riverauction.riverauction.deeplink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.riverauction.riverauction.feature.main.MainActivity;

public final class DeepLinkParser {
    public static final String AUTHORITY_HOME = "home";

    private DeepLinkParser(){
    }

    public static StartActivityRequest createRequest(Context context, Intent incomingIntent) {
        Uri scheme = incomingIntent.getData();

        if (scheme != null) {
            String authority = scheme.getAuthority();
            if (AUTHORITY_HOME.equals(authority)) {
                return new MainActivityRedirectRequest(MainActivity.EXTRA_ACTIVITY_REDIRECT_HOME, false);
            }
//            else if (AUTHORITY_FRIENDS.equals(authority)) {
//                String path = scheme.getPath();
//                if (path.matches(PATH_FRIENDS_FACEBOOK)) {
//                    return new FriendsFacebookActivityRequest();
//                }
//            }

            // 스킴이 있는데 정의된 게 없으면 앱을 업데이트 하라고 한다.
            return new MainActivityRedirectRequest(MainActivity.EXTRA_ACTIVITY_REDIRECT_HOME, true);
        }

        // 일반적으로 앱을 실행시킬때 동작
        return new MainActivityRedirectRequest(MainActivity.EXTRA_ACTIVITY_REDIRECT_HOME, false);
    }
}
