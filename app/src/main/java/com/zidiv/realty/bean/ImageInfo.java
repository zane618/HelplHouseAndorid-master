package com.zidiv.realty.bean;

/**
 * 轮播图的信息实体
 * Created by Administrator on 2016/3/24.
 */
public class ImageInfo {
    private String imageurl;
    private String title;
    private String src;
    private String foreign_id;
    private String type_id;
    private String is_web;
    public ImageInfo(String imageurl){this.imageurl = imageurl;}
    public ImageInfo(String is_web, String type_id, String foreign_id, String src, String title, String imageurl) {
        this.is_web = is_web;
        this.type_id = type_id;
        this.foreign_id = foreign_id;
        this.src = src;
        this.title = title;
        this.imageurl = imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public void setForeign_id(String foreign_id) {
        this.foreign_id = foreign_id;
    }

    public void setIs_web(String is_web) {
        this.is_web = is_web;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getTitle() {
        return title;
    }

    public String getSrc() {
        return src;
    }

    public String getForeign_id() {
        return foreign_id;
    }

    public String getType_id() {
        return type_id;
    }

    public String getIs_web() {
        return is_web;
    }
}
