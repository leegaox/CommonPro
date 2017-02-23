package com.biu.modulebase.common.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.biu.modulebase.R;

/**
 * 封装一些常用的方法
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

   private static final String TAG = "BaseActivity";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void hideNavBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void showNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 1.Android5.0以上：material design风格，半透明(APP 的内容不被上拉到状态
     * 2.Android4.4(kitkat)以上至5.0：全透明(APP 的内容不被上拉到状态)
     * 3.Android4.4(kitkat)以下:不占据status bar
     *
     */
    protected void setStatusBar(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 软盘控制
     * 显示或隐藏软键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showSetNetworkSnackbar() {
        Snackbar snackbar = Snackbar.make(((ViewGroup) this.getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), "当前无网络", Snackbar.LENGTH_SHORT).setAction("去设置",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setNetwork();
                    }
                });
//        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.white));
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void setNetwork(){
        Intent intent = null;
        /**
         * 判断手机系统的版本！如果API大于10 就是3.0+
         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
         */
        if (Build.VERSION.SDK_INT > 10) {
            intent = new Intent(Settings.ACTION_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(
                    "com.android.settings","com.android.settings.Settings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        startActivity(intent);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeybord(){
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager.isActive()&& getCurrentFocus()!=null){
            inputManager.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(),0);
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
//            View v = getCurrentFocus();
//
//            if (isShouldHideInput(v, ev)) {
//                hideSoftKeybord();
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public Toolbar getToolbar() {
        if(toolbar ==null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    public void setBackNavigationIcon(int... icon) {
        getToolbar();
//        toolbar.setNavigationIcon(icon != null && icon.length > 0 ? icon[0]
//                : R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationIcon(icon != null && icon.length > 0 ? icon[0]
                : R.mipmap.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }





}
