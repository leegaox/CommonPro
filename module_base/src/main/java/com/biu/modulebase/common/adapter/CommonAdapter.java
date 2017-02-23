package com.biu.modulebase.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected final int mItemLayoutId;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
    }

    public void onDateChange(List<T> result) {
        this.mDatas = result;
        this.notifyDataSetChanged();
    }

    public void addDataFirst(List<T> data){
        if (mDatas!=null) {
            mDatas.addAll(0,data);
            notifyDataSetChanged();
        }
    }

    public void addData(List<T> data) {
        if (mDatas!=null) {
            mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void removeData(List<T> data) {
        if (mDatas != null) {
            mDatas.removeAll(data);
            notifyDataSetChanged();
        }
    }

    public void setData(int pos,T data){
        if (mDatas!=null) {
            mDatas.set(pos,data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        convert(viewHolder, getItem(position));
        return viewHolder.getConvertView();

    }

    public abstract void convert(ViewHolder helper, T item);

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

}
