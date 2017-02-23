package com.biu.modulebase.common.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.biu.modulebase.common.widget.loading.LoadingDialogFragment;
import com.biu.modulebase.R;
import com.biu.modulebase.common.util.LogUtil;
import com.biu.modulebase.common.util.PreferencesUtil;
import com.biu.modulebase.common.util.Util;


/**
 * @author Lee
 * @Title: {Fragment 基类}
 * @Description:{描述}
 * @date 2016/1/25
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BaseFragment";
    protected BaseFragmentActivity mBaseFragmetActivity;
    protected BaseActivity mBaseActivity;

    private ViewGroup mContainert;
    private ViewGroup loading_layout;
    private ViewGroup no_data_layout;
    private FrameLayout no_net_layout;
    private Button noNetWorkBtn;
    private AnimationDrawable frameAnimation;

    /**
     * 设置OptionMenu
     */
    protected void setOptionMenu(boolean hasOption,int menuId) {
    }

    /**
     * 初始化控件
     */
    protected abstract void initView(View rootView);
    /**
     * 加载数据，一切网络请求方法在此方法中写
     */
    public abstract void loadData();
    /**
     * 获取intent中的extra
     */
    protected  void getIntentData(){}
    /**
     * 设置监听事件
     */
    protected void setListener(){}

    /**
     * 重新加载数据(子 Fragment 加载数据失败可覆盖该方法通过点击 NoDataLayout 任意位置进行重新加载数据)
     */
    protected void reLoadData() {}

    protected ViewGroup getContainer(View rootView){return null;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        ViewGroup cont=getContainer(container);
        View rootView = inflater.inflate(R.layout.fragment_base_frame_load, cont!=null?cont:container,
            true);
        loading_layout = (ViewGroup) rootView.findViewById(R.id.loading_layout);
        no_data_layout = (ViewGroup) rootView.findViewById(R.id.no_data_layout);
        no_net_layout =(FrameLayout)rootView.findViewById(R.id.no_net_layout);
        noNetWorkBtn = (Button) rootView.findViewById(R.id.tryBtn);
        noNetWorkBtn.setOnClickListener(this);
        no_data_layout.setOnClickListener(this);
        ProgressBar loadingImg = (ProgressBar) rootView.findViewById(R.id.loading);
//        if (loadingImg != null) {
//            frameAnimation = (AnimationDrawable) loadingImg.getDrawable();
//        }
        return cont != null ? container : rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity=getActivity();
        if (activity instanceof BaseFragmentActivity) {
            mBaseFragmetActivity = (BaseFragmentActivity) activity;
        }
        if (activity instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) activity;
        }
        getIntentData();
        initView(getView());
        setListener();
        loadData();
    }
//
//    /**
//     * 举报Intent
//     * @param project_id 具体的举报项的id
//     * @param type {@link ReportContentFragment#REPORT_TYPE_ORDER } or {@link ReportContentFragment#REPORT_TYPE_COMMENT}
//     */
//    public void startIntent4Report(String project_id, int type){
//        Intent intent = new Intent(getActivity(), ReportTypeActivity.class);
//        intent.putExtra("project_id", project_id);
//        intent.putExtra("type", type);
//        startActivity(intent);
//    }
//
//    /**
//     * 跳轉图片预览界面的Intent
//     * @param position  点击图片在图片集中的位置
//     * @param imgList   图片集
//     */
//    public void startIntent4PhotoPreview(int position, ArrayList<String> imgList){
//        Intent intent = new Intent(getActivity(), PhotoViewActivity.class);
//        intent.putExtra("position",position);
//        intent.putStringArrayListExtra("imgs",imgList);
//        startActivity(intent);
//    }


///**
// * 跳转他人的个人中心
// * 传入id
// * **/
//   public  void startOtherInfoIntent(String id){
//        Intent intent=new Intent(getActivity(), OthersInfoActvity.class);
//       intent.putExtra("id",id);
//       startActivity(intent);
//   }

    /**
     * Toast
     *
     * @param msg
     * @param time LENGTH_SHORT = 0;/ LENGTH_LONG = 1;
     */
    public void showTost(String msg, int time) {
        if(getActivity()!=null)
            Toast.makeText(getActivity(), msg, time).show();
    }


    /**
     * 显示loading
     *      初始化界面时首先显示的元素
     */
    public void visibleLoading() {
        inVisibleNoData();
        inVisibleNoNetWork();
        if (!loading_layout.isShown()) {
            LogUtil.LogE(TAG,"visibleLoading");
            loading_layout.setVisibility(View.VISIBLE);
            // Start the animation (looped playback by default).
            if (frameAnimation != null) {
                frameAnimation.start();
            }
        }
    }

    /**
     * 隐藏loading
     */
    public void inVisibleLoading() {
        LogUtil.LogE(TAG, "inVisibleLoading");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            if (frameAnimation != null && frameAnimation.isRunning()) {
                frameAnimation.stop();
            }
                if(loading_layout!=null)
                    loading_layout.setVisibility(View.GONE);
            }
        },1000);

    }

    /**
     * 显示空数据布局
     */
    public void visibleNoData() {
        inVisibleLoading();
        inVisibleNoNetWork();
        if (!no_data_layout.isShown()) {
            no_data_layout.setVisibility(View.VISIBLE);
        }
    }


    public void visibleNoNetWork(){
        inVisibleLoading();
        inVisibleNoData();
        if(no_net_layout!=null)
            no_net_layout.setVisibility(View.VISIBLE);
    }

    public void inVisibleNoNetWork(){
        if(no_net_layout!=null)
            no_net_layout.setVisibility(View.GONE);
    }

    /**
     * 影藏空数据布局
     */
    public void inVisibleNoData() {
        if(no_data_layout!=null)
            no_data_layout.setVisibility(View.GONE);
    }

    /**
     * 显示progress loading
     *    进行操作需请求网络时显示  如 登录 注册...
     *    @param  requestTag  网络请求tag，用于取消请求
     */
    public void showProgress(String requestTag) {
            LoadingDialogFragment loading =LoadingDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE, R.layout.loading,requestTag);
            FragmentTransaction ft =getChildFragmentManager().beginTransaction();
            ft.addToBackStack(null);
            loading.show(ft, "loading");
    }

    /**
     *隐藏progress loading
     */
    public void dismissProgress() {
        FragmentManager fm = getChildFragmentManager();
        LoadingDialogFragment loading = (LoadingDialogFragment) fm.findFragmentByTag("loading");
        if (loading != null) {
            loading.dismiss();
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                FragmentManager fm = getChildFragmentManager();
//                LoadingDialogFragment loading = (LoadingDialogFragment) fm.findFragmentByTag("loading");
//                if (loading != null) {
//                    loading.dismiss();
//                }
//            }
//        },300);

    }


    /**
     * 软盘控制
     * 显示或隐藏软键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showSoftKeyboard2(View focusedView) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(focusedView, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if(inputManager.isActive()&& getActivity().getCurrentFocus()!=null){
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
    }
    public boolean checkIsLogin(BaseActivity loginActivity){
        if(Util.isEmpty(PreferencesUtil.getString(getActivity(), PreferencesUtil.KEY_TOKEN))){
            showUnLoginSnackbar(loginActivity);
            return false;
        }
        return true;
    }

    public void showUnLoginSnackbar(final BaseActivity loginActivity) {
        Snackbar snackbar = Snackbar.make(
                ((ViewGroup) getActivity().getWindow().getDecorView().getRootView().findViewById(
                        android.R.id.content)).getChildAt(0),
                "未登录，请先登录", Snackbar
                .LENGTH_SHORT)
                .setAction("去登录",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isAdded())
                            startActivity(new Intent(getActivity(), loginActivity.getClass()));
                    }
                });
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.white));
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSetNetworkSnackbar() {
        Snackbar snackbar = Snackbar.make( ((ViewGroup) getActivity().getWindow().getDecorView().getRootView().findViewById(
                android.R.id.content)).getChildAt(0), "当前无网络", Snackbar.LENGTH_SHORT).setAction("去设置",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setNetwork();
                    }
               });
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.white));
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


    public void hideNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getActivity().getWindow().getDecorView();
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
            View decorView = getActivity().getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public BaseFragmentActivity getBaseFragmentActivity() {
        if (mBaseFragmetActivity == null) {
            Activity activity = getActivity();
            if (activity instanceof BaseFragmentActivity) {
                mBaseFragmetActivity = (BaseFragmentActivity) activity;
            }
        }
        return mBaseFragmetActivity;
    }

    public BaseActivity getBaseActivity() {
        if (mBaseActivity == null) {
            Activity activity = getActivity();
            if(activity instanceof BaseActivity) {
                mBaseActivity = (BaseActivity) activity;
            }
        }
        return mBaseActivity;
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.no_data_layout){
            visibleLoading();
                loadData();
        }else if(v.getId() == R.id.tryBtn){
            visibleLoading();
                loadData();
        }

    }

}