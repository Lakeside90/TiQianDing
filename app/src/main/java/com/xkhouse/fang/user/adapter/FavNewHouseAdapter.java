package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.user.entity.FavHouse;
import com.xkhouse.fang.user.view.FavoriteListItemView.ItemLongClickListener;
import com.xkhouse.lib.utils.StringUtil;

/**
* @Description: 我的收藏--新房  
* @author wujian  
* @date 2015-11-3 上午11:42:17
 */
public class FavNewHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<FavHouse> newHouseList;
	private DisplayImageOptions options;
	private ItemLongClickListener clickListener;
	
	public FavNewHouseAdapter(Context context, ArrayList<FavHouse> newHouseList,
			ItemLongClickListener clickListener){
		
		this.context = context;
		this.newHouseList = newHouseList;
		this.clickListener = clickListener;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<FavHouse> newHouseList){
		this.newHouseList = newHouseList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return newHouseList.size();
	}

	@Override
	public Object getItem(int position) {
		return newHouseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fav_new_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		FavHouse house = newHouseList.get(position);
		
		holder.house_name_txt.setText(house.getProjectName());
		ImageLoader.getInstance().displayImage(house.getEffectPhoto(), holder.house_icon_iv, options);
		
		if(StringUtil.isEmpty(house.getSaleState())){
			holder.house_status_txt.setVisibility(View.GONE);
		}else {
			holder.house_status_txt.setText(house.getSaleState());
			holder.house_status_txt.setVisibility(View.VISIBLE);
		}
		
		String[] property = house.getPropertyType().split("#");
		if(property != null){
			if(property.length == 1){
				holder.house_property1_txt.setVisibility(View.VISIBLE);
				holder.house_property2_txt.setVisibility(View.GONE);
				holder.house_property1_txt.setText(property[0]);
			}else if(property.length > 1){
				holder.house_property1_txt.setVisibility(View.VISIBLE);
				holder.house_property2_txt.setVisibility(View.VISIBLE);
				holder.house_property1_txt.setText(property[0]);
				holder.house_property2_txt.setText(property[1]);
			}
			
		}else{
			holder.house_property1_txt.setVisibility(View.GONE);
			holder.house_property2_txt.setVisibility(View.GONE);
		}
		
		
		holder.house_price_txt.setText(house.getAveragePrice());
		holder.house_area_txt.setText(house.getAreaName());
		
		
        if(StringUtil.isEmpty(house.getProjectFeature())){
            holder.house_feature_txt.setText("暂无优惠");
        }else{
            StringBuilder sb = new StringBuilder();
            String[] feature = house.getProjectFeature().split("#");
            if(feature != null){
                for(int i = 0; i < feature.length; i++){
                    if(i < 3){
                        sb.append(feature[i] + "    ");
                    }
                }
            }
            holder.house_feature_txt.setText(sb.toString());
        }

		holder.favorite_date_txt.setText(house.getCreateTime());
		
		
		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  newHouseList.get(position).getProjectId());
				bundle.putString("projectName", newHouseList.get(position).getProjectName());
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				clickListener.onLongClick(position);
				return true;
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		TextView house_status_txt;
		TextView house_property1_txt;
		TextView house_property2_txt;
		TextView house_price_txt;
		TextView house_area_txt;
		TextView house_discount_txt;
		TextView house_feature_txt;
		TextView favorite_date_txt;
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_status_txt = (TextView) view.findViewById(R.id.house_status_txt);
			house_property1_txt = (TextView) view.findViewById(R.id.house_property1_txt);
			house_property2_txt = (TextView) view.findViewById(R.id.house_property2_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			house_discount_txt = (TextView) view.findViewById(R.id.house_discount_txt);
			house_feature_txt = (TextView) view.findViewById(R.id.house_feature_txt);
			favorite_date_txt = (TextView) view.findViewById(R.id.favorite_date_txt);
		}
	}
	
	
	
	

}
