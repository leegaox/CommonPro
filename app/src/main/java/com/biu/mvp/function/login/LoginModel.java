package com.biu.mvp.function.login;

import android.content.Context;

import com.biu.architecture.MCallBack;
import com.biu.architecture.retrofit.ApiResponseBody;
import com.biu.architecture.rxjava.ApiResponseErrorFunc;
import com.biu.architecture.rxjava.BaseSubscriber;
import com.biu.modulebase.ServiceApi;
import com.biu.modulebase.ServiceUtil;
import com.biu.modulebase.common.util.GsonUtil;
import com.biu.modulebase.common.util.LogUtil;
import com.biu.modulebase.common.util.PreferencesUtil;
import com.biu.mvp.bean.UserInfoBean;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2017/1/19
 */
public class LoginModel implements LoginContract.Model{

    private String type;

    private String token;


    @Override
    public void doLogin(Map parmas, final MCallBack callBack) {
        //RxJava+Retrofit 使用flatMap进行登录后获取用户信息嵌套请求
        ServiceUtil.createService(ServiceApi.class)
                .doLogin(parmas)
                .onErrorResumeNext(new ApiResponseErrorFunc<ApiResponseBody>())
                .flatMap(new Func1<ApiResponseBody, Observable<ApiResponseBody>>() {
                    @Override
                    public Observable<ApiResponseBody> call(ApiResponseBody responseBody) {
                        LogUtil.LogD("RxJava", Thread.currentThread().toString()+"call...");
                        JsonObject jsonObject = GsonUtil.getJsonElement(responseBody.getResult().toString()).getAsJsonObject();
                        token= GsonUtil.getString(jsonObject,"token");
                        type= GsonUtil.getString(jsonObject,"type");
                        HashMap map = new HashMap();
                        map.put("token",token);
                        return ServiceUtil.createService(ServiceApi.class).getUserInfo(map);
                    }
                })
                //io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，使用computation(),否则 I/O 操作的等待时间会浪费 CPU。
                .subscribeOn(Schedulers.io())//指定订阅的行为（网络请求、IO操作等）发生的线程;
                .observeOn(AndroidSchedulers.mainThread())//指定订阅回调（影响onNext、onError）发生的线程 ；而onStart不是订阅回调，是在订阅之前的行为。
                .subscribe(new BaseSubscriber<ApiResponseBody<UserInfoBean>>(callBack) {

                    @Override
                    public void onNext(ApiResponseBody<UserInfoBean> responseBody) {
                        LogUtil.LogD("RxJava", Thread.currentThread().toString()+"onNext");
                        callBack.onSuccess(responseBody);
//
                    }

                });


    }


    @Override
    public void saveLoginInfo(Context context, UserInfoBean userInfoBean) {
//        MyApplication.userInfo = userInfoModle;
        PreferencesUtil.putString(context, PreferencesUtil.KEY_TYPE,type);
        PreferencesUtil.putString(context, PreferencesUtil.KEY_TOKEN, token);
        PreferencesUtil.putString(context, PreferencesUtil.KEY_IS_REALNAME,userInfoBean.getUserType());
        PreferencesUtil.putString(context, PreferencesUtil.KEY_USER_INFO, GsonUtil.toJson(userInfoBean).toString());
    }
}
