package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.OldHouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
* @Description: 地图找房  租房列表 
* @author wujian  
* @date 2015-9-16 下午4:25:36
 */
public class MapRentHouseAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<OldHouse> oldHouseList;
	
	public MapRentHouseAdapter(Context context, ArrayList<OldHouse> oldHouseList){
		this.context = context;
		this.oldHouseList = oldHouseList;
	}
	
	@Override
	public int getCount() {
		return oldHouseList.size();
	}

	@Override
	public Object getItem(int position) {
		return oldHouseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_map_rent_house_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		
		return convertView;
	}
	
	public class ViewHolder{
		ImageView house_icon_iv;
		TextView house_name_txt;
		
		TextView house_space_txt;	//X室X厅
		TextView sale_type_txt;		//中介，个人
		TextView house_price_txt;	//租金
		
		public ViewHolder(View view){
			house_icon_iv = (ImageView) view.findViewById(R.id.house_icon_iv);
			house_name_txt = (TextView) view.findViewById(R.id.house_name_txt);
			house_space_txt = (TextView) view.findViewById(R.id.house_space_txt);
			sale_type_txt = (TextView) view.findViewById(R.id.sale_type_txt);
			house_price_txt = (TextView) view.findViewById(R.id.house_price_txt);
		}
	}

}
