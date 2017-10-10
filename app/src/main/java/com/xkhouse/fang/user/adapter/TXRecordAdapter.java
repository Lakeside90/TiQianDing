package com.xkhouse.fang.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.booked.entity.TXRecord;
import com.xkhouse.fang.user.entity.MSGNews;

import java.util.ArrayList;

/**
* 提现记录
 */
public class TXRecordAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TXRecord> recordList;
	private ModelApplication modelApp;

	public TXRecordAdapter(Context context, ArrayList<TXRecord> recordList){
		this.context = context;
		this.recordList = recordList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
	}
	
	public void setData(ArrayList<TXRecord> recordList){
		this.recordList = recordList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return recordList.size();
	}

	@Override
	public Object getItem(int position) {
		return recordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_tx_record, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		TXRecord txRecord = recordList.get(position);

		holder.money_txt.setText("体现金额：" + txRecord.getMoney());
		holder.date_txt.setText(txRecord.getCreate_time());
		if ("1".equals(txRecord.getStatus())) {
			holder.status_txt.setText("等待审核");
			holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_red));
		} else if ("1".equals(txRecord.getStatus())) {
			holder.status_txt.setText("等待汇款");
			holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_red));
		} else if ("1".equals(txRecord.getStatus())) {
			holder.status_txt.setText("打款成功");
			holder.status_txt.setTextColor(context.getResources().getColor(R.color.common_green));
		}

		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView money_txt;
		TextView date_txt;
		TextView status_txt;


		public ViewHolder(View view){

			money_txt = (TextView) view.findViewById(R.id.money_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			status_txt = (TextView) view.findViewById(R.id.status_txt);
		}
	}

}
