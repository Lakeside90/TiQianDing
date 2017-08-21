package com.xkhouse.fang.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.fang.app.task.ADListRequest;
import com.xkhouse.fang.app.task.HouseLikeListRequest;
import com.xkhouse.fang.app.task.NewsLikeListRequest;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.house.adapter.BaikeTabAdapter;
import com.xkhouse.fang.house.entity.BaikeTab;
import com.xkhouse.fang.house.view.baike.BuyAbilityView;
import com.xkhouse.fang.house.view.baike.DaiKuanView;
import com.xkhouse.fang.house.view.baike.DingFangView;
import com.xkhouse.fang.house.view.baike.FangChanZhengView;
import com.xkhouse.fang.house.view.baike.HeTongView;
import com.xkhouse.fang.house.view.baike.JiaoFangView;
import com.xkhouse.fang.house.view.baike.LuoHuView;
import com.xkhouse.fang.house.view.baike.ZhaoFangView;
import com.xkhouse.fang.user.activity.MyRecommendActivity;
import com.xkhouse.fang.user.view.ItemCJListView;
import com.xkhouse.fang.user.view.ItemRecommendView;
import com.xkhouse.fang.widget.autoscrollviewpager.AutoScrollViewPager;
import com.xkhouse.fang.widget.fancycoverflow.FancyCoverFlow;
import com.xkhouse.fang.widget.viewpagerindicator.TabPageIndicator;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖--首页
 */
public class CJFragment extends AppBaseFragment {
	
	
	private View rootView;
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    //轮询图
    private AutoScrollViewPager home_viewpager;
    private LinearLayout home_point_lay;
    private ArrayList<XKAd> adList;
    private List<ImageView> pointViews;


    //分类列表
    private PagerAdapter adapter;
    private ViewPager pager;
    private TabPageIndicator indicator;

    public static final String RECOMMEND_ALL = "0";			//全部
    public static final String RECOMMEND_ING = "1";			//推荐中
    public static final String RECOMMEND_SUCCESS = "2";		//推荐成功
    public static final String RECOMMEND_FAIL = "3";		//推荐失败

    private String[] titles = {"全部抽奖", "人气", "上架时间", "已开奖"};
    private String[] types = {RECOMMEND_ALL, RECOMMEND_ING, RECOMMEND_SUCCESS, RECOMMEND_FAIL};
    private List<ItemCJListView> recommendViews = new ArrayList<ItemCJListView>();






    private ModelApplication modelApp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.NewsStyledIndicators);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        modelApp = (ModelApplication) getActivity().getApplication();

		rootView = localInflater.inflate(R.layout.activity_chou_jiang, container, false);



		findViews();
		setListeners();

        for(int i = 0; i < types.length; i++){
            ItemCJListView view = new ItemCJListView(getContext(), types[i]);
            recommendViews.add(view);
        }

        getDataFromLocal();

        getDataFromNet();

		return rootView;
	}


	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}

    @Override
    public void onResume() {
        super.onResume();

        refreshData();
    }

    public void refreshData(){
        for (int i = 0; i < recommendViews.size(); i++) {
            recommendViews.get(i).refreshView();
        }
    }


	private void findViews() {
        initTitle();

        home_viewpager = (AutoScrollViewPager) rootView.findViewById(R.id.home_viewpager);
        home_point_lay = (LinearLayout) rootView.findViewById(R.id.home_point_lay);

        // ViewPager的adapter
        pager = (ViewPager) rootView.findViewById(R.id.pager);
        adapter = new TabPageIndicatorAdapter();
        pager.setAdapter(adapter);

        // 实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
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
		iv_head_left = (ImageView) rootView.findViewById(R.id.iv_head_left);
		iv_head_left.setVisibility(View.INVISIBLE);
		
		tv_head_title = (TextView) rootView.findViewById(R.id.tv_head_title);
		tv_head_title.setText("抽奖");

	}


    private void setListeners() {
        home_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                for (int i = 0; i < adList.size(); i++) {
                    if (arg0 == i) {
                        pointViews.get(i).setImageResource(R.drawable.home_cricle_light_bg);
                    } else {
                        pointViews.get(i).setImageResource(R.drawable.home_cricle_dark_bg);
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


    private void fillAdData(){
        if(adList == null) return;

        if(adList.size() < 1){
            home_point_lay.setVisibility(View.GONE);
        }else{
            home_point_lay.setVisibility(View.VISIBLE);
        }

        pointViews = new ArrayList<>();
        home_point_lay.removeAllViews();

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(DisplayUtil.dip2px(getActivity(), 6),
                DisplayUtil.dip2px(getActivity(), 6));
        lps.leftMargin = DisplayUtil.dip2px(getActivity(), 3);
        for(int i=0; i < adList.size(); i++){
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.home_cricle_dark_bg);
            home_point_lay.addView(imageView, lps);
            pointViews.add(imageView);
        }
        if(pointViews.size() > 1){
            pointViews.get(0).setImageResource(R.drawable.home_cricle_light_bg);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.nopic)   // 加载的图片
                .showImageOnFail(R.drawable.nopic) // 错误的时候的图片
                .showImageForEmptyUri(R.drawable.nopic)
                .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
                .cacheOnDisk(true).build();

        List<View> views = new ArrayList<>();
        for (int i = 0; i < adList.size(); i++) {
            ImageView image = new ImageView(getActivity());
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(image);
            ImageLoader.getInstance().displayImage(adList.get(i).getPhotoUrl(), image, options);
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    goADDetail();
                }
            });
        }

        ADPagerAdapter pagerAdapter = new ADPagerAdapter(views);
        home_viewpager.setAdapter(pagerAdapter);

        home_viewpager.setInterval(3000);
        home_viewpager.startAutoScroll();

    }

    private void goADDetail(){

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

    private RequestListener adListListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {

            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    break;

                case Constants.NO_DATA_FROM_NET:
                    if (adList != null) {
                        adList.clear();
                        fillAdData();
                    }
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<XKAd> temp = (ArrayList<XKAd>) message.getData().getSerializable("adList");

                    if (adList == null) {
                        adList = new ArrayList<XKAd>();
                        adList.addAll(temp);
                    } else {
                        adList.clear();
                        adList.addAll(temp);
                    }
                    fillAdData();
                    break;
            }
        }
    };


    private void getDataFromNet(){
        if (NetUtil.detectAvailable(getActivity())) {

            //轮询图广告
            ADListRequest adListRequest = new ADListRequest(modelApp.getSite().getSiteId(), "187", adListListener);
            adListRequest.doRequest();

        } else {
            Toast.makeText(getActivity(), R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    /** 获取本地缓存数据  **/
    private void getDataFromLocal(){
        //轮询图广告
        ADListRequest adListRequest = new ADListRequest();
        adListRequest.parseResult(AppCache.readHomeAdJson(modelApp.getSite().getSiteId()));
        adList = adListRequest.getAdList();
        fillAdData();

    }




}
