package com.biu.mvp.function.login;


import android.app.Activity;
import android.support.annotation.NonNull;

import com.biu.architecture.MCallBack;
import com.biu.architecture.retrofit.ApiException;
import com.biu.architecture.retrofit.ApiResponseBody;
import com.biu.modulebase.common.base.BaseFragment;
import com.biu.modulebase.common.util.Util;
import com.biu.mvp.bean.UserInfoBean;

import java.util.HashMap;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{Listens to user actions from the View({@link LoginFragment}), retrieves the data from the Model({@link LoginModel}) and updates the UI as required.}
 * @date 2017/1/19
 */
public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View mLoginView;
    private final LoginContract.Model mLoginModel;

    public LoginPresenter(@NonNull  LoginContract.View loginView) {
        mLoginView = loginView;
        mLoginModel =new LoginModel();
        mLoginView.setPresenter(this);//调用view中setPresenter的方法将View Presenter关联
    }

    @Override
    public void requestLogin(String account, String pwd, String deviceId) {
        if (account.isEmpty()) {
            mLoginView.showToast("请输入手机号码");
        } else if(!Util.isMobileNO(account)){
            mLoginView.showToast("您的手机号码输入有误...");
        }else if (pwd.isEmpty()) {
            mLoginView.showToast("请输入密码");
        } else {
            mLoginView.setLoadingIndicator(true);
            final HashMap params = new HashMap();
            params.put("mobile", account+"");
            String s= Util.encodeMD5(pwd);
            String pass=Util.encodeBase64(s);
            params.put("password", pass+"");
            params.put("device_type", "anim-v21");
            mLoginModel.doLogin(params, new MCallBack<UserInfoBean>() {
                @Override
                public void onSuccess(ApiResponseBody<UserInfoBean> responseBody) {
                    //登录成功且获取用户信息成功
                    mLoginView.setLoadingIndicator(false);
                    UserInfoBean userInfoModle =responseBody.getResult();
                    mLoginModel.saveLoginInfo( ((BaseFragment)mLoginView).getActivity(),userInfoModle);
                    ((BaseFragment)mLoginView).getActivity().setResult(Activity.RESULT_OK);
                    ((BaseFragment)mLoginView).getActivity().finish();
                }

                @Override
                public void onError(ApiException e) {
                    mLoginView.setLoadingIndicator(false);
                    mLoginView.showToast(e.getMessage());
                }

                @Override
                public void onTokenInvalid() {
                    mLoginView.setLoadingIndicator(false);
                    mLoginView.showLoginSnackBar();
                }

                @Override
                public void onNoNetwork() {
                    mLoginView.setLoadingIndicator(false);
                    mLoginView.showNetSnackBar();
                }
            });
        }
    }

    @Override
    public void start() {

    }

}
