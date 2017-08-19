package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.house.entity.XKRoom;

/**
* @Description: 地图找房  二手房列表 
* @author wujian  
* @date 2015-9-16 下午4:25:36
 */
public class MapOldHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRoom> oldHouseList;
	
	private DisplayImageOptions options;
	
	
	public MapOldHouseAdapter(Context context, ArrayList<XKRoom> oldHouseList){
		this.context = context;
		this.oldHouseList = oldHouseList;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	      .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
		
	}
	
	public void setData(ArrayList<XKRoom> oldHouseList) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_map_old_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKRoom room = oldHouseList.get(position);
		
		ImageLoader.getInstance().displayImage(room.getPhotoPath(), holder.house_icon_iv, options);
		holder.house_name_txt.setText(room.getTitle());
		holder.house_floor_txt.setText("楼层  " + room.getFloor());
		holder.house_space_txt.setText(room.getHouseTitle() + "  " + room.getTypeArea());
		holder.house_price_txt.setText(room.getTotalPrice());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  oldHouseList.get(position).getChoiceId());
				bundle.putString("projectName", oldHouseList.get(position).getProjectName());
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_OLD);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		TextView house_floor_txt;	//楼层
		TextView house_space_txt;	//X室X厅
		TextView house_price_txt;	//总价
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_floor_txt = (TextView) view.findViewById(R.id.house_floor_txt);
			house_space_txt = (TextView) view.findViewById(R.id.house_space_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
		}
	}

}
