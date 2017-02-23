package com.biu.architecture.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{API返回的{"key":1,"message":"成功","result":""}中对应的“result”POJI }
 * @date 2016/12/14
 */
public class ApiResult<T> {

    @SerializedName("allPageNumber")
    private int allPageNumber;
    @SerializedName("time")
    private String time;
    @SerializedName(value = "list",alternate = {"orderList"})
    private List<T> list;

    public int getAllPageNumber() {
        return allPageNumber;
    }

    public void setAllPageNumber(int allPageNumber) {
        this.allPageNumber = allPageNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
