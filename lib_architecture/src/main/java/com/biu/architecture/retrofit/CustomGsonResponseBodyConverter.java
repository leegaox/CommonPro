package com.biu.architecture.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * @author Lee
 * @Title: {自定义GsonResponseBdyConverter,处理ResponseBody}
 * @Description:{重写了converter方法：HttpStatus.key ! = 1 的情况, key ! = 1,就抛出异常}
 * @date 2016/12/14
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T>{
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        ApiResponseBody apiResult = gson.fromJson(response, ApiResponseBody.class);
        if (!apiResult.isOk()) {
            value.close();
            //抛出一个RuntimeException, 这里抛出的异常会到Subscriber的onError()/Callback的onFailure方法中统一处理
            throw new ApiException(apiResult.getKey(), apiResult.getMessage());
        }
        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
