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
import com.xkhouse.fang.user.entity.XKBank;
import com.xkhouse.fang.user.view.CashBankView;
import com.xkhouse.fang.user.view.CashZFBView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

/**
* @Description: 申请提现 
* @author wujian  
* @date 2015-10-21 下午2:50:10
 */
public class CashWithdrawActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	
	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;
	
	private String[] titles = {"提现至银行卡", "提现至支付宝"};
	
	private List<View> views = new ArrayList<View>();
	private CashBankView bankView;
	private CashZFBView zfbView;
	
	private String sum;		//钱包余额
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_cash_withdraw);
		
		
	}
	
	@Override
	protected void init() {
		super.init();
		sum = getIntent().getExtras().getString("sum");
		
		bankView = new CashBankView(mContext, sum);
		zfbView = new CashZFBView(mContext, sum);
		views.add(bankView.getView());
		views.add(zfbView.getView());
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
		tv_head_title.setText("申请提现");
		tv_head_right.setText("提现说明");
		tv_head_right.setVisibility(View.VISIBLE);
		
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(mContext, CashWithdrawDeclareActivity.class));
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
			container.addView(views.get(position), 0);
			return views.get(position);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//选择的银行
        if (requestCode == CashBankView.REQUEST_CODE) {
        	if(resultCode == CashBankView.RESULT_CODE){
				XKBank bank = (XKBank) data.getExtras().getSerializable("bank");
				bankView.setBankData(bank);
			}
        }
	}
	
	
}
