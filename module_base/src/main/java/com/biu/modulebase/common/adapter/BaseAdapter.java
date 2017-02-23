package com.biu.modulebase.common.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.biu.modulebase.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter {
    private static final String TAG = "BaseAdapter";
    private Context mContext;
    private List<T> mData;

    public static final int DATA_ADD_FIRST=0;
    public static final int DATA_ADD_LASE=1;

   public enum AddType {
        FIRST,
        LASE
    }

    public BaseAdapter(Context context, ArrayList<T> data) {
        mContext = context;
        mData = data;
    }

    public BaseAdapter(Context context) {
        mContext = context;
        mData=new ArrayList<>();
    }

    /**
     * 设置 全新数据集 ，刷新整个 RecyclerView
     * @param data RecyclerView 列表项数据
     */
    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }


    public List<T> getData() {
        return mData;
    }

    /**
     * 获取指定postion的数据
     * @param position
     * @return
     */
    public T getData(int position) {
        return mData.get(position);
    }

    public List<T> getData(int positionStart, int count) {
        return mData.subList(positionStart, count);
    }

    /**
     * @param addtype
     * @param data
     */
    public void addData(AddType addtype, List<T> data) {
        if (data == null || mData == null) {
            return;
        }
        int count = data.size();
        if (addtype == AddType.FIRST) {
            mData.addAll(0,data);
            notifyItemRangeInserted(0, count);
        } else if (addtype == AddType.LASE) {
            int curSize = mData.size();
            mData.addAll(curSize, data);
            notifyDataSetChanged();
//            notifyItemRangeInserted(curSize, count);

        }
    }

    public void addData(AddType addtype, List<T> data, int notifyPositionStart) {
        if (data == null || mData == null) {
            return;
        }
        int curSize = mData.size();
        int count = data.size();
        if (addtype == AddType.FIRST) {
            mData.addAll(0, data);
            notifyItemRangeInserted(notifyPositionStart, count);
        } else if (addtype == AddType.LASE) {
            mData.addAll(curSize, data);
            notifyItemRangeInserted(notifyPositionStart + curSize, count);
        }
    }

    public void addData(int location, List<T> data) {
        if (data == null || mData == null) {
            return;
        }
        mData.addAll(location, data);
        notifyItemRangeInserted(location, data.size());
    }

    public void addData(int location, T data) {
        if (data == null || mData == null) {
            return;
        }
        mData.add(location,data);
        notifyItemInserted(location);
    }


    public void addData(AddType addtype,T data){
        if (mData==null||data==null) return;
        if (addtype== AddType.FIRST) {
            mData.add(0,data);
            notifyItemInserted(0);
        } else if(addtype== AddType.LASE){
            mData.add(getItemCount(),data);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void removeData(List<T> data) {
        if (data == null || mData == null) {
            return;
        }
        int positionStart=mData.indexOf(data.get(0));
        mData.removeAll(data);
        notifyItemRangeRemoved(positionStart, data.size());
    }

    public void removeData(int location){
        if (mData == null) {
            return;
        }
        mData.remove(location);
        notifyItemRemoved(location);
    }


    public void removeData(int positionStart, int count) {
        if (mData == null || positionStart < 0 || count <= 0 ||
                positionStart > count + positionStart ||
                positionStart + count > mData.size() || positionStart >= mData.size())
        {
            return;
        }
        List<T> deleteData = mData.subList(positionStart, count + positionStart);
        Iterator<T> iterator = deleteData.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        notifyItemRangeRemoved(positionStart, count);
    }

    public void removeData(T data) {
        if (mData == null || data == null) {
            return;
        }
        mData.remove(data);
        notifyItemRemoved(mData.indexOf(data));
    }

    public void changeData(int position,T data){
        mData.set(position,data);
        notifyItemChanged(position);
    }

    public void removeAllData() {
        int itemCount = mData.size();
        if (itemCount > 0) {
            mData.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }


    /**
     * 更新一串数据
     * @param newDatas
     */
    public void addItems( List<T> newDatas) {
        int startPosition = mData.size()-1;
        mData.addAll(newDatas);
        notifyItemRangeChanged(startPosition,newDatas.size());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "-----------------------getViewHolder &&&&&&&&&&&&&&&&&&&");
        return getViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseViewHolder) {
            BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
            if (mData != null && mData.size() > 0) {
                baseViewHolder.bindData(mData.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isTest) {
            return count;
        }
        return mData != null ? mData.size() : 0;
    }

    public boolean isTest;
    public int count;

    public void testLayoutData(int count) {
        isTest = true;
        List<String> datas = new ArrayList<>();
        for (int i=0;i<count;i++){
            datas.add("");
        }
        this.count =count;
        addItems((List<T>) datas);
    }

    public abstract BaseViewHolder getViewHolder(ViewGroup parent, int viewType);


    /**
     * 默认的 ItemOffsets 为每个 itemView 的上面,子类可重写进行自定义 RecyclerView itemView 之间的间隔
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    protected void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                  RecyclerView.State state)
    {
        outRect.set(0,
                mContext.getResources().getDimensionPixelSize(R.dimen.view_height_30), 0,
                0);
    }

    public ItemDecoration getItemDecoration() {
        return new ItemDecoration();
    }

    public class ItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state)
        {

            BaseAdapter.this.getItemOffsets(outRect, view, parent, state);
        }
    }
}
