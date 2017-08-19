package com.xkhouse.fang.app.activity;

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
import com.xkhouse.fang.app.view.NewsItemView;
import com.xkhouse.fang.widget.IndexViewPager;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/** 
 * @Description: 资讯首页（入口） 
 * @author wujian  
 * @date 2015-10-12 上午9:11:14  
 */
public class NewsIndexActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	
	private PagerAdapter adapter;
	private IndexViewPager pager;
	private TabPageIndicator indicator;
	
	private String[] titles = {"头条", "导购", "行情"};
	private List<NewsItemView> newsItemViews = new ArrayList<NewsItemView>();
	
	private int currentItem = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_news_index);
		
		
	}
	
	@Override
	protected void init() {
		super.init();
		
		String[] urls = {modelApp.getNewsIndex(), modelApp.getNewsDG(), modelApp.getNewsHQ()};
		
		if(getIntent().getExtras() != null){
			currentItem = getIntent().getExtras().getInt("currentItem");
		}
		
		
		for (int i = 0; i < 3; i++) {
			NewsItemView view = new NewsItemView(mContext, urls[i]);
			newsItemViews.add(view);
		}
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		// ViewPager的adapter
		pager = (IndexViewPager) findViewById(R.id.pager);
        pager.setScanScroll(false);

		adapter = new TabPageIndicatorAdapter();
		pager.setAdapter(adapter);
		
		// 实例化TabPageIndicator然后设置ViewPager与之关联
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setCurrentItem(currentItem);
		
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
		tv_head_title.setText("资讯");
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
			container.addView(newsItemViews.get(position).getView(), 0);
			return newsItemViews.get(position).getView();
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
