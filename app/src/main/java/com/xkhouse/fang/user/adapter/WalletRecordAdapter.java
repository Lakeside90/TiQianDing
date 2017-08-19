package com.xkhouse.fang.user.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.WalletRecord;
import com.xkhouse.lib.utils.StringUtil;

/**
* @Description: 钱包账单
* @author wujian  
* @date 2015-10-10 上午11:58:41
 */
public class WalletRecordAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<WalletRecord> recordList;
	
	public WalletRecordAdapter(Context context, ArrayList<WalletRecord> recordList){
		this.context = context;
		this.recordList = recordList;
		
	}
	
	public void setData(ArrayList<WalletRecord> recordList){
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
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_wallet_record_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
		WalletRecord record = recordList.get(position);
		
		holder.record_title_txt.setText(record.getTitle());
		holder.record_date_txt.setText(record.getDate());

		if("1".equals(record.getType())){
			holder.record_icon_iv.setImageResource(R.drawable.zhangdan_ico_yong);
		}else if("2".equals(record.getType())){
            holder.record_icon_iv.setImageResource(R.drawable.zhangdan_ico_yuan);
		}
        holder.record_num_txt.setText(record.getMoney() + "元");

        if(!StringUtil.isEmpty(record.getStateName())){
            holder.record_date_txt.setText(record.getStateName());
            holder.record_date_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
        }else{
            holder.record_date_txt.setText(record.getDate());
            holder.record_date_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
        }

		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView record_icon_iv;
		TextView record_title_txt;
		TextView record_date_txt;
		TextView record_num_txt;

		
		public ViewHolder(View view){
            record_icon_iv = (ImageView) view.findViewById(R.id.record_icon_iv);
            record_title_txt = (TextView) view.findViewById(R.id.record_title_txt);
			record_date_txt = (TextView) view.findViewById(R.id.record_date_txt);
			record_num_txt = (TextView) view.findViewById(R.id.record_num_txt);
			
		}
	}
	


	private String getDate(long time, String format){
		String str = "";
		if(time == 0){
			return str;
		}
		try {
			Date date = new Date(time);
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			str = sdf.format(date);
		} catch (Exception e) {
		}
		
		return str;
	}

}
