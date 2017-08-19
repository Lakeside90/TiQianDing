package com.xkhouse.fang.user.activity;

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
import com.xkhouse.fang.user.view.JJRRentDeleteItemView;
import com.xkhouse.fang.user.view.JJRRentReleaseItemView;
import com.xkhouse.fang.user.view.JJRRentUnreleaseItemView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 经纪人出租房源管理
* @author wujian  
* @date 2016-04-14
 */
public class JJRRentReleaseManageActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private ImageView iv_head_right;

	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;
	
	private String[] titles = {"已发布", "待发布", "已删除"};
	private List<View> sellItemViews = new ArrayList<View>();
    private JJRRentReleaseItemView releaseItemView;
    private JJRRentUnreleaseItemView unreleaseItemView;
    private JJRRentDeleteItemView deleteItemView;

    private int index = 0;

    private boolean isFirstLoad = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (releaseItemView == null){
            releaseItemView = new JJRRentReleaseItemView(this);
        }
        sellItemViews.add(releaseItemView.getView());

        if (unreleaseItemView == null){
            unreleaseItemView = new JJRRentUnreleaseItemView(this);
        }
        sellItemViews.add(unreleaseItemView.getView());

        if (deleteItemView == null){
            deleteItemView = new JJRRentDeleteItemView(this);
        }
        sellItemViews.add(deleteItemView.getView());

	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_release_manage);
	}
	
	@Override
	protected void init() {
		super.init();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
        if(isFirstLoad){
            if(releaseItemView != null){
                releaseItemView.refreshAfterOperate();
            }
            isFirstLoad = false;
        }else{
            refreshAll();
        }

	}

    //刷新
    public void refreshAll(){
        if(releaseItemView != null){
            releaseItemView.refreshAfterOperate();
        }
        if(unreleaseItemView != null){
            unreleaseItemView.refreshAfterOperate();
        }
        if(deleteItemView != null){
            deleteItemView.refreshAfterOperate();
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

                switch (arg0){
                    case 0:
                        index = 0;
                        releaseItemView.refreshView();
                        break;

                    case 1:
                        index = 1;
                        unreleaseItemView.refreshView();
                        break;

                    case 2:
                        index = 2;
                        deleteItemView.refreshView();
                        break;

                    default:
                        break;
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
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        iv_head_right = (ImageView) findViewById(R.id.iv_head_right);
        iv_head_right.setImageResource(R.drawable.icon_release_add);

        tv_head_title.setText("出租房源");
		
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JJRRentReleaseManageActivity.this, RentReleaseActivity.class));
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

            container.addView(sellItemViews.get(position), 0);
            return sellItemViews.get(position);
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
