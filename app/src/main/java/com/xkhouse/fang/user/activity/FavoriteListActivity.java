package com.xkhouse.fang.user.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.xkhouse.fang.user.view.FavoriteListItemView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

/**
* @Description: 我的收藏 
* @author wujian  
* @date 2015-11-3 上午8:42:27
 */
public class FavoriteListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;
	
	private String[] titles = {"新房", "二手房", "租房", "资讯"};
	private List<FavoriteListItemView> favoriteListItemViews = new ArrayList<FavoriteListItemView>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		favoriteListItemViews.get(0).refreshView();
	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_favorite_list);
	}
	
	@Override
	protected void init() {
		super.init();
		
		FavoriteListItemView newHouseView = new FavoriteListItemView(mContext, FavoriteListItemView.FAV_TYPE_NEW);
		FavoriteListItemView oldHouseView = new FavoriteListItemView(mContext, FavoriteListItemView.FAV_TYPE_OLD);
		FavoriteListItemView rentHouseView = new FavoriteListItemView(mContext, FavoriteListItemView.FAV_TYPE_RENT);
		FavoriteListItemView newsView = new FavoriteListItemView(mContext, FavoriteListItemView.FAV_TYPE_NEWS);
		
		favoriteListItemViews.add(newHouseView);
		favoriteListItemViews.add(oldHouseView);
		favoriteListItemViews.add(rentHouseView);
		favoriteListItemViews.add(newsView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
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
				
				favoriteListItemViews.get(arg0).refreshView();
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
		
		tv_head_title.setText("我的收藏");
		
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
			container.addView(favoriteListItemViews.get(position).getView(), 0);
			return favoriteListItemViews.get(position).getView();
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
	public void onStop() {
		super.onStop();
	}

}
