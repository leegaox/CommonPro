package com.biu.modulebase;

import android.os.Environment;

import java.io.File;


public class Constant {
    /**
     * 友盟推送的flag
     *
     * **/
    public  static  int flag_umPush=0;
    public  static String id_umPush="";

    /** Log Tag **/
    public static String TAG = "buqiuren";
    /** 本地缓存文件名 **/
    public static String PREFERENCE_NAME = "Biu_buqiuren_Pref";

    public static String APP_NAME = "buqiuren";
    public static String APPNAME = "不求人";
    public static String APK_NAME = "buqiuren.apk";
    // ***************************↓↓↓↓↓↓↓接口默认传参↓↓↓↓↓↓↓***********************************
    /** 安卓通讯渠道号 **/
    public static String ANDROID_CHANNEL = "2";
    /** 数据交互的平台验证 **/
    public static String KEY = "";
    /** 通讯版本 **/
    public static String VERSION = "1.0";
    /**发送验证码签名**/
    public static String sign="signature007";

    public static String START_TIME = "1900-12-30 00:00:00";

    // ***************************↓↓↓↓↓↓↓本地缓存路径↓↓↓↓↓↓↓***********************************
    /************************************** 存储根路径 **************************************/
    public static final String STORAGE_HOME_PATH = Environment.getExternalStorageDirectory() + File.separator;
    /************************************** APP缓存路径 **************************************/
    public static final String CACHE_PATH = STORAGE_HOME_PATH + APP_NAME;
    /*************************************** APP 错误日志存储路径**************************************/
    public static final String ERROR_LOG_PATH = STORAGE_HOME_PATH + APP_NAME + File.separator + "crash" + File.separator;
    /*************************************** APP 本地图片ImageLoader缓存路径**************************************/
    public static final String IMAGE_LOADER_CACHE_PATH = APP_NAME + File.separator + "imgCache";

    public static final String MY_IMAGE_LOADER_PATH = STORAGE_HOME_PATH + APP_NAME + File.separator + "mImg";

    // ***************************↓↓↓↓↓↓↓数据库↓↓↓↓↓↓↓*******************************************

    // ***************************↓↓↓↓↓↓↓接口↓↓↓↓↓↓↓*********************************************

    /**服务器根地址**/
    public static final String BASE_URL = BuildConfig.BASE_URL+"mutualHelp/";
    /**图片根路径**/
    public static final String IMAGE_URL = BuildConfig.BASE_URL;

    /** 图片单选 **/
    public static final String SINGLE_CHOICE_IMG = "SINGLE_CHOICE_IMG";
    /** ListView加载数据类型(刷新) **/
    public static final int LIST_REFRESH = 1;
    /** ListView加载数据类型(加载更多) **/
    public static final int LIST_LOAD_MORE = 2;
    /** 拍照 **/
    public static final int CAPTURE_PHOTO = 3;
    /** 相册选择 **/
    public static final int CHOICE_PHOTO = 4;
    /** 图片压缩 **/
    public static final int COMPRESS_IMG = 5;
    /** 图片裁剪 **/
    public static final int CROP_IMG = 6;
    /** 上传的照片最大数 **/
    public static final int PREVIEW_IMG_MAX_NUM = 9;


    /**图片种类，用于加载图片是url拼接*/
    public static final String IMG_SOURCE="source";
    public static final String IMG_COMPRESS="compress";
    public static final String IMG_THUMBNAIL="thumbnail";




}
