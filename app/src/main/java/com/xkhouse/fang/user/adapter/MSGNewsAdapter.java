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
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.user.entity.MSGNews;

/**
* @Description: 每日要闻
* @author wujian  
* @date 2015-11-4 下午8:36:06
 */
public class MSGNewsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<MSGNews> newsList;
	private DisplayImageOptions options;
	private ModelApplication modelApp;
	
	public MSGNewsAdapter(Context context, ArrayList<MSGNews> newsList){
		this.context = context;
		this.newsList = newsList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<MSGNews> newsList){
		this.newsList = newsList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_msg_news_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}
		
	
		MSGNews news = newsList.get(position);
		holder.title_txt.setText(news.getTitle());
		holder.date_txt.setText(news.getDate());
		holder.content_txt.setText(news.getContent());
		ImageLoader.getInstance().displayImage(news.getPhotoUrl(), holder.photo_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewsDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url", modelApp.getNewsIndex() + newsList.get(position).getId() + ".html");
				data.putString("newsId", newsList.get(position).getId());
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	
	public class ViewHolder{
		
		ImageView photo_iv;
		TextView title_txt;
		TextView date_txt;
		TextView content_txt;
		
		public ViewHolder(View view){
			
			photo_iv = (ImageView) view.findViewById(R.id.photo_iv);
			title_txt = (TextView) view.findViewById(R.id.title_txt);
			date_txt = (TextView) view.findViewById(R.id.date_txt);
			content_txt = (TextView) view.findViewById(R.id.content_txt);
			
		}
	}

}
