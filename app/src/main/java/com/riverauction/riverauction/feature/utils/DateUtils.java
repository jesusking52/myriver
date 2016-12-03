package com.riverauction.riverauction.feature.utils;

import android.content.Context;

import com.google.common.primitives.Ints;
import com.riverauction.riverauction.R;

/**
 * 경매 시간같은 {@code Long} 형태의 timestamp 를 유저가 보기 좋은 형태로 바꿔준다
 */
public class DateUtils {

    public static String convertLessonRemainTimeToString(Context context, Long remainTime) {
        return "12:30";
    }

    public static String convertBiddingDateToString(Context context, Long createdAt) {
        final Long currentTime = System.currentTimeMillis();

        Long secSub = (currentTime - createdAt) / 1000;
        if (secSub < 0) {
            secSub = 0L;
        }

        final Long minSub = (secSub) / 60;
        final Long hourSub = (minSub) / 60;
        final Long daySub = (hourSub) / 24;
        final Long weekSub = (daySub) / 7;

        if (secSub < 60) {
            int sec = Ints.checkedCast(secSub);
            return context.getResources().getString(R.string.format_sec_ago, sec);
        }

        if (minSub < 60) {
            int minute = Ints.checkedCast(minSub);
            return context.getResources().getString(R.string.format_minute_ago, minute);
        }

        if (hourSub < 24) {
            int hour = Ints.checkedCast(hourSub);
            return context.getResources().getString(R.string.format_hour_ago, hour);
        }

        if (daySub < 7) {
            int day = Ints.checkedCast(daySub);
            return context.getResources().getString(R.string.format_day_ago, day);
        }

        if (weekSub < 2) {
            int week = Ints.checkedCast(weekSub);
            return context.getResources().getString(R.string.format_week_ago, week);
        }

        return createDateString(context, createdAt);
    }

    public static String createDateString(Context context, Long dateMillis) {
        int flags = android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_YEAR | android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY | android.text.format.DateUtils.FORMAT_ABBREV_MONTH | android.text.format.DateUtils.FORMAT_ABBREV_WEEKDAY;
        return android.text.format.DateUtils.formatDateTime(context, dateMillis, flags);
    }
}
