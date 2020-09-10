package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

public class NameList extends MStatus {

    private List<NameList.NameInfo> ds;

    public List<NameList.NameInfo> getDs() {
        return ds;
    }

    public void setDs(List<NameList.NameInfo> ds) {
        this.ds = ds;
    }

    public class NameInfo implements Serializable {
        private String houseName;

        public String getHousename() {
            return houseName;
        }
    }
}

