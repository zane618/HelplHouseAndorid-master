package com.zidiv.realty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.bean.NameList;

import java.util.List;

public class NameAdapter extends BaseAdapter {
    private List<NameList.NameInfo> list;
    private Context context;
    private LayoutInflater inflater;
    public NameAdapter(Context context, List<NameList.NameInfo> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public void addItems(List<NameList.NameInfo> newItems) {
        list.addAll(newItems);
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
        NameAdapter.ViewHolder mViewHolder = null;
        if (view == null) {
            mViewHolder = new NameAdapter.ViewHolder();
            view = inflater.inflate(R.layout.adapter_name, null);
            mViewHolder.txt_search_name = (TextView) view.findViewById(R.id.txt_search_name);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (NameAdapter.ViewHolder) view.getTag();
        }

        NameList.NameInfo info = list.get(i);
        mViewHolder.txt_search_name.setText(info.getHousename());
        return view;
    }

    class ViewHolder {
        TextView txt_search_name;
    }
}
