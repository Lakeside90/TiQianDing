package com.xkhouse.fang.house.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.OldHouseListActivity;
import com.xkhouse.fang.house.activity.RentHouseListActivity;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.house.entity.XKSearchResult;
import com.xkhouse.fang.house.service.SearchDbService;

import java.util.ArrayList;

/** 
 * @Description: 搜索页的热词列表 
 * @author wujian  
 * @date 2015-8-27 下午6:58:09  
 */
public class SearchHotKeysAdapter extends BaseAdapter {

	private Context context;
	private ModelApplication modelApp;
	
	private ArrayList<XKSearchResult> keyList;
	
	public SearchHotKeysAdapter(Context context, ArrayList<XKSearchResult> keyList){
		this.context = context;
		this.keyList = keyList;
		this.modelApp = (ModelApplication) ((Activity) context).getApplication();
	}
	
	@Override
	public int getCount() {
		return keyList.size();
	}

	@Override
	public Object getItem(int position) {
		return keyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search_hotkey_list, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.hot_key_txt.setText(keyList.get(position).getProjectName());
		holder.hot_key_txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dispatchSearchTask(keyList.get(position));
			}
		});
		
		
		return convertView;
	}
	
	public class ViewHolder{
		
		TextView hot_key_txt;
		
		public ViewHolder(View view){
			
			hot_key_txt = (TextView) view.findViewById(R.id.hot_key_txt);
		}
	}
	
	
	private void dispatchSearchTask(XKSearchResult result){
		
		SearchDbService dbService = new SearchDbService();
		dbService.saveSearchContent(result, "", modelApp.getSite().getSiteId());
		
		String keyword = result.getProjectName();
		Bundle data = new Bundle();
		data.putString("keyword", keyword);
		Intent intent = null;
		
		if(SearchActivity.SEARCH_NEW_HOUSE.equals(result.getType())){
			intent = new Intent(context, NewHouseListActivity.class);
			intent.putExtras(data);
			context.startActivity(intent);
			
		}else if(SearchActivity.SEARCH_OLD_HOUSE.equals(result.getType())){
			intent = new Intent(context, OldHouseListActivity.class);
			intent.putExtras(data);
			context.startActivity(intent);
			
		}else if(SearchActivity.SEARCH_RENT_HOUSE.equals(result.getType())){
			intent = new Intent(context, RentHouseListActivity.class);
			intent.putExtras(data);
			context.startActivity(intent);
			
		}else if(SearchActivity.SEARCH_NEWS.equals(result.getType())){
			
		}
		
		
	}

}
