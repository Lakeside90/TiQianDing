package com.xkhouse.fang.user.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.user.entity.MSGActivity;
import com.xkhouse.fang.user.entity.MSGNews;
import com.xkhouse.fang.user.entity.MSGSystem;

/**
* @Description: 我的消息
* @author wujian  
* @date 2015-11-4 下午8:36:06
 */
public class MSGSystemAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MSGSystem> systemList;
	private DisplayImageOptions options;
	
	public MSGSystemAdapter(Context context, ArrayList<MSGSystem> systemList){
		this.context = context;
		this.systemList = systemList;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<MSGSystem> systemList){
		this.systemList = systemList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return systemList.size();
	}

	@Override
	public Object getItem(int position) {
		return systemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_msg_system_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}


		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		TextView content_txt;
		TextView date_txt;
		
		public ViewHolder(View view){
			
			content_txt = (TextView) view.findViewById(R.id.content_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			
		}
	}

}
