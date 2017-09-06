package com.xuenan.example.base.adapterbase;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuenan.example.commonutil.GlideUtils;


/**
 * Author: implistarry
 * Date: 2016-07-01
 * Time: 10:54
 * Usage: 适配一切RecyclerView.ViewHolder
 */
public class RecyclerHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;

    public RecyclerHolder(View itemView) {
        super(itemView);
        //缓存View应该一般不会超过10个
        this.mViews = new SparseArray<>(10);
    }

    public SparseArray<View> getAllView() {
        return mViews;
    }


    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public RecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public RecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public RecyclerHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public RecyclerHolder setImageByUrl(Context mContext,final int viewId, String url, int lodingImage, int errorImageView) {
        GlideUtils.loadImageViewLoding(mContext,url,(ImageView) getView(viewId),lodingImage,errorImageView);
        return this;
    }
}
