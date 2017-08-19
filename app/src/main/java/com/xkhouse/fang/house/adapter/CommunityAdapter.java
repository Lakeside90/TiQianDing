package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.house.activity.CommunityRoomListActivity;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.house.entity.CommunityInfo;
import com.xkhouse.fang.widget.ScrollGridView;

import java.util.ArrayList;

/** 
 * @Description: 划片小区页
 * @author wujian  
 * @date 2016-8-9
 */
public class CommunityAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CommunityInfo> communityList;

	public CommunityAdapter(Context context, ArrayList<CommunityInfo> communityList){
		this.context = context;
		this.communityList = communityList;
	}

	public void setData(ArrayList<CommunityInfo> communityList){
		this.communityList = communityList;
        notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return communityList.size();
	}

	@Override
	public Object getItem(int position) {
		return communityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_community_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}


        CommunityInfo communityInfo = communityList.get(position);
        holder.community_name_txt.setText(communityInfo.getProjectName());
        holder.community_count_txt.setText(communityInfo.getCount()+"套");

		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CommunityRoomListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("communityName", communityList.get(position).getProjectName());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{

		TextView community_name_txt;
		TextView community_count_txt;

		public ViewHolder(View view){
            community_name_txt = (TextView) view.findViewById(R.id.community_name_txt);
            community_count_txt = (TextView) view.findViewById(R.id.community_count_txt);
		}
	}


}
