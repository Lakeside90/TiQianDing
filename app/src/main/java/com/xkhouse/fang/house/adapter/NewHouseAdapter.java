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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.lib.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/** 
 * @Description: 新房列表页 
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class NewHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<House> newHouseList;
    private ArrayList<XKAd> adList;
    private int adIndex;        //在第几条数据后面插广告
	private LatLng startLatlng;
	private DisplayImageOptions options;
	
	public NewHouseAdapter(Context context, ArrayList<House> newHouseList,
                           ArrayList<XKAd> adList, int adIndex, LatLng startLatlng){
		this.context = context;
		this.newHouseList = newHouseList;
        this.adList = adList;
        this.adIndex = adIndex;
		this.startLatlng = startLatlng;
		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}

	public void setData(ArrayList<House> newHouseList, ArrayList<XKAd> adList, int adIndex, LatLng startLatlng){
		this.newHouseList = newHouseList;
        this.adList = adList;
        this.adIndex = adIndex;
		this.startLatlng = startLatlng;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return newHouseList.size();
	}

	@Override
	public Object getItem(int position) {
		return newHouseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_new_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		House house = newHouseList.get(position);
		
		holder.house_name_txt.setText(house.getProjectName());
		ImageLoader.getInstance().displayImage(house.getEffectPhoto(), holder.house_icon_iv, options);

        if("1".equals(house.getHaveVideo())){
            holder.video_icon.setVisibility(View.VISIBLE);
        }else{
            holder.video_icon.setVisibility(View.GONE);
        }

        if("1".equals(house.getIsgroup())){
            holder.kan_txt.setVisibility(View.VISIBLE);
        }else{
            holder.kan_txt.setVisibility(View.GONE);
        }

		if(StringUtil.isEmpty(house.getSaleState())){
			holder.house_status_txt.setVisibility(View.GONE);
		}else {
			holder.house_status_txt.setText(house.getSaleState());
			holder.house_status_txt.setVisibility(View.VISIBLE);
		}
		
		if("1".equals(house.getHaveCommission())){
			holder.house_yong_iv.setVisibility(View.VISIBLE);
		}else{
			holder.house_yong_iv.setVisibility(View.GONE);
		}
		
		holder.house_price_txt.setText(house.getAveragePrice());
		holder.house_area_txt.setText(house.getAreaName());
		
		holder.house_type_txt.setText("在售户型：" + house.getHouseType());
        if("1".equals(house.getIsgroup()) && !StringUtil.isEmpty(house.getGroupinfo())){
            holder.house_feature_txt.setText(house.getGroupinfo());
        }else{
            if(StringUtil.isEmpty(house.getProjectFeature())){
                holder.house_feature_txt.setText("暂无优惠");
            }else{
                StringBuilder sb = new StringBuilder();
                String[] feature = house.getProjectFeature().split("#");
                if(feature != null){
                    for(int i = 0; i < feature.length; i++){
                        if(i < 3){
                            sb.append(feature[i] + "    ");
                        }
                    }
                }
                holder.house_feature_txt.setText(sb.toString());  //最多显示三个特色，好蛋疼
            }
        }


		if(StringUtil.isEmpty(house.getJuniorSchool()) && StringUtil.isEmpty(house.getMiddleSchool())){
			holder.house_school_lay.setVisibility(View.GONE);
		}else {
			holder.house_school_lay.setVisibility(View.VISIBLE);
			if(!StringUtil.isEmpty(house.getJuniorSchool())){
				holder.house_school_txt.setText(house.getJuniorSchool());
			}else{
				holder.house_school_txt.setText(house.getMiddleSchool());
			}
		}
		
		//计算距离
		if(startLatlng != null && !StringUtil.isEmpty(house.getLatitude()) && !StringUtil.isEmpty(house.getLongitude())){
			holder.house_distance_txt.setVisibility(View.VISIBLE);
			
			float distace = AMapUtils.calculateLineDistance(startLatlng, new LatLng(Double.parseDouble(house.getLatitude()),
					Double.parseDouble(house.getLongitude())));
			DecimalFormat decimalFormat=new DecimalFormat(".00");
			if (distace != 0 ) {
				if(distace > 1000){
					holder.house_distance_txt.setText(decimalFormat.format(distace/1000) + "km");
				}else{
					holder.house_distance_txt.setText(decimalFormat.format(distace) + "m");
				}
				
			}else {
				holder.house_distance_txt.setText("0km");
			}
		}else{
			holder.house_distance_txt.setVisibility(View.GONE);
		}

        if (position+1 == adIndex && adList != null){
            holder.ad_grid.setVisibility(View.VISIBLE);
            holder.ad_grid.setAdapter(new HouseADListAdapter(context, adList));
        }else{
            holder.ad_grid.setVisibility(View.GONE);
        }


		//跳转到详情页
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  newHouseList.get(position).getProjectId());
				bundle.putString("projectName", newHouseList.get(position).getProjectName());
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		ImageView video_icon;
		TextView house_name_txt;
		TextView kan_txt;
		ImageView house_yong_iv;
		TextView house_status_txt;
		TextView house_type_txt;
		TextView house_price_txt;
		TextView house_distance_txt;
		TextView house_area_txt;
		TextView house_feature_txt;
		TextView house_school_txt;
		LinearLayout house_school_lay;
        ScrollGridView ad_grid;

		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
            video_icon = (ImageView) view.findViewById(R.id.video_icon);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
            kan_txt = (TextView) view.findViewById(R.id.kan_txt);
            house_yong_iv = (ImageView) view.findViewById(R.id.house_yong_iv);
			house_status_txt = (TextView) view.findViewById(R.id.house_status_txt);
            house_type_txt = (TextView) view.findViewById(R.id.house_type_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
			house_distance_txt = (TextView) view.findViewById(R.id.house_distance_txt);
			house_area_txt = (TextView) view.findViewById(R.id.house_area_txt);
			house_feature_txt = (TextView) view.findViewById(R.id.house_feature_txt);
			house_school_txt = (TextView) view.findViewById(R.id.house_school_txt);
			house_school_lay = (LinearLayout) view.findViewById(R.id.house_school_lay);
            ad_grid = (ScrollGridView) view.findViewById(R.id.ad_grid);
		}
	}

}
