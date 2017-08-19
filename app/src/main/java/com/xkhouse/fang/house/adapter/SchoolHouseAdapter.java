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

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.SchoolHouseDetailActivity;
import com.xkhouse.fang.house.entity.SchoolHouse;
import com.xkhouse.lib.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
* @Description: 学区列表页
* @author wujian  
* @date 2016-6-22
 */
public class SchoolHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<SchoolHouse> schoolHouseList;
    private LatLng startLatlng;
	private DisplayImageOptions options;


	public SchoolHouseAdapter(Context context, ArrayList<SchoolHouse> schoolHouseList, LatLng startLatlng){
		this.context = context;
		this.schoolHouseList = schoolHouseList;
        this.startLatlng = startLatlng;

		options = new DisplayImageOptions.Builder()
	       .showImageOnLoading(R.drawable.nopic)   // 加载的图片
	       .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
	       .showImageForEmptyUri(R.drawable.nopic)
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
	}
	
	public void setData(ArrayList<SchoolHouse> schoolHouseList, LatLng startLatlng) {
		this.schoolHouseList = schoolHouseList;
        this.startLatlng = startLatlng;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return schoolHouseList.size();
	}

	@Override
	public Object getItem(int position) {
		return schoolHouseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_school_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

        SchoolHouse schoolHouse = schoolHouseList.get(position);
		
		ImageLoader.getInstance().displayImage(schoolHouse.getPhotourl(), holder.school_icon_iv, options);
		holder.school_name_txt.setText(schoolHouse.getName());
        if(!StringUtil.isEmpty(schoolHouse.getLevel().trim())){
            holder.school_important_txt.setVisibility(View.VISIBLE);
            if("小学".equals(schoolHouse.getLevel().trim())){
                holder.school_level_txt.setText("小");
            }else if("初中".equals(schoolHouse.getLevel().trim())){
                holder.school_level_txt.setText("中");
            }
        }else{
            holder.school_important_txt.setVisibility(View.GONE);
        }


        if(!StringUtil.isEmpty(schoolHouse.getIsImportant())){
            holder.school_important_txt.setVisibility(View.VISIBLE);
        }else{
            holder.school_important_txt.setVisibility(View.GONE);
        }

		holder.school_address_txt.setText(schoolHouse.getAreaName().trim());

        //计算距离
        if(startLatlng != null &&
                !StringUtil.isEmpty(schoolHouse.getLatitude()) &&
                !StringUtil.isEmpty(schoolHouse.getLongitude()) &&
                !"0".equals(schoolHouse.getLatitude()) &&
                !"0".equals(schoolHouse.getLongitude())
                ){
            holder.school_distance_txt.setVisibility(View.VISIBLE);

            float distace = AMapUtils.calculateLineDistance(startLatlng, new LatLng(Double.parseDouble(schoolHouse.getLatitude()),
                    Double.parseDouble(schoolHouse.getLongitude())));
            DecimalFormat decimalFormat=new DecimalFormat(".00");
            if (distace != 0 ) {
                if(distace > 1000){
                    holder.school_distance_txt.setText(decimalFormat.format(distace/1000) + "km");
                }else{
                    holder.school_distance_txt.setText(decimalFormat.format(distace) + "m");
                }

            }else {
                holder.school_distance_txt.setText("0km");
            }
        }else{
            holder.school_distance_txt.setVisibility(View.GONE);
        }


        if (!StringUtil.isEmpty(schoolHouse.getNum())){
            holder.school_num_txt.setText("划片小区  "+schoolHouse.getNum() + "个");
        }else{
            holder.school_num_txt.setText("划片小区0个");
        }
        if (!StringUtil.isEmpty(schoolHouse.getOldNum())){
            holder.old_count_txt.setText("在售房源" + schoolHouse.getOldNum() + "套");
        }else{
            holder.old_count_txt.setText("在售房源0套");
        }



		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SchoolHouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId", schoolHouseList.get(position).getId());
				bundle.putString("projectName", schoolHouseList.get(position).getName());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView school_icon_iv;
		TextView school_name_txt;
		
		TextView school_level_txt;
		TextView school_important_txt;
		TextView school_address_txt;
		TextView school_distance_txt;
		TextView school_num_txt;
		TextView old_count_txt;

		
		public ViewHolder(View view){
            school_icon_iv = (ImageView) view.findViewById(R.id.school_icon_iv);
            school_name_txt = (TextView) view.findViewById(R.id.school_name_txt);
            school_level_txt = (TextView) view.findViewById(R.id.school_level_txt);
            school_important_txt = (TextView) view.findViewById(R.id.school_important_txt);
            school_address_txt = (TextView) view.findViewById(R.id.school_address_txt);
            school_distance_txt = (TextView) view.findViewById(R.id.school_distance_txt);
			school_num_txt = (TextView) view.findViewById(R.id.school_num_txt);
            old_count_txt = (TextView) view.findViewById(R.id.old_count_txt);

		}
	}

}
