package com.riverauction.riverauction.widget.recyclerview;

import android.content.Context;

import com.jhcompany.android.libs.utils.DisplayUtils;
import com.riverauction.riverauction.R;

public final class DividerUtils {
    private DividerUtils() {
    }

    public static HorizontalDividerItemDecoration getHorizontalDividerItemDecoration(Context context) {
        return new HorizontalDividerItemDecoration.Builder(context)
                .color(context.getResources().getColor(R.color.river_auction_white_six))
                .size(DisplayUtils.getPixelFromDP(context, 0.5f))
                .build();
    }
}
