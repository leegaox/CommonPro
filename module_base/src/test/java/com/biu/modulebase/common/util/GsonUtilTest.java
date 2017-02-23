package com.biu.modulebase.common.util;

import android.content.Context;

import com.biu.modulebase.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2017/2/8
 */
@RunWith(MockitoJUnitRunner.class)
public class GsonUtilTest {
    private static final String FAKE_STRING = "HELLO WORLD";
    @Mock
    Context mMockContext;

    @Test
    public void getJsonElement() throws Exception {
        System.out.print(GsonUtil.getJsonElement("{\"name\":\"怪盗kidou\",\"age\":24}"));
    }

    @Test
    public void getJsonObject() throws Exception {
        String jsonString ="{\"name\":\"怪盗kidou\",\"age\":24}";
        String jsonArray ="[\"{\\\"name\\\":\\\"怪盗kidou\\\",\\\"age\\\":24}\",\"{\\\"name\\\":\\\"怪盗kidou\\\",\\\"age\\\":24}\"]";
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.action_settings)).thenReturn(FAKE_STRING);
        System.out.println((GsonUtil.getJsonElement(jsonString)));
        System.out.println(GsonUtil.getJsonElement(jsonArray));
        System.out.println(GsonUtil.getJsonString(GsonUtil.getJsonElement("111")));
    }

    @Test
    public void getElement() throws Exception {
        String jsonString ="{\"name\":\"怪盗kidou\",\"age\":24}";
        String jsonString2 ="{\"result\":{\"school\":\"华罗庚中学\"}}";
        String jsonArray ="{\"result\":[\"{\\\"name\\\":\\\"怪盗kidou\\\",\\\"age\\\":24}\",\"{\\\"name\\\":\\\"怪盗kidou\\\",\\\"age\\\":24}\"]}";
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.action_settings)).thenReturn(FAKE_STRING);
        System.out.println(GsonUtil.getLong(GsonUtil.getJsonElement(jsonString).getAsJsonObject(),"sex"));
        System.out.println(GsonUtil.getString(GsonUtil.getJsonElement(jsonString).getAsJsonObject(),"name"));
        System.out.println(GsonUtil.getBoolean(GsonUtil.getJsonElement(jsonString).getAsJsonObject(),"sex"));
        System.out.println(GsonUtil.getInt(GsonUtil.getJsonElement(jsonString).getAsJsonObject(),"age"));

        System.out.println(GsonUtil.getJsonObject(GsonUtil.getJsonElement(jsonString2).getAsJsonObject(),"result"));
        System.out.println(GsonUtil.getJsonArray(GsonUtil.getJsonElement(jsonArray).getAsJsonObject(),"result"));
    }

}