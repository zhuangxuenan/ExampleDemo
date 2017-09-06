package com.xuenan.example.commonutil;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by implistarry on 2016/3/9 0009.
 * 设置顶部状态栏覆盖部分高度及背景色     沉浸式状态栏
 */
public class SystemStatesBarUtils {
    public static final int NONECOLOR = 0;//默认颜色

    /**
     * *设置顶部留空高度,根据状态栏高度
     *
     * @param context
     * @param view_topview
     * @param backgroundColorId
     */
    public static void setTopViewHeightColor(Context context, View view_topview, int backgroundColorId) {
        ViewGroup.LayoutParams linearParams = view_topview.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = AppScreenMgr.getStatusHeight(context);
        linearParams.width = AppScreenMgr.getScreenWidth(context);
        view_topview.setLayoutParams(linearParams);
        if (NONECOLOR != backgroundColorId) {
            //view_topview.setBackgroundResource(backgroundColorId);
            view_topview.setBackgroundColor(ContextCompat.getColor(context,backgroundColorId));
        }
    }

    /**
     * 自定义状态栏高度
     * @param context
     * @param height
     * @param view_topview
     * @param backgroundColorId
     */
    public static void setTopViewHeightColor(Context context,int height, View view_topview, int backgroundColorId) {
        ViewGroup.LayoutParams linearParams = view_topview.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = height;
        linearParams.width = AppScreenMgr.getScreenWidth(context);
        view_topview.setLayoutParams(linearParams);
        if (NONECOLOR != backgroundColorId) {
            view_topview.setBackgroundColor(ContextCompat.getColor(context,backgroundColorId));
        }
    }

}
