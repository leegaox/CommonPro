package com.biu.architecture.rxjava;

import android.net.ParseException;

import com.biu.architecture.retrofit.ApiException;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2017/1/10
 */
public class ExceptionHandle {

    public static ApiException handleException(Throwable e) {
        String message ="未知错误";
        int errorCode = ERROR.UNKNOWN;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case ERROR.UNAUTHORIZED:
                case ERROR.FORBIDDEN:
                case ERROR.NOT_FOUND:
                case ERROR.REQUEST_TIMEOUT:
                case ERROR.GATEWAY_TIMEOUT:
                case ERROR.INTERNAL_SERVER_ERROR:
                case ERROR.BAD_GATEWAY:
                case ERROR.SERVICE_UNAVAILABLE:
                default:
                    errorCode = ERROR.HTTP_ERROR;
                    message = "网络错误";
                    break;
            }
        } else if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            message = apiException.getMessage();
            errorCode =apiException.getErrorCode();
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            errorCode = ERROR.PARSE_ERROR;
            message = "解析错误";
        } else if (e instanceof ConnectException) {
            errorCode = ERROR.NETWORD_ERROR;
            message = "连接失败";
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            errorCode = ERROR.SSL_ERROR;
            message = "证书验证失败";
        } else if (e instanceof UnknownHostException){
            errorCode = ERROR.NO_NET;
            message = "没有网络";
        }
        else {
            errorCode = ERROR.UNKNOWN;
            message = "未知错误";
        }
        return new ApiException(errorCode,message);
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**Common Http Status Code**/
        private static final int UNAUTHORIZED = 401;// （未授权） 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
        private static final int FORBIDDEN = 403;//（禁止） 服务器拒绝请求。
        private static final int NOT_FOUND = 404;//（未找到） 服务器找不到请求的网页
        private static final int REQUEST_TIMEOUT = 408;//（请求超时）  服务器等候请求时发生超时。
        private static final int INTERNAL_SERVER_ERROR = 500;//(服务器内部错误）  服务器遇到错误，无法完成请求。
        private static final int BAD_GATEWAY = 502;//（错误网关） 服务器作为网关或代理，从上游服务器收到无效响应。
        private static final int SERVICE_UNAVAILABLE = 503;//（服务不可用） 服务器目前无法使用（由于超载或停机维护）。 通常，这只是暂时状态。
        private static final int GATEWAY_TIMEOUT = 504;//（网关超时）  服务器作为网关或代理，但是没有及时从上游服务器收到请求。

        //API 约定 Status Code
        /**没有相关数据 也算成功**/
        public static final int NO_DATA =3;
        /**token失效**/
        public static final int TOKEN_EXPRIED= 1024;


        /**没有网络**/
        public static final int NO_NET = 999;
        /**未知错误**/
        public static final int UNKNOWN = 1000;
        /**解析错误**/
        public static final int PARSE_ERROR = 1001;
        /**网络错误**/
        public static final int NETWORD_ERROR = 1002;
        /**协议出错**/
        public static final int HTTP_ERROR = 1003;
        /**证书出错**/
        public static final int SSL_ERROR = 1005;
    }

}
