package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.MSGSystem;

import java.util.ArrayList;

/**
 * 打赏详情列表
 */
public class DSDetailAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MSGSystem> systemList;

	public DSDetailAdapter(Context context, ArrayList<MSGSystem> systemList){
		this.context = context;
		this.systemList = systemList;

	}
	
	public void setData(ArrayList<MSGSystem> systemList){
		this.systemList = systemList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
//		return systemList.size();
        return 10;
	}

	@Override
	public Object getItem(int position) {
//		return systemList.get(position);
        return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_employee_ds_detail, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}


		
		return convertView;
	}
	
	
	public class ViewHolder{
		
//		TextView content_txt;
//		TextView date_txt;
		
		public ViewHolder(View view){
			
//			content_txt = (TextView) view.findViewById(R.id.content_txt);
//			date_txt = (TextView) view.findViewById(R.id.date_txt);
			
		}
	}

}
