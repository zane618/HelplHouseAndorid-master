package com.zidiv.realty.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/11.
 */
@Table(name = "HistoryBean")
public class HistoryBean implements Serializable{

    private int id;

    @Column(column = "content")
    private String content;

    public HistoryBean() {
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

}
