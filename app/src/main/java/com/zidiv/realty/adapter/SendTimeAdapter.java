package com.zidiv.realty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zidiv.realty.R;

import java.util.List;

public class SendTimeAdapter extends BaseAdapter {
	
	private List<String> data;
	private Context context;
	private LayoutInflater inflater;
	
	
	public SendTimeAdapter(List<String> data, Context context) {
		super();
		this.data = data;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.adapter_send_time, null);
			viewHolder.tv_send_time = (TextView) convertView.findViewById(R.id.tv_send_time);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv_send_time.setText(data.get(position));


		return convertView;
	}

	public class ViewHolder {
		TextView tv_send_time;
	}

}
