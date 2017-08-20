package com.xkhouse.fang.app.adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.activity.NewsDetailActivity;
import com.xkhouse.fang.app.entity.News;
import com.xkhouse.lib.utils.DateUtil;

import java.util.ArrayList;

/** 
 * @Description: 猜你喜欢 (资讯)
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class NewsLikeAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<News> newsList;
	
	private DisplayImageOptions options;
	private ModelApplication modelApp;
	
	public NewsLikeAdapter(Context context, ArrayList<News> newsList){
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		this.newsList = newsList;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<News> newsList) {
		this.newsList = newsList;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_news_like_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		News news = newsList.get(position); 



		
		ImageLoader.getInstance().displayImage(news.getPhotoUrl(), holder.news_icon_iv, options);



		convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


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
