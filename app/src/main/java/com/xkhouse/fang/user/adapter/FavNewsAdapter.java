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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.user.entity.FavNews;
import com.xkhouse.fang.user.view.FavoriteListItemView.ItemLongClickListener;

/**
* @Description: 我的收藏--资讯
* @author wujian  
* @date 2015-11-3 下午12:08:38
 */
public class FavNewsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<FavNews> newsList;
	private DisplayImageOptions options;
	private ItemLongClickListener clickListener;
	private ModelApplication modelApp;
	
	public FavNewsAdapter(Context context, ArrayList<FavNews> newsList,
			ItemLongClickListener clickListener){
		this.context = context;
		this.newsList = newsList;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		this.clickListener = clickListener;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<FavNews> newsList) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_fav_news_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		FavNews news = newsList.get(position); 
		holder.news_title_txt.setText(news.getTitle());
		holder.news_date_txt.setText(news.getCreateTime());
		ImageLoader.getInstance().displayImage(news.getPhotoUrl(), holder.news_icon_iv, options);
		
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewsDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url",  modelApp.getWebHost()+"/" + newsList.get(position).getUrl()+ 
						newsList.get(position).getNewsId()+".html");
				data.putString("newsId", newsList.get(position).getNewsId());
				intent.putExtras(data);
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
		ImageView news_icon_iv;
		TextView news_title_txt;
		TextView news_date_txt;
		
		public ViewHolder(View view){
			news_icon_iv = (ImageView) view.findViewById(R.id.news_icon_iv);
			news_title_txt = (TextView) view.findViewById(R.id.news_title_txt);
			news_date_txt = (TextView) view.findViewById(R.id.news_date_txt);
		}
	}

}
