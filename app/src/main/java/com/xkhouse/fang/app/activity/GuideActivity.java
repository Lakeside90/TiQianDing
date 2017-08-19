package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.frame.log.Logger;

import java.util.ArrayList;
import java.util.List;

/** 
 * @Description: 引导页 
 * @author wujian  
 * @date 2015-8-25 下午2:13:21  
 */
public class GuideActivity extends AppBaseActivity {

	private TextView guide_btn;
	
	ViewPager viewPager;
    private LinearLayout guide_point_lay;
    private List<ImageView> pointViews;




	final int imageID[] = new int[] { R.drawable.guide_img_1, R.drawable.guide_img_2, R.drawable.guide_img_3};

	int x;
	
	private int currentItem = 0;
	
	@Override
	protected void onResume() {
	    super.onResume();
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_guide);
		
		guide_btn = (TextView) findViewById(R.id.guide_btn);
		viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
        guide_point_lay = (LinearLayout) findViewById(R.id.guide_point_lay);

        List<View> views = new ArrayList<View>();
		for (int i = 0; i < imageID.length; i++) {
			View image = new View(this);
			image.setBackgroundResource(imageID[i]);
			views.add(image);
		}

        pointViews = new ArrayList<ImageView>();
        guide_point_lay.removeAllViews();

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(DisplayUtil.dip2px(GuideActivity.this, 6),
                DisplayUtil.dip2px(GuideActivity.this, 6));
        lps.leftMargin = DisplayUtil.dip2px(GuideActivity.this, 6);
        for(int i=0; i < imageID.length; i++){
            ImageView imageView = new ImageView(GuideActivity.this);
            imageView.setImageResource(R.drawable.guide_cricle_dark_bg);
            guide_point_lay.addView(imageView, lps);
            pointViews.add(imageView);
        }
        if(pointViews.size() > 1){
            pointViews.get(0).setImageResource(R.drawable.guide_cricle_light_bg);
        }



		
		SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(views);
		viewPager.setAdapter(splashPagerAdapter);
		viewPager.clearAnimation();
		Preference.getInstance().writeAppFirstStart(false);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentItem = position;
				if (currentItem == imageID.length - 1) {
					guide_btn.setVisibility(View.VISIBLE);
				}else{
					guide_btn.setVisibility(View.INVISIBLE);
				}

                for (int i = 0; i < imageID.length; i++) {
                    if(position == i){
                        pointViews.get(i).setImageResource(R.drawable.guide_cricle_light_bg);
                    }else{
                        pointViews.get(i).setImageResource(R.drawable.guide_cricle_dark_bg);
                    }
                }

			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
	}

	
	
	@Override
	protected void setListeners() {
		guide_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 跳转到城市选择页
				Intent intent = null;
                intent = new Intent(mContext, CitySelectActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public int dip2px(float dpValue) {  
	        final float scale = getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	}
	
	
	class SplashPagerAdapter extends PagerAdapter {

		private List<View> listViews;// content

		private int size;// 页数

		public SplashPagerAdapter(List<View> views) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = views;
			size = views == null ? 0 : views.size();
		}

		@Override
		public int getCount() {// 返回数量
			return size;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
			} catch (Exception e) {
				Logger.e(TAG, "exception：" + e.getMessage());
			}
			return listViews.get(arg1 % size);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
}
