package com.riverauction.riverauction.feature.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.riverauction.riverauction.R;

import java.util.Arrays;


public final class PermissionUtils {
    private PermissionUtils() {
    }

    public static void checkSelfPermission(Object o, String[] permissions, int requestCode, @Nullable String[] rationales) {
        Preconditions.checkArgument(o instanceof Activity || o instanceof Fragment);
        if (rationales != null) {
            Preconditions.checkArgument(permissions.length == rationales.length);
        }
        final Activity activity = o instanceof Activity ? (Activity) o : null;
        final Fragment fragment = o instanceof Fragment ? (Fragment) o : null;

        boolean checkGrantedResult;
        if (activity != null) {
            checkGrantedResult = checkGranted(activity, permissions);
        } else {
            checkGrantedResult = checkGranted(fragment, permissions);
        }

        if (checkGrantedResult) {
            onRequestPermissionsResult(o, permissions, requestCode, PackageManager.PERMISSION_GRANTED);
        } else {
            shouldShowRequestPermissionRationale(o, permissions, requestCode, rationales, 0);
        }
    }

    private static void shouldShowRequestPermissionRationale(Object o, String[] permissions, int requestCode, @Nullable String[] rationales, int i) {
        Preconditions.checkArgument(o instanceof Activity || o instanceof Fragment);
        final Activity activity = o instanceof Activity ? (Activity) o : null;
        final Fragment fragment = o instanceof Fragment ? (Fragment) o : null;

        if (i >= permissions.length) {
            requestPermissions(o, permissions, requestCode);
            return;
        }

        boolean shouldShowRequestPermissionRationale;
        if (activity != null) {
            shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
        } else {
            shouldShowRequestPermissionRationale = fragment.shouldShowRequestPermissionRationale(permissions[i]);
        }

        if (shouldShowRequestPermissionRationale) {
            if (rationales != null && !Strings.isNullOrEmpty(rationales[i])) {
                new AlertDialog.Builder(activity != null ? activity : fragment.getActivity())
                        .setMessage(rationales[i])
                        .setPositiveButton(R.string.common_button_ok, (dialog, which) -> {
                            shouldShowRequestPermissionRationale(o, permissions, requestCode, rationales, i + 1);
                        })
                        .setOnCancelListener(dialog -> onRequestPermissionsResult(o, permissions, requestCode, PackageManager.PERMISSION_DENIED))
                        .show();
                return;
            }
        }
        shouldShowRequestPermissionRationale(o, permissions, requestCode, rationales, i + 1);
    }

    private static void requestPermissions(Object o, String[] permissions, int requestCode) {
        Preconditions.checkArgument(o instanceof Activity || o instanceof Fragment);
        final Activity activity = o instanceof Activity ? (Activity) o : null;
        final Fragment fragment = o instanceof Fragment ? (Fragment) o : null;

        if (activity != null) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            fragment.requestPermissions(permissions, requestCode);
        }
    }

    private static void onRequestPermissionsResult(Object o, String[] permissions, int requestCode, int grantResult) {
        Preconditions.checkArgument(o instanceof Activity || o instanceof Fragment);
        final Activity activity = o instanceof Activity ? (Activity) o : null;
        final Fragment fragment = o instanceof Fragment ? (Fragment) o : null;

        int[] grantResults = new int[permissions.length];
        Arrays.fill(grantResults, grantResult);

        if (activity != null) {
            ActivityCompat.OnRequestPermissionsResultCallback callback = (ActivityCompat.OnRequestPermissionsResultCallback) activity;
            callback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean checkGranted(int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkGranted(Fragment fragment, String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(fragment.getContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkGranted(Activity activity, String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showDeniedMessage(Context context, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            Toast.makeText(context, R.string.permission_denied_default, Toast.LENGTH_LONG).show();
        }
    }
}
