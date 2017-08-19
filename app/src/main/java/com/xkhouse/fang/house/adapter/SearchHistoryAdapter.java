package com.xkhouse.fang.house.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.house.entity.XKSearchResult;

import java.util.ArrayList;

/** 
 * @Description: 搜索页的搜索历史列表 
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class SearchHistoryAdapter extends BaseAdapter {

	private Context context;
	
	private ArrayList<XKSearchResult> searchHistoryList;
	
	public SearchHistoryAdapter(Context context, ArrayList<XKSearchResult> searchHistoryList){
		this.context = context;
		this.searchHistoryList = searchHistoryList;
	}
	
	public void setData(ArrayList<XKSearchResult> searchHistoryList){
		this.searchHistoryList = searchHistoryList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return searchHistoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return searchHistoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_history_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		XKSearchResult result = searchHistoryList.get(position);
		String type = "";
		
		if(SearchActivity.SEARCH_NEW_HOUSE.equals(result.getType())){
			type = "-新房";
			
		}else if(SearchActivity.SEARCH_OLD_HOUSE.equals(result.getType())) {
			type = "-二手房";
			
		}else if(SearchActivity.SEARCH_RENT_HOUSE.equals(result.getType())) {
			type = "-租房";
			
		}else if(SearchActivity.SEARCH_NEWS.equals(result.getType())) {
			type = "-资讯";
		}
		
		holder.search_history_txt.setText(result.getProjectName());
		holder.search_history_type_txt.setText(type);
		
		return convertView;
	}
	
	public class ViewHolder{
		
		TextView search_history_txt;
		TextView search_history_type_txt;
		
		public ViewHolder(View view){
			
			search_history_txt = (TextView) view.findViewById(R.id.search_history_txt);
			search_history_type_txt = (TextView) view.findViewById(R.id.search_history_type_txt);
		}
	}

}
