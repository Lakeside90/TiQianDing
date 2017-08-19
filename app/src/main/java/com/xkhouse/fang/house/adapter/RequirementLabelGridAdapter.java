package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;

import java.util.ArrayList;

/**
* @Description: 特色需求
* @author wujian  
* @date 2016-6-6 上午11:28:41
 */
public class RequirementLabelGridAdapter extends BaseAdapter {

	private Context context;
    private ArrayList<String> featureList = new ArrayList<>();

	public RequirementLabelGridAdapter(ArrayList<String> featureList, Context context){
		this.featureList = featureList;
		this.context = context;
	}

	@Override
	public int getCount() {
		return featureList.size();
	}

	@Override
	public Object getItem(int position) {
		return featureList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_requirement_grid, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name_txt.setText(featureList.get(position));

		return convertView;
	}

	public class ViewHolder{
		TextView name_txt;
		
		public ViewHolder(View view){
			name_txt = (TextView) view.findViewById(R.id.name_txt);
		}
	}
	
}
