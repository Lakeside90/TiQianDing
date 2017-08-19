package com.xkhouse.fang.house.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.house.entity.XKSearchResult;

/** 
 * @Description: 搜索结果 
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class SearchProjectResultAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<XKSearchResult> resultList;
	
	public SearchProjectResultAdapter(Context context, ArrayList<XKSearchResult> resultList){
		this.context = context;
		this.resultList = resultList;
	}
	
	public void setData(ArrayList<XKSearchResult> resultList){
		this.resultList = resultList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Object getItem(int position) {
		return resultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_project_result_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		XKSearchResult result = resultList.get(position);
		
		holder.result_key_txt.setText(result.getProjectName());
		
		String type = "";
		if(SearchActivity.SEARCH_NEW_HOUSE.equals(result.getType())){
			type = "-新房";
		}else if(SearchActivity.SEARCH_OLD_HOUSE.equals(result.getType())){
			type = "-二手房";
		}else if(SearchActivity.SEARCH_RENT_HOUSE.equals(result.getType())){
			type = "-租房";
		}
		
		holder.result_type_txt.setText(type);
		
		return convertView;
	}
	
	public class ViewHolder{
		
		TextView result_key_txt;
		TextView result_type_txt;
		
		public ViewHolder(View view){
			
			result_key_txt = (TextView) view.findViewById(R.id.result_key_txt);
			result_type_txt = (TextView) view.findViewById(R.id.result_type_txt);
		}
	}

}
