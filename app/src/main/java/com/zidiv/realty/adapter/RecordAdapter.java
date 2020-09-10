package com.zidiv.realty.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zidiv.realty.R;
import com.zidiv.realty.bean.HistoryBean;

import java.util.List;

/**
 * 历史搜索
 */
public class RecordAdapter extends BaseAdapter {

	private List<HistoryBean> data;
	private Context context;
	private LayoutInflater inflater;
	public ToDelete toDelete;

	public interface ToDelete {
		public void onDelete(int position);
	}

	public RecordAdapter(List<HistoryBean> data, Context context, ToDelete toDelete) {
		super();
		this.data = data;
		this.context = context;
		this.toDelete = toDelete;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.adapter_record, null);
			viewHolder.content = (TextView) convertView.findViewById(R.id.content);
			viewHolder.delete = (TextView) convertView.findViewById(R.id.delete);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.content.setText(data.get(position).getContent());
		viewHolder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (toDelete != null) {
					toDelete.onDelete(position);
				}
			}
		});
		return convertView;
	}

	public class ViewHolder {
		TextView content;
		TextView delete;
	}

}
