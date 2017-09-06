package com.xuenan.example.inter;

import android.view.View;

/**
 * Author: implistarry
 * Date: 2016-07-01
 * Time: 11:18
 * Usage:
 */

public interface OnViewClickLisenter<T> {
    int TYPE_ONE = 0x01;
    int TYPE_TWO = 0x02;
    int TYPE_THREE = 0x03;
    int TYPE_fOUR = 0x04;
    //根据传入的type类型来区分子View的点击事件 比如个人中心  点赞 评论 删除等等
    //此接口回调用于listView,RecyclerView的item的子View的点击事件
    void onChanageViewClick(int viewType, T model, int position, View v);
}
