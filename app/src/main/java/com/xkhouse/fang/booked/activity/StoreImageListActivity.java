package com.xkhouse.fang.booked.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.CJFragment;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.booked.entity.StoreAlbumCategory;
import com.xkhouse.fang.booked.entity.StoreDetail;
import com.xkhouse.fang.booked.task.StoreAlbumCategoryListRequest;
import com.xkhouse.fang.booked.task.StoreDetailRequest;
import com.xkhouse.fang.booked.view.ItemStoreImageView;
import com.xkhouse.fang.user.view.ItemCJListView;
import com.xkhouse.fang.widget.CustomScrollView;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 商户相册
 */
public class StoreImageListActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    //加载
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout content_lay;

    //分类列表
    private PagerAdapter adapter;
    private ViewPager pager;
    private TabPageIndicator indicator;

    private String[] titles;
    private String[] types;
    private List<ItemStoreImageView> recommendViews = new ArrayList<>();

    private StoreAlbumCategoryListRequest categoryListRequest;
    private ArrayList<StoreAlbumCategory> categoryList;

    private String id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        startTask();
	}


    @Override
    public void onResume() {
        super.onResume();

    }

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_store_image);
	}

	@Override
	protected void init() {
		super.init();
        id = getIntent().getExtras().getString("id");
	}

	@Override
	protected void findViews() {
		initTitle();

        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
        content_lay = (LinearLayout) findViewById(R.id.content_lay);

        // ViewPager的adapter
        pager = (ViewPager) findViewById(R.id.pager);

        // 实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

	}


	private void initTitle() {
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("商户详情");
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
        error_lay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){

            case R.id.error_lay:
                startTask();
                break;
        }
	}
	
	
	private void fillData(){

        if (categoryList == null || categoryList.isEmpty()) return;

        types = new String[categoryList.size()];
        titles = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            titles[i] = categoryList.get(i).getCategory_id();
            titles[i] = categoryList.get(i).getTitle();
        }

        for(int i = 0; i < types.length; i++){
            ItemStoreImageView view = new ItemStoreImageView(mContext, id, types[i]);
            recommendViews.add(view);
            view.refreshView();
        }

        adapter = new TabPageIndicatorAdapter();
        pager.setAdapter(adapter);


	}


    RequestListener detailRequestListener = new RequestListener() {
        @Override
        public void sendMessage(Message message) {
            rotate_loading.stop();
            rotate_loading.setVisibility(View.GONE);
            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    if (categoryList == null) {
                        content_lay.setVisibility(View.GONE);
                        error_lay.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.NO_DATA_FROM_NET:
                    error_lay.setVisibility(View.GONE);
                    content_lay.setVisibility(View.VISIBLE);
                    if (categoryList == null || categoryList.isEmpty()) {
                        content_lay.setVisibility(View.GONE);
                    }
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    content_lay.setVisibility(View.VISIBLE);
                    error_lay.setVisibility(View.GONE);

                    ArrayList<StoreAlbumCategory> temp = (ArrayList<StoreAlbumCategory>) message.obj;
                    if (categoryList != null) {
                        categoryList.clear();
                    }else {
                        categoryList = new ArrayList<>();
                    }
                    categoryList.addAll(temp);

                    fillData();
                    break;
            }
        }
    };

    private void startTask() {
        if (NetUtil.detectAvailable(mContext)) {
            if (categoryListRequest == null) {
                categoryListRequest = new StoreAlbumCategoryListRequest(id, detailRequestListener);
            } else {
                categoryListRequest.setData(id);
            }
            content_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.VISIBLE);
            rotate_loading.start();

            categoryListRequest.doRequest();

        } else {
            if (categoryList == null || categoryList.isEmpty()) {
                content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
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
