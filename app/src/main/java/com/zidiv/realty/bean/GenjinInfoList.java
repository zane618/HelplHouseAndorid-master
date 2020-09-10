package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/25.
 */
public class GenjinInfoList extends MStatus{

    private List<GenjinInfo> ds;

    public List<GenjinInfo> getDs() {
        return ds;
    }

    public class GenjinInfo implements Serializable {
        private String id;
        private String addUser;
        private String createTime;
        private String houseId;
        private String kehuId;
        private String gendanState;
        private String gendanContent;
        private String isapp;
        private String theType;
        private String UserName;
        private String Phone;
//        id,[addUser],[createTime],[houseId],[kehuId],[gendanState],[gendanContent],[isapp],[theType],UserName,Phone

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getAddUser() {
            return addUser;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getHouseId() {
            return houseId;
        }

        public String getKehuId() {
            return kehuId;
        }

        public String getGendanState() {
            return gendanState;
        }

        public String getGendanContent() {
            return gendanContent;
        }

        public String getIsapp() {
            return isapp;
        }

        public String getTheType() {
            return theType;
        }
        public String getUserName() {
            return UserName;
        }

        public String getPhone() {
            return Phone;
        }
    }
}
