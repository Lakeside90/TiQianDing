package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.XKDynamic;

/**
* @Description: 楼盘销售动态 
* @author wujian  
* @date 2015-10-8 下午2:47:52
 */
public class HouseDynamicAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKDynamic> dynamicList;
	
	public HouseDynamicAdapter(Context context, ArrayList<XKDynamic> dynamicList){
		this.context = context;
		this.dynamicList = dynamicList;
	}
	
	@Override
	public int getCount() {
		return dynamicList.size();
	}

	@Override
	public Object getItem(int position) {
		return dynamicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_house_dynamic_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKDynamic dynamic = dynamicList.get(position);
		
		holder.dynamic_date_txt.setText(dynamic.getDataDate());
		holder.dynamic_content_txt.setText(dynamic.getContent());
		
		
		return convertView;
	}
	
	public class ViewHolder{
		
		TextView dynamic_date_txt;   
		TextView dynamic_content_txt;   
		
		
		public ViewHolder(View view){
			
			dynamic_date_txt = (TextView) view.findViewById(R.id.dynamic_date_txt);
			dynamic_content_txt = (TextView) view.findViewById(R.id.dynamic_content_txt);
		}
	}

}
