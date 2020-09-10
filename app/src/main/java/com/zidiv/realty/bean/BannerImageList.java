package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

public class BannerImageList extends MStatus{

    private List<BannerImageInfo> ds;

    public void setDs(List<BannerImageInfo> ds) {
        this.ds = ds;
    }

    public List<BannerImageInfo> getDs() {
        return ds;
    }

    public class BannerImageInfo implements Serializable {

        private String id;
        private String imageurl;
        private String url;
        private String CreateTime;


        @Override
        public String toString() {
            return id + "。。" + url + "。。" + imageurl + "。。" + CreateTime ;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageurl() {
            return imageurl;
        }

        public String getID() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        public String getCreateTime() {
            return CreateTime;
        }
    }
}