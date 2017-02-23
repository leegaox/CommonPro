package com.biu.modulebase.function.cameraroll;


import android.support.v4.app.Fragment;

import com.biu.modulebase.common.base.BaseFragmentActivity;


/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2016/1/19
 */
public class SelectImgActivity extends BaseFragmentActivity {
    @Override
    protected Fragment getFragment() {
        return new SelectImgFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "相机胶卷";
    }


}
