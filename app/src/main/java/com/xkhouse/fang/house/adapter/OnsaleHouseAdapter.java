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
import com.xkhouse.fang.house.activity.RoomDetailActivity;
import com.xkhouse.fang.house.entity.XKRoom;

/**
* @Description: 在售房源（房源列表）
* @author wujian  
* @date 2015-9-11 上午11:04:21
 */
public class OnsaleHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<XKRoom> roomList;
	private String projectId;
	
	private DisplayImageOptions options;
	
	public OnsaleHouseAdapter(Context context, String projectId, ArrayList<XKRoom> roomList){
		this.context = context;
		this.roomList = roomList;
		this.projectId = projectId;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<XKRoom> roomList) {
		this.roomList = roomList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return roomList.size();
	}

	@Override
	public Object getItem(int position) {
		return roomList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_onsale_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKRoom room = roomList.get(position);
		
		ImageLoader.getInstance().displayImage(room.getPhotoPath(), holder.house_icon_iv, options);
		holder.house_num_txt.setText(room.getProjectName()+room.getBuildNo()+"-"+room.getFloor()+"楼"+"-"+room.getRoomNo());
		holder.house_type_name_txt.setText(room.getHouseTitle());   
		holder.house_price_txt.setText(room.getTotalPrice());
		holder.house_area_txt.setText(room.getTypename()+"  "+room.getBuildArea());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, RoomDetailActivity.class);
				Bundle data = new Bundle();
				data.putInt("roomType", RoomDetailActivity.ROOM_TYPE_FY);
				data.putString("projectId", projectId);
				data.putString("roomId", roomList.get(position).getChoiceId());
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});

		return convertView;
	}
	 
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_num_txt;  //楼号-楼层-房号
		
		TextView house_type_name_txt;   //户型名称
		TextView house_price_txt;   //参考总价
		TextView house_area_txt;   //X室X厅，面积
		
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_num_txt = (TextView) view.findViewById(R.id.house_num_txt);
			house_type_name_txt = (TextView) view.findViewById(R.id.house_type_name_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
		}
	}

}
