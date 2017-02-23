package com.biu.modulebase.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Json处理
 *
 * @author Lee
 */
public class GsonUtil {
    /************************
     * Gson
     **************************************/
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static Gson getGson() {
        return gson;
    }

    /**
     * 基本数据类型解析
     *    int i = gson.fromJson("100", int.class);              //100
     *    double d = gson.fromJson("\"99.99\"", double.class);  //99.99
     *    boolean b = gson.fromJson("true", boolean.class);     // true
     *    String str = gson.fromJson("String", String.class);   // String
     *    String jsonString = "{\"name\":\"怪盗kidou\",\"age\":24}";
     *    User user = gson.fromJson(jsonString, User.class);POJO类的生成
     * @param json
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        try {
            return gson.fromJson(json, clz);

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  eg.
     *   Type userType = new TypeToken<Result<User>>(){}.getType();
     *   Result<User> userResult = fromJson(json,userType);
     *   User user = userResult.data;
     *
     *   Type userListType = new TypeToken<Result<List<User>>>(){}.getType();
     *   Result<List<User>> userListResult = fromJson(json,userListType);
     *   List<User> users = userListResult.data;
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Type type) {

        return gson.fromJson(json, type);
    }

    /**
     * 基本数据类型的生成
     *  String jsonNumber = gson.toJson(100);       // 100
     *  String jsonBoolean = gson.toJson(false);    // false
     *  String jsonString = gson.toJson("String"); //"String"
     *   User user = new User("怪盗kidou",24);
     *   String jsonObject = gson.toJson(user); // {"name":"怪盗kidou","age":24}POJO类的解析
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * string转换成JsonElement
     * @param jsonString
     * @return
     */
    public static JsonElement getJsonElement(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonEl = parser.parse(jsonString);
            if (jsonEl != null && !jsonEl.isJsonNull()) {
                return jsonEl;
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Java原始类型的JsonElement 转换成String
     * @param jsonElement
     * @return
     */
    public static String getJsonString(JsonElement jsonElement) {
        if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
        }
        return ""; // 默认""
    }

    @SuppressWarnings("null")
    public static Integer getJsonInt(JsonElement jsonElement) {
        if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonPrimitive()) {
            boolean isInt = true;
            try {
                int num = jsonElement.getAsInt();
                return num;
            } catch (JsonParseException e) {
                isInt = false;
            } catch (Exception e) {
            }
            if (!isInt) {
                try {
                    String str = jsonElement.getAsString();
                    if (str != null || !str.equals("null") || !str.equals("")) {
                        return Integer.valueOf(str);
                    }
                } catch (JsonParseException e) {
                } catch (Exception e) {
                }
            }
        }
        return 0; // 默认==0
    }

    public static Boolean getJsonBoolean(JsonElement jsonElement) {
        if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsBoolean();
        }
        return false; // 默认false
    }

    /**
     * 获取JsonObject中的JsonObject
     * @param jsonObject 待取值的JsonObject
     * @param name       目标在JsonObject中的键值
     * @return
     */
    public static JsonObject getJsonObject(JsonObject jsonObject,String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){

        }
        JsonObject result =jsonObject.get(name).getAsJsonObject();
        return null;
    }


    /**
     * 获取JsonObject中的JsonArray
     * @param jsonObject 待取值的JsonObject
     * @param name       目标在JsonObject中的键值
     * @return
     */
    public static JsonArray getJsonArray(JsonObject jsonObject,String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){
            return jsonObject.get(name).getAsJsonArray();
        }
        return null;
    }

    /**
     * 获取JsonObject中的Boolean
     * @param jsonObject 待取值的JsonObject
     * @param name       目标在JsonObject中的键值
     * @return
     */
    public static Boolean getBoolean(JsonObject jsonObject,String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){
            return jsonObject.get(name).getAsBoolean();
        }
        return false;
    }

    /**
     * 获取JsonObject中的Long
     * @param jsonObject 待取值的JsonObject
     * @param name       目标在JsonObject中的键值
     * @return
     */
    public static Long getLong(JsonObject jsonObject,String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){
            return jsonObject.get(name).getAsLong();
        }
        return Long.valueOf(0);
    }

    /**
     * 获取JsonObject中的String
     * @param jsonObject 待取值的jsonObject
     * @param name 目标在jsonObject中的键值
     * @return
     */
    public static String getString(JsonObject jsonObject, String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){
            return jsonObject.get(name).getAsString();
        }
        return null;
    }

    /**
     * 获取JsonObject中的Int
     * @param jsonObject 待取值的jsonObject
     * @param name 目标在jsonObject中的键值
     * @return
     */
    public static int getInt(JsonObject jsonObject,String name){
        if(!jsonObject.isJsonNull()&& jsonObject.has(name)){
            return jsonObject.get(name).getAsInt();
        }
        return 0;
    }

}
