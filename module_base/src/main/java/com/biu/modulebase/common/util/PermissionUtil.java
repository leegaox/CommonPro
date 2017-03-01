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
import android.widget.Toast;

import com.biu.modulebase.R;

import static android.R.attr.permission;

/**
 * @author Lee
 * @Title: {M+ RunTimePermissionUtil}
 * @Description:{Utility class that wraps access to the runtime permissions API in M and provides basic helper methods.
 * office guide: https://developer.android.google.cn/training/permissions/requesting.html
 * https://developer.android.google.cn/training/permissions/best-practices.html#dont-overwhelm  }
 * TODO: 请求危险权限列表（官方说明： 某些情况下，一项或多项权限可能是应用所必需的。在这种情况下，合理的做法是，在应用启动之后立即要求提供这些权限。例如，如果您运行摄影应用，应用需要访问设备的相机。在用户首次启动应用时，他们不会对提供相机使用权限的要求感到惊讶。但是，如果同一应用还具备与用户联系人共享照片的功能，您不应在应用首次启动时要求用户提供 READ_CONTACTS 权限，而应等到用户尝试使用“共享”功能之后，再要求提供该权限。）
 * @date 2017/2/20
 */
public class PermissionUtil {

    /**请求危险权限组**/
    public static final int REQUEST_PERMISSIONS =10;
    //请求危险权限requestCode
    /**日历*/
    public static final int REQUEST_CALENDAR = 0;
    /**相机**/
    public static final int REQUEST_CAMERA = 1;
    /**通讯录**/
    public static final int REQUEST_CONTACTS = 2;
    /**位置信息**/
    public static final int REQUEST_LOCATION = 3;
    /**麦克风**/
    public static final int REQUEST_MICROPHONE = 4;
    /**电话**/
    public static final int REQUEST_PHONE = 5;
    /**身体传感器**/
    public static final int REQUEST_SENSORS = 6;
    /**短信**/
    public static final int REQUEST_SMS = 7;
    /**存储空间**/
    public static final int REQUEST_STORAGE = 8;

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
     * 当用户使用app时需要危险组权限时调用请求
     *
     * @param activity
     * @param permission
     * @param requestCode MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
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
     * <p>
     * 根据requestCode显示缺失权限提示 {@link com.biu.modulebase.R.array.permission_rationale}
     * rationale：显示相机预览需要相机许可权限。
     * 联系人权限需要证明访问。
     * </p>
     *          注：R.array.permission_rationale 需要具体项目具体配置获取权限的原理 以告知用户
     * @param activity
     */
    private static void showRequestPermissionRationale(final Activity activity, final String permission, final int requestCode) {
        new SnackBarBuilder(activity, activity.getResources().getStringArray(R.array.permission_rationale)[requestCode], Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission(activity, permission, requestCode);
            }
        }).show();
    }

    /**
     * 当用户使用app时需要危险组权限时调用请求
     * TODO: 记录允许 和 被拒的权限；重新请求被拒权限；所有权限都通过进入app主页
     *
     * @param activity
     * @param permission
     * @param requestCode MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant. The callback method gets the result of the request.
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * 在app第一次运行时请求app 必要 危险权限列表，非必要危险权限在用户使用时动态请求
     * <p>
     * 在{@link Activity#onRequestPermissionsResult 里调用 {@link #verifyPermissions}检查请求结果 ,如果全部被授权 则进入下一步，
     * 否则继续调用{@link #requestPermissions}继续请求权限组，系统会记录一杯授权的权限，请求剩下未被授权的权限
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 根据{@link PermissionUtil#REQUEST_CAMERA ...} 跳转到应用设置界面提示用户打开 危险权限组{@link R.array#permission_dangergous_group_name}
     *
     * @param mActivity
     * @param requestCode
     */
    public static void showAppSettingsSnackBar(final Activity mActivity, final int requestCode) {
        new SnackBarBuilder(mActivity, mActivity.getString(R.string.permission_not_ask), Snackbar.LENGTH_LONG).setBackgroundColor(R.color.white).setAction(mActivity.getString(R.string.permission_enable_now), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dangerousGroup[] = mActivity.getResources().getStringArray(R.array.permission_dangergous_group_name);
                Toast.makeText(mActivity, mActivity.getString(R.string.permission_setting) + " " + dangerousGroup[requestCode], Toast.LENGTH_LONG).show();
                startAppSettings(mActivity);
            }
        }).show();
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
