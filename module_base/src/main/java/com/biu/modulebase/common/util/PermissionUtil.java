package com.biu.modulebase.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.biu.modulebase.R;

import static android.Manifest.permission_group.LOCATION;

/**
 * @author Lee
 * @Title: {M+ RunTimePermissionUtil}
 * @Description:{Utility class that wraps access to the runtime permissions API in M and provides basic helper methods.
 * office guide: https://developer.android.google.cn/training/permissions/requesting.html
 * https://developer.android.google.cn/training/permissions/best-practices.html#dont-overwhelm  }
 * @date 2017/2/20
 */
public class PermissionUtil {

    //请求危险权限requestCode
    /**
     * 日历
     **/
    public static final int REQUEST_CALENDAR = 1;
    /**
     * 相机
     **/
    public static final int REQUEST_CAMERA = 2;
    /**
     * 通讯录
     **/
    public static final int REQUEST_CONTACTS = 3;
    /**
     * 位置信息
     **/
    public static final int REQUEST_LOCATION = 4;
    /**
     * 麦克风
     **/
    public static final int REQUEST_MICROPHONE = 5;
    /**
     * 电话
     **/
    public static final int REQUEST_PHONE = 6;
    /**
     * 身体传感器
     **/
    public static final int REQUEST_SENSORS = 7;
    /**
     * 短信
     **/
    public static final int REQUEST_SMS = 8;
    /**
     * 存储空间
     **/
    public static final int REQUEST_STORAGE = 9;

    /**
     * 如果您的应用需要危险权限，则每次执行需要这一权限的操作时您都必须检查自己是否具有该权限
     *
     * @param context
     * @param permission
     * @return true ：应用可以继续操作  ；false： 请求权限
     */
    public static boolean checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        int id = ContextCompat.checkSelfPermission(context, permission);
        if (id == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (id == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return false;
    }


    /**
     * @param activity
     * @param permission
     * @param requestCode MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
     *                    TODO:
     *                    1.定义危险权限组
     *                    2.根据请求的permission遍历危险权限组
     */
    public static void doRequest(Activity activity, String permission, int requestCode) {
        // Should we show an explanation?
        if (shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user8
            // sees the explanation, try again to request the permission.
            showRequestPermissionRationale(activity, permission, requestCode);

        } else {
            //permissions have not been granted yet. Request them directly.
            //调用 requestPermissions() 时，系统将向用户显示一个标准对话框。您的应用无法配置或更改此对话框
            requestPermission(activity, permission, requestCode);

        }
    }

    /**
     * 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
     * 如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
     * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * @param activity
     * @param permission
     * @param requestCode MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * 显示缺失权限提示
     * rationale：显示相机预览需要相机许可权限。
     * 联系人权限需要证明访问。
     * <p>
     * TODO:
     * 1.根据请求的权限提供权限解释
     *
     * @param activity
     */
    private static void showRequestPermissionRationale(final Activity activity, final String permission, final int requestCode) {
        Snackbar snackbar = Snackbar.make(((ViewGroup) activity.getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), "显示相机预览需要相机许可权限", Snackbar.LENGTH_INDEFINITE).setAction("OK",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestPermission(activity, permission, requestCode);
                    }
                });
        snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.white));
        snackbar.show();

    }

    /**
     * @param mActivity TODO:
     *                  根据请求的权限showToast
     */
    public static void showAppSettingsSnackBar(final Activity mActivity, final String permission) {
        Snackbar snackbar = Snackbar.make(((ViewGroup) mActivity.getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), "已关闭这个功能所需的权限", Snackbar.LENGTH_LONG).setAction("立即启用",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mActivity, "请点击\"权限\"，然后打开 " + LOCATION, Toast.LENGTH_LONG).show();
                        startAppSettings(mActivity);
                    }
                });
        snackbar.getView().setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        snackbar.show();

    }

    /**
     * 启动应用的设置
     */
    private static void startAppSettings(Context mContext) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
