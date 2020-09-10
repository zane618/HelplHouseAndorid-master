package com.zidiv.realty.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zidiv.realty.bean.BrowserBean;
import com.zidiv.realty.util.L;

import java.util.Collections;
import java.util.List;

public class SqliteBrowser {

    /**
     * 添加
     * @param context
     * @param browser
     */
    public static boolean dataAdd(Context context, String browser) {
        DbUtils utils = DbUtils.create(context);
        BrowserBean info = new BrowserBean();
        info.setBrowser(browser);
        try {
            BrowserBean bean = utils.findFirst(Selector.from(BrowserBean.class).where("browser", "=", browser));
            if (bean == null) {
                utils.save(info);
                L.e("存储成功");
            } else {
                L.e("已存在");
            }

        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        utils.close();
        return true;
    }

    /**
     * 删除一条
     * @param context
     * @param browser
     */
    public static boolean dataExists(Context context, String browser) {
        DbUtils utils = DbUtils.create(context);
        BrowserBean info = new BrowserBean();
        info.setBrowser(browser);
        try {
            BrowserBean bean = utils.findFirst(Selector.from(BrowserBean.class).where("browser", "=", browser));
            utils.close();
            if (bean == null) {
               return  false;
            } else {
               return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
            return false;
       }

    }

}