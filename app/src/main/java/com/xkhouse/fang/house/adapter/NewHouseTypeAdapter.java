package com.xkhouse.fang.house.adapter;

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
import com.xkhouse.fang.house.activity.RoomDetailActivity;
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.lib.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/** 
 * @Description: 新房户型列表页
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class NewHouseTypeAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRoom> newHouseTypeList;
	private LatLng startLatlng;
	private DisplayImageOptions options;

	public NewHouseTypeAdapter(Context context, ArrayList<XKRoom> newHouseTypeList, LatLng startLatlng){
		this.context = context;
		this.newHouseTypeList = newHouseTypeList;
		this.startLatlng = startLatlng;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<XKRoom> newHouseTypeList, LatLng startLatlng){
		this.newHouseTypeList = newHouseTypeList;
		this.startLatlng = startLatlng;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return newHouseTypeList.size();
	}

	@Override
	public Object getItem(int position) {
		return newHouseTypeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_new_house_type_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        XKRoom house = newHouseTypeList.get(position);
		

		ImageLoader.getInstance().displayImage(house.getPhotoPath(), holder.house_icon_iv, options);
        holder.house_name_txt.setText(house.getProjectName());
        holder.house_area_txt.setText(house.getAreaName());
        holder.total_price_txt.setText(house.getTotalPrice());
        holder.house_type_txt.setText(house.getHouseTitle());
        if (StringUtil.isEmpty(house.getTypeArea())){
            holder.type_area_txt.setVisibility(View.INVISIBLE);
        }else{
            holder.type_area_txt.setVisibility(View.VISIBLE);
            holder.type_area_txt.setText(house.getTypeArea()+"㎡");
        }
        holder.house_price_txt.setText("参考价："+house.getAveragePrice());
		holder.sale_status_txt.setText(house.getSaleState());

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
		
		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, RoomDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId", newHouseTypeList.get(position).getPid());
				bundle.putString("roomId", newHouseTypeList.get(position).getHousetypeId());
				bundle.putInt("roomType", RoomDetailActivity.ROOM_TYPE_HX);
                bundle.putBoolean("isFromList", true);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		TextView house_area_txt;
		TextView house_distance_txt;
		TextView total_price_txt;
		TextView house_type_txt;
		TextView type_area_txt;
		TextView house_price_txt;
		TextView sale_status_txt;

		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
            house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
            house_distance_txt = (TextView) view.findViewById(R.id.house_distance_txt);
            total_price_txt = (TextView) view.findViewById(R.id.total_price_txt);
            house_type_txt = (TextView) view.findViewById(R.id.house_type_txt);
            type_area_txt = (TextView) view.findViewById(R.id.type_area_txt);
            house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
            sale_status_txt = (TextView) view.findViewById(R.id.sale_status_txt);
		}
	}

}
