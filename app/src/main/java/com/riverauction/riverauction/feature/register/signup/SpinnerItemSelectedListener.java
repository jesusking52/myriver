package com.riverauction.riverauction.feature.register.signup;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.riverauction.riverauction.R;

/**
 * position 이 0 일때 textColor 를 {@code river_auction_greyish} 로 하고
 * 그외에 실제 데이터를 선택했을 때는 textColor 를 {@code river_auction_greyish_brown} 로 한다
 */
public class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Context context;

    public SpinnerItemSelectedListener(Context context) {
        this.context = context;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView titleView = (TextView) view.findViewById(R.id.item_spinner_title);
        if (position == 0) {
            titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish));
        } else {
            titleView.setTextColor(context.getResources().getColor(R.color.river_auction_greyish_brown));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
