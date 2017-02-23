package com.biu.architecture.retrofit;

import static com.biu.architecture.rxjava.ExceptionHandle.ERROR.NO_DATA;
import static com.biu.architecture.rxjava.ExceptionHandle.ERROR.NO_NET;
import static com.biu.architecture.rxjava.ExceptionHandle.ERROR.TOKEN_EXPRIED;

/**
 * @author Lee
 * @Title: {接口返回的异常  key!=1的情况}
 * @Description:{描述}
 * @date 2016/12/14
 */
public class ApiException extends RuntimeException {

    public static final int SUCCESS =1;

    private int errorCode;

    public ApiException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * PAI token失效
     * @return
     */
    public boolean isTokenInvalid(){
        return errorCode == TOKEN_EXPRIED;
    }

    /**
     * 是否有网络
     * @return
     */
    public boolean isNetConnected(){
        return errorCode == NO_NET;
    }

    /**
     * PAI 是否数据为空
     * @return
     */
    public boolean isDataEmpty(){
        return errorCode == NO_DATA;
    }



}
