package com.xkhouse.fang.house.adapter;

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
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/** 
 * @Description: 购房能力评估结果--楼盘列表
 * @author wujian  
 * @date 2016-2-26
 */
public class BuyAbilityResultAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<House> houseList;
	private DisplayImageOptions options;

	public BuyAbilityResultAdapter(Context context, ArrayList<House> houseList){
		this.context = context;
		this.houseList = houseList;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}

	public void setData(ArrayList<House> houseList){
		this.houseList = houseList;
		notifyDataSetChanged();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_buy_ability_result_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		House house = houseList.get(position);
		
		holder.house_name_txt.setText(house.getProjectName());
		ImageLoader.getInstance().displayImage(house.getEffectPhoto(), holder.house_icon_iv, options);
		
		if(StringUtil.isEmpty(house.getSaleState())){
			holder.house_status_txt.setVisibility(View.GONE);
		}else {
			holder.house_status_txt.setText(house.getSaleState());
			holder.house_status_txt.setVisibility(View.VISIBLE);
		}

		holder.house_price_txt.setText(house.getAveragePrice());
		holder.house_area_txt.setText(house.getAreaName());
		
		holder.house_type_txt.setText("在售户型：" + house.getHouseType());
        if(StringUtil.isEmpty(house.getGroupDiscountInfo())) {
            holder.house_feature_txt.setText("暂无优惠");
        }else{
            holder.house_feature_txt.setText(house.getGroupDiscountInfo());
        }


		





		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  houseList.get(position).getProjectId());
				bundle.putString("projectName", houseList.get(position).getProjectName());
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		TextView house_status_txt;
		TextView house_type_txt;
		TextView house_price_txt;
		TextView house_area_txt;
		TextView house_feature_txt;

		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_status_txt = (TextView) view.findViewById(R.id.house_status_txt);
            house_type_txt = (TextView) view.findViewById(R.id.house_type_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			house_feature_txt = (TextView) view.findViewById(R.id.house_feature_txt);
		}
	}

}
