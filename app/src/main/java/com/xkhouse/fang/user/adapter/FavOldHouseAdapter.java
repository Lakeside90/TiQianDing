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

/**
* @Description: 我的收藏--二手房 
* @author wujian  
* @date 2015-11-3 上午11:41:40
 */
public class FavOldHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<FavHouse> oldHouseList;
	private DisplayImageOptions options;
	private ItemLongClickListener clickListener;
	
	public FavOldHouseAdapter(Context context, ArrayList<FavHouse> oldHouseList,
			ItemLongClickListener clickListener){
		
		this.context = context;
		this.oldHouseList = oldHouseList;
		this.clickListener = clickListener;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<FavHouse> oldHouseList){
		this.oldHouseList = oldHouseList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return oldHouseList.size();
	}

	@Override
	public Object getItem(int position) {
		return oldHouseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fav_old_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		FavHouse house = oldHouseList.get(position);
		
		ImageLoader.getInstance().displayImage(house.getEffectPhoto(), holder.house_icon_iv, options);
		holder.house_price_txt.setText(house.getPrice());
		holder.house_title_txt.setText(house.getSaletitle());
		holder.house_area_txt.setText(house.getAreaName());
		holder.house_name_txt.setText(house.getProjectName());
		
		holder.room_type_txt.setText(house.getRoomtype());
		holder.house_space_txt.setText(house.getHouseArea());
		
		holder.favorite_date_txt.setText(house.getCreateTime());
		
		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  oldHouseList.get(position).getProjectId());
				bundle.putString("projectName", oldHouseList.get(position).getProjectName());
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_OLD);
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
		TextView house_title_txt;
		
		TextView house_price_txt;
		
		TextView house_area_txt;
		TextView house_name_txt;
		
		TextView room_type_txt;
		TextView house_space_txt;
		
		TextView favorite_date_txt;
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_title_txt = (TextView) view.findViewById(R.id.house_title_txt);
			
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			
			room_type_txt = (TextView) view.findViewById(R.id.room_type_txt);
			house_space_txt = (TextView) view.findViewById(R.id.house_space_txt);
			
			favorite_date_txt = (TextView) view.findViewById(R.id.favorite_date_txt);
		}
	}

}
