package com.xuenan.example.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Author: admin
 * Date: 2015/12/9
 * Time: 9:34
 * Usage:
 */
public abstract class BaseDialogFragment extends DialogFragment {
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();
    /**
     * 依附的Activity
     **/
    protected AppCompatActivity mContext = null;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        //缓存当前依附的activity
        mContext = (BaseActivity) activity;
    }
    //外部方法获取activity对象
    public FragmentActivity getAttachActivity() {
        return mContext;
    }

    public interface DialogFragmentListener {
        void doSomething(Object obj);
    }

    public static DialogFragmentListener mDialogFragmentListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(bindLayout(), container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(view);
        doBusiness(getAttachActivity());
        return view;
    }

    /**
     * 是否显示标题头
     *
     * @param is: false 不显示 true 显示
     */
    public void setIsTitle(boolean is) {
        if (!is) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
    //处理业务逻辑
    public abstract void doBusiness(Context mContext);
    //初始化控件
    public abstract void initView(View view);
    //绑定布局
    public abstract int bindLayout();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialogFragmentListener = null;
    }
}
