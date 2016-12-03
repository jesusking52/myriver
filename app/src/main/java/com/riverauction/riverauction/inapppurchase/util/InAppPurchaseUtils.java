package com.riverauction.riverauction.inapppurchase.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.feature.profile.shop.ShopActivity;

public class InAppPurchaseUtils {

    public static String getMessage(int responseCode) {
        String reason;
        switch (responseCode) {
            case -1:
                reason = "UNKNOWN";
                break;
            case 1:
                reason = "BILLING_RESPONSE_RESULT_USER_CANCELED";
                break;
            case 2:
                reason = "BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE";
                break;
            case 3:
                reason = "BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE";
                break;
            case 4:
                reason = "BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE";
                break;
            case 5:
                reason = "BILLING_RESPONSE_RESULT_DEVELOPER_ERROR";
                break;
            case 6:
                reason = "BILLING_RESPONSE_RESULT_ERROR";
                break;
            case 7:
                reason = "BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED";
                break;
            case 8:
                reason = "BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED";
                break;
            default:
                throw new IllegalStateException(String.format("Unknown response code: %d", responseCode));
        }

        return "Reason = " + reason;
    }

    /**
     * 코인이 부족할때 보여주는 dialog
     */
        public static void showInsufficientCoinDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.insufficient_coins_dialog_title)
                .setMessage(R.string.insufficient_coins_dialog_message)
                .setPositiveButton(R.string.common_button_go_to_shop, (dialog, which) -> {
                    Intent intent = new Intent(context, ShopActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton(R.string.common_button_cancel, null)
                .show();
    }
}
