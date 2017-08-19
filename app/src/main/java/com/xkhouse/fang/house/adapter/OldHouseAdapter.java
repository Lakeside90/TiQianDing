package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.entity.OldHouse;

/**
* @Description: 二手房（租房）列表页 
* @author wujian  
* @date 2015-9-8 上午10:49:03
 */
public class OldHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OldHouse> oldHouseList;
	private int houseType;
	
	private DisplayImageOptions options;
	
	
	public OldHouseAdapter(Context context, ArrayList<OldHouse> oldHouseList, int houseType){
		this.context = context;
		this.oldHouseList = oldHouseList;
		this.houseType = houseType;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<OldHouse> oldHouseList) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_old_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		OldHouse house = oldHouseList.get(position);
		
		ImageLoader.getInstance().displayImage(house.getHousephoto(), holder.house_icon_iv, options);
		holder.house_name_txt.setText(house.getTitle());
		holder.sale_type_txt.setText(house.getMembertype());
		holder.house_price_txt.setText(house.getPrice());
		holder.house_area_txt.setText(house.getAreaName() + "    " + house.getPorjectName());
		holder.house_space_txt.setText(house.getRoomtype() + "    " + house.getHouseArea());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  oldHouseList.get(position).getPorjectId());
				bundle.putString("projectName", oldHouseList.get(position).getPorjectName());
				bundle.putInt("houseType", houseType);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		
		TextView sale_type_txt;   //个人，中介
		TextView house_price_txt;   
		TextView house_area_txt;   //地理位置
		TextView house_space_txt;   //几室几厅   面积
//		TextView house_distance_txt;   //距离当前位置
		
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			sale_type_txt = (TextView) view.findViewById(R.id.sale_type_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			house_space_txt = (TextView) view.findViewById(R.id.house_space_txt);
//			house_distance_txt = (TextView) view.findViewById(R.id.house_distance_txt);
			
		}
	}

}
