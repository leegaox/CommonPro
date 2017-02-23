package com.biu.modulebase.common.base;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biu.modulebase.R;

/**
 * Fragment为主要界面显示时使用该Activity
 */
public abstract class BaseFragmentActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView title;
    private AppBarLayout mBarLayout;

    protected abstract Fragment getFragment();

    protected abstract String getToolbarTitle();

    protected int getFragmentContainerId() {
        return R.id.fragmentContainer;
    }

    protected View getFragmentContainer() {
        return findViewById(getFragmentContainerId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        init();
    }

    protected void init() {
        setTitle(null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(getToolbarTitle());
        mBarLayout= (AppBarLayout) findViewById(R.id.layout_app_bar);
        FragmentManager fm = getSupportFragmentManager();
        Fragment content = fm.findFragmentById(R.id.fragmentContainer);
        if (content == null) {
            content = getFragment();
            if (content!=null) {
                fm.beginTransaction().add(getFragmentContainerId(), content).commitAllowingStateLoss();
            }
        }
    }

    /**
     *
     * @param layoutId
     */
    public View setToolBarCustomView(int layoutId) {
        setBackNavigationIcon();
        View view=getLayoutInflater().inflate(layoutId, null, false);
        ActionBar toolbar=getSupportActionBar();
        if (toolbar!=null) {
            Toolbar.LayoutParams layoutParams=new Toolbar.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity= Gravity.CENTER;
            toolbar.setCustomView(view,layoutParams);
            toolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            toolbar.setDisplayShowTitleEnabled(false);
            toolbar.setDisplayShowCustomEnabled(true);
            toolbar.setIcon(R.color.transparent);
            toolbar.setHomeButtonEnabled(true);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }
//    public void setToolbarVisible(Boolean f){
//        if (f ==true) {
//            mBarLayout.setVisibility(View.GONE);
//        }
//        else {
//            mBarLayout.setVisibility(View.VISIBLE);
//        }
//
//    }

    public View setToolBarCustomView(int layoutId, Toolbar.LayoutParams layoutParams) {
        setBackNavigationIcon();
        View view=getLayoutInflater().inflate(layoutId, null, false);
        ActionBar toolbar=getSupportActionBar();
        if (toolbar!=null) {
            layoutParams.gravity= Gravity.CENTER;
            toolbar.setCustomView(view,layoutParams);
            toolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            toolbar.setDisplayShowCustomEnabled(true);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }

    public Toolbar getToolbar() {
        if(toolbar ==null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        return toolbar;
    }

    /**
     * 设置标题
     * @param titleString
     */
    public void setToolBarTitle(String titleString) {
        if (title == null)
            title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(titleString);
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
