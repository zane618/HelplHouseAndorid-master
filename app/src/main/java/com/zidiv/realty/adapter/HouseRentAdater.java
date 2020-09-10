package com.zidiv.realty.adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseInfoList;
import com.zidiv.realty.database.SqliteBrowser;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class HouseRentAdater extends BaseAdapter {
    private List<HouseInfoList.HouseInfo> list;
    private Context context;
    private LayoutInflater inflater;

    public HouseRentAdater(Context context, List<HouseInfoList.HouseInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void addItems(List<HouseInfoList.HouseInfo> newImtes) {
        list.addAll(newImtes);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mViewHolder = null;
        if (view == null) {
            mViewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_rent_adapter, null);
            mViewHolder.housename = (TextView) view.findViewById(R.id.housename);
            mViewHolder.houseZone = (TextView) view.findViewById(R.id.houseZone);
            mViewHolder.mianji = (TextView) view.findViewById(R.id.mianji);
            mViewHolder.housezhujia = (TextView) view.findViewById(R.id.housezhujia);
            mViewHolder.huxing = (TextView) view.findViewById(R.id.huxing);
            mViewHolder.ceng = (TextView) view.findViewById(R.id.ceng);
            mViewHolder.fanghao = (TextView) view.findViewById(R.id.fanghao);
            mViewHolder.ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
            mViewHolder.date = (TextView) view.findViewById(R.id.date);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        if (i % 2 == 0) {
            mViewHolder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            mViewHolder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.adapter_gb));
        }
        HouseInfoList.HouseInfo info = list.get(i);

        if (SqliteBrowser.dataExists(context, "rent" + info.getId())) {
            String str_Temp = info.getHousename() + "<font color='#09A7F0'>[已浏览]</font>";
            mViewHolder.housename.setText(Html.fromHtml(str_Temp));
        } else {
            mViewHolder.housename.setText(info.getHousename());
        }

        mViewHolder.houseZone.setText(info.getHouseZone());
        mViewHolder.mianji.setText(info.getHouseMianji() + "㎡");
        mViewHolder.housezhujia.setText(info.getHouseZhujia() == null ? "0" : info.getHouseZhujia() + info.getHouseZhuJiaType());
        mViewHolder.huxing.setText(info.getHousehuxing());
        mViewHolder.ceng.setText(info.getHouseCen() + "/" + info.getHouseCen2());
        if (MApplication.getMApplication().getIsLoginFlag()&&MApplication.getMApplication().getIsVIPFlag()) {
            mViewHolder.fanghao.setText(info.getHouseZD() + "#" + info.getHouseFangHao());
        } else {
            mViewHolder.fanghao.setText("***");
        }

        String strDate = info.getHouseCreateTime().substring(0, info.getHouseCreateTime().indexOf(' '));
        mViewHolder.date.setText(strDate);

        return view;
    }

    class ViewHolder {
        TextView housename;
        TextView houseZone;
        TextView mianji;
        TextView housezhujia;
        TextView huxing;
        TextView ceng;
        //2016 07 11
        TextView fanghao;
        TextView date;
        LinearLayout ll_bg;
    }
}