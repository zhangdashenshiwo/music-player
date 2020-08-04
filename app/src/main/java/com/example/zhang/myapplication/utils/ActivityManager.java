package com.example.zhang.myapplication.utils;


import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class ActivityManager extends Application {
    //建立链表集合
    private static List<Activity> activityList = new LinkedList<Activity>();

    //实例
    private static ActivityManager instance;

    //私有化构造方法
    private ActivityManager() {
    }

    /**
     * 获取唯一实例
     * @return ActivityManager 实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }


    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static List<Activity> getActivity() {
        return activityList;
    }

    public void exit() {

        //从后面往前面Kill
        for (int i = activityList.size() - 1; i >= 0; i--) {
            Activity activity = activityList.get(i);
            if (activity!=null && !activity.isFinishing()) {
                activity.finish();
            }
        }

        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }
}
