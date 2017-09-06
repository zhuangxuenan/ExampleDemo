package com.xuenan.example.base.adapterbase;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuenan.example.commonutil.GlideUtils;

/**
 * Author: chenguang
 * listView通用ViewHolder
 * Date: 15/9/16
 * Time: 9:34
 * Usage:
 */
public class ListViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public ListViewHolder(Context context, ViewGroup parent, int layoutId, int position){
        mPosition=position;
        mViews=new SparseArray<View>();
        mConvertView= LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    public static ListViewHolder getViewHolder(Context context,ViewGroup parent,
                                           int layoutId,int position,View convertView){
        if(convertView==null){
            return new ListViewHolder(context,parent,layoutId,position);
        }else{
            ListViewHolder holder= (ListViewHolder) convertView.getTag();
            holder.mPosition=position;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view=mViews.get(viewId);
        if(view==null){
            view=mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T)view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 为TextView设置text值
     * @param viewId
     * @param text
     * @return
     */
    public ListViewHolder setText(int viewId,String text){
        TextView tv= getView(viewId);
        tv.setText(text);
        return this;
    }
    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ListViewHolder setImageByUrl(Context context,final int viewId, String url,int lodingImage, int errorImageView) {
        GlideUtils.loadImageViewLoding(context,url,(ImageView) getView(viewId),lodingImage,errorImageView);
        return this;
    }
    /**
     * 为ImageView设置src
     * @param viewId
     * @param resId
     * @return
     */
    public ListViewHolder setImageResource(int viewId,int resId){
        ImageView iv=getView(viewId);
        iv.setImageResource(resId);
        return this;
    }

    public int getPosition() {
        return mPosition;
    }
}
