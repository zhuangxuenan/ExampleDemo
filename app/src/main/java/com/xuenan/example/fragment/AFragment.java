package com.xuenan.example.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuenan.example.R;
import com.xuenan.example.SecondActivity;
import com.xuenan.example.base.BaseFragment;
import com.xuenan.example.commonutil.SystemStatesBarUtils;


/**
 * Created by Administrator on 2017/5/16.
 */

public class AFragment extends BaseFragment {
    /**
     * 当前Fragment渲染的视图View
     **/
    private View mContextView = null;
    private View mTopView;
    private Toolbar toolbar_common;
    private TextView tv_title_common;
    private Button bt;
    @Override
    public void limitOnClick(View itemView) {
        switch (itemView.getId()){
            case R.id.bt:
                startActivity(new Intent(mActivity, SecondActivity.class));
                mActivity.overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
                break;
        }
    }

    @Override
    public void doOnceBusiness(Context mContext, View view) {
        super.doOnceBusiness(mContext, view);
        bt.setOnClickListener(this);
    }

    @Override
    public int bindLayout() {
        return R.layout.homepage_layout;
    }

    @Override
    public void initView(View view) {
        mContextView = view;
        mTopView = mContextView.findViewById(R.id.view_topview);
        SystemStatesBarUtils.setTopViewHeightColor(getActivity(),mTopView,R.color.colorPrimaryDark);
        toolbar_common = (Toolbar) mContextView.findViewById(R.id.toolbar_common);
        toolbar_common.setTitle("");
        tv_title_common = (TextView) mContextView.findViewById(R.id.tv_title_common);
        tv_title_common.setText("首页");
        setToolbarStatus(true);
        mActivity.setSupportActionBar(toolbar_common);
        bt = (Button) mContextView.findViewById(R.id.bt);
    }
}
