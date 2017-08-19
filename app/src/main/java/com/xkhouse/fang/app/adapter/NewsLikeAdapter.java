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
		holder.news_title_txt.setText(news.getTitle());

		long time = 0;
		try {
			time = Long.parseLong(news.getCreateTime());
		} catch (Exception e) {
		}
		holder.news_date_txt.setText(DateUtil.getDateFromLong(time, "yyyy-MM-dd HH:mm:ss"));
		
		ImageLoader.getInstance().displayImage(news.getPhotoUrl(), holder.news_icon_iv, options);

        if(news.getAtlas() != null ){
            if("2".equals(news.getAtlas().getType())){
                holder.tuji_content_lay.setVisibility(View.GONE);
                holder.video_content_lay.setVisibility(View.VISIBLE);
                holder.video_title_txt.setText(news.getAtlas().getTitle());
                ImageLoader.getInstance().displayImage(news.getAtlas().getaPhotoUrl(), holder.video_left_iv, options);

            }else if("1".equals(news.getAtlas().getType())){
                holder.tuji_content_lay.setVisibility(View.VISIBLE);
                holder.video_content_lay.setVisibility(View.GONE);
                holder.tuji_title_txt.setText(news.getAtlas().getTitle());
                ImageLoader.getInstance().displayImage(news.getAtlas().getaPhotoUrl(), holder.tujia_left_iv, options);
                ImageLoader.getInstance().displayImage(news.getAtlas().getbPhotoUrl(), holder.tujia_middle_iv, options);
                ImageLoader.getInstance().displayImage(news.getAtlas().getcPhotoUrl(), holder.tujia_right_iv, options);
            }

        }else{
            holder.tuji_content_lay.setVisibility(View.GONE);
            holder.video_content_lay.setVisibility(View.GONE);
        }

		convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, NewsDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", modelApp.getNewsIndex() + newsList.get(position).getNewsId() + ".html");
                data.putString("newsId", newsList.get(position).getNewsId());
                intent.putExtras(data);
                context.startActivity(intent);
			}
		});

        holder.tuji_content_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", modelApp.getNewsPhoto() + newsList.get(position).getAtlas().getAid() + ".html");
                data.putString("newsId", newsList.get(position).getAtlas().getAid());
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });

        holder.video_content_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", modelApp.getNewsVideo() + newsList.get(position).getAtlas().getAid() + ".html");
                data.putString("newsId", newsList.get(position).getAtlas().getAid());
                intent.putExtras(data);
                context.startActivity(intent);
            }
        });

		return convertView;
	}
	
	public class ViewHolder{
		ImageView news_icon_iv;
		TextView news_title_txt;
		TextView news_date_txt;

        LinearLayout tuji_content_lay;
        TextView tuji_title_txt;
        ImageView tujia_left_iv;
        ImageView tujia_middle_iv;
        ImageView tujia_right_iv;

        LinearLayout video_content_lay;
        TextView video_title_txt;
        ImageView video_left_iv;

		public ViewHolder(View view){
			news_icon_iv = (ImageView) view.findViewById(R.id.news_icon_iv);
			news_title_txt = (TextView) view.findViewById(R.id.news_title_txt);
			news_date_txt = (TextView) view.findViewById(R.id.news_date_txt);

            tuji_content_lay = (LinearLayout) view.findViewById(R.id.tuji_content_lay);
            tuji_title_txt = (TextView) view.findViewById(R.id.tuji_title_txt);
            tujia_left_iv = (ImageView) view.findViewById(R.id.tujia_left_iv);
            tujia_middle_iv = (ImageView) view.findViewById(R.id.tujia_middle_iv);
            tujia_right_iv = (ImageView) view.findViewById(R.id.tujia_right_iv);

            video_content_lay = (LinearLayout) view.findViewById(R.id.video_content_lay);
            video_title_txt = (TextView) view.findViewById(R.id.video_title_txt);
            video_left_iv = (ImageView) view.findViewById(R.id.video_left_iv);
        }


	}

}
