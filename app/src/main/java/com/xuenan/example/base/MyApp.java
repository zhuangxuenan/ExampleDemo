package com.xuenan.example.base;

import android.app.Activity;
import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xuenan.example.commonutil.CrashHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * application类通畅用来进行一些初始化的操作
 * Created by xuenan on 2017/02/25.
 */
public class MyApp extends Application {
    private static MyApp instance;
    private static RequestQueue queue;
    private List<Activity> activityList = new ArrayList<>();

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        queue = Volley.newRequestQueue(this.getApplicationContext());
    }

    //入口
    public static RequestQueue getQueue() {
        return queue;
    }
}
