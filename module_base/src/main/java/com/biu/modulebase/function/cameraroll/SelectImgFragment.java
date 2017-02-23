package com.biu.modulebase.function.cameraroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biu.modulebase.Constant;
import com.biu.modulebase.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2016/1/19
 */
public class SelectImgFragment extends Fragment implements
        ListImageDirPopupWindow.OnImageDirSelected {

    private ProgressDialog mProgressDialog;

    private MenuItem right;
    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    private GridView mGirdView;
    private SelectImgAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFloderVO> mImageFloders = new ArrayList<ImageFloderVO>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    int totalCount = 0;

    private int mScreenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;


    public static boolean SINGLEE_CHOICE_IMG = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mProgressDialog.dismiss();
            // 为View绑定数据
            data2View();
            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_img, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        SINGLEE_CHOICE_IMG = getActivity().getIntent().getBooleanExtra(
                Constant.SINGLE_CHOICE_IMG, false);
        initView();
        getImages();
        initEvent();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
       mProgressDialog = ProgressDialog.show(getActivity(), null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getActivity().getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFloderVO imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFloderVO();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                   String[] picList =parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    });
                    int picSize = picList==null?0:picList.length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private void initView() {
        View view = getView();
        mGirdView = (GridView) view.findViewById(R.id.id_gridView);
        mChooseDir = (TextView) view.findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) view.findViewById(R.id.id_total_count);
        mBottomLy = (RelativeLayout) view.findViewById(R.id.id_bottom_ly);
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mBottomLy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.popwin_anim_style);
                mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = .3f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            Toast.makeText(getActivity().getApplicationContext(), "一张图片没扫描到,赶紧去拍一张吧...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new SelectImgAdapter(getActivity().getApplicationContext(), mImgs,
                R.layout.select_img_grid_item, mImgDir.getAbsolutePath(),
                new SelectImgAdapter.OnImgSelectListener() {

                    @Override
                    public void onImgSelect() {
                        if (SINGLEE_CHOICE_IMG) {// 单选
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra(
                                    "imgPaths",
                                    (ArrayList<String>) SelectImgAdapter.mSelectedImage);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        } else {
                            int imgSize = SelectImgAdapter.mSelectedImage
                                    .size();
                            if (imgSize == 0) {
                                right.setTitle("取消");
                            } else if (imgSize > 0 && imgSize <= Constant.PREVIEW_IMG_MAX_NUM) {
                                right.setTitle("确定" + "(" + imgSize + ")");
                            }
                        }

                    }

                    @Override
                    public void onImgMax() {
                        Toast.makeText(getActivity().getApplicationContext(), "最多只能选择" + Constant.PREVIEW_IMG_MAX_NUM + "张图片~",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
    }

    ;

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getActivity().getApplicationContext())
                .inflate(R.layout.select_img_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sure, menu);
        right = menu.getItem(0);
        int imgSize = SelectImgAdapter.mSelectedImage.size();
        if (SINGLEE_CHOICE_IMG) {
            right.setTitle("取消");
            SelectImgAdapter.mSelectedImage.clear();
        } else {
            if (imgSize == 0) {
                right.setTitle("取消");
            } else if (imgSize > 0 && imgSize <= 9) {
                right.setTitle("确定" + "(" + imgSize + ")");
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sure) {
            try {
                if (SelectImgAdapter.mSelectedImage.size() != 0) {
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra("imgPaths",
                            (ArrayList<String>) SelectImgAdapter.mSelectedImage);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
                getActivity().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void selected(ImageFloderVO floder) {
        mImgDir = new File(floder.getDir());
        String list[] =mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        });
        if(list!=null)
            mImgs = Arrays.asList(list);
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new SelectImgAdapter(getActivity().getApplicationContext(), mImgs,
                R.layout.select_img_grid_item, mImgDir.getAbsolutePath(),
                new SelectImgAdapter.OnImgSelectListener() {

                    @Override
                    public void onImgSelect() {
                        if (SINGLEE_CHOICE_IMG) {// 单选
                            Intent intent = new Intent();
                            intent.putStringArrayListExtra(
                                    "imgPaths",
                                    (ArrayList<String>) SelectImgAdapter.mSelectedImage);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        } else {
                            int imgSize = SelectImgAdapter.mSelectedImage
                                    .size();
                            if (imgSize == 0) {
                                right.setTitle("取消");
                            } else if (imgSize > 0 && imgSize <= Constant.PREVIEW_IMG_MAX_NUM) {
                                right.setTitle("确定" + "(" + imgSize + ")");
                            }
                        }

                    }

                    @Override
                    public void onImgMax() {
                        Toast.makeText(getActivity().getApplicationContext(), "最多只能选择" + Constant.PREVIEW_IMG_MAX_NUM + "张图片~",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        mGirdView.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
        mImageCount.setText(floder.getCount() + "张");
        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }
}
