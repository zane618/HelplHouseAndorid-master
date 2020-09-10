package com.zidiv.realty.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 房子信息类
 * Created by Administrator on 2016/5/24.
 */
public class HouseInfoList extends MStatus{

    private List<HouseInfo> ds;

    public void setDs(List<HouseInfo> ds) {
        this.ds = ds;
    }

    public List<HouseInfo> getDs() {
        return ds;
    }

    public class HouseInfo implements Serializable{
        private String id;
        private String house;
        private String housename;
        private String houseZD;
        private String houseFangHao;
        private String househuxing;
        private String houseMianji;
        private String houseCreateTime;
        private String houseZone;
        private String houseLianxiren;
        private String houseLianxifangshi;
        private String houseSoujia;
        private String houseZhuJiaType;
        private String houseZhujia;
        private String houseCen;
        private String houseCen2;
        private String houseYear;
        private String houseZhuangxiu;
        private String houseType;
        private String houseAddr;
        private String qq;
        private String houseBeizhu;
        private String km;

        @Override
        public String toString() {
            return id + "。。" + house + "。。" + housename + "。。" + houseZD + "。。" + houseFangHao + "。。" + househuxing + "面积" + houseMianji + "。。" + houseCreateTime + "。。" + houseZone + "。。" + houseLianxiren + "。。" + houseLianxifangshi + "售价" + houseSoujia
                    + "。。" + houseZhuJiaType + "。。" + houseCen + "。。" + houseCen2 + "。。" + houseYear + "。。" + houseZhuangxiu + "。。" + houseType + "。。" + houseAddr + "备注：" + houseBeizhu + "。。" +km;
        }

        public String getHouseZhujia() {
            return houseZhujia;
        }

        public String getHouseBeizhu() {
            return houseBeizhu;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public String getHouse() {
            return house;
        }

        public String getHousename() {
            return housename;
        }

        public String getHouseZD() {
            return houseZD;
        }

        public String getHouseFangHao() {
            return houseFangHao;
        }

        public String getHousehuxing() {
            return househuxing;
        }

        public String getHouseMianji() {
            return houseMianji;
        }

        public String getHouseCreateTime() {
            return houseCreateTime;
        }

        public String getHouseZone() {
            return houseZone;
        }

        public String getHouseLianxiren() {
            return houseLianxiren;
        }

        public String getHouseLianxifangshi() {
            return houseLianxifangshi;
        }

        public String getHouseSoujia() {
            return houseSoujia;
        }

        public String getHouseZhuJiaType() {
            return houseZhuJiaType;
        }

        public String getHouseCen() {
            return houseCen;
        }

        public String getHouseCen2() {
            return houseCen2;
        }

        public String getHouseYear() {
            return houseYear;
        }

        public String getHouseZhuangxiu() {
            return houseZhuangxiu;
        }

        public String getHouseType() {
            return houseType;
        }

        public String getHouseAddr() {
            return houseAddr;
        }

        public String getQq() {
            return qq;
        }

        public String getKm() {
            return km;
        }
    }
}
