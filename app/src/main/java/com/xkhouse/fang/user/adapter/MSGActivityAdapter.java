package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.app.Activity;
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
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.discount.activity.DiscountDetailActivity;
import com.xkhouse.fang.user.entity.MSGActivity;

/**
* @Description: 最新活动
* @author wujian  
* @date 2015-11-4 下午8:36:06
 */
public class MSGActivityAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MSGActivity> activityList;
	private DisplayImageOptions options;
	private ModelApplication modelApp;
	
	public MSGActivityAdapter(Context context, ArrayList<MSGActivity> activityList){
		this.context = context;
		this.activityList = activityList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<MSGActivity> activityList){
		this.activityList = activityList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return activityList.size();
	}

	@Override
	public Object getItem(int position) {
		return activityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_msg_activity_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
	
		MSGActivity activity = activityList.get(position);
		
		holder.title_txt.setText(activity.getTitle());
		holder.date_txt.setText(activity.getDate());
		holder.end_time_txt.setText(activity.getEndTime()+"");
		ImageLoader.getInstance().displayImage(activity.getPhotoUrl(), holder.photo_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DiscountDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url", modelApp.getHuoDong() + activityList.get(position).getId());
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});
		
		try {
			if(activity.getEndTime() < activity.getNowTime()){
				holder.end_time_txt.setText("活动已结束！");
				
			}else if(activity.getStartTime() > activity.getNowTime()) {
				holder.end_time_txt.setText("活动未开始！");
			}else {
				int time = (int) (activity.getEndTime() - activity.getNowTime());
				int day = time/(24*60*60*1000);
				int hour = (time - (day*24*60*60*1000))/(60*60*1000);
				int min = (time - (day*24*60*60*1000) - (hour*60*60*1000))/(60*1000);
				int second = (time - (day*24*60*60*1000) - (hour*60*60*1000) - (min*60*1000))/(1000);
				
				holder.end_time_txt.setText("距离结束：  " + day+"天" + hour+"时" + min+"分" + second+"秒");
			}
		} catch (Exception e) {
			holder.end_time_txt.setText("距离结束：  " + "0天" + "0时" + "0分" + "0秒");
		}
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView photo_iv;
		TextView title_txt;
		TextView date_txt;
		TextView end_time_txt;
		
		public ViewHolder(View view){
			
			photo_iv = (ImageView) view.findViewById(R.id.photo_iv);
			title_txt = (TextView) view.findViewById(R.id.title_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			end_time_txt = (TextView) view.findViewById(R.id.end_time_txt);
			
		}
	}

}
