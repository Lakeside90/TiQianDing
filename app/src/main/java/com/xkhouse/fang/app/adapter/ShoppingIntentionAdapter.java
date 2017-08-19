package com.xkhouse.fang.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.entity.CommonType;


/**
* @Description: 家居--预约陪购，购买意向
* @author wujian  
* @date 2015-10-20 下午3:22:20
 */
public class ShoppingIntentionAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<CommonType> typeList;
	
	public ShoppingIntentionAdapter(ArrayList<CommonType> typeList, Context context){
		this.typeList = typeList;
		this.context = context;
	}
	
	public void setData(ArrayList<CommonType> typeList){
		this.typeList = typeList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return typeList.size();
	}

	@Override
	public Object getItem(int position) {
		return typeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_jj_shopping_intention, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		CommonType type = typeList.get(position);
		
		holder.intention_txt.setText(type.getName());
		
		if(type.isSelected()){
			holder.intention_check_iv.setImageResource(R.drawable.checkbox_checked);
		}else {
			holder.intention_check_iv.setImageResource(R.drawable.checkbox_normal);
		}
		
		return convertView;
	}

	
	
	public class ViewHolder{
		ImageView intention_check_iv;
		TextView intention_txt;
		
		public ViewHolder(View view){
			intention_check_iv = (ImageView) view.findViewById(R.id.intention_check_iv);
			intention_txt = (TextView) view.findViewById(R.id.intention_txt);
		}
	}
	
}
