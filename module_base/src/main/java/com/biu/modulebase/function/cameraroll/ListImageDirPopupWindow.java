package com.biu.modulebase.function.cameraroll;


import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.biu.modulebase.R;
import com.biu.modulebase.common.adapter.CommonAdapter;
import com.biu.modulebase.common.adapter.ViewHolder;

import java.util.List;

public class ListImageDirPopupWindow extends
        BasePopupWindowForListView<ImageFloderVO> {
    private ListView mListDir;

    public ListImageDirPopupWindow(int width, int height,
                                   List<ImageFloderVO> datas, View convertView) {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews() {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new CommonAdapter<ImageFloderVO>(context, mDatas,
                R.layout.select_img_list_dir_item) {
            @Override
            public void convert(ViewHolder helper, ImageFloderVO item) {
                helper.setText(R.id.id_dir_item_name, item.getName());
                ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(item.getFirstImagePath(),
                        (ImageView) helper.getView(R.id.id_dir_item_image));
                helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
            }
        });
    }

    public interface OnImageDirSelected {
        void selected(ImageFloderVO floder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents() {
        mListDir.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (mImageDirSelected != null) {
                    mImageDirSelected.selected(mDatas.get(position));
                }
            }
        });
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params) {
        // TODO Auto-generated method stub
    }

}
