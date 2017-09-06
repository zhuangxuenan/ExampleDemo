package com.xuenan.example.base;

import android.view.View;

import com.xuenan.example.commonutil.ToastUtils;

import java.util.Calendar;

/**
 * Author: implistarry
 * Date: 2016/8/26 0026
 * Time: 17:16
 * Usage: 限制点击频率
 */
public abstract class LimitOnClickLisenter implements View.OnClickListener {
    public abstract void onLimitClick(View view);

    private long lastClickTime;

    private String tips = "网络较慢,请耐心等待";//提示信息
    private long limitTime = 1500;//限制时间间隔  单位毫秒
    private boolean isShowTips = false;//是否显示提示信息  默认不显示
    private boolean flag;
    public LimitOnClickLisenter() {
    }

    public LimitOnClickLisenter(boolean isShowTips) {
        this.isShowTips = isShowTips;
    }

    public LimitOnClickLisenter(long limitTime) {
        this.isShowTips = true;
        this.limitTime = limitTime;
    }

    public LimitOnClickLisenter(String tips, long limitTime) {
        this.isShowTips = true;
        this.tips = tips;
        this.limitTime = limitTime;
    }

    @Override
    public void onClick(View v) {
        flag = isFastDoubleClick();
        if (flag) {
            onLimitClick(v);
        } else {
            if (isShowTips){
                ToastUtils.showShort(MyApp.getInstance(),tips);
            }
        }
    }


    public boolean isFastDoubleClick() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if ((currentTime - lastClickTime) > limitTime) {
            lastClickTime = currentTime;
            return true;
        }
         return false;
    }
}
