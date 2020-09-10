package com.zidiv.realty.bean;


import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/11.
 */
@Table(name = "LoginBean")
public class LoginBean implements Serializable{

    private int id;

    @Column(column = "login_name")
    private String login_name;

    @Column(column = "login_pwd")
    private String login_pwd;

    @Column(column = "login_out")
    private String login_out;


    public LoginBean() {
    }

    public String getLogin_name() {
        return login_name;
    }

    public String getLogin_pwd() {
        return login_pwd;
    }

    public String getLogin_out() {
        return login_out;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public void setLogin_pwd(String login_pwd) {
        this.login_pwd = login_pwd;
    }

    public void setLogin_out(String login_out) {
        this.login_out = login_out;
    }
}