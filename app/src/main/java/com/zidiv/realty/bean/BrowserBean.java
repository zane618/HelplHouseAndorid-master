package com.zidiv.realty.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/11.
 */
@Table(name = "BrowserBean")
public class BrowserBean implements Serializable{

    private int id;

    @Column(column = "browser")
    private String browser;

    public BrowserBean() {
    }

    public String getBrowser() {
        return browser;
    }


    public void setBrowser(String content) {
        this.browser = content;
    }

}
