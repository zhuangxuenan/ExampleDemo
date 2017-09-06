package com.xuenan.example.inter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

/**
 * Fragment接口
 */
public interface IBaseFragment {

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    int bindLayout();

    /**
     * 初始化界面参数
     *
     * @param parms
     */
    void initParms(Bundle parms);

    /**
     * 初始化控件
     */
    void initView(final View view);

    /**
     * 业务处理操作（onCreateView方法中调用）
     *
     * @param mContext 当前Activity对象
     */
    void doOnceBusiness(Context mContext, View view)throws PackageManager.NameNotFoundException, IOException;

    void onCreateSavedInstanceState(Bundle savedInstanceState, View view);
}
