package com.xkhouse.fang.app.adapter;

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
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.app.entity.XKAd;

import java.util.List;

/** 
 * @Description: 首页专题资讯 
 * @author wujian  
 * @date 2015-8-27 下午4:12:56  
 */
public class HomeNewsGridAdapter extends BaseAdapter {

	private Context context;
	private List<XKAd> themes;
	
	private DisplayImageOptions options;
	
	public HomeNewsGridAdapter(List<XKAd> themes, Context context){
		this.themes = themes;
		this.context = context;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(List<XKAd> themes) {
		this.themes = themes;
	}
	
	@Override
	public int getCount() {
		return themes.size();
	}

	@Override
	public Object getItem(int position) {
		return themes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_news_theme_grid, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKAd theme = themes.get(position);
		
		holder.news_title_txt.setText(theme.getTitle());
		holder.news_content_txt.setText(theme.getRemark());
		ImageLoader.getInstance().displayImage(theme.getPhotoUrl(), holder.news_icon_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewsDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url", themes.get(position).getNewsUrl());
//                data.putString("isAd", NewsDetailActivity.AD_FLAG);
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView news_icon_iv;
		TextView news_title_txt;
		TextView news_content_txt;
		
		public ViewHolder(View view){
			news_icon_iv = (ImageView) view.findViewById(R.id.news_icon_iv);
			news_title_txt = (TextView) view.findViewById(R.id.news_title_txt);
			news_content_txt = (TextView) view.findViewById(R.id.news_content_txt);
		}
	}

	
	
	
}
