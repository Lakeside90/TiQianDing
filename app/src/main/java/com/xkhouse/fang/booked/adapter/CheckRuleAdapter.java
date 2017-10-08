package com.xkhouse.fang.booked.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xkhouse.fang.R;
import com.xkhouse.fang.booked.entity.PayDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 买单规则
 */
public class CheckRuleAdapter extends BaseAdapter {

	private Context context;
	private List<PayDetail.Rule> ruleList;

	private DisplayImageOptions options;

	public CheckRuleAdapter(Context context, List<PayDetail.Rule> ruleList){
		this.context = context;
		this.ruleList = ruleList;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nopic)   // 加载的图片
				.showImageOnFail(R.drawable.nopic) // 错误的时候的图片
				.showImageForEmptyUri(R.drawable.nopic)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}
	
	public void setData(List<PayDetail.Rule> ruleList){
		this.ruleList = ruleList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return ruleList.size();
	}

	@Override
	public Object getItem(int position) {
		return ruleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_check_rule_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		PayDetail.Rule rule = ruleList.get(position);

        holder.name_txt.setText(rule.getCheck_discount_name());

        holder.use_time_txt.setText(rule.getUse_time());

		return convertView;
	}
	


	public class ViewHolder{

        TextView name_txt;
        TextView use_time_txt;

		public ViewHolder(View view){

            name_txt = (TextView) view.findViewById(R.id.name_txt);
            use_time_txt = (TextView) view.findViewById(R.id.use_time_txt);
		}
	}



}
