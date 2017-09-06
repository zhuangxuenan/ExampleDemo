package com.xuenan.example.commonutil;


import android.app.Activity;

import java.util.Stack;

/**
 * 主要功能: 管理和回收Act
 * @Prject: CommonUtilLibrary
 * @Package: com.jingewenku.abrahamcaijin.commonutil
 * @author: AbrahamCaiJin
 * @date: 2017年05月03日 16:37
 * @Copyright: 个人版权所有
 * @Company:
 * @version: 1.0.0
 */
/**
 getScreenManager      : 单例堆栈集合对象
 removeActivity        : 堆栈中销毁并移除
 removeAllActivity     : 栈中销毁并移除所有Act对象
 currentActivity       : 取当前Act对象
 getCurrentActivityName: 获得当前Act的类名
 addActivity           : 将Act纳入推栈集合中
 exitApp               : 退出栈中所有Activity
 */
public class AppDavikActivityMgr {

    
    //存储ActivityStack
    private static Stack<Activity> activityStack = new Stack<>();

    //单例模式
    private static AppDavikActivityMgr instance;


    /**
     * 单列堆栈集合对象
     * @return AppDavikActivityMgr 单利堆栈集合对象
     */
    public static AppDavikActivityMgr getScreenManager() {
        if (instance == null) {
            synchronized (AppDavikActivityMgr.class){
                if (instance == null) {
                    instance = new AppDavikActivityMgr();
                }
            }
        }
        return instance;
    }
    

    /**remove the finished activity in the list.
     *the activity is removed from activityList
     * 堆栈中销毁并移除
     * @param activity 指定Act对象
     */
    public void removeActivity(Activity activity) {
        AppLogMessageMgr.i(activity != null ? activity.toString() : "");
        if (null != activity) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }



    /**
     * 栈中销毁并移除所有Act对象
     */
    public void removeAllActivity() {
        if (null != activityStack && activityStack.size() > 0) {
                //创建临时集合对象
                Stack<Activity> activityTemp = new Stack<Activity>();
                for (Activity activity : activityStack) {
                    if (null != activity) {
                        //添加到临时集合中
                        activityTemp.add(activity);
                        //结束Activity
                        activity.finish();
                    }
                }
                activityStack.removeAll(activityTemp);
        }
        AppLogMessageMgr.i("removeAllActivity");
        System.gc();
        System.exit(0);
    }


    /**
     * 获取当前Act对象
     * @return Activity 当前act
     */
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty()){
            activity = activityStack.lastElement();
        }
        AppLogMessageMgr.i(activity + "");
        return activity;
    }


    /**
     * 获得当前Act的类名
     * @return String
     */
    public String getCurrentActivityName() {
        String actSimpleName = "";
        if (!activityStack.empty()) {
            actSimpleName = activityStack.lastElement().getClass().getSimpleName();
        }
        AppLogMessageMgr.i(actSimpleName);
        return actSimpleName;
    }


    /**add the activity in to a list end
     * 将Act纳入推栈集合中
     * @param activity Act对象
     */
    public void addActivity(Activity activity) {
        AppLogMessageMgr.i(activity != null ? activity.toString() : "");
        if (null == activityStack) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    
    

    /**
     * 退出栈中所有Activity
     * @param cls
     * @return void
     */
    public void exitApp(Class<?> cls) {
        AppLogMessageMgr.i("exitApp-->>占用内存：" + Runtime.getRuntime().totalMemory());
        while (true) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            removeActivity(activity);
        }
        System.gc();
        System.exit(0);
    }
}
