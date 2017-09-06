package com.xuenan.example.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xuenan.example.R;
import com.xuenan.example.commonutil.AppNetworkMgr;
import com.xuenan.example.commonutil.SVProgressHUDUtil;
import com.xuenan.example.commonutil.ToastUtils;
import com.xuenan.example.inter.IBaseFragment;
import com.xuenan.example.inter.IMemoryState;

import java.lang.reflect.Field;
import java.util.Calendar;

/**基类fragment
 * Created by xuenan on 2017/02/23.
 */
public abstract class BaseFragment extends Fragment implements IBaseFragment,IMemoryState,View.OnClickListener{
    protected BaseActivity mActivity;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    /**
     * 当前Fragment渲染的视图View
     **/
    private View mContextView = null;
    private View mTopView;
    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getHoldingActivity());       //统计时长
        SVProgressHUDUtil.dismiss(getHoldingActivity());
        resume();
    }
    //用于刷新
    public void resume(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        // 渲染视图View
        if (null == mContextView) {
            getHoldingActivity().setMemoryListener(this);
            mContextView = inflater.inflate(R.layout.fragment_base, container, false);
            mTopView = mContextView.findViewById(R.id.view_topview);
            ((FrameLayout) mContextView.findViewById(R.id.fl_content)).addView(inflater.inflate(bindLayout(), null));
            // 控件初始化
            initView(mContextView);
            //初始化参数 activity与fragment交互
            initParms(getArguments());
            //处理业务逻辑
            doOnceBusiness(getHoldingActivity(), mContextView);
            onCreateSavedInstanceState(savedInstanceState, mContextView);
        } else {
            ViewParent vp = mContextView.getParent();
            if (vp != null) {
                ViewGroup vg = (ViewGroup) vp;
                vg.removeView(mContextView);
            }
        }
        return mContextView;
    }
    public abstract void limitOnClick(View itemView);
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            if(AppNetworkMgr.isConnected(mActivity)){
                limitOnClick(v);
            }else{
                ToastUtils.showShort(mActivity,"请检查网络");
            }
        }else{
            return;
        }
    }
    @Override
    public void memorystate(int memory) {
        memoryChangeState(memory);
    }
    /**
     * fragment 内存监听
     *
     * @param state 内存状态
     */
    public void memoryChangeState(int state) {

    }
    @Override
    public void onCreateSavedInstanceState(Bundle savedInstanceState, View view) {

    }
    //接收从activity过来的传值
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public void doOnceBusiness(Context mContext, View view) {

    }
    @Override
    public void onPause() {
        super.onPause();
        hideSoftInput();
        MobclickAgent.onPause(getHoldingActivity());       //统计时长
    }

    @Override
    public void onStop() {
        System.gc();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideSoftInput();
        SVProgressHUDUtil.dismiss(getHoldingActivity());
    }

    //    强制隐藏输入法
    protected void hideSoftInput() {
        View v = getHoldingActivity().getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getHoldingActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (isOpen) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启加载中弹框(加载数据时调用)
     */
    public void showLoading() {
        getHoldingActivity().showLoading();
    }

    /**
     * 关闭加载中弹框
     */
    public void dismissLoading() {
        getHoldingActivity().dismissLoading();
    }
    /**
     * 设置Toolbar是否显示
     */
    public void setToolbarStatus(boolean status) {
        Toolbar toolbar = (Toolbar) mContextView.findViewById(R.id.toolbar_common);
        if(status){
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setTitle("");
        }else{
            toolbar.setVisibility(View.GONE);
        }
        getHoldingActivity().setSupportActionBar(toolbar);
    }
    /**
     * 设置标题文字
     */
    public void setTitle(String title) {
        TextView tv_reusable_title = (TextView) mContextView.findViewById(R.id.tv_title_common);
        if (title == null) {
            tv_reusable_title.setVisibility(View.GONE);
        } else {
            tv_reusable_title.setVisibility(View.VISIBLE);
            tv_reusable_title.setText(title);
        }
    }
    /**
     * 设置标题左侧图标
     *
     * @param resourseID
     */
    public void setLeftImgBtnImageRes(int resourseID) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_left_common);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resourseID);
    }
    /**
     * 处理标题左侧图片点击事件
     *
     * @param onClickListener
     */
    public void setLeftImgBtnEvent(View.OnClickListener onClickListener) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_left_common);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(onClickListener);
    }
    /**
     * 处理标题左侧图片点击事件
     *
     * @param onClickListener
     */
    public void setLeftImgBtnEvent(int resourseID,View.OnClickListener onClickListener) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_left_common);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resourseID);
        imageView.setOnClickListener(onClickListener);
    }



    /**
     * 设置标题右侧图标
     *
     * @param resourseID
     */
    public void setRightImgBtnImageRes(int resourseID) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_right_common);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resourseID);
    }
    /**
     * 设置标题右侧图标
     *
     * @param resourseID
     */
    public void setRightImgBtnImageRes2(int resourseID) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_right_common2);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resourseID);
    }
    /**
     * 设置标题右侧图标
     *
     * @param resourseID
     */
    public void setRightImgBtnImageRes(int resourseID, View.OnClickListener onClickListener) {
        ImageView imageView = (ImageView) mContextView.findViewById(R.id.iv_right_common);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resourseID);
        if (onClickListener != null) {
            imageView.setOnClickListener(onClickListener);
        }
    }

    /**
     * 设置右侧文字并显示(VISIBLE)
     *
     * @param str
     * @param lisenter
     */
    public void setRightText(String str, View.OnClickListener lisenter) {
        if (!TextUtils.isEmpty(str)) {
            TextView textView = (TextView) mContextView.findViewById(R.id.tv_right_common);
            textView.setVisibility(View.VISIBLE);
            textView.setText(str);
            if (lisenter != null) {
                textView.setOnClickListener(lisenter);
            }
        }
    }

    /**
     * 设置右侧文字并显示(VISIBLE)
     *
     * @param str
     * @param
     */
    public void setRightText(String str) {
        if (!TextUtils.isEmpty(str)) {
            TextView textView = (TextView) mContextView.findViewById(R.id.tv_right_common);
            textView.setVisibility(View.VISIBLE);
            textView.setText(str);
        }
    }
}
