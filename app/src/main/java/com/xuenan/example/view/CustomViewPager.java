/*
 * @author http://blog.csdn.net/singwhatiwanna
 */

package com.xuenan.example.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义Viewpager禁止滑动
 * */
public class CustomViewPager extends ViewPager {  
  
    private boolean isScrollable = false;  
  
    public CustomViewPager(Context context) {  
        super(context);  
    }  
  
    public CustomViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    if (isScrollable == false) {
    return false;
    } else {
    return super.onTouchEvent(ev);
    }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (isScrollable == false) {
    return false;
    } else {
    return super.onInterceptTouchEvent(ev);
    }

    }
}