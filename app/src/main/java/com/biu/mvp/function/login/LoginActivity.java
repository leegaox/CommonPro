package com.biu.mvp.function.login;


import android.os.Bundle;

import com.biu.modulebase.common.base.BaseActivity;
import com.biu.modulebase.common.util.ActivityUtil;
import com.biu.mvp.R;


/**
 * @author chenbixin
 * @Title: {登录}
 * @Description:{描述}
 * @date ${2016/9/13}
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_back);
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), loginFragment, R.id.contentFrame);
        }
        // Create the presenter：创建后的fragment实例作为presenter的构造函数参数被传入
        new LoginPresenter(loginFragment);
    }



}
