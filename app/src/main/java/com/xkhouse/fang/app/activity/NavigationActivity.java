package com.xkhouse.fang.app.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.adapter.AppListAdapter;
import com.xkhouse.fang.app.adapter.NavigationAdapter;
import com.xkhouse.fang.app.entity.XKNavigation;
import com.xkhouse.fang.widget.ScrollGridView;

/** 
 * @Description: 更多页面 
 * @author wujian  
 * @date 2015-9-8 下午6:51:01  
 */
public class NavigationActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private ScrollGridView naviagtion_grid;	//频道导航
	private NavigationAdapter navigationAdapter;
	private ArrayList<XKNavigation> moreList;
	
	
	private ScrollGridView app_grid;	//更多App
	private AppListAdapter appListAdapter;
	private ArrayList<XKNavigation> appList;
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_navigation);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fillMoreGridData();
		fillAppGridData();
	}
	
	@Override
	protected void init() {
		super.init();
		moreList = (ArrayList<XKNavigation>) getIntent().getExtras().get("more");
		appList = (ArrayList<XKNavigation>) getIntent().getExtras().get("app");
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		naviagtion_grid = (ScrollGridView) findViewById(R.id.naviagtion_grid);
		app_grid = (ScrollGridView) findViewById(R.id.app_grid);
		
		app_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//下载app
//				Uri uri = Uri.parse("");
//				Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//				startActivity(intent);
			}
		});
		
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("更多");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void fillMoreGridData(){
		if (navigationAdapter == null) {
			navigationAdapter = new NavigationAdapter(moreList, mContext);
		}
		naviagtion_grid.setAdapter(navigationAdapter);
	}
	
	private void fillAppGridData(){
		if(appList == null) return;
		
		if (appListAdapter == null) {
			appListAdapter = new AppListAdapter(appList, mContext);
			app_grid.setAdapter(appListAdapter);
		}else {
			appListAdapter.setData(appList);
		}
		
	}
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
	}
	
	
}
