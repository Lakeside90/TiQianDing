package com.xkhouse.fang.booked.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.booked.activity.StoreImageDeatilActivity;
import com.xkhouse.fang.booked.entity.StoreAlbum;
import com.xkhouse.fang.user.entity.XKRecommend;

import java.util.ArrayList;

/**
 * 商家相册
 */
public class StoreImageAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<StoreAlbum> albumList;

	private DisplayImageOptions options;

	public StoreImageAdapter(Context context, ArrayList<StoreAlbum> albumList){
		this.context = context;
		this.albumList = albumList;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nopic)   // 加载的图片
				.showImageOnFail(R.drawable.nopic) // 错误的时候的图片
				.showImageForEmptyUri(R.drawable.nopic)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<StoreAlbum> albumList){
		this.albumList = albumList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_store_image_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();  
		}

		final StoreAlbum storeAlbum = albumList.get(position);

		ImageLoader.getInstance().displayImage(storeAlbum.getImg(), holder.icon_iv, options);

		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, StoreImageDeatilActivity.class);
				intent.putExtra("albumId", storeAlbum.getId());
				context.startActivity(intent);
			}
		});


		return convertView;
	}
	


	public class ViewHolder{

		ImageView icon_iv;

		public ViewHolder(View view){

			icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
		}
	}



}
