package com.zidiv.realty.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.application.MApplication;
import com.zidiv.realty.bean.HouseNearList;


import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class HouseNearAdapter extends BaseAdapter{
    private List<HouseNearList.NearInfo> list;
    private Context context;
    private LayoutInflater inflater;
    public HouseNearAdapter(Context context, List<HouseNearList.NearInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void addItems(List<HouseNearList.NearInfo> newImtes) {
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
            view = inflater.inflate(R.layout.item_near_adapter, null);
            mViewHolder.housename = (TextView) view.findViewById(R.id.housename);
//            mViewHolder.counts = (TextView) view.findViewById(R.id.counts);
            mViewHolder.KM = (TextView) view.findViewById(R.id.km);
            mViewHolder.ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        if (i % 2 == 0) {
            mViewHolder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            mViewHolder.ll_bg.setBackgroundColor(context.getResources().getColor(R.color.adapter_gb));
        }
        HouseNearList.NearInfo info = list.get(i);
        mViewHolder.housename.setText(info.getHousename());
//        mViewHolder.counts.setText("总计："+info.getCounts() + "套");
        Double mile= Double.parseDouble(info.getKM());

        if (mile.intValue()< 1000)
        {
            mViewHolder.KM.setText(mile.intValue() +"米" );
        }
        else {
            mViewHolder.KM.setText(mile.intValue()/1000.0 +"千米" );
        }

        return view;
    }

    class ViewHolder {
        TextView housename;
        TextView counts;
        TextView KM;
        TextView nums;
        LinearLayout ll_bg;
    }
}
