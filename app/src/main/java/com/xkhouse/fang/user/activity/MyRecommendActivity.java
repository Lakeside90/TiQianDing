package com.xkhouse.fang.user.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.money.activity.CustomerAddActivity;
import com.xkhouse.fang.user.view.CashBankView;
import com.xkhouse.fang.user.view.CashZFBView;
import com.xkhouse.fang.user.view.ItemRecommendView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

/**
* @Description: 我的推荐
* @author wujian  
* @date 2015-10-22 下午1:57:55
 */
public class MyRecommendActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	
	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;

	public static final String RECOMMEND_ALL = "0";			//全部
	public static final String RECOMMEND_ING = "1";			//推荐中
	public static final String RECOMMEND_SUCCESS = "2";		//推荐成功
	public static final String RECOMMEND_FAIL = "3";		//推荐失败
	
	private String[] titles = {"全部", "推荐中", "推荐成功", "推荐失败"};
	private String[] types = {RECOMMEND_ALL, RECOMMEND_ING, RECOMMEND_SUCCESS, RECOMMEND_FAIL};
	private List<ItemRecommendView> recommendViews = new ArrayList<ItemRecommendView>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_my_recommend);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		refreshData();
	}

    public void refreshData(){
        for (int i = 0; i < recommendViews.size(); i++) {
            recommendViews.get(i).refreshView();
        }
    }
	
	@Override
	protected void init() {
		super.init();
		
		for(int i = 0; i < types.length; i++){
			ItemRecommendView view = new ItemRecommendView(this, types[i]);
			recommendViews.add(view);
		}
		
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		// ViewPager的adapter
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new TabPageIndicatorAdapter();
		pager.setAdapter(adapter);
		
		// 实例化TabPageIndicator然后设置ViewPager与之关联
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);

		// 如果我们要对ViewPager设置监听，用indicator设置就行了
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_title.setText("我的推荐");
		tv_head_right.setText("我要推荐");
		tv_head_right.setVisibility(View.VISIBLE);
		
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, CustomerAddActivity.class));
			}
		});

		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	
	/**
	 * ViewPager适配器
	 */
	class TabPageIndicatorAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(recommendViews.get(position).getView(), 0);
			return recommendViews.get(position).getView();
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
			return titles[(position % titles.length)];
		}

	}
	
	
	
	
}
