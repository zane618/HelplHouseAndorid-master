package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

public class PushInfoList extends MStatus{

    private List<PushInfo> ds;

    public List<PushInfo> getDs() {
        return ds;
    }

    public class PushInfo implements Serializable {
        private String UserID;
        private String RegistrationID;
        private String Switch;
        private String PushArea;
        private String IsDefaultSet;

        public void setId(String UserID) {
            this.UserID = UserID;
        }

        public String getUserID() {
            return UserID;
        }

        public String getRegistrationID() {
            return RegistrationID;
        }

        public String getSwitch() {
            return Switch;
        }

        public String getPushArea() {
            return PushArea;
        }

        public String getIsDefaultSet() {
            return IsDefaultSet;
        }


    }
}
