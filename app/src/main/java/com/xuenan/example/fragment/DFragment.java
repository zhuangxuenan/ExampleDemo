package com.xuenan.example.fragment;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.xuenan.example.R;
import com.xuenan.example.base.BaseFragment;


/**
 * Created by Administrator on 2017/5/16.
 */

public class DFragment extends BaseFragment {
    /**
     * 当前Fragment渲染的视图View
     **/
    private View mContextView = null;
    //private View mTopView;
    private Toolbar toolbar_common;
    private TextView tv_title_common;
    @Override
    public void limitOnClick(View itemView) {

    }

    @Override
    public int bindLayout() {
        return R.layout.my_layout;
    }

    @Override
    public void initView(View view) {
        mContextView = view;
        //mTopView = mContextView.findViewById(R.id.view_topview);
        //SystemStatesBarUtils.setTopViewHeightColor(getActivity(),mTopView,R.color.colorPrimaryDark);
        toolbar_common = (Toolbar) mContextView.findViewById(R.id.toolbar_common);
        toolbar_common.setTitle("");
        //tv_title_common = (TextView) mContextView.findViewById(R.id.tv_title_common);
        //tv_title_common.setText("我的");
        //mTopView.setVisibility(View.GONE);
        setToolbarStatus(false);
        mActivity.setSupportActionBar(toolbar_common);
    }

    @Override
    public void doOnceBusiness(Context mContext, View view) {
        super.doOnceBusiness(mContext, view);
    }
}
