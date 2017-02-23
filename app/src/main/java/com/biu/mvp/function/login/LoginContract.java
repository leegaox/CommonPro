package com.biu.mvp.function.login;


import android.content.Context;

import com.biu.architecture.BaseModel;
import com.biu.architecture.BasePresenter;
import com.biu.architecture.BaseView;
import com.biu.architecture.MCallBack;
import com.biu.mvp.bean.UserInfoBean;

import java.util.Map;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{指定view 和presenter之间的契约关系}
 * @date 2017/1/18
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showToast(String message);

        void visiblePwd();

        void startIntent4Register();

        void startIntent4ForgetPwd();

    }

    interface Presenter extends BasePresenter {

        void requestLogin(String account, String pwd, String deviceId);
    }

    interface Model extends BaseModel {

        void doLogin(Map parmas, MCallBack callBack);

        void saveLoginInfo(Context context, UserInfoBean userInfoModle);

    }

}
