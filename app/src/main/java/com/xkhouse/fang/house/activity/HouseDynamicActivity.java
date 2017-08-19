package com.xkhouse.fang.house.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.XKDynamic;
import com.xkhouse.fang.house.task.HouseDynamicRequest;
import com.xkhouse.fang.house.view.HouseDynamicView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 楼盘销售动态
* @author wujian  
* @date 2015-10-8 下午2:24:17
 */
public class HouseDynamicActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;
	
	private ArrayList<CommonType> yearList = new ArrayList<CommonType>();  
	private ArrayList<XKDynamic> dynamicList = new ArrayList<XKDynamic>();
	
	private List<HouseDynamicView> houseDynamicViews = new ArrayList<HouseDynamicView>();
	
	private String projectId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startDataTask();
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_house_dynamic);
		
	}
	
	@Override
	protected void init() {
		super.init();
		projectId = getIntent().getExtras().getString("projectId");
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		pager = (ViewPager) findViewById(R.id.pager);
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
	}
	
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("销售动态");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//解決黑屏問題
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
	}
	
	private void fillData() {
		if(yearList == null || yearList.size() < 1) return;
		
		for(int i = 0; i < yearList.size(); i++){
			HouseDynamicView view = new HouseDynamicView(mContext, yearList.get(i).getId(), projectId);
			houseDynamicViews.add(view);
		}
		houseDynamicViews.get(0).fillData(dynamicList);
		
		adapter = new TabPageIndicatorAdapter();
		pager.setAdapter(adapter);
		
		indicator.setViewPager(pager);
		indicator.setVisibility(View.VISIBLE);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if(arg0 < houseDynamicViews.size()){
					houseDynamicViews.get(arg0).refreshView();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	
	/**
	 * ViewPager适配器
	 */
	class TabPageIndicatorAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return yearList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(houseDynamicViews.get(position).getView(), 0);
			return houseDynamicViews.get(position).getView();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return yearList.get(position % yearList.size()).getName();
		}

	}
	
	private void startDataTask() {
		if(NetUtil.detectAvailable(mContext)){
			HouseDynamicRequest request = new HouseDynamicRequest(projectId, "", 1, 10, new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
					case Constants.NO_DATA_FROM_NET:
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						Bundle data = message.getData();
						ArrayList<CommonType> tempTitle = (ArrayList<CommonType>) data.getSerializable("yearList");
						ArrayList<XKDynamic> tempDynamic = (ArrayList<XKDynamic>) data.getSerializable("dynamicList");
						yearList.clear();
						dynamicList.clear();
						yearList.addAll(tempTitle);
						dynamicList.addAll(tempDynamic);
						fillData();
						break;
					}
				}
			});
			
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
}
