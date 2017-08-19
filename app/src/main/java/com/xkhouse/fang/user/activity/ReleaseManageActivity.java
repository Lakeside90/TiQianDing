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
import com.xkhouse.fang.user.view.ReleaseManageItemView;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 个人房源管理
* @author wujian  
* @date 2016-04-11
 */
public class ReleaseManageActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private ImageView iv_head_right;

	private PagerAdapter adapter;
	private ViewPager pager;
	private TabPageIndicator indicator;
	
	private String[] titles_sell = {"出售", "求购"};
	private List<ReleaseManageItemView> sellItemViews = new ArrayList<ReleaseManageItemView>();

    private String[] titles_rent = {"出租", "求租"};
	private List<ReleaseManageItemView> rentItemViews = new ArrayList<ReleaseManageItemView>();

	public static final int RELEASE_SELL = 1001;
	public static final int RELEASE_RENT = 1002;

    private int release_type;

    private int type;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_release_manage);
	}
	
	@Override
	protected void init() {
		super.init();

        release_type = getIntent().getExtras().getInt("release_type");

        if(release_type == RELEASE_SELL){
            ReleaseManageItemView sellOutView = new ReleaseManageItemView(ReleaseManageActivity.this,
                    ReleaseManageItemView.RELEASE_SELL_OUT);
            ReleaseManageItemView sellInView = new ReleaseManageItemView(ReleaseManageActivity.this,
                    ReleaseManageItemView.RELEASE_SELL_IN);
            sellItemViews.add(sellOutView);
            sellItemViews.add(sellInView);
//            sellOutView.refreshView();
            type = ReleaseManageItemView.RELEASE_SELL_OUT;

        }else if(release_type == RELEASE_RENT){
            ReleaseManageItemView rentOutView = new ReleaseManageItemView(ReleaseManageActivity.this,
                    ReleaseManageItemView.RELEASE_RENT_OUT);
            ReleaseManageItemView rentInView = new ReleaseManageItemView(ReleaseManageActivity.this,
                    ReleaseManageItemView.RELEASE_RENT_IN);
            rentItemViews.add(rentOutView);
            rentItemViews.add(rentInView);
//            rentOutView.refreshView();
            type = ReleaseManageItemView.RELEASE_RENT_OUT;
        }

	}
	
	@Override
	protected void onResume() {
		super.onResume();

        switch (type){
            case ReleaseManageItemView.RELEASE_SELL_OUT:
                sellItemViews.get(0).refreshAfterOperate();
                break;

            case ReleaseManageItemView.RELEASE_SELL_IN:
                sellItemViews.get(1).refreshAfterOperate();
                break;

            case ReleaseManageItemView.RELEASE_RENT_OUT:
                rentItemViews.get(0).refreshAfterOperate();
                break;

            case ReleaseManageItemView.RELEASE_RENT_IN:
                rentItemViews.get(1).refreshAfterOperate();
                break;

            default:
                break;
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
                if(release_type == RELEASE_SELL){
                    sellItemViews.get(arg0).refreshView();
                    if (arg0 == 0){
                        type = ReleaseManageItemView.RELEASE_SELL_OUT;
                    }else{
                        type = ReleaseManageItemView.RELEASE_SELL_IN;
                    }
                }else if(release_type == RELEASE_RENT){
                    rentItemViews.get(arg0).refreshView();
                    if (arg0 == 0){
                        type = ReleaseManageItemView.RELEASE_RENT_OUT;
                    }else{
                        type = ReleaseManageItemView.RELEASE_RENT_IN;
                    }
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
        iv_head_right.setVisibility(View.VISIBLE);

        tv_head_title.setText("房源管理");
		
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_head_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type){
                    case ReleaseManageItemView.RELEASE_SELL_OUT:
                        startActivity(new Intent(ReleaseManageActivity.this, SellReleaseActivity.class));
                        break;

                    case ReleaseManageItemView.RELEASE_SELL_IN:
                        startActivity(new Intent(ReleaseManageActivity.this, SellInActivity.class));
                        break;

                    case ReleaseManageItemView.RELEASE_RENT_OUT:
                        startActivity(new Intent(ReleaseManageActivity.this, RentReleaseActivity.class));
                        break;

                    case ReleaseManageItemView.RELEASE_RENT_IN:
                        startActivity(new Intent(ReleaseManageActivity.this, RentInActivity.class));
                        break;

                    default:
                        break;
                }
            }
        });

	}
	
	
	/**
	 * ViewPager适配器
	 */
	class TabPageIndicatorAdapter extends PagerAdapter {

		@Override
		public int getCount() {
            if (release_type == RELEASE_SELL){
                return titles_sell.length;
            }else{
                return titles_rent.length;
            }
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

            if (release_type == RELEASE_SELL){
                container.addView(sellItemViews.get(position).getView(), 0);
                return sellItemViews.get(position).getView();
            }else{
                container.addView(rentItemViews.get(position).getView(), 0);
                return rentItemViews.get(position).getView();
            }

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
            if (release_type == RELEASE_SELL){
                return titles_sell[(position % titles_sell.length)];
            }else{
                return titles_rent[(position % titles_rent.length)];
            }
		}

	}

	
	
	@Override
	public void onStop() {
		super.onStop();
	}

}
