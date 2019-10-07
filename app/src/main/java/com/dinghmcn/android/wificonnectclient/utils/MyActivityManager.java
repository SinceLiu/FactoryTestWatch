package com.dinghmcn.android.wificonnectclient.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MyActivityManager {
    private static MyActivityManager sInstance = new MyActivityManager();
    private List<WeakReference<Activity>> mActivity;

    private MyActivityManager() {
        mActivity = new ArrayList<>();
    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public void clearAllActivity() {
        for (WeakReference<Activity> weakReference : mActivity) {
            try {
                weakReference.get().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentActivity(Activity activity) {
        mActivity.add(new WeakReference<>(activity));
    }


}
