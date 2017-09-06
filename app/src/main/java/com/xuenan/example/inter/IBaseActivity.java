package com.xuenan.example.inter;

import android.content.Context;
import android.view.View;

/**
 * Activity接口
 */
public interface IBaseActivity {

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    int bindLayout();


    /**
     * 初始化控件
     */
    void initView(final View view);

    /**
     * 业务处理操作（onCreate方法中调用）
     *
     * @param mContext 当前Activity对象
     */
    void doBusiness(Context mContext);

    /**
     * 暂停恢复刷新相关操作（onResume方法中调用）
     */
    void resume();

    /**
     * 销毁、释放资源相关操作（onDestroy方法中调用）
     */
    void destroy();

}
