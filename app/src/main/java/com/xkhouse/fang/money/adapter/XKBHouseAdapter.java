package com.xkhouse.fang.money.adapter;

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

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.lib.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
* @Description:可赚佣楼盘列表（星空宝） 
* @author wujian  
* @date 2015-9-9 上午11:42:03
 */
public class XKBHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<House> houseList;
	private LatLng startLatlng;
	private DisplayImageOptions options;
	
	public XKBHouseAdapter(Context context, ArrayList<House> houseList, LatLng startLatlng){
		this.context = context;
		this.houseList = houseList;
		this.startLatlng = startLatlng;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
		
	}
	
	public void setData(ArrayList<House> houseList, LatLng startLatlng){
		this.houseList = houseList;
		this.startLatlng = startLatlng;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return houseList.size();
	}

	@Override
	public Object getItem(int position) {
		return houseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_xkb_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		House house = houseList.get(position);
		
		holder.house_name_txt.setText(house.getProjectName());
		holder.house_cateory_txt.setText(house.getPropertyType());
		holder.want_num_txt.setText(house.getIntent());
		holder.house_money_txt.setText(house.getCommission()+"元");
		
		//计算距离
		if(startLatlng != null && !StringUtil.isEmpty(house.getLatitude()) && !StringUtil.isEmpty(house.getLongitude())){
			holder.house_distance_txt.setVisibility(View.VISIBLE);
			float distace = AMapUtils.calculateLineDistance(startLatlng, new LatLng(Double.parseDouble(house.getLatitude()),
					Double.parseDouble(house.getLongitude())));
			DecimalFormat decimalFormat=new DecimalFormat(".00");
			if (distace != 0 ) {
				if(distace > 1000){
					holder.house_distance_txt.setText(decimalFormat.format(distace/1000) + "km");
				}else{
					holder.house_distance_txt.setText(decimalFormat.format(distace) + "m");
				}
				
			}else {
				holder.house_distance_txt.setText("0km");
			}
			
		}else{
			holder.house_distance_txt.setVisibility(View.GONE);
		}
		
		holder.house_area_txt.setText(house.getAreaName());

        if(StringUtil.isEmpty(house.getEndTimeStr().trim())){
            holder.house_discount_txt.setVisibility(View.GONE);
        }else{
            holder.house_discount_txt.setText(house.getEndTimeStr());
            holder.house_discount_txt.setVisibility(View.VISIBLE);
        }

		
		ImageLoader.getInstance().displayImage(house.getEffectPhoto(), holder.house_icon_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  houseList.get(position).getProjectId());
				bundle.putString("projectName", houseList.get(position).getProjectName());
				bundle.putBoolean("isXKB", true);
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		
		ImageView house_icon_iv;
		TextView house_name_txt;
		TextView house_cateory_txt;		//住宅，写字楼
		TextView want_num_txt;			//意向人数
		TextView house_money_txt;		//可赚佣金
		TextView house_distance_txt;
		TextView house_area_txt;
		TextView house_discount_txt;
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_cateory_txt = (TextView) view.findViewById(R.id.house_cateory_txt);
			want_num_txt = (TextView) view.findViewById(R.id.want_num_txt);
			house_money_txt = (TextView) view.findViewById(R.id.house_money_txt);
			house_distance_txt = (TextView) view.findViewById(R.id.house_distance_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			house_discount_txt = (TextView) view.findViewById(R.id.house_discount_txt);
			
		}
	}

}
