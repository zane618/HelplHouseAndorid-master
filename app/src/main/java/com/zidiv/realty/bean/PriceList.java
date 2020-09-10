package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

public class PriceList extends MStatus{

    private List<PriceInfo> ds;

    public void setDs(List<PriceInfo> ds) {
        this.ds = ds;
    }

    public List<PriceInfo> getDs() {
        return ds;
    }

    public class PriceInfo implements Serializable {

        private String PriceID;
        private String PriceValue;
        private String AddDay;
        private String CreateTime;
        private String PriceName;


        @Override
        public String toString() {
            return PriceID + "。。" + PriceValue + "。。" + AddDay + "。。"+ PriceName + "。。" + CreateTime ;
        }

        public void setId(String PriceID) {
            this.PriceID = PriceID;
        }

        public String getPriceValue() {
            return PriceValue;
        }

        public String getPriceName() {
            return PriceName;
        }

        public String getPriceID() {
            return PriceID;
        }

        public String getAddDay() {
            return AddDay;
        }

        public String getCreateTime() {
            return CreateTime;
        }
    }
}