package com.biu.architecture.rxjava;

import android.support.annotation.Nullable;

import com.biu.architecture.MCallBack;
import com.biu.architecture.retrofit.ApiException;

import rx.Subscriber;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{统一对Subscriber对网络返回进行处理 和 有无网络做判断;构建抽象的BaseSubscribe类，只处理start()和onCompleted（） ，上层处理时只处理onError（）和onNext（）}
 *      TODO...
 *      1.请求前的token判断；
 *      2.在onError里统一处理服务器返回的一些异常，如登录过期 (无数据暂时不处理 算是请求成功，在noNext中自行判断);
 * @date 2017/1/10
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private static final String TAG = "BaseSubscriber";
    private MCallBack mCallBack;

    public BaseSubscriber() {
    }

    public BaseSubscriber(@Nullable MCallBack mCallBack) {
        this.mCallBack =mCallBack;
    }

    @Override
    public void onStart() {
//        super.onStart();
//        if (!NetworkUtil.isNetworkConnected(context.getActivity())) {
//            onError(new ApiException(ExceptionHandle.ERROR.NO_NET,""));
//            unsubscribe();
//            return;
//        }

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        // todo error somthing
        ApiException apiException = ExceptionHandle.handleException(e);
        if (apiException.isTokenInvalid()) {
            mCallBack.onTokenInvalid();
        }else if(apiException.isNetConnected()){
            mCallBack.onNoNetwork();
        }else{
            mCallBack.onError(apiException);
        }
    }

}
