package com.xuenan.example.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.xuenan.example.R;
import com.xuenan.example.commonutil.AppDavikActivityMgr;
import com.xuenan.example.commonutil.AppNetworkMgr;
import com.xuenan.example.commonutil.SVProgressHUDUtil;
import com.xuenan.example.commonutil.SystemBarTintManager;
import com.xuenan.example.commonutil.ToastUtils;
import com.xuenan.example.inter.IBaseActivity;
import com.xuenan.example.inter.IMemoryState;

import java.util.Calendar;

/**
 * @anthor zhuangxuenan
 * @time 2017/02/26
 * 基类activity
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity,View.OnClickListener {
    /***
     * 整个应用Applicaiton
     **/
    protected Context context;
    private Context mContext;
    /**
     * 当前Activity渲染的视图View
     **/
    private ViewGroup mContextView = null;
    private IMemoryState mIMemoryState;
    private SystemBarTintManager tintManager;
    protected final String TAG = this.getClass().getSimpleName();
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow2();
        setContentView(bindLayout());
        //沉浸状态栏
       //if(!TAG.equals("MainActivity")){
       //     initWindow();
       //}else{

        //}
        context = this;
        mContext = getApplicationContext();
        AppDavikActivityMgr.getScreenManager().addActivity(this);
        // 初始化控件 监听注册
        initView(mContextView);
        // 业务操作
        doBusiness(this);
        //友盟统计代码集成
        //发送策略定义了用户由统计分析SDK产生的数据发送回友盟服务器的频率。
        MobclickAgent.updateOnlineConfig(this);
        // 当应用在后台运行超过30秒（默认）再回到前端，将被认为是两个独立的session(启动)，例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。可通过接口：MobclickAgent.setSessionContinueMillis(long
        // interval) 来自定义这个间隔（参数单位为毫秒）。
        MobclickAgent.setSessionContinueMillis(40);
        // 需要在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false)
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity。
        MobclickAgent.openActivityDurationTrack(false);
        // 日志加密设置:加密模式可以有效防止网络攻击，提高数据安全性
        AnalyticsConfig.enableEncrypt(true);
        // 如不需要错误统计功能，可通过此方法关闭
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    // session的统计
    // 正确集成如下代码，才能够保证获取正确的新增用户、活跃用户、启动次数、使用时长等基本数据。
    // 使用 onResume 和 onPause 方法统计时长
    // 使用 onPageStart 和 onPageEnd 方法统计页面
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//统计时长
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        resume();
    }
    public abstract void limitOnClick(View itemView);
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            hideSoftInput();
            if(AppNetworkMgr.isConnected(context)){
                limitOnClick(v);
            }else{
                ToastUtils.showShort(context,"请检查网络");
            }
        }else{
            return;
        }
    }

    @TargetApi(19)
    public void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏 如果只是想要更改状态栏 这句可以注销
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建状态栏导航栏的管理实例
            tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置,如果Navigation不想改变颜色，此处设置为false即可
            tintManager.setNavigationBarTintEnabled(false);
            // 设置一个颜色给系统栏,这个方法会把状态栏和导航栏设置为一样的颜色，这就不好了
            //当然,如果你想设置一样的颜色，那么你随便啊
            //tintManager.setTintColor(Color.parseColor("#FFFF6666"));
            //给状态栏设置颜色
            //Apply the specified drawable or color resource to the system status bar.
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
            //Apply the specified drawable or color resource to the system navigation bar.
            //给导航栏设置资源
            //tintManager.setNavigationBarTintResource(R.color.mask_tags_2);
        }
    }
    /**
     *状态栏透明 自定义状态栏
     */
    @TargetApi(19)
    public void initWindow2() {
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏,,
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN SYSTEM_UI_FLAG_LAYOUT_STABLE,
            //注意两个Flag必须要结合在一起使用，表示会让应用的主体内容占用系统状态栏的空间
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);//设置一个透明的状态栏
        }
    }

    /**
     * 次方法最低兼容 API14 如果此方法继续向下兼容，此方法则不可用
     *
     * @param level 内存常量
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (mIMemoryState != null) {
            mIMemoryState.memorystate(level);
        }
        getMemoryState(level);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

/**
 * activity 获取内存状态
 *
 * @param state 状态值
 *TRIM_MEMORY_UI_HIDDEN        当我们程序中的所有UI组件全部不可见
 *TRIM_MEMORY_COMPLETE         目前内存已经很低了，并且我们的程序处于LRU缓存列表的最边缘位置，系统会最优先考虑杀掉我们的应用程序
 *TRIM_MEMORY_MODERATE         表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的中间位置，如果手机内存还得不到进一步释放的话，那么我们的程序就有被系统杀掉的风险了
 *TRIM_MEMORY_BACKGROUND       内存已经很低了，系统准备开始根据LRU缓存来清理进程。这个时候我们的程序在LRU缓存列表的最近位置，是不太可能被清理掉的，但这时去释放掉一些比较容易恢复的资源能够让手机的内存变得比较充足，从而让我们的程序更长时间地保留在缓存当中，这样当用户返回我们的程序时会感觉非常顺畅，而不是经历了一次重新启动的过程
 *TRIM_MEMORY_RUNNING_CRITICAL 程序正常运行，但是系统已经根据LRU缓存规则杀掉了大部分缓存的进程了。这个时候我们应当尽可能地去释放任何不必要的资源，不然的话系统可能会继续杀掉所有缓存中的进程，并且开始杀掉一些本来应当保持运行的进程，比如说后台运行的服务
 *TRIM_MEMORY_RUNNING_LOW      表示应用程序正常运行，并且不会被杀掉。但是目前手机的内存已经非常低了，我们应该去释放掉一些不必要的资源以提升系统的性能，同时这也会直接影响到我们应用程序的性能
 *TRIM_MEMORY_RUNNING_MODERATE 正常运行，不会被杀掉。但是目前手机的内存已经有点低了，系统可能会开始根据LRU缓存规则来去杀死进程
 */
    public void getMemoryState(int state) {

    }

    /**
     * 此方法适合于activity上的fragment进行内存使用情况监听管理
     *
     * @param iMemoryState 回调接口
     */
    public void setMemoryListener(IMemoryState iMemoryState) {
        mIMemoryState = iMemoryState;
    }

    /**
     * 开启加载中弹框(加载数据时调用)
     */
    public void showLoading() {
        SVProgressHUDUtil.showWithStatus(this,"加载中", SVProgressHUD.SVProgressHUDMaskType.BlackCancel);
    }

    /**
     * 关闭加载中弹框
     */
    public void dismissLoading() {
        SVProgressHUDUtil.dismiss(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
        AppDavikActivityMgr.getScreenManager().removeActivity(this);
        hideSoftInput();
        SVProgressHUDUtil.dismiss(this);
    }

    @Override
    public void destroy() {

    }

    /**
     * 更换全局字体
     *
     * @param
     */
    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/
    //    强制隐藏输入法
    protected void hideSoftInput() {
        View v = getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (isOpen) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
