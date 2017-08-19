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
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.CustomHouseDetailActivity;
import com.xkhouse.fang.house.entity.CustomHouse;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
* @Description: 定制新房（需求列表） 
* @author wujian  
* @date 2015-9-17 下午2:19:20
 */
public class CustomHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<CustomHouse> customHouses;
	private boolean isUser = false;  //是否是个人定制

	public CustomHouseAdapter(Context context, ArrayList<CustomHouse> customHouses, boolean isUser){
		this.context = context;
		this.customHouses = customHouses;
		this.isUser = isUser;
	}
	
	public void setData(ArrayList<CustomHouse> customHouses, boolean isUser) {
		this.customHouses = customHouses;
        this.isUser = isUser;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return customHouses.size();
	}

	@Override
	public Object getItem(int position) {
		return customHouses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		CustomHouse customHouse = customHouses.get(position);

        if(isUser){
            holder.custom_status_iv.setVisibility(View.VISIBLE);
            holder.custom_status_txt.setVisibility(View.VISIBLE);
            holder.custom_status_txt.setText(customHouse.getStatus());
            if("定制中".equals(customHouse.getStatus().trim())) {
                holder.custom_status_iv.setImageResource(R.drawable.ico_dinzhi);
                holder.custom_status_txt.setTextColor(context.getResources().getColor(R.color.common_red_txt));
            }else if("已定制".equals(customHouse.getStatus().trim())){
                holder.custom_status_iv.setImageResource(R.drawable.ico_dinzhi_gray);
                holder.custom_status_txt.setTextColor(context.getResources().getColor(R.color.common_gray_txt));
            }
        }else{
            holder.custom_status_iv.setVisibility(View.GONE);
            holder.custom_status_txt.setVisibility(View.GONE);
        }


        if(!StringUtil.isEmpty(customHouse.getPhone()) && customHouse.getPhone().trim().length() == 11){
            holder.custom_phone_txt.setText(customHouse.getPhone().substring(0, 3) + "****" +
                    customHouse.getPhone().substring(7, customHouse.getPhone().length()));
        }

        holder.custom_time_txt.setText(customHouse.getTime());

		if(!StringUtil.isEmpty(customHouse.getAreaName())){
            holder.custom_area_txt.setText(customHouse.getAreaName());
		}else{
            holder.custom_area_txt.setText("不限");
        }
		
		if(!StringUtil.isEmpty(customHouse.getPriceRange())){
            holder.custom_price_txt.setText(customHouse.getPriceRange());
		}else{
            holder.custom_price_txt.setText("不限");
        }
		
		if(!StringUtil.isEmpty(customHouse.getSpace())){
            holder.custom_space_txt.setText(customHouse.getSpace());
		}else{
            holder.custom_space_txt.setText("不限");
        }

        if(!StringUtil.isEmpty(customHouse.getRequriement())){
            holder.requirement_txt.setText(customHouse.getRequriement());
        }else{
            holder.requirement_txt.setText("无其他需求");
        }


        if (!StringUtil.isEmpty(customHouse.getFeature())){
            ArrayList<String> featureList = new ArrayList<>();
            String[] featureStrs = customHouse.getFeature().split("#");
            if(featureStrs != null){
                for (String feature : featureStrs){
                    if (!StringUtil.isEmpty(feature)){
                        featureList.add(feature);
                    }
                }
            }
            holder.requirement_grid.setVisibility(View.VISIBLE);
            holder.requirement_grid.setAdapter(new RequirementLabelGridAdapter(featureList, context));
        }else{
            holder.requirement_grid.setVisibility(View.GONE);
        }

		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CustomHouseDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("projectId", customHouses.get(position).getCustomId());
                if("定制中".equals(customHouses.get(position).getStatus().trim())){
                    data.putBoolean("isShowNotice", true);
                }else{
                    data.putBoolean("isShowNotice", false);
                }
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{

        ImageView custom_status_iv;
		TextView custom_status_txt;
		TextView custom_phone_txt;	//手机号
		TextView custom_time_txt;	//时间
		TextView custom_area_txt;	//高新区
		TextView custom_price_txt;	//15000以上
		TextView custom_space_txt;	//120平方米
		TextView requirement_txt;	//其它需求
        ScrollGridView requirement_grid; //需求

		public ViewHolder(View view){
            custom_status_iv = (ImageView) view.findViewById(R.id.custom_status_iv);
            custom_status_txt = (TextView) view.findViewById(R.id.custom_status_txt);
            custom_phone_txt = (TextView) view.findViewById(R.id.custom_phone_txt);
            custom_time_txt = (TextView) view.findViewById(R.id.custom_time_txt);
			custom_area_txt = (TextView) view.findViewById(R.id.custom_area_txt);
			custom_price_txt = (TextView) view.findViewById(R.id.custom_price_txt);
			custom_space_txt = (TextView) view.findViewById(R.id.custom_space_txt);
			requirement_txt = (TextView) view.findViewById(R.id.requirement_txt);
            requirement_grid = (ScrollGridView) view.findViewById(R.id.requirement_grid);
		}
	}

}
