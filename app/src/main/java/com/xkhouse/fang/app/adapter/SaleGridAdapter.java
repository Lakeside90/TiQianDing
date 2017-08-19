package com.xkhouse.fang.app.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
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
import com.xkhouse.fang.app.entity.SaleHouse;
import com.xkhouse.fang.discount.activity.DiscountDetailActivity;

/** 
 * @Description: 限时抢购楼盘 
 * @author wujian  
 * @date 2015-8-27 下午4:12:56  
 */
public class SaleGridAdapter extends BaseAdapter {

	private Context context;
	private List<SaleHouse> houseList;
	private DisplayImageOptions options;
	private ModelApplication modelApp;
	
	public SaleGridAdapter(List<SaleHouse> houseList, Context context){
		this.houseList = houseList;
		this.context = context;
		modelApp = (ModelApplication) ((Activity) context).getApplication();
		
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	@Override
	public int getCount() {
		return houseList.size();
	}

	@Override
	public Object getItem(int position) {
		return houseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_sale_grid, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		SaleHouse house = houseList.get(position);
		holder.house_name_txt.setText(house.getName());
		holder.house_num_area_txt.setText(house.getNum()+ "    " + house.getArea()+"㎡");
		holder.discount_price_txt.setText(house.getDiscountPrice() + "万元");
		holder.price_txt.setText("/" + house.getPrice() + "万元");
		ImageLoader.getInstance().displayImage(house.getPhotoUrl(), holder.sale_icon_iv, options);
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, DiscountDetailActivity.class);
				Bundle data = new Bundle();
				data.putString("url", modelApp.getHuoDong()+"/"+houseList.get(position).getId()+"/" );
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView sale_icon_iv;
		TextView house_name_txt;
		TextView house_num_area_txt;
		TextView discount_price_txt;
		TextView price_txt;
		
		public ViewHolder(View view){
			sale_icon_iv = (ImageView) view.findViewById(R.id.sale_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_num_area_txt = (TextView) view.findViewById(R.id.house_num_area_txt);
			discount_price_txt = (TextView) view.findViewById(R.id.discount_price_txt);
			price_txt = (TextView) view.findViewById(R.id.price_txt);
			price_txt.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
		}
	}

	
	
	
}
