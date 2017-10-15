package com.xkhouse.fang.booked.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.booked.view.ItemLuckDetailView;
import com.xkhouse.fang.widget.CustomViewPager;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动详情--抽奖
 */
public class LuckDetailBakActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
    private TextView tv_head_title;



    //分类列表
    private PagerAdapter adapter;
    private CustomViewPager pager;
    private TabPageIndicator indicator;

    public static final String LUCK_DETAIL = "0";
    public static final String LUCK_RULE = "1";
    public static final String LUCK_JOIN = "2";

    private String[] titles = {"详情", "活动规则", "参与记录"};
    private String[] types = {LUCK_DETAIL, LUCK_RULE, LUCK_JOIN};
    private List<ItemLuckDetailView> tabViews = new ArrayList<>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        for(int i = 0; i < types.length; i++){
            ItemLuckDetailView view = new ItemLuckDetailView(mContext, types[i]);
            tabViews.add(view);
        }

	}


	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_luck_detail_bak);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();


        // ViewPager的adapter
        pager = (CustomViewPager) findViewById(R.id.pager);
        adapter = new TabPageIndicatorAdapter();
        pager.setAdapter(adapter);

        // 实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        // 如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("活动详情");

        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}

	@Override
	protected void setListeners() {



	}

    @Override
    public void onResume() {
        super.onResume();

//        refreshData();
    }

//    public void refreshData(){
//        for (int i = 0; i < tabViews.size(); i++) {
//            tabViews.get(i).refreshView();
//        }
//    }
//
//    private void fillAdData(){
//
//
//    }



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
            container.addView(tabViews.get(position).getView(), 0);
            return tabViews.get(position).getView();
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
