package com.xkhouse.fang.booked.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.booked.entity.StoreAlbum;
import com.xkhouse.fang.widget.autoscrollviewpager.AutoScrollViewPager;
import com.xkhouse.frame.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 商户详情--大图
 */
public class StoreImageDeatilActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;


    private TextView image_dec_txt;
    private TextView index_txt;
    private TextView count_txt;

    //轮询图
    private AutoScrollViewPager home_viewpager;
    private ArrayList<StoreAlbum> albumList = new ArrayList<>();



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.c_303030));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fillData();

        if (albumList != null && albumList.size() > 0) {
            image_dec_txt.setText(albumList.get(0).getContent());
        }
	}


	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_store_image_detail);
	}

	@Override
	protected void init() {
		super.init();

        albumList = (ArrayList<StoreAlbum>) getIntent().getExtras().getSerializable("albumList");
	}

	@Override
	protected void findViews() {
		initTitle();

        home_viewpager = (AutoScrollViewPager) findViewById(R.id.home_viewpager);

        image_dec_txt = (TextView) findViewById(R.id.image_dec_txt);
        index_txt = (TextView) findViewById(R.id.index_txt);
        count_txt = (TextView) findViewById(R.id.count_txt);
    }


	private void initTitle() {

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

        home_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                index_txt.setText(String.valueOf(arg0 + 1));
                image_dec_txt.setText(albumList.get(arg0).getContent());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

	}



    private void fillData(){
        if(albumList == null) return;

        count_txt.setText("/" + albumList.size());

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();

        List<View> views = new ArrayList<>();
        for (int i = 0; i < albumList.size(); i++) {
            ImageView image = new ImageView(mContext);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(image);
            ImageLoader.getInstance().displayImage(albumList.get(i).getImg(), image, options);
        }

        ADPagerAdapter pagerAdapter = new ADPagerAdapter(views);
        home_viewpager.setAdapter(pagerAdapter);


    }


    class ADPagerAdapter extends PagerAdapter {

        private List<View> listViews;// content

        private int size;// 页数

        public ADPagerAdapter(List<View> views) {// 构造函数
            // 初始化viewpager的时候给的一个页面
            this.listViews = views;
            size = views == null ? 0 : views.size();
        }

        public void setData(List<View> views){
            this.listViews = views;
            notifyDataSetChanged();
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
                Logger.e("HomeFragment", "exception：" + e.getMessage());
            }
            return listViews.get(arg1 % size);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }



}
