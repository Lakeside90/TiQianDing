package com.xkhouse.fang.house.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.OldHouseListActivity;
import com.xkhouse.fang.house.activity.RentHouseListActivity;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.house.adapter.SearchProjectResultAdapter;
import com.xkhouse.fang.house.entity.XKSearchResult;
import com.xkhouse.fang.house.service.SearchDbService;
import com.xkhouse.fang.house.task.SearchProjectNameRequest;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/** 
 * @Description: 搜索结果 
 * @author wujian  
 * @date 2015-9-1 下午3:04:14  
 */
public class SearchResultView {

	private SearchActivity context;
	private View rootView;
	
	private ListView search_result_listview;
	private SearchProjectResultAdapter adapter;
	private ArrayList<XKSearchResult> resultList;
	
	private SearchProjectNameRequest request;
	
	private ModelApplication modelApp;
	
	private SearchDbService searchDbService = new SearchDbService();
	
	public View getView() {
        return rootView;
    } 
	
	public SearchResultView(SearchActivity context) {
		this.context = context;
		modelApp = (ModelApplication) context.getApplication();
		
		initView();
	    setListener();
	}
	
	private void initView() {
		rootView = LayoutInflater.from(context).inflate(R.layout.view_search_result, null);
		
		search_result_listview = (ListView) rootView.findViewById(R.id.search_result_listview);
		
	}
	
	private void setListener(){
		search_result_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String keyword = resultList.get(position).getProjectName();
				Bundle data = new Bundle();
				data.putString("keyword", keyword);
				Intent intent = null;
				
				if(SearchActivity.SEARCH_NEW_HOUSE.equals(resultList.get(position).getType())){
					intent = new Intent(context, NewHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
					
				}else if(SearchActivity.SEARCH_OLD_HOUSE.equals(resultList.get(position).getType())){
					intent = new Intent(context, OldHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
					
				}else if(SearchActivity.SEARCH_RENT_HOUSE.equals(resultList.get(position).getType())){
					intent = new Intent(context, RentHouseListActivity.class);
					intent.putExtras(data);
					context.startActivity(intent);
				}
				
				searchDbService.saveSearchContent(resultList.get(position), "", modelApp.getSite().getSiteId());
			}
		});
		
	}
	
	public void refreshView(String keyword) {
		startSearchTask(keyword);
	}
	
	private void fillData(){
		if(resultList == null) return;
		
		if(adapter == null){
			adapter = new SearchProjectResultAdapter(context, resultList);
			search_result_listview.setAdapter(adapter);
		}else{
			adapter.setData(resultList);
		}
	}
	
	private RequestListener listener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(context, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<XKSearchResult> temp = (ArrayList<XKSearchResult>) message.obj;
				if(resultList != null){
					resultList.clear();
				}else{
					resultList = new ArrayList<XKSearchResult>();
				}
				resultList.addAll(temp);
				
				fillData();
				break;
			}
		}
	};
	
	private void startSearchTask(String keyword){
		if(NetUtil.detectAvailable(context)){
			if(request == null){
				 request = new SearchProjectNameRequest(modelApp.getSite().getSiteId(), 
						 keyword, listener);
			}else{
				request.setData(modelApp.getSite().getSiteId(), keyword);
			}
			
			request.doRequest();
		}
	}
	

	
}
