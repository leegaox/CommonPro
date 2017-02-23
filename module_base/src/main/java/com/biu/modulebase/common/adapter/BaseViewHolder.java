package com.biu.modulebase.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * Created by jhj_Plus on 2016/1/14.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder implements
        View
        .OnClickListener {

    private static final String TAG = "BaseViewHolder";
    private Callbacks mCallbacks;
    private Callbacks2 mCallbacks2;
    private final SparseArray<View> mViews = new SparseArray<>();
    private BaseViewHolder holder;

    /**
     * @deprecated 使用 {@link BaseViewHolder} 带{@code ViewHolderCallbacks}构造方法
     * @param itemView {@code RecyclerView} Item所显示的View
     */
    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        holder =this;
    }

    public BaseViewHolder(View itemView, Callbacks mCallbacks) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mCallbacks = mCallbacks;
        holder =this;
    }


    public BaseViewHolder(View itemView, Callbacks2 callbacks2) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.mCallbacks2 = callbacks2;
        holder =this;
    }

    public void setItemChildViewClickListener(int... itemChildViewIds) {
        for (int i = 0; i < itemChildViewIds.length; i++) {
            View childView = mViews.get(itemChildViewIds[i]);
            if (childView == null) {
                childView = itemView.findViewById(itemChildViewIds[i]);
            }
            if (childView != null) {
                childView.setOnClickListener(this);
                mViews.put(itemChildViewIds[i], childView);
            }
        }
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId){
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void removeAllViews() {
        mViews.clear();
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public BaseViewHolder setText(int viewId,CharSequence text) {
        TextView childView = getView(viewId);
        if (childView != null) {
            childView.setText(text);
        }
        return this;
    }

    /**
     * 为ImageView设置本地图片资源
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public BaseViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    public void bindData(Object data) {
        if (mCallbacks != null) {
            mCallbacks.bind(holder,data);
        }
        if (mCallbacks2!=null){
            mCallbacks2.bind(this,data);
        }
    }

    @Override
    public void onClick(View v) {
        if (mCallbacks != null) {
            mCallbacks.onItemClick(v, getAdapterPosition());
        }
        if (mCallbacks2!=null) {
           mCallbacks2.onItemClick(this,v,getAdapterPosition());
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public interface Callbacks {

        void bind(BaseViewHolder holder, Object data);

        void onItemClick(View view, int position);
    }

    public void setCallbacks(Callbacks2 callbacks2) {
        mCallbacks2 = callbacks2;
    }

    public interface Callbacks2 {

        void bind(BaseViewHolder holder, Object data);

        void onItemClick(BaseViewHolder holder, View view, int position);
    }
}
