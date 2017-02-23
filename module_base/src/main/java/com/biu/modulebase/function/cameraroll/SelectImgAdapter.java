package com.biu.modulebase.function.cameraroll;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.biu.modulebase.Constant;
import com.biu.modulebase.R;
import com.biu.modulebase.common.adapter.CommonAdapter;
import com.biu.modulebase.common.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectImgAdapter extends CommonAdapter<String> {

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static List<String> mSelectedImage = new ArrayList<String>();

    /**
     * 文件夹路径
     */
    private String mDirPath;

    private OnImgSelectListener listener;

    public SelectImgAdapter(Context context, List<String> mDatas,
                            int itemLayoutId, String dirPath,
                            OnImgSelectListener onImgSelectListener) {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
        this.listener = onImgSelectListener;
    }

    @Override
    public void convert(final ViewHolder helper, final String item) {
        // 设置no_pic
        helper.setImageResource(R.id.id_item_image, R.mipmap.pictures_no);
        // 设置no_selected
        helper.setImageResource(R.id.id_item_select,
                R.mipmap.picture_unselected);
        // 设置图片
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(mDirPath + "/" + item,
                (ImageView) helper.getView(R.id.id_item_image));

        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);
        if (SelectImgFragment.SINGLEE_CHOICE_IMG) {// 单选模式
            mSelect.setVisibility(View.GONE);
            mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (SelectImgFragment.SINGLEE_CHOICE_IMG) {
                        mSelectedImage.add(mDirPath + "/" + item);
                        listener.onImgSelect();
                    }

                }
            });
        }
        mImageView.setColorFilter(null);
        // 设置ImageView的点击事件
        mSelect.setOnClickListener(new OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.mipmap.picture_unselected);
                    mImageView.setColorFilter(null);
                    listener.onImgSelect();
                } else {// 未选择该图片
                    if (mSelectedImage.size() == Constant.PREVIEW_IMG_MAX_NUM) {
                        listener.onImgMax();
                        return;
                    }
                    mSelectedImage.add(mDirPath + "/" + item);
                    mSelect.setImageResource(R.mipmap.pictures_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                    listener.onImgSelect();

                }

            }
        });

        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item)) {
            mSelect.setImageResource(R.mipmap.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }

    }

    public interface OnImgSelectListener {

        void onImgSelect();

        void onImgMax();
    }

}
