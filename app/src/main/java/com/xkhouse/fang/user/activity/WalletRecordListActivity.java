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
import com.xkhouse.fang.user.view.ItemRecommendView;
import com.xkhouse.fang.user.view.ItemWalletRecordView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

/**
* @Description: 交易账单
* @author wujian  
* @date 2015-10-30 上午11:08:10
 */
public class WalletRecordListActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	
	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;

	public static final String WALLET_ALL = "0";		//全部
	public static final String WALLET_IN = "1";			//收入
	public static final String WALLET_OUT = "2";		//提现
	
	private String[] titles = {"交易记录", "收入", "提现"};
	private String[] types = {WALLET_ALL, WALLET_IN, WALLET_OUT};
	
	private List<ItemWalletRecordView> walletRecordViews = new ArrayList<ItemWalletRecordView>();
	
	private int index = 0;  //显示那一页
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        pager.setCurrentItem(index);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_wallet_record_list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		for (int i = 0; i < walletRecordViews.size(); i++) {
			walletRecordViews.get(i).refreshView();
		}
	}
	
	
	@Override
	protected void init() {
		super.init();

        index = getIntent().getExtras().getInt("index");

		for(int i = 0; i < types.length; i++){
			ItemWalletRecordView view = new ItemWalletRecordView(mContext, types[i]);
			walletRecordViews.add(view);
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
		tv_head_title.setText("交易账单");
		
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
			container.addView(walletRecordViews.get(position).getView(), 0);
			return walletRecordViews.get(position).getView();
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
