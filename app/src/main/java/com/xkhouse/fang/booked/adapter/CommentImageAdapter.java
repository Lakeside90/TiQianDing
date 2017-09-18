package com.xkhouse.fang.booked.adapter;

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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.booked.entity.CommentInfo;
import com.xkhouse.fang.widget.circle.CircleImageView;

import java.util.ArrayList;

/**
 * 评论图片--商家详情
 */
public class CommentImageAdapter extends BaseAdapter {

	private Context context;
	private String[] images;

	private DisplayImageOptions options;


	public CommentImageAdapter(Context context, String[] images){
		this.context = context;
		this.images = images;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nopic)   // 加载的图片
				.showImageOnFail(R.drawable.nopic) // 错误的时候的图片
				.showImageForEmptyUri(R.drawable.nopic)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}
	
	public void setData(String[] images){
		this.images = images;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return images[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_comment_image, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		ImageLoader.getInstance().displayImage(images[position], holder.comment_iv, options);

		return convertView;
	}


	public class ViewHolder{

		ImageView comment_iv;

		public ViewHolder(View view){
			comment_iv = (ImageView) view.findViewById(R.id.comment_iv);
		}
	}

}
