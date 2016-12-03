package com.jhcompany.android.libs.widget.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.common.base.Preconditions;
import com.jhcompany.android.libs.R;

public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        int pageCount = pagerAdapter.getCount();

        if (getChildCount() > 0) {
            removeAllViews();
        }

        populateViews(pageCount);
        viewPager.setOnPageChangeListener(this);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    private void populateViews(int pageCount) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < pageCount; i++) {
            final int position = i;
            ImageView item = (ImageView) inflater.inflate(R.layout.item_view_pager_indicator, this, false);
            Preconditions.checkState(item != null);
            assert item != null;
            item.setImageResource(R.drawable.ic_dashboard_pageindicator_off);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(position);
                }
            });
            addView(item);
        }
        onPageSelected(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView item = (ImageView) getChildAt(i);
            Preconditions.checkState(item != null);
            assert item != null;

            if (i == position) {
                item.setImageResource(R.drawable.ic_dashboard_pageindicator_on);
            } else {
                item.setImageResource(R.drawable.ic_dashboard_pageindicator_off);
            }
        }
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }
}
