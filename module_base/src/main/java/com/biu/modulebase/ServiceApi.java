package com.biu.modulebase;

import com.biu.architecture.retrofit.ApiResponseBody;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author Lee
 * @Title: {接口服务}
 * @Description:{使用Retrofit将HTTP API转换为Java接口}
 *      TODO...
 *      1. 通用Get,Post API 请求封装
 *
 * @date 2016/11/25
 */
public interface ServiceApi {


    /**
     * 通用Get Api
     * @param url
     * @param maps
     * @param <T>
     * @return
     */
    @GET()
    <T> Observable<ApiResponseBody> get(@Url String url, @QueryMap Map<String, T> maps);

    /**
     * 通用Post Api
     * @param url
     * @param parmas
     * @param <T>
     * @return
     */
    @POST
    <T> Observable<ApiResponseBody<T>> post(@Url String url, @FieldMap Map<String, T> parmas);

    /**
     * 登录
     * @param params
     * @return ApiResult为服务器返回的基本Json数据的Model类
     */
//    @Headers({"Content-Type: application/json;charset=UTF-8","Accept: application/json"})
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("app_login")
    @FormUrlEncoded
    Observable<ApiResponseBody> doLogin(@FieldMap Map<String, Object> params);

    /**
     * 获取个人信息
     * @param params 请求参数
     * @return ApiResult为服务器返回的基本Json数据的Model类
     */
    @POST("user_userInfo")
    @FormUrlEncoded
    <T> Observable<ApiResponseBody<T>> getUserInfo(@FieldMap Map<String, Object> params);
//
//    /**根据主评论获取回复列表**/
//    @POST("app_findBillReplyList")
//    @FormUrlEncoded
//    Call<ApiResponseBody<ApiResult<ReplyDetailItem>>> getReplyListByCommentId(@FieldMap Map<String, Object> params);

    /**回复评论**/
    @POST("user_replyCommentBill")
    @FormUrlEncoded
    Call<ApiResponseBody> replyComment(@FieldMap Map<String, Object> params);


    /**删除评论**/
    @POST("user_deleteBillComment")
    @FormUrlEncoded
    Call<ApiResponseBody> deleteComment(@FieldMap Map<String, Object> params);

    /**删除回复**/
    @POST("user_deleteBillReply")
    @FormUrlEncoded
    Call<ApiResponseBody> deleteReply(@FieldMap Map<String, Object> params);


    /**举报*/
    @Multipart//多文件上传
    @POST("user_report")
    Call<ApiResponseBody> doReport(@PartMap Map<String, RequestBody> params, @Part List<MultipartBody.Part> parts);

    /**
     * 修改个人信息接口
     * 注意1：必须使用{@code @POST}注解为post请求<br>
     * 注意：使用{@code @Multipart}注解方法，必须使用{@code @Part},{@code @PartMap}注解其参数<br>/<br>
     * 本接口中将文本数据和文件数据分为了两个参数，是为了方便将封装<br>
     * {@link MultipartBody.Part}的代码抽取到工具类中<br>
     * 也可以合并成一个{@code @Part}参数
     * @param params 用于封装文本数据
     * @param parts 用于封装文件数据
     * @return ApiResult为服务器返回的基本Json数据的Model类
     */
    @Multipart//多文件上传
    @POST("user_updateInfo")
    Call<ApiResponseBody<JsonObject>> updateInfo(@PartMap Map<String, RequestBody> params, @Part List<MultipartBody.Part> parts);

    /**
     * 上传错误日志
     *
     * 注意1：必须使用{@code @POST}注解为post请求<br>
     * 注意2：使用{@code @Body}注解参数，则不能使用{@code @Multipart}注解方法了<br>
     * 直接将所有的{@link MultipartBody.Part}合并到一个{@link MultipartBody}中
     */
    @POST("app_errorUpload")
    Call<String> uploadErrorLog2(@Body MultipartBody body);


    /**帮帮订单上传抢单人经纬度**/
    @POST("user_upPosition")
    @FormUrlEncoded
    Call<ApiResponseBody> upPosition(@FieldMap Map<String, Object> params);

    /**根据token查询是否有未完成的帮帮订单**/
    @POST("user_getNoSuccess")
    @FormUrlEncoded
    Call<ApiResponseBody<JsonObject>> checkHelpOrderNum(@FieldMap Map<String, Object> params);


}
