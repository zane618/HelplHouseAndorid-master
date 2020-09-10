package com.zidiv.realty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.bean.GenjinInfoList;
import com.zidiv.realty.bean.HouseInfoList;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class GenjinAdater extends BaseAdapter{
    private List<GenjinInfoList.GenjinInfo> list;
    private Context context;
    private LayoutInflater inflater;
    public GenjinAdater(Context context, List<GenjinInfoList.GenjinInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
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
            view = inflater.inflate(R.layout.adapter_genjin, null);
            mViewHolder.txt_state = (TextView) view.findViewById(R.id.txt_state);
            mViewHolder.txt_content = (TextView) view.findViewById(R.id.txt_content);
            mViewHolder.txt_time = (TextView) view.findViewById(R.id.txt_time);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        GenjinInfoList.GenjinInfo info = list.get(i);
        mViewHolder.txt_state.setText(info.getGendanState().toString().trim());
        mViewHolder.txt_time.setText(info.getCreateTime().toString().trim());

        if (info.getTheType().equals("1"))
        {
            mViewHolder.txt_content.setText("我："+info.getGendanContent().toString().trim());
        }else
        {
            if (info.getIsapp().equals("1"))
            {
                String strTemp=info.getPhone();
                if (strTemp.length()>3)
                {
                    mViewHolder.txt_content.setText(strTemp.substring(0,3)+"***"+strTemp.substring(strTemp.length()-3)+":"+info.getGendanContent().toString().trim());
                }else
                {
                    mViewHolder.txt_content.setText(strTemp+"***"+strTemp+":"+info.getGendanContent().toString().trim());
                }
            }else
            {
                mViewHolder.txt_content.setText(info.getUserName().toString().trim()+":"+info.getGendanContent().toString().trim());
            }
        }


        return view;
    }

    class ViewHolder {
        TextView txt_state;
        TextView txt_content;
        TextView txt_time;
    }
}
