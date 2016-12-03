package com.riverauction.riverauction.feature.common.dayofweek;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.jhcompany.android.libs.utils.DisplayUtils;
import com.jhcompany.android.libs.widget.recyclerview.HorizontalDividerItemDecoration;
import com.riverauction.riverauction.R;
import com.riverauction.riverauction.api.model.CDayOfWeekType;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectDayOfWeeksView extends LinearLayout {
    LayoutInflater inflater;
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private DayOfWeekAdapter adapter;

    public SelectDayOfWeeksView(Context context) {
        super(context);
        initialize(context);
    }

    public SelectDayOfWeeksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SelectDayOfWeeksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate(R.layout.layout_select_day_of_week, this);
        ButterKnife.bind(this);

        adapter = new DayOfWeekAdapter(makeDayOfWeeks());

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context)
                .color(getResources().getColor(R.color.river_auction_white_six))
                .size(DisplayUtils.getPixelFromDP(context, 0.5f))
                .build());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<CDayOfWeekType> makeDayOfWeeks() {
        List<CDayOfWeekType> dayOfWeekTypes = Lists.newArrayList();
        dayOfWeekTypes.add(CDayOfWeekType.MON);
        dayOfWeekTypes.add(CDayOfWeekType.TUE);
        dayOfWeekTypes.add(CDayOfWeekType.WED);
        dayOfWeekTypes.add(CDayOfWeekType.THU);
        dayOfWeekTypes.add(CDayOfWeekType.FRI);
        dayOfWeekTypes.add(CDayOfWeekType.SAT);
        dayOfWeekTypes.add(CDayOfWeekType.SUN);
        return dayOfWeekTypes;
    }

    public List<CDayOfWeekType> getSelectedDayOfWeeks() {
        return Lists.newArrayList(adapter.getSelectedDayOfWeeks());
    }

    /**
     * Recycler View 의 Holder
     */
    public static class DayOfWeekItemHolder extends RecyclerView.ViewHolder {
        public View iconView;
        public TextView titleView;

        public DayOfWeekItemHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.item_select_day_of_week_icon);
            titleView = (TextView) itemView.findViewById(R.id.item_select_day_of_week_text_view);
        }
    }

    /**
     * Adapter
     */
    private class DayOfWeekAdapter extends RecyclerView.Adapter {
        private static final int TYPE_ALL = 1;
        private static final int TYPE_DAY_OF_WEEK = 2;
        private List<CDayOfWeekType> dayOfWeekTypes;
        private List<CDayOfWeekType> selectedDayOfWeekTypes;

        public DayOfWeekAdapter(List<CDayOfWeekType> dayOfWeekTypes) {
            this.dayOfWeekTypes = dayOfWeekTypes;
            this.selectedDayOfWeekTypes = Lists.newArrayList();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ALL) {
                return new DayOfWeekItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_day_of_week, parent, false));
            } else if (viewType == TYPE_DAY_OF_WEEK) {
                return new DayOfWeekItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_day_of_week, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (TYPE_ALL == getItemViewType(position)) {
                DayOfWeekItemHolder dayOfWeekItemHolder = (DayOfWeekItemHolder) holder;
                dayOfWeekItemHolder.titleView.setText(R.string.common_select_all);
                dayOfWeekItemHolder.itemView.setOnClickListener(v -> {
                    if (dayOfWeekItemHolder.iconView.isSelected()) {
                        dayOfWeekItemHolder.iconView.setSelected(false);
                        selectedDayOfWeekTypes.clear();
                    } else {
                        dayOfWeekItemHolder.iconView.setSelected(true);
                        selectedDayOfWeekTypes.clear();
                        selectedDayOfWeekTypes.addAll(dayOfWeekTypes);
                    }
                    notifyDataSetChanged();
                });

                // 초기화
                if (selectedDayOfWeekTypes.size() == getItemCount() - 1) {
                    dayOfWeekItemHolder.iconView.setSelected(true);
                } else {
                    dayOfWeekItemHolder.iconView.setSelected(false);
                }
            } else if (TYPE_DAY_OF_WEEK == getItemViewType(position)) {
                DayOfWeekItemHolder dayOfWeekItemHolder = (DayOfWeekItemHolder) holder;
                CDayOfWeekType dayOfWeekType = dayOfWeekTypes.get(position - 1);
                boolean isSelected = false;
                for (CDayOfWeekType selectDayOfWeek : selectedDayOfWeekTypes) {
                    if (selectDayOfWeek == dayOfWeekType) {
                        isSelected = true;
                        break;
                    }
                }
                dayOfWeekItemHolder.iconView.setSelected(isSelected);
                switch (dayOfWeekType) {
                    case MON: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_mon_long);
                        break;
                    }
                    case TUE: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_tue_long);
                        break;
                    }
                    case WED: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_wed_long);
                        break;
                    }
                    case THU: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_thu_long);
                        break;
                    }
                    case FRI: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_fri_long);
                        break;
                    }
                    case SAT: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_sat_long);
                        break;
                    }
                    case SUN: {
                        dayOfWeekItemHolder.titleView.setText(R.string.common_sun_long);
                        break;
                    }
                }

                dayOfWeekItemHolder.itemView.setOnClickListener(v -> {
                    if (dayOfWeekItemHolder.iconView.isSelected()) {
                        dayOfWeekItemHolder.iconView.setSelected(false);
                        selectedDayOfWeekTypes.remove(dayOfWeekType);
                    } else {
                        dayOfWeekItemHolder.iconView.setSelected(true);
                        selectedDayOfWeekTypes.add(dayOfWeekType);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            // select all 때문에 +1
            return dayOfWeekTypes.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_ALL;
            } else {
                return TYPE_DAY_OF_WEEK;
            }
        }

        public List<CDayOfWeekType> getSelectedDayOfWeeks() {
            return selectedDayOfWeekTypes;
        }
    }
}
