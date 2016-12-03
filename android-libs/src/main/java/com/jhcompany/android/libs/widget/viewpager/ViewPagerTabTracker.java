package com.jhcompany.android.libs.widget.viewpager;

import android.content.Context;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.WeakHashMap;

public final class ViewPagerTabTracker {

    private ViewPagerTabTracker() {
    }

    private static final String TAB_HOME = "home";
    private static final String TAB_NEARBY = "nearby";
    private static final String TAB_THEMES = "themes";
    private static final Map<Integer, String> TABS = Maps.newHashMap();
    static {
        TABS.put(0, TAB_HOME);
        TABS.put(1, TAB_NEARBY);
        TABS.put(2, TAB_THEMES);
    }

    private static final WeakHashMap<Integer, ViewPagerTabTrackerListener> TAB_VIEWS = new WeakHashMap<>();

    private static int OLD_POSITION;
    private static int NEW_POSITION;

    public static void registerTabCallback(ViewPagerTabTrackerListener viewPagerTabTrackerListener, int position, Context context) {
        TAB_VIEWS.put(position, viewPagerTabTrackerListener);

        // HomeView 에서 registerTabCallback() 보다 ViewPager.OnPageChangeListener 의 onPageSelected() 가 먼저 호출되면
        // onEnterTab() 이 안불리므로 registerTabCallback 시의 ViewPager index 를 비교해서 onEnterTab() 을 호출한다.
        if (position == NEW_POSITION) {
            invokeOnEnterTab(position, context);
        }
    }

    public static void unRegisterTabCallback(int position) {
        TAB_VIEWS.remove(position);
    }

    public static void notifyTabSelected(int position, Context context) {
        OLD_POSITION = NEW_POSITION;
        NEW_POSITION = position;
        boolean isTabChanged = OLD_POSITION != NEW_POSITION;

        if (isTabChanged) {
            invokeOnLeaveTab(OLD_POSITION);
            invokeOnEnterTab(NEW_POSITION, context);
        }
    }

    // static 이라서 android back 키 로 Activity 가 내려갔다 올라와도 position 의 위치가 기존 걊으로 설정되어있다.
    // 그래서 MainActivity 에서 ViewPager 에 HomeView, NearbyView 등 4가지 View 를 만들기 전에
    // clearPosition() 을 호출해서 ViewPager 값을 초기화 시킨다.
    public static void clearPosition() {
        OLD_POSITION = 0;
        NEW_POSITION = 0;
        TAB_VIEWS.clear();
    }

    public static int getCurrentPosition() {
        return NEW_POSITION;
    }

    private static void invokeOnLeaveTab(int position) {
        if (!TAB_VIEWS.containsKey(position)) {
            return;
        }

        ViewPagerTabTrackerListener viewPagerTabTrackerListener = TAB_VIEWS.get(position);
        viewPagerTabTrackerListener.onLeaveTab();
    }

    private static void invokeOnEnterTab(int position, Context context) {
        if (!TAB_VIEWS.containsKey(position)) {
            return;
        }
        // GA 로 Page 의 Screen 정보를 전송한다.

        ViewPagerTabTrackerListener viewPagerTabTrackerListener = TAB_VIEWS.get(position);
        viewPagerTabTrackerListener.onEnterTab();
    }
}

