package com.zidiv.realty.database;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zidiv.realty.bean.HistoryBean;
import com.zidiv.realty.util.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 操作数据库
 * Created by Administrator on 2016/7/11.
 */
public class OperateDataBaseStatic {

    /**
     * 添加
     * @param context
     * @param content
     */
    public static boolean dataAdd(Context context, String content) {
        DbUtils utils = DbUtils.create(context);
        HistoryBean info = new HistoryBean();
        info.setContent(content);
        try {
            HistoryBean bean = utils.findFirst(Selector.from(HistoryBean.class).where("content", "=", content));
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
     * @param content
     */
    public static boolean dataDelete(Context context, String content) {
        DbUtils utils = DbUtils.create(context);
        try {
            utils.delete(HistoryBean.class, WhereBuilder.b("content", "=", content));
            L.e("删除成功");
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        utils.close();
        return true;
    }

    /**
     * 删除所有数据
     * @param context
     */
    public static boolean dataDeleteAll(Context context) {
        DbUtils utils = DbUtils.create(context);
        try {
            List<HistoryBean> list = utils.findAll(HistoryBean.class);
            utils.deleteAll(list);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        utils.close();
        return true;
    }

    /**
     * 查询所有记录
     * @param context
     * @return
     */
    public static List<HistoryBean> dataSelectAll(Context context) {
        DbUtils utils = DbUtils.create(context);
        List<HistoryBean> list =null;
        try {
            list = utils.findAll(HistoryBean.class);
            if (list != null && list.size() > 0) {
                Collections.reverse(list);
            }
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        utils.close();
        return list;
    }

}
