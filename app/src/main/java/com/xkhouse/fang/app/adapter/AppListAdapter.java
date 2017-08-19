package com.xkhouse.fang.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.xkhouse.fang.app.entity.XKNavigation;


/**
* @Description: 更多App
* @author wujian  
* @date 2015-9-28 下午2:29:40
 */
public class AppListAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<XKNavigation> appList;
	private DisplayImageOptions options;
	
	public AppListAdapter(ArrayList<XKNavigation> appList, Context context){
		this.appList = appList;
		this.context = context;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
		
	}
	
	public void setData(ArrayList<XKNavigation> appList){
		this.appList = appList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return appList.size();
	}

	@Override
	public Object getItem(int position) {
		return appList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_home_grid, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKNavigation navigation = appList.get(position);
		
		holder.name_txt.setText(navigation.getTitle());
		
		ImageLoader.getInstance().displayImage(navigation.getPhotoUrl(), holder.icon_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse(appList.get(position).getLink());  
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
                context.startActivity(intent);  
			}
		});
		
		return convertView;
	}

	public class ViewHolder{
		ImageView icon_iv;
		TextView name_txt;
		
		public ViewHolder(View view){
			icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
			name_txt = (TextView) view.findViewById(R.id.name_txt);
		}
	}
	
}
