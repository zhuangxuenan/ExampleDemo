package com.xuenan.example;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuenan.example.base.BaseActivity;
import com.xuenan.example.commonutil.AppScreenMgr;
import com.xuenan.example.fragment.AFragment;
import com.xuenan.example.fragment.BFragment;
import com.xuenan.example.fragment.CFragment;
import com.xuenan.example.fragment.DFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuenan on 2017/5/16.
 */

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    private TextView text_homepage,text_chongzhi_find,text_chongzhi_healthy,text_chongzhi_my;
    private ViewPager vp_charge_ft;
    private View iv_1_charge;
    private int screenWidth;
    private List<Fragment> mList;
    private int preposition=0;
    private LinearLayout linearlayout_charge;
    @Override
    public void limitOnClick(View itemView) {
        switch (itemView.getId()){
            case R.id.text_homepage:
                text_homepage.setTextColor(Color.parseColor("#EE30A7"));
                text_chongzhi_find.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_healthy.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_my.setTextColor(Color.parseColor("#66CD00"));
                vp_charge_ft.setCurrentItem(0);
                break;
            case R.id.text_chongzhi_find:
                text_homepage.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_find.setTextColor(Color.parseColor("#EE30A7"));
                text_chongzhi_healthy.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_my.setTextColor(Color.parseColor("#66CD00"));
                vp_charge_ft.setCurrentItem(1);
                break;
            case R.id.text_chongzhi_healthy:
                text_homepage.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_find.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_healthy.setTextColor(Color.parseColor("#EE30A7"));
                text_chongzhi_my.setTextColor(Color.parseColor("#66CD00"));
                vp_charge_ft.setCurrentItem(2);
                break;
            case R.id.text_chongzhi_my:
                text_homepage.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_find.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_healthy.setTextColor(Color.parseColor("#66CD00"));
                text_chongzhi_my.setTextColor(Color.parseColor("#EE30A7"));
                vp_charge_ft.setCurrentItem(3);
                break;
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (position) {
            case 0:
                iv_1_charge.setX((screenWidth/4.0f)*positionOffset);
                break;
            case 1:
                iv_1_charge.setX(screenWidth/4.0f+(screenWidth/4.0f)*positionOffset);
                break;
            case 2:
                iv_1_charge.setX(screenWidth/2.0f+(screenWidth/4.0f)*positionOffset);
                break;
            case 3:
                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public int bindLayout() {
        return R.layout.charge_layout;
    }

    @Override
    public void initView(View view) {
        //得到屏幕的宽度
        screenWidth = AppScreenMgr.getScreenWidth(this);
        iv_1_charge = findViewById(R.id.iv_1_charge);
        LayoutParams layoutParams = iv_1_charge.getLayoutParams();
        float f =screenWidth/4.0f;
        layoutParams.width =(int)f;
        layoutParams.height =20;
        iv_1_charge.setLayoutParams(layoutParams);
        linearlayout_charge = (LinearLayout) findViewById(R.id.linearlayout_charge);
        text_homepage = (TextView) findViewById(R.id.text_homepage);
        text_chongzhi_find = (TextView) findViewById(R.id.text_chongzhi_find);
        text_chongzhi_healthy = (TextView) findViewById(R.id.text_chongzhi_healthy);
        text_chongzhi_my = (TextView) findViewById(R.id.text_chongzhi_my);
    }

    @Override
    public void doBusiness(Context mContext) {
        text_homepage.setOnClickListener(this);
        text_chongzhi_find.setOnClickListener(this);
        text_chongzhi_healthy.setOnClickListener(this);
        text_chongzhi_my.setOnClickListener(this);
        vp_charge_ft = (ViewPager) findViewById(R.id.vp_charge_ft);
        vp_charge_ft.addOnPageChangeListener(this);

        mList = new ArrayList<>();
        AFragment fragment = new AFragment();
        BFragment fragment2 = new BFragment();
        CFragment fragment3 = new CFragment();
        DFragment dFragment = new DFragment();
        mList.add(fragment);
        mList.add(fragment2);
        mList.add(fragment3);
        mList.add(dFragment);
        vp_charge_ft.setAdapter(new VpAdapter(getSupportFragmentManager(), mList));
    }

    @Override
    public void resume() {

    }
}
