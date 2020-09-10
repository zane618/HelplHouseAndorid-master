package com.zidiv.realty.bean;

/**
 * Created by Administrator on 2016/3/18.
 */
public class UserInfo {
    private String user_name;
    private String user_phone;
    private String user_avatar;
    private int user_sex;
    private String user_sex_name;
    private String user_email;
    private String clientid;
    private String devicetoken;

    public UserInfo() {
    }

    public UserInfo(String user_name, String user_phone, String user_avatar, int user_sex, String user_sex_name, String user_email, String clientid, String devicetoken) {
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.user_avatar = user_avatar;
        this.user_sex = user_sex;
        this.user_sex_name = user_sex_name;
        this.user_email = user_email;
        this.clientid = clientid;
        this.devicetoken = devicetoken;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public void setUser_sex(int user_sex) {
        this.user_sex = user_sex;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public void setUser_sex_name(String user_sex_name) {
        this.user_sex_name = user_sex_name;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public int getUser_sex() {
        return user_sex;
    }

    public String getUser_sex_name() {
        return user_sex_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getClientid() {
        return clientid;
    }

    public String getDevicetoken() {
        return devicetoken;
    }
}
