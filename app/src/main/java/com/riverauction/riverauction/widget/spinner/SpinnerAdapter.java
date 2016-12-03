package com.riverauction.riverauction.widget.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.riverauction.riverauction.R;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {

    public static final String TAG_DROPDOWN = "dropdown";
    public static final String TAG_NON_DROPDOWN = "non_dropdown";

    private ArrayList<SpinnerItem> mItems = Lists.newArrayList();

    private Context mContext;
    private int itemSpinnerResId;

    public SpinnerAdapter(Context context, int itemSpinnerResId) {
        mContext = context;
        this.itemSpinnerResId = itemSpinnerResId;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public SpinnerItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || !convertView.getTag().toString().equals(TAG_NON_DROPDOWN)) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemSpinnerResId, parent, false);
            convertView.setTag(TAG_NON_DROPDOWN);
        }
        TextView title = (TextView) convertView.findViewById(R.id.item_spinner_title);
        title.setText(getItem(position).getTitle());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null || !convertView.getTag().toString().equals(TAG_DROPDOWN)) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_spinner_dropdown, parent, false);
            convertView.setTag(TAG_DROPDOWN);
        }

        TextView title = (TextView) convertView.findViewById(R.id.item_spinner_dropdown_title);
        title.setText(getItem(position).getTitle());

        return convertView;
    }

    public void addItem(String title) {
        mItems.add(new SpinnerItem(title));
    }

    public void removeItem(int position) {
        mItems.remove(position);
    }
}
