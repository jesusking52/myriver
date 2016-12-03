package com.riverauction.riverauction.feature.common.dayofweek;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDayOfWeekType;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectDayOfWeeksDialog extends Dialog {
    @Bind(R.id.close_button) View closeButton;
    @Bind(R.id.confirm_button) View confirmButton;
    @Bind(R.id.select_day_of_week_view) SelectDayOfWeeksView selectDayOfWeeksView;

    private OnSelectedDayOfWeeksListener onSelectedDayOfWeeksListener;

    public SelectDayOfWeeksDialog(Context context,  OnSelectedDayOfWeeksListener onSelectedDayOfWeeksListener) {
        super(context);
        this.onSelectedDayOfWeeksListener = onSelectedDayOfWeeksListener;
        initialize(context);
    }

    public SelectDayOfWeeksDialog(Context context, int theme) {
        super(context, theme);
        initialize(context);
    }

    protected SelectDayOfWeeksDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize(context);
    }

    private void initialize(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_day_of_weeks);
        ButterKnife.bind(this);

        closeButton.setOnClickListener(v -> dismiss());

        confirmButton.setOnClickListener(v -> {
            if (onSelectedDayOfWeeksListener != null) {
                onSelectedDayOfWeeksListener.getSelectedDayOfWeeks(selectDayOfWeeksView.getSelectedDayOfWeeks());
            }
            dismiss();
        });
    }

    public interface OnSelectedDayOfWeeksListener {
        void getSelectedDayOfWeeks(List<CDayOfWeekType> selectedDayOfWeekTypes);
    }
}
