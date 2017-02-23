package com.biu.modulebase.common.util;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Base64;

import com.biu.modulebase.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ImageUtil {
    public interface onTakePictureFinishListener {
        void onTakePictureFinished(Uri photoUri);
    }

    public static Uri takePhoto(Fragment fragment, Dialog dialog) {
        // 拍照
        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        String path = String.valueOf(System.currentTimeMillis()) + ".jpg";
        values.put(MediaStore.Images.Media.DISPLAY_NAME, path);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri photoUri = fragment.getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        photoIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        fragment.getActivity().startActivityFromFragment(fragment, photoIntent, Constant.CAPTURE_PHOTO);
        if (dialog != null) {
            dialog.dismiss();
        }
        return photoUri;
    }

//
//    public static void selectPicture(Fragment fragment, Dialog dialog,
//                                     boolean singleChoice)
//    {
//        Intent selectIntent = new Intent(fragment.getActivity(), SelectImgActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(Constant.SINGLE_CHOICE_IMG, singleChoice);
//        selectIntent.putExtras(bundle);
//        fragment.getActivity().startActivityFromFragment(fragment,selectIntent, Constant.CHOICE_PHOTO);
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//    }


    /**
     * @param activity
     * @param uri
     * @param outputX
     * @param outputY
     */
    public static void cropImg(Fragment activity, Uri uri, int outputX, int outputY) {
        if (null == uri)
            return;
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true"); // 可裁剪
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);// 输出图片大小
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);// 若为false则表示不返回数据
        activity.startActivityForResult(intent, Constant.CROP_IMG);

    }

    /**
     * 压缩图片并写入文件
     *
     * @param fromPath  原图片路径
     * @param reqWidth
     * @param reqHeight
     * @return 压缩后的图片的路径
     * @throws Exception
     */
    public static String compressToFile(String fromPath, int reqWidth, int reqHeight) throws Exception {
        Bitmap map = getSmallBitmap(fromPath, reqWidth, reqHeight);
        String toPath = ImageUtil.bitmapToFile(map);
        map = null;
        return toPath;
    }

    /**
     * 压缩图片并写入文件
     *
     * @param fromPaths 原图片路径集
     * @param reqWidth
     * @param reqHeight
     * @return List<String> toPaths 压缩后的图片的路径集
     * @throws Exception
     */
    public static ArrayList<String> compressToFiles(List<String> fromPaths, int reqWidth, int reqHeight)
            throws Exception {
        ArrayList<String> toPaths = new ArrayList<String>();
        int size = fromPaths.size();
        File dir = new File(Constant.MY_IMAGE_LOADER_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (int i = 0; i < size; i++) {
            String index = i + "";
            String name = DateFormat.format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA)) + index + ".png";
            String toPath = dir.getAbsolutePath() + File.separator + name;
            File file = new File(toPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            toPaths.add(toPath);
        }
        for (int j = 0; j < size; j++) {
            String path = fromPaths.get(j);
            Bitmap map = getSmallBitmap(path, reqWidth, reqHeight);
            bitmapToFile(map, toPaths.get(j));
        }
        return toPaths;
    }

    /**
     * @param bitmap 压缩后的bitmap对象
     * @throws Exception
     */
    public static String bitmapToFile(Bitmap bitmap) throws Exception {
        String name = DateFormat.format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA)) + ".png";
        File dir = new File(Constant.MY_IMAGE_LOADER_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String toPath = dir.getAbsolutePath() + File.separator + name;
        File file = new File(toPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        if (bitmap != null) {
            FileOutputStream fops = new FileOutputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            fops.write(baos.toByteArray());
            fops.flush();
            fops.close();
            return toPath;
        } else {
            System.out.println("获得bitmap的对象是空");
            return "";
        }
    }

    /**
     * @param bitmap 压缩后的bitmap对象
     * @return toPath 图片写入文件的路径
     * @throws Exception
     */
    public static String bitmapToFile(Bitmap bitmap, String toPath) throws Exception {
        File file = new File(toPath);
        if (bitmap != null) {
            FileOutputStream fops = new FileOutputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            fops.write(baos.toByteArray());
            fops.flush();
            fops.close();
            return toPath;
        } else {
            System.out.println("获得bitmap的对象是空");
            return "";
        }
    }

    /**
     * 官方  计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 生成缩略图用于预览
     *
     * @param filePath     图片路径
     * @param inSampleSize 缩放比
     * @return
     */
    public static Bitmap generateThumbnail(String filePath, int inSampleSize) {
        final Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 把bitmap转换成String
     *
     * @param filePath
     * @return
     */
    public static String bitmapToBase64String(String filePath, int reqWidth, int reqHeight) {
        Bitmap bm = getSmallBitmap(filePath, reqWidth, reqHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        bm.recycle();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 把bitmap转换成String
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64String2(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        bitmap.recycle();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap drawableToBitmap(Drawable src) {
        Bitmap des = Bitmap.createBitmap(src.getIntrinsicWidth(), src.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(des);
        src.setBounds(0, 0, src.getIntrinsicWidth(), src.getIntrinsicHeight());
        src.draw(canvas);
        return des;
    }

    public static String drawableToBase64String(Drawable drawable) {
        return bitmapToBase64String2(drawableToBitmap(drawable));
    }
}
