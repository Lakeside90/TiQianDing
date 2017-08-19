package com.xkhouse.fang.house.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.activity.NewsSearchActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.OldHouseListActivity;
import com.xkhouse.fang.house.activity.RentHouseListActivity;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.house.adapter.SearchHistoryAdapter;
import com.xkhouse.fang.house.adapter.SearchHotKeysAdapter;
import com.xkhouse.fang.house.entity.XKSearchResult;
import com.xkhouse.fang.house.service.SearchDbService;
import com.xkhouse.fang.house.task.SearchHotWordsRequest;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/** 
 * @Description: 搜索历史+热门推荐 
 * @author wujian  
 * @date 2015-9-1 下午3:04:14  
 */
public class SearchHistoryView {

	private SearchActivity context;
	private View rootView;
	
	private ScrollListView search_history_listview;
	private SearchHistoryAdapter historyAdapter;
	private ArrayList<XKSearchResult> searchHistoryList;
//	private List<String> searchHistoryList;

    private LinearLayout hot_key_lay;
	private GridView hot_grid;
	private SearchHotKeysAdapter hotKeysAdapter;
	private ArrayList<XKSearchResult> hotList;
	
	private TextView clear_txt;
	
	private ModelApplication modelApp;
	private SearchDbService searchDbService = new SearchDbService();
	
	public View getView() {
        return rootView;
    } 
	
	public SearchHistoryView(SearchActivity context) {
		this.context = context;
		modelApp = (ModelApplication) context.getApplication();
		
		initView();
	    setListener();
	    startHotWordTask();
	    
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_search_history, null);
		
		search_history_listview = (ScrollListView) rootView.findViewById(R.id.search_history_listview);
		hot_grid = (GridView) rootView.findViewById(R.id.hot_keys_grid);
        hot_key_lay = (LinearLayout) rootView.findViewById(R.id.hot_key_lay);

		clear_txt = (TextView) rootView.findViewById(R.id.clear_txt);
	}
	
	private void setListener(){
		//清空搜索历史
		clear_txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchDbService.clearSearchContent("", modelApp.getSite().getSiteId());
				fillSearchHistoryData();
			}
		});
		
		search_history_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				String keyword = searchHistoryList.get(position).getProjectName();
				Bundle data = new Bundle();
				data.putString("keyword", keyword);
				Intent intent = null;
				
				if(SearchActivity.SEARCH_NEW_HOUSE.equals(searchHistoryList.get(position).getType())){
					intent = new Intent(context, NewHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
					
				}else if(SearchActivity.SEARCH_OLD_HOUSE.equals(searchHistoryList.get(position).getType())){
					intent = new Intent(context, OldHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
					
				}else if(SearchActivity.SEARCH_RENT_HOUSE.equals(searchHistoryList.get(position).getType())){
					intent = new Intent(context, RentHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
					
				}else if(SearchActivity.SEARCH_NEWS.equals(searchHistoryList.get(position).getType())){
					intent = new Intent(context, NewsSearchActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
				}
			}
		});
		
	}

    public void showContentView(){
        if(searchHistoryList == null || searchHistoryList.size() ==0){
            rootView.setVisibility(View.INVISIBLE);
            return;
        }
        rootView.setVisibility(View.VISIBLE);
    }

    public void dismissContentView(){
        rootView.setVisibility(View.GONE);
    }

	//热词
	private void fillHotKeysData() {
				
		int size = hotList.size();
		int length = 120;
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int gridviewWidth = (int) (size * (length + 8) * density);
		int itemWidth = (int) (length * density);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
		hot_grid.setLayoutParams(params); // 重点
		hot_grid.setColumnWidth(itemWidth); // 重点
		hot_grid.setHorizontalSpacing(DisplayUtil.dip2px(context, 8)); // 间距
		hot_grid.setStretchMode(GridView.NO_STRETCH);
		hot_grid.setNumColumns(size); // 重点
		hot_grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		hotKeysAdapter = new SearchHotKeysAdapter(context, hotList);
		hot_grid.setAdapter(hotKeysAdapter);
	}
	
	public void fillSearchHistoryData() {
		searchHistoryList = searchDbService.getSearchResult("", modelApp.getSite().getSiteId());
		if(searchHistoryList == null || searchHistoryList.size() ==0){
            rootView.setVisibility(View.INVISIBLE);
            return;
        }
        rootView.setVisibility(View.VISIBLE);

		if (historyAdapter == null) {
			historyAdapter = new SearchHistoryAdapter(context, searchHistoryList);
			search_history_listview.setAdapter(historyAdapter);
		} else {
			historyAdapter.setData(searchHistoryList);
		}
	}
	
	
	
	private void startHotWordTask(){
		if(NetUtil.detectAvailable(context)){
			SearchHotWordsRequest request = new SearchHotWordsRequest(modelApp.getSite().getSiteId(), 
					new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
					case Constants.NO_DATA_FROM_NET:
                        hot_key_lay.setVisibility(View.GONE);
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						ArrayList<XKSearchResult> temp = (ArrayList<XKSearchResult>) message.obj;
						if(hotList != null){
							hotList.clear();
						}else{
							hotList = new ArrayList<XKSearchResult>();
						}
						hotList.addAll(temp);
						if(hotList.size() > 0){
                            hot_key_lay.setVisibility(View.VISIBLE);
                            fillHotKeysData();
                        }else{
                            hot_key_lay.setVisibility(View.GONE);
                        }

						break;
					}
				}
			});
			request.doRequest();
		}
	}
	
}
