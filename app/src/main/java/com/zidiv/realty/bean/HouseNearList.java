package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 */
public class HouseNearList extends MStatus {

    private List<NearInfo> ds;

    public List<NearInfo> getDs() {
        return ds;
    }

    public void setDs(List<NearInfo> ds) {
        this.ds = ds;
    }

    public class NearInfo implements Serializable {
        private String housename;
        private String nums;
        private String counts;
        private String KM;

        public String getHousename() {
            return housename;
        }

        public String getNums() {
            return nums;
        }

        public String getCounts() {
            return counts;
        }

        public String getKM() {
            return KM;
        }
    }
}
