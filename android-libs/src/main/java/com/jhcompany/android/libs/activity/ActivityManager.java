package com.jhcompany.android.libs.activity;

import android.app.Activity;

import com.google.common.collect.Sets;
import com.jhcompany.android.libs.utils.WeakReferences;

import java.util.Set;

public class ActivityManager {

    public interface OnActivityStateChangedListener {
        void onActivityStateChanged(ActivityManager manager);
    }

    private Class mainActivityClass;
    private final Set<Integer> mCreatedActivityCnt;
    private final Set<Integer> mRunningActivityCnt;
    private final Set<Integer> mShowingActivityCnt;

    private Activity mCurrentShownActivity;
    private Class<? extends Activity> mPreviousShownActivityClazz;

    /** For MainActivity existence & uniqueness check. For uniqueness, boolean is not enough. */
    private Activity mActiveMainActivity = null;
    private WeakReferences<OnActivityStateChangedListener> mListeners = new WeakReferences<OnActivityStateChangedListener>();

    public ActivityManager(Class mainActivityClass) {
        this.mainActivityClass = mainActivityClass;
        mCreatedActivityCnt = Sets.newHashSet();
        mRunningActivityCnt = Sets.newHashSet();
        mShowingActivityCnt = Sets.newHashSet();
    }

    public void createActivity(Activity activity) {
        mCreatedActivityCnt.add(activity.hashCode());
        if (activity.getClass().equals(mainActivityClass)) {
            mActiveMainActivity = activity;
        }
    }

    public void startActivity(Activity activity) {
        mRunningActivityCnt.add(activity.hashCode());
    }

    public void resumeActivity(Activity activity) {
        mShowingActivityCnt.add(activity.hashCode());
        mCurrentShownActivity = activity;
    }

    public void pauseActivity(Activity activity) {
        mShowingActivityCnt.remove(activity.hashCode());
        if (mCurrentShownActivity == activity) {
            mPreviousShownActivityClazz = mCurrentShownActivity.getClass();
            mCurrentShownActivity = null;
        }
    }

    public void stopActivity(Activity activity) {
        mRunningActivityCnt.remove(activity.hashCode());
    }

    public void destroyActivity(Activity activity) {
        mCreatedActivityCnt.remove(activity.hashCode());
        if (activity.getClass().equals(mainActivityClass)) {
            if (mActiveMainActivity == activity) {
                mActiveMainActivity = null;
            }
        }
    }

    public int getShowingActivityCount() {
        return mShowingActivityCnt.size();
    }

    public boolean isMainActivityInStack() {
        return mActiveMainActivity != null;
    }

    public Activity getCurrentShownActivity() {
        return mCurrentShownActivity;
    }
}
