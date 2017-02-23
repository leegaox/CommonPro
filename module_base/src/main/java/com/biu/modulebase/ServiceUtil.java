package com.biu.modulebase;


import com.biu.architecture.retrofit.CustomGsonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{}
 * @date 2016/12/14
 */
public class ServiceUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MULTIPART = MediaType.parse("multipart/form-data");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//通过RxJavaCallAdapterFactory为Retrofit添加RxJava支持
                    .addConverterFactory(CustomGsonConverterFactory.create());//通过CustomGsonConverterFactory为Retrofit添加Gson支持

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
//        if (authToken != null) {
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                // Request customization: add request headers
//                  requestBuilder.header("Authorization", authToken)
//                  requestBuilder.method(original.method(), original.body());

                // Request customization: add request body
                if (original.body() instanceof FormBody) {
                    FormBody.Builder newFormBody = new FormBody.Builder();
                    FormBody oidFormBody = (FormBody) original.body();
                    //循环遍历formBody添加原始数据,追加默认参数
                    for (int i = 0; i < oidFormBody.size(); i++) {
                        newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
                    }
//                        String deviceId = MyApplication.getDeviceId();
//                        newFormBody.add("device_id",deviceId);
                    newFormBody.add("channel", Constant.ANDROID_CHANNEL);
                    newFormBody.add("version", Constant.VERSION);
//                        newFormBody.add("signature", Util.encodeMD5(deviceId.substring(0,5)+deviceId+Constant.ANDROID_CHANNEL));
                    requestBuilder.method(original.method(), newFormBody.build());
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
//        }
//        httpClient.interceptors().add(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//            @Override
//            public void log(String message) {
//                LogUtil.LogD("OkHttp",message);
//            }
//        }));
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    /**
     * 设置默认参数
     *
     * @param bodyMap
     * @param type
     */
    public static void setDefaultParams(Map<String, RequestBody> bodyMap, MediaType type) {
//        String dev= MyApplication.getDeviceId();
//        bodyMap.put("device_id",RequestBody.create(type,dev));
//        bodyMap.put("channel",RequestBody.create(type,Constant.ANDROID_CHANNEL));
//        bodyMap.put("version",RequestBody.create(type,Constant.VERSION));
//        bodyMap.put("signature",RequestBody.create(type,Util.encodeMD5(dev.substring(0,5)+dev+Constant.ANDROID_CHANNEL)));
    }

    /**
     * 将文件路径数组封装为{@link List < MultipartBody.Part>}
     *
     * @param key         对应请求正文中name的值。目前服务器给出的接口中，所有图片文件使用<br>
     *                    同一个name值，实际情况中有可能需要多个
     * @param filePaths   文件路径数组
     * @param contentType 文件类型  "multipart/form-data"
     */
    public static List<MultipartBody.Part> files2Parts(String key, Object[] filePaths, MediaType contentType) {
        List<MultipartBody.Part> parts = new ArrayList<>(filePaths.length);
        for (Object filePath : filePaths) {
            File file = new File((String) filePath);
            // 根据类型及File对象创建RequestBody（okhttp的类）
            RequestBody requestBody = RequestBody.create(contentType, file);
            // 将RequestBody封装成MultipartBody.Part类型（同样是okhttp的）
            MultipartBody.Part part = MultipartBody.Part.
                    createFormData(key, file.getName(), requestBody);
            // 添加进集合
            parts.add(part);
        }
        return parts;
    }

    /**
     * 其实也是将File封装成RequestBody，然后再封装成Part，<br>
     * 不同的是使用MultipartBody.Builder来构建MultipartBody
     *
     * @param key       同上
     * @param filePaths 同上
     * @param imageType 同上
     */
    public static MultipartBody filesToMultipartBody(String key, String[] filePaths, MediaType imageType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            RequestBody requestBody = RequestBody.create(imageType, file);
            builder.addFormDataPart(key, file.getName(), requestBody);
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

}
