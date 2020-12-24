package com.maxwell.youchat.controller;

import android.util.Log;

import com.maxwell.youchat.activity.BaseActivity;

import java.util.HashMap;

public class ActivityController {
    private static HashMap<Class<?>, BaseActivity> activityMap = new HashMap<>();

    public static final String TAG = "ActivityController";

    public static void addActivity(Class<?> clazz, BaseActivity activity) {
        activityMap.put(clazz, activity);
        Log.d(TAG, "Add " + clazz.toString());
    }

    public static void removeActivity(Class<?> clazz) {
        activityMap.remove(clazz);
        Log.d(TAG, "Remove " + clazz.toString());
    }

    public static boolean isActivityExist(Class<?> clazz) {
        return activityMap.containsKey(clazz);
    }

    public static void removeAllActivity() {
        activityMap = new HashMap<>();
    }
}
