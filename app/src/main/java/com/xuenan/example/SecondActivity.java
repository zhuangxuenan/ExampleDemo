package com.xuenan.example;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.xuenan.example.base.BaseActivity;
import com.xuenan.example.commonutil.SystemStatesBarUtils;

/**
 * Created by Android on 2017/9/6.
 */

public class SecondActivity extends BaseActivity {
    private View top_view;
    private Toolbar toolbar_common;
    private TextView tv_title_common;
    @Override
    public int bindLayout() {
        return R.layout.second_layout;
    }

    @Override
    public void initView(View view) {
       top_view = findViewById(R.id.view_topview);
       SystemStatesBarUtils.setTopViewHeightColor(context,top_view,R.color.colorPrimaryDark);
       toolbar_common = (Toolbar) findViewById(R.id.toolbar_common);
       toolbar_common.setTitle("");
       setSupportActionBar(toolbar_common);
       tv_title_common = (TextView) findViewById(R.id.tv_title_common);
       tv_title_common.setText("Example");
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void resume() {

    }

    @Override
    public void limitOnClick(View itemView) {

    }
}
