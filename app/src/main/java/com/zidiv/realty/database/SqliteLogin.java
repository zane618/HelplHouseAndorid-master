package com.zidiv.realty.database;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zidiv.realty.bean.LoginBean;
import com.zidiv.realty.util.L;

import java.util.Collections;
import java.util.List;

public class SqliteLogin {

    public static boolean addLoginBean(Context context, String login_name, String login_pwd, String login_out) {
        DbUtils utils = DbUtils.create(context);
        LoginBean info = new LoginBean();
        info.setLogin_name(login_name);
        info.setLogin_pwd(login_pwd);
        info.setLogin_out(login_out);
        try {
            dataDeleteAll(context, utils);
            utils.save(info);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }finally {
            utils.close();
        }

        return true;
    }


    public static LoginBean getLoginBean(Context context) {
        DbUtils utils = DbUtils.create(context);
        try {
            LoginBean bean = utils.findFirst(Selector.from(LoginBean.class).limit(2));
            return bean;
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 删除所有数据
     *
     * @param context
     */
    public static boolean dataDeleteAll(Context context, DbUtils utils) {

        try {
            List<LoginBean> list = utils.findAll(LoginBean.class);
            utils.deleteAll(list);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
