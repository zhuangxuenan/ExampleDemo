package com.xuenan.example.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuenan.example.R;
import com.xuenan.example.base.BaseFragment;
import com.xuenan.example.commonutil.SystemStatesBarUtils;
import com.xuenan.example.view.CustomListView;
import com.xuenan.example.view.GradationScrollView;


/**
 * Created by Administrator on 2017/5/16.
 */

public class BFragment extends BaseFragment implements GradationScrollView.ScrollViewListener{
    /**
     * 当前Fragment渲染的视图View
     **/
    private View mContextView = null;
    private View mTopView;
    private TextView tv_title_common;
    private Toolbar toolbar_custom;
    View top_toolbar;
    private GradationScrollView scrollView;
    private CustomListView listView;
    private int height;
    private ImageView ivBanner;
    @Override
    public void limitOnClick(View itemView) {

    }

    /**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {
        ViewTreeObserver vto = ivBanner.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                top_toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                height = ivBanner.getHeight();
                scrollView.setScrollViewListener(BFragment.this);
            }
        });
    }
    private void initData() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getActivity().getResources().getStringArray(R.array.data));
        listView.setAdapter(adapter);
    }
    /**
     * 滑动监听
     * @param scrollView
     * @param x
     * @param y
     * @param oldx
     * @param oldy
     */
    @Override
    public void onScrollChanged(GradationScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色 滑动到顶部
            top_toolbar.setBackgroundColor(Color.argb(0,67,107,179));
            tv_title_common.setTextColor(Color.argb(0, 255,255,255));
        } else if (y > 0 && y <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变 中间渐变
            float scale = (float) y / height;
            float alpha = (255 * scale);
            tv_title_common.setTextColor(Color.argb((int) alpha, 255,255,255));
            top_toolbar.setBackgroundColor(Color.argb((int) alpha, 67,107,179));
            Log.e("111-------------------","scale------"+scale+"alpha-----"+alpha);
        } else {    //滑动到banner下面设置普通颜色 滑动到底部了
            top_toolbar.setBackgroundColor(Color.argb(255,67,107,179));
            tv_title_common.setTextColor(Color.argb(255, 255,255,255));
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.find_layout;
    }

    @Override
    public void initView(View view) {
        mContextView = view;
        setToolbarStatus(false);
        mTopView = mContextView.findViewById(R.id.view_topview2);
        SystemStatesBarUtils.setTopViewHeightColor(getActivity(),mTopView,R.color.transparent);
        tv_title_common = (TextView) mContextView.findViewById(R.id.tv_title_custom);
        toolbar_custom = (Toolbar) mContextView.findViewById(R.id.toolbar_custom);
        toolbar_custom.setTitle("");
        tv_title_common.setText("发现");
        top_toolbar =mContextView.findViewById(R.id.toal_title);
        mActivity.setSupportActionBar(toolbar_custom);
        scrollView = (GradationScrollView)mContextView.findViewById(R.id.scrollview);
        listView = (CustomListView) mContextView.findViewById(R.id.listview);
        ivBanner = (ImageView)mContextView.findViewById(R.id.iv_banner);
        ivBanner.setFocusable(true);
        ivBanner.setFocusableInTouchMode(true);
        ivBanner.requestFocus();
    }

    @Override
    public void doOnceBusiness(Context mContext, View view) {
        super.doOnceBusiness(mContext, view);
        initListeners();
        initData();
    }
}
