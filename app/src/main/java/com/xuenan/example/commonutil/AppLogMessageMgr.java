package com.xuenan.example.commonutil;

import android.util.Log;

import java.util.Locale;


/**
 * 主要功能： 系统日志输出工具类 log工具类
 * @Prject: CommonUtilLibrary
 * @Package: com.jingewenku.abrahamcaijin.commonutil
 * @author: AbrahamCaiJin
 * @date: 2017年05月04日 14:13
 * @Copyright: 个人版权所有
 * @Company:
 * @version: 1.0.0
 */
public class AppLogMessageMgr {
    //是否输出
    private static boolean isDebug = true;
    //得到当前调用类的名称
    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(AppLogMessageMgr.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }
    //得到方法名行号等信息
    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(AppLogMessageMgr.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
    }
    /*
     * 设置debug模式(true:打印日志  false：不打印)
     */
    public static void isEnableDebug(boolean isDebug){
        AppLogMessageMgr.isDebug = isDebug;
    }
    public static void i(String msg){
        if(isDebug){
            Log.v(getTag(), msg != null ? buildMessage(msg) : "");
        }
    }
    public static void d(String msg){
        if(isDebug){
            Log.d(getTag(), msg != null ? buildMessage(msg) : "");
        }
    }
    public static void w(String msg){
        if(isDebug){
            Log.w(getTag(), msg != null ? buildMessage(msg) : "");
        }
    }
    public static void e(String msg){
        if(isDebug){
            Log.e(getTag(), msg != null ? buildMessage(msg) : "");
        }
    }
    public static void v( String msg){
        if(isDebug){
            Log.v(getTag(), msg != null ? buildMessage(msg) : "");
        }
    }
}