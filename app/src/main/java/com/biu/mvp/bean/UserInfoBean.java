package com.biu.mvp.bean;

import com.biu.modulebase.common.base.BaseItem;

/**
 * @author chenbixin
 * @Title: {标题}
 * @Description:{描述}
 * @date ${2016/9/13}
 */
public class UserInfoBean extends BaseItem {
//    /**用户id**/
//    private String id;

    /**昵称**/
    private String nickName;
    /**头像**/
    private String headImg;
    /** 性别 1男 2女**/
    private int gender;
    /**签名**/
    private String signature;
    /** 认证状态 1未认证 2已认证 3认证中**/
    private String userType;
    /** 取消次数**/
    private String quitNumber;
    /**是否有新消息 1有  2没有**/
    private String hasMessage;
    /**电话号码**/
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getQuitNumber() {
        return quitNumber;
    }

    public void setQuitNumber(String quitNumber) {
        this.quitNumber = quitNumber;
    }

    public String getHasMessage() {
        return hasMessage;
    }

    public void setHasMessage(String hasMessage) {
        this.hasMessage = hasMessage;
    }

    /**
     *
     * @return 返回性别图标资源id
     */
//    public int getGenderImg(){
//        return this.gender==1? R.mipmap.gender2:R.mipmap.gender1;
//    }
}
