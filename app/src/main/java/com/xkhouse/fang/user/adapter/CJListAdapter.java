package com.xkhouse.fang.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.user.activity.RecommendDetailActivity;
import com.xkhouse.fang.user.entity.XKRecommend;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 抽奖--首页
 */
public class CJListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRecommend> recommendList;


	public CJListAdapter(Context context, ArrayList<XKRecommend> recommendList){
		this.context = context;
		this.recommendList = recommendList;

	}
	
	public void setData(ArrayList<XKRecommend> recommendList){
		this.recommendList = recommendList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
//		return recommendList.size();
        return 10;
	}

	@Override
	public Object getItem(int position) {
//		return recommendList.get(position);
        return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_cj_all_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		


		



		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});


		return convertView;
	}
	
	
	public class ViewHolder{
		



		public ViewHolder(View view){
			


		}
	}



}
