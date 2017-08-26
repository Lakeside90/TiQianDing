package com.xkhouse.fang.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.app.entity.News;

import java.util.ArrayList;

/**
 * 推荐预定列表
 */
public class BookedInfoAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<BookedInfo> bookedInfos;
	
	private DisplayImageOptions options;
	private ModelApplication modelApp;

    public BookedInfoAdapter(Context context) {
        this.context = context;
        modelApp = (ModelApplication) ((Activity) context).getApplication();
    }

    public BookedInfoAdapter(Context context, ArrayList<BookedInfo> bookedInfos){
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		this.bookedInfos = bookedInfos;
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<BookedInfo> newsList) {
		this.bookedInfos = newsList;
	}


	
	@Override
	public int getCount() {
//		return bookedInfos.size();
        return 10;
	}

	@Override
	public Object getItem(int position) {
//		return bookedInfos.get(position);
        return 0;
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
		
//		News news = bookedInfos.get(position);



		
//		ImageLoader.getInstance().displayImage(news.getPhotoUrl(), holder.news_icon_iv, options);



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
