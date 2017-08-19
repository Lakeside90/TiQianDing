package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.baidu.android.pushservice.PushManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.adapter.HomeGridAdapter;
import com.xkhouse.fang.app.adapter.HomeNewsGridAdapter;
import com.xkhouse.fang.app.adapter.HouseLikeAdapter;
import com.xkhouse.fang.app.adapter.NewsLikeAdapter;
import com.xkhouse.fang.app.adapter.SaleGridAdapter;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.FSHQ;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.entity.KanFang;
import com.xkhouse.fang.app.entity.News;
import com.xkhouse.fang.app.entity.SaleHouse;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.fang.app.entity.XKNavigation;
import com.xkhouse.fang.app.service.SiteDbService;
import com.xkhouse.fang.app.task.ADListRequest;
import com.xkhouse.fang.app.task.FSHQRequest;
import com.xkhouse.fang.app.task.HotActivityListRequest;
import com.xkhouse.fang.app.task.HouseLikeListRequest;
import com.xkhouse.fang.app.task.KanFangListRequest;
import com.xkhouse.fang.app.task.NavigationRequest;
import com.xkhouse.fang.app.task.NewsLikeListRequest;
import com.xkhouse.fang.app.task.SiteListRequest;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.app.view.UpMarqueeTextView;
import com.xkhouse.fang.discount.activity.DiscountDetailActivity;
import com.xkhouse.fang.house.activity.CustomHouseListActivity;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.activity.SearchActivity;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.fang.widget.CustomScrollView;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.fang.widget.ScrollXListView;
import com.xkhouse.fang.widget.autoscrollviewpager.AutoScrollViewPager;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description:  首页
 * @author wujian  
 * @date 2015-8-25 下午4:28:25  
 */
public class HomeFragment extends AppBaseFragment implements OnClickListener, AMapLocationListener{
	
	
	private View rootView;
	//title bar
	private View home_search_bar;
	private TextView city_txt;
	
	private CustomScrollView content_scroll;
    private ImageView scroll_top_iv;
	
	//轮询图
	private AutoScrollViewPager home_viewpager;
	private LinearLayout home_point_lay;
	private LinearLayout home_ad_lay;
	private TextView ad_name_txt;
	private ArrayList<XKAd> adList;
	private List<ImageView> pointViews;
	
	//功能入口图标
	private ScrollGridView home_grid;
	private HomeGridAdapter homeGridAdapter;
	private ArrayList<XKNavigation> homeList;		//首页
	private ArrayList<XKNavigation> moreList;		//更多
	private ArrayList<XKNavigation> appList;		//app下载

    //免费看房
    private LinearLayout kanfang_lay;
    private UpMarqueeTextView kanfang_txt;
    private ArrayList<KanFang> kanFangList;
    private Timer timerKF;
    private int newsIndex;

	
	//限时抢购
	private View sale_lay;
	private ScrollGridView home_sale_grid;
	private TextView sale_time_txt;
	private List<SaleHouse> houseList;
	private SaleGridAdapter saleGridAdapter;

	//专题
	private ScrollGridView news_grid;
	private List<XKAd> themes;
	private HomeNewsGridAdapter newsGridAdapter;

    //房市行情
    private LinearLayout hq_lay;
    private TextView hq_month_txt;
    private TextView hq_price_txt;
    private TextView hq_count_txt;
    private FSHQ fshq;

	//猜你喜欢
    private LinearLayout house_like_more_lay;
    private LinearLayout news_like_more_lay;
	private ScrollListView house_like_listview;
	private ScrollXListView news_like_listview;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 5; //每次请求10条数据
	private boolean isPullDown = false; // 下拉
	private boolean isLoading = false; //是否正在加载数据
	
	
	private ArrayList<House> houseLikeList;
	private HouseLikeAdapter houseLikeAdapter;
	
	private ArrayList<News> newsLikeList = new ArrayList<News>();
	private NewsLikeAdapter newsLikeAdapter;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
	
	private ModelApplication modelApp;
	private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
	private AMapLocation mAmapLocation = null;			//当前定位的位置
	private LatLng startLatlng; 						//当前经纬度
	
	
	private SiteListRequest siteListRequest;
	private SiteDbService siteDbService = new SiteDbService();
	
	private Site currentSite;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		initData();
		
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		findViews();
		setListeners();
		
		//开始定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
		
		city_txt.setText(modelApp.getSite().getArea());
        getDataFromLocal();

		getDataFromNet();
		startNavigationTask();
		

		currentSite = modelApp.getSite();
		return rootView;
	}
	
	private void initData() {
		modelApp = (ModelApplication) getActivity().getApplication();

        try{
            PushManager.setTags(getActivity().getApplicationContext(),
                    getTagsList(modelApp.getSite().getSiteId()));
        }catch (Exception e){
            e.printStackTrace();
        }

		startSiteListTask();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (modelApp.getSite() != null
				&& !city_txt.getText().toString()
						.equals(modelApp.getSite().getArea())) {
            changeSiteData();
		} else {
			home_viewpager.startAutoScroll();
		}
	}

    //站点切换了，重新加载数据
    private void changeSiteData(){
        news_like_more_lay.setVisibility(View.GONE);
        house_like_more_lay.setVisibility(View.GONE);
        content_scroll.scrollTo(0,0);
        city_txt.setText(modelApp.getSite().getArea());

        //清除上一个城市数据
        clearLastSiteData();

        if (kanFangList != null){
            if (timerKF != null) timerKF.cancel();
            kanFangList.clear();
            fillKanFangData();
        }

        getDataFromNet();
        Toast.makeText(getActivity(), "正在切换站点...", Toast.LENGTH_SHORT).show();

        startNavigationTask();


        //百度推送设置tag
        if(currentSite != null){
            PushManager.delTags(getActivity().getApplicationContext(),
                    getTagsList(currentSite.getSiteId()));
        }
        currentSite = modelApp.getSite();
        PushManager.setTags(getActivity().getApplicationContext(),
                getTagsList(currentSite.getSiteId()));
    }



    @Override
    public void onStop() {
        super.onStop();
    }

    private void findViews() {
		home_search_bar = rootView.findViewById(R.id.home_search_bar);
		city_txt = (TextView) rootView.findViewById(R.id.city_txt);
		
		home_viewpager = (AutoScrollViewPager) rootView.findViewById(R.id.home_viewpager);
		home_point_lay = (LinearLayout) rootView.findViewById(R.id.home_point_lay);
		home_ad_lay = (LinearLayout) rootView.findViewById(R.id.home_ad_lay);
		ad_name_txt = (TextView) rootView.findViewById(R.id.ad_name_txt);
		
		home_grid = (ScrollGridView) rootView.findViewById(R.id.home_grid);

        kanfang_lay = (LinearLayout) rootView.findViewById(R.id.kanfang_lay);
        kanfang_txt = (UpMarqueeTextView) rootView.findViewById(R.id.kanfang_txt);

		content_scroll = (CustomScrollView) rootView.findViewById(R.id.content_scroll);
        scroll_top_iv = (ImageView) rootView.findViewById(R.id.scroll_top_iv);
		
		sale_lay = rootView.findViewById(R.id.sale_lay);
		home_sale_grid= (ScrollGridView) rootView.findViewById(R.id.home_sale_grid);
		sale_time_txt = (TextView) rootView.findViewById(R.id.sale_time_txt);
		
		news_grid = (ScrollGridView) rootView.findViewById(R.id.news_grid);

        house_like_more_lay = (LinearLayout) rootView.findViewById(R.id.house_like_more_lay);
        news_like_more_lay = (LinearLayout) rootView.findViewById(R.id.news_like_more_lay);
        house_like_listview = (ScrollListView) rootView.findViewById(R.id.house_like_listview);
		news_like_listview = (ScrollXListView) rootView.findViewById(R.id.news_like_listview);


        hq_lay = (LinearLayout) rootView.findViewById(R.id.hq_lay);
        hq_month_txt = (TextView) rootView.findViewById(R.id.hq_month_txt);
        hq_price_txt = (TextView) rootView.findViewById(R.id.hq_price_txt);
        hq_count_txt = (TextView) rootView.findViewById(R.id.hq_count_txt);
    }
	
	private void setListeners() {
		home_search_bar.setOnClickListener(this);
		city_txt.setOnClickListener(this);
		sale_time_txt.setOnClickListener(this);
        kanfang_txt.setOnClickListener(this);
        hq_lay.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);

        house_like_more_lay.setOnClickListener(this);
        news_like_more_lay.setOnClickListener(this);

		home_viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                ad_name_txt.setText(adList.get(arg0).getTitle());

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
		
		//猜你喜欢  资讯加载更多
		news_like_listview.setPullLoadEnable(true);
		news_like_listview.setPullRefreshEnable(false);
		news_like_listview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                if (!isLoading) {
                    startNewsListTask(currentPageIndex);
                }
            }
        }, R.id.news_like_listview);

        content_scroll.setOnBorderListener(new CustomScrollView.OnBorderListener() {
            @Override
            public void onBottom() {
                //可能会调用多次
                if (news_like_listview.getEnablePullLoad()) {
                    news_like_listview.startLoadMore();
                }
            }

            @Override
            public void onTop() {
            }

            @Override
            public void onHideBar() {
                if (scroll_top_iv.getVisibility() == View.VISIBLE) {
                    scroll_top_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onShowBar() {
                if (scroll_top_iv.getVisibility() == View.GONE) {
                    scroll_top_iv.setVisibility(View.VISIBLE);
                }
            }
        });


		sale_time_txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(houseList != null && houseList.size() > 0){
					Intent intent = new Intent(getActivity(), DiscountDetailActivity.class);
					Bundle data = new Bundle();
					data.putString("url", modelApp.getHuoDong()+"/"+houseList.get(0).getId()+"/" );
					intent.putExtras(data);
					getActivity().startActivity(intent);
				}
			}
		});
	}

	private Timer timer;
	private double startTime;	//活动开始时间
	private double endTime;		//活动开始时间
	private double nowTime;		//服务器当前时间
	
	private void fillSaleGridData() {
        if(houseList == null || houseList.size() ==0 ){
            sale_lay.setVisibility(View.GONE);
        }else{
            sale_lay.setVisibility(View.VISIBLE);
        }
		if (saleGridAdapter == null) {
			saleGridAdapter = new SaleGridAdapter(houseList, getActivity());
			home_sale_grid.setAdapter(saleGridAdapter);
		}else{
			saleGridAdapter.notifyDataSetChanged();
		}
	}
	
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			nowTime = nowTime + 1000;
            if(getActivity() == null) return;
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
						if(endTime < nowTime){
							sale_time_txt.setText("活动已结束！");
							sale_lay.setVisibility(View.GONE);
							
						}else if(startTime > nowTime) {
//							sale_time_txt.setText("活动未开始！");
							
							int time = (int) (startTime - nowTime);
							int day = time/(24*60*60*1000);
							int hour = (time - (day*24*60*60*1000))/(60*60*1000);
							int min = (time - (day*24*60*60*1000) - (hour*60*60*1000))/(60*1000);
							int second = (time - (day*24*60*60*1000) - (hour*60*60*1000) - (min*60*1000))/(1000);
							
							sale_lay.setVisibility(View.VISIBLE);
							sale_time_txt.setText("距离开始：  " + day+"天" + hour+"时" + min+"分" + second+"秒");
						}else {
							int time = (int) (endTime - nowTime);
							int day = time/(24*60*60*1000);
							int hour = (time - (day*24*60*60*1000))/(60*60*1000);
							int min = (time - (day*24*60*60*1000) - (hour*60*60*1000))/(60*1000);
							int second = (time - (day*24*60*60*1000) - (hour*60*60*1000) - (min*60*1000))/(1000);
							
							sale_lay.setVisibility(View.VISIBLE);
							sale_time_txt.setText("距离结束：  " + day+"天" + hour+"时" + min+"分" + second+"秒");
						}
					} catch (Exception e) {
						sale_time_txt.setText("距离结束：  0天00时00分00秒");
					}
				}
			});
			
		}
	}
	
	//轮询图
	private void fillAdData(){
		if(adList == null) return;

        if(adList.size() < 1){
            ad_name_txt.setText("");
            home_ad_lay.setVisibility(View.GONE);
        }else{
            ad_name_txt.setText(adList.get(0).getTitle());
            home_ad_lay.setVisibility(View.VISIBLE);
        }

		
		pointViews = new ArrayList<ImageView>();
		home_point_lay.removeAllViews();
		
		LayoutParams lps = new LayoutParams(DisplayUtil.dip2px(getActivity(), 6),
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
		
		List<View> views = new ArrayList<View>();
		for (int i = 0; i < adList.size(); i++) {
			ImageView image = new ImageView(getActivity());
			image.setScaleType(ScaleType.FIT_XY);
			views.add(image);
			ImageLoader.getInstance().displayImage(adList.get(i).getPhotoUrl(), image, options);
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					goADDetail();
				}
			});
		}

		ADPagerAdapter pagerAdapter = new ADPagerAdapter(views);
		home_viewpager.setAdapter(pagerAdapter);

//        home_viewpager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
		home_viewpager.setInterval(3000);
        home_viewpager.startAutoScroll();

	}
	
	private void goADDetail(){
        String url = adList.get(home_viewpager.getCurrentItem()).getNewsUrl();

        if(!StringUtil.isEmpty(url) && url.contains("/newhouse/")){
            String params[] = url.split("/");
            if(params != null && params.length == 5){
                Intent intent = new Intent(getActivity(), HouseDetailActivity.class);
                Bundle data = new Bundle();
                data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
                data.putString("projectId", params[params.length-1]);
                data.putString("projectName", "");
                intent.putExtras(data);
                startActivity(intent);
                return ;
            }
        }

        Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
        Bundle data = new Bundle();
        data.putString("url", adList.get(home_viewpager.getCurrentItem()).getNewsUrl());
        data.putString("isAd", NewsDetailActivity.AD_FLAG);
        intent.putExtras(data);
        getActivity().startActivity(intent);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.home_search_bar:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                getActivity().startActivity(searchIntent);
                break;

            case R.id.city_txt:
                Intent cityIntent = new Intent(getActivity(), CitySelectActivity.class);
                Bundle cityData = new Bundle();
                if(mAmapLocation == null){
                    cityData.putString("city", null);
                }else{
                    cityData.putString("city", mAmapLocation.getCity());
                }
                cityIntent.putExtras(cityData);
                getActivity().startActivity(cityIntent);
                break;

            case R.id.sale_time_txt:
                break;

            case R.id.custom_house_btn:
                startActivity(new Intent(getActivity(), CustomHouseListActivity.class));
                break;

            case R.id.kanfang_txt:
                if(kanFangList == null || kanFangList.size() == 0) return;
                String id = (String)kanfang_txt.getTag();
                String url = modelApp.getKanFang() + id + ".html";
                Intent intent = new Intent(getActivity(), KanFangDetailActivity.class);
                Bundle data = new Bundle();
                data.putString("url", url);
                intent.putExtras(data);
                getActivity().startActivity(intent);
                break;

            case R.id.scroll_top_iv:
                content_scroll.fullScroll(ScrollView.FOCUS_UP);
                break;

            case R.id.hq_lay:
                Intent fshqIntent = new Intent(getActivity(), FSHQDetailActivity.class);
                Bundle fshqData = new Bundle();
                fshqData.putString("url", fshq.getUrl());
                fshqIntent.putExtras(fshqData);
                getActivity().startActivity(fshqIntent);
                break;

            case R.id.house_like_more_lay:
                startActivity(new Intent(getActivity(), NewHouseListActivity.class));
                break;

            case R.id.news_like_more_lay:
                startActivity(new Intent(getActivity(), NewsIndexActivity.class));
                break;
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
	
	
	/********************************** 网络数据请求相关     *******************************************/
	
	//功能入口
	private void fillHomeGridData(){
		if (homeGridAdapter == null) {
			homeGridAdapter = new HomeGridAdapter(homeList, moreList, appList, getActivity());
			home_grid.setAdapter(homeGridAdapter);
		}else{
			homeGridAdapter.setData(homeList, moreList, appList);
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	//功能入口
	private void startNavigationTask() {
		if(NetUtil.detectAvailable(getActivity())){
            NavigationRequest navigationRequest = new NavigationRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                        return;
                    }

                    switch (message.what) {
                    case Constants.ERROR_DATA_FROM_NET:
                    case Constants.NO_DATA_FROM_NET:
                        if(homeList != null) homeList.clear();
                        if(moreList != null) moreList.clear();
                        if(appList != null) appList.clear();
                        fillHomeGridData();
                        break;

                    case Constants.SUCCESS_DATA_FROM_NET:
                        Bundle data = message.getData();
                        ArrayList<XKNavigation> tempHomeList = (ArrayList<XKNavigation>) data.getSerializable("home");
                        if(homeList != null){
                            homeList.clear();
                        }else{
                            homeList = new ArrayList<XKNavigation>();
                        }
                        homeList.addAll(tempHomeList);

                        ArrayList<XKNavigation> tempMoreList = (ArrayList<XKNavigation>) data.getSerializable("more");
                        if(moreList != null){
                            moreList.clear();
                        }else{
                            moreList = new ArrayList<XKNavigation>();
                        }
                        moreList.addAll(tempMoreList);

                        ArrayList<XKNavigation> tempAppList = (ArrayList<XKNavigation>) data.getSerializable("app");
                        if(appList != null){
                            appList.clear();
                        }else{
                            appList = new ArrayList<XKNavigation>();
                        }
                        appList.addAll(tempAppList);

                        fillHomeGridData();
                        break;
                    }
                }
            });
			navigationRequest.doRequest();
		}else{
			fillHomeGridData();
		}
	}
	
	
	//猜你喜欢（楼盘）
	private void fillHouseLikeData() {
		if(houseLikeList == null) return;
		
		if (houseLikeAdapter == null) {
			houseLikeAdapter = new HouseLikeAdapter(getActivity(), houseLikeList, startLatlng);
			house_like_listview.setAdapter(houseLikeAdapter);
		} else {
			houseLikeAdapter.setData(houseLikeList, startLatlng);
			houseLikeAdapter.notifyDataSetChanged();
		}
	}
	
	//猜你喜欢 （资讯）
	private void fillNewsLikeData() {
		if(newsLikeList == null) return;
		
		if (newsLikeAdapter == null) {
			newsLikeAdapter = new NewsLikeAdapter(getActivity(), newsLikeList);
			news_like_listview.setAdapter(newsLikeAdapter);
		} else {
			newsLikeAdapter.setData(newsLikeList);
			newsLikeAdapter.notifyDataSetChanged();
		}
	}
	
	//专题
	private void fillNewsThemeData() {
		if(themes == null) return;
		news_grid.setVisibility(View.VISIBLE);
		if (newsGridAdapter == null) {
			newsGridAdapter = new HomeNewsGridAdapter(themes, getActivity());
			news_grid.setAdapter(newsGridAdapter);
		} else {
			newsGridAdapter.setData(themes);
			newsGridAdapter.notifyDataSetChanged();
		}
	}

    //免费看房团
    private void fillKanFangData(){
        if (kanFangList == null || kanFangList.size() == 0) {
            kanfang_txt.setText("暂无看房团信息");
            kanfang_lay.setVisibility(View.GONE);
            return;
        }
        kanfang_lay.setVisibility(View.VISIBLE);
        if (kanFangList.size() == 1) {
            kanfang_txt.setTag(kanFangList.get(0).getId());
            kanfang_txt.setText(kanFangList.get(0).getTitle());
            return;
        }
        if(timerKF !=null){
            timerKF.cancel();
            timerKF= null;
        }
        timerKF = new Timer();
        newsIndex = 0;
        timerKF.schedule(new TimerTask() {
            @Override
            public void run() {
                if(getActivity() == null) return;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (newsIndex == kanFangList.size()) {
                            newsIndex = 0;
                        }
                        kanfang_txt.setText(kanFangList.get(newsIndex).getTitle());
                        kanfang_txt.setTag(kanFangList.get(newsIndex).getId());
                        newsIndex++;
                    }
                });
            }
        }, 0, 3 * 1000);

    }

		
	private RequestListener houseLikeListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (houseLikeList != null) {
					houseLikeList.clear();
					fillHouseLikeData();
				}
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<House> temp = (ArrayList<House>) message.getData().getSerializable("houseList");
				//最多显示6条数据
				if(temp != null && temp.size() > 10){
					int size = temp.size();
					for(int i = 10; i < size; i++){
						temp.remove(temp.size()-1);
					}
				}
				if (houseLikeList == null) {
					houseLikeList = new ArrayList<House>();
					houseLikeList.addAll(temp);
				} else {
					houseLikeList.clear();
					houseLikeList.addAll(temp);
				}
				fillHouseLikeData();
				house_like_more_lay.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	
	private RequestListener newsLikeListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (newsLikeList != null) {
					newsLikeList.clear();
					fillNewsLikeData();
				}
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<News> temp = (ArrayList<News>) message.getData().getSerializable("newsList");
				//根据返回的数据量判断是否隐藏加载更多
				if(temp.size() < pageSize || (newsLikeList != null && newsLikeList.size() >=30)){
					news_like_listview.setPullLoadEnable(false);
                    news_like_more_lay.setVisibility(View.VISIBLE);
				}else{
					news_like_listview.setPullLoadEnable(true);
				}
				
				//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
				if(isPullDown && newsLikeList != null){
					newsLikeList.clear();
					currentPageIndex = 1;
				}
				currentPageIndex++;
				newsLikeList.addAll(temp);
				fillNewsLikeData();
				break;
			}
			
			isPullDown = false;
			isLoading = false;
			news_like_listview.stopRefresh();
			news_like_listview.stopLoadMore();
		}
	};
	
	private RequestListener newsThemeListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				news_grid.setVisibility(View.GONE);
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (themes != null) {
					themes.clear();
					fillNewsThemeData();
				}
				news_grid.setVisibility(View.GONE);
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<XKAd> temp = (ArrayList<XKAd>) message.getData().getSerializable("adList");
				//最多显示4条数据
				if(temp != null && temp.size() > 4){
					int size = temp.size();
					for(int i = 4; i < size; i++){
						temp.remove(temp.size()-1);
					}
				}
				if (themes == null) {
					themes = new ArrayList<XKAd>();
					themes.addAll(temp);
				} else {
					themes.clear();
					themes.addAll(temp);
				}
				fillNewsThemeData();
				break;
			}
		}
	};
	
	private RequestListener hotActivityListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {

            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (houseList != null) {
					houseList.clear();
					fillSaleGridData();
				}
				if(timer != null) {
					timer.cancel();
					timer = null;
				}
				sale_time_txt.setText("距离结束：  0天00时00分00秒");
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				if(timer != null) {
					timer.cancel();
					timer = null;
				}
				Bundle data = message.getData();
				ArrayList<SaleHouse> temp = (ArrayList<SaleHouse>) data.getSerializable("houseList");
				startTime = data.getDouble("startTime");
				endTime = data.getDouble("endTime");
				nowTime = data.getDouble("nowTime");
				//最多显示4条数据
				if(temp != null && temp.size() > 2){
					int size = temp.size();
					for(int i = 2; i < size; i++){
						temp.remove(temp.size()-1);
					}
				}
				if (houseList == null) {
					houseList = new ArrayList<SaleHouse>();
					houseList.addAll(temp);
				} else {
					houseList.clear();
					houseList.addAll(temp);
				}
				fillSaleGridData();
				timer = new Timer();
				timer.schedule(new MyTimerTask(), 0, 1000);
				break;
			}
		}
	};
	
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


    private RequestListener kanFangListListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    break;

                case Constants.NO_DATA_FROM_NET:
                    if (kanFangList != null) {
                        kanFangList.clear();
                    }
                    fillKanFangData();
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    ArrayList<KanFang> temp = (ArrayList<KanFang>) message.getData().getSerializable("kanFangList");

                    if (kanFangList == null) {
                        kanFangList = new ArrayList<KanFang>();
                        kanFangList.addAll(temp);
                    } else {
                        kanFangList.clear();
                        kanFangList.addAll(temp);
                    }
                    fillKanFangData();
                    break;
            }
        }
    };

    private RequestListener fshqListListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    hq_lay.setVisibility(View.GONE);
                    break;

                case Constants.NO_DATA_FROM_NET:
                    hq_lay.setVisibility(View.GONE);
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:

                    fshq = (FSHQ) message.getData().getSerializable("fshq");
                    if(fshq != null){
                        hq_count_txt.setText(fshq.getCount());
                        hq_month_txt.setText(fshq.getMonth()+"月均价");
                        hq_price_txt.setText(fshq.getPrice());
                        hq_lay.setVisibility(View.VISIBLE);
                    }else{
                        hq_lay.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };


	private void getDataFromNet(){
		if (NetUtil.detectAvailable(getActivity())) {
			//猜你喜欢
            HouseLikeListRequest houseLikeListRequest = new HouseLikeListRequest(modelApp.getSite().getSiteId(), 10, 1, houseLikeListener);
			houseLikeListRequest.doRequest();
			
			isPullDown = true;
			currentPageIndex = 1;
			startNewsListTask(1);
			
			//新闻专题
            ADListRequest topicListRequest = new ADListRequest(modelApp.getSite().getSiteId(), "188", newsThemeListener);
			topicListRequest.doRequest();
			
			//热门活动
            HotActivityListRequest	hotActivityListRequest = new HotActivityListRequest(modelApp.getSite().getSiteId(), hotActivityListener);
			hotActivityListRequest.doRequest();
			
			//轮询图广告
            ADListRequest adListRequest = new ADListRequest(modelApp.getSite().getSiteId(), "187", adListListener);
			adListRequest.doRequest();

            //免费看房
            KanFangListRequest kanFangListRequest = new KanFangListRequest(modelApp.getSite().getSiteId(), kanFangListListener);
            kanFangListRequest.doRequest();

            //房市行情
            FSHQRequest fshqRequest = new FSHQRequest(modelApp.getSite().getSiteId(), fshqListListener);
            fshqRequest.doRequest();

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

        //功能入口
        NavigationRequest navigationRequest = new NavigationRequest();
        navigationRequest.parseResult(AppCache.readNavigationJson(modelApp.getSite().getSiteId()));
        homeList = navigationRequest.getHomeList();
        moreList = navigationRequest.getMoreList();
        appList = navigationRequest.getAppList();
        fillHomeGridData();

        //专题
        ADListRequest topicRequest = new ADListRequest();
        topicRequest.parseResult(AppCache.readHomeTopicJson(modelApp.getSite().getSiteId()));
        themes = topicRequest.getAdList();
        fillNewsThemeData();

        //猜你喜欢
        HouseLikeListRequest houseLikeListRequest = new HouseLikeListRequest();
        houseLikeListRequest.parseResult(AppCache.readHouseLikeJson(modelApp.getSite().getSiteId()));
        houseLikeList = houseLikeListRequest.getHouseList();
        fillHouseLikeData();

        NewsLikeListRequest newsLikeListRequest = new NewsLikeListRequest();
        newsLikeListRequest.parseResult(AppCache.readNewsLikeJson(modelApp.getSite().getSiteId()));
        newsLikeList = newsLikeListRequest.getNewsList();
        fillNewsLikeData();
    }

    //清除上个站点的数据
    private void clearLastSiteData(){
        if(adList != null){
            adList.clear();
            fillAdData();
        }
        if(homeList != null){
            homeList.clear();
            moreList.clear();
            appList.clear();
            fillHomeGridData();
        }

        if(themes != null){
            themes.clear();
            fillNewsThemeData();
        }

        if(houseLikeList != null){
            houseLikeList.clear();
            fillHouseLikeData();
        }

        if(newsLikeList != null){
            newsLikeList.clear();
            fillNewsLikeData();
        }

        if (houseList != null){
            houseList.clear();
            fillSaleGridData();
        }


    }


	private void startNewsListTask(int page){
		if(newsLikeList != null && newsLikeList.size() >=30){
			news_like_listview.setPullLoadEnable(false);
            news_like_more_lay.setVisibility(View.VISIBLE);
			return;
		}else{
			news_like_listview.setPullLoadEnable(true);
		}
		
		if (NetUtil.detectAvailable(getActivity())) {
            NewsLikeListRequest newsLikeListRequest = new NewsLikeListRequest(modelApp.getSite().getSiteId(),
						pageSize, page, newsLikeListener);
			isLoading = true;
			newsLikeListRequest.doRequest();
		}else {
			Toast.makeText(getActivity(), R.string.net_warn, Toast.LENGTH_SHORT).show();
			isPullDown = false;
			news_like_listview.stopRefresh();
			news_like_listview.stopLoadMore();
		}
	}
	
	
	
	private RequestListener siteListListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<Site> temp = (ArrayList<Site>) message.obj;
				siteDbService.insertSites(temp);
				break;
			}
		}
	};
	
	private void startSiteListTask() {
		ArrayList<Site> sites = siteDbService.getSiteList();
		if (sites != null && sites.size() > 0)  return;
		
		if (NetUtil.detectAvailable(getActivity())) {
			if (siteListRequest == null) {
				siteListRequest = new SiteListRequest(siteListListener);
			}
			siteListRequest.doRequest();
		} else {
			Toast.makeText(getActivity(), "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	


	/********************************** 定位相关回调   **********************************/
	@Override
	public void onLocationChanged(Location location) {}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}


	@Override
	public void onProviderEnabled(String provider) {}


	@Override
	public void onProviderDisabled(String provider) {}


	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(mAmapLocation != null){
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destroy();
			return;
		}
		
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			mAmapLocation = amapLocation;
			startLatlng = new LatLng(mAmapLocation.getLatitude(), mAmapLocation.getLongitude());
			Toast.makeText(getActivity(), "当前定位：" + amapLocation.getAddress(), Toast.LENGTH_SHORT).show();
            //TODO
            if(!amapLocation.getCity().equals(modelApp.getSite().getArea()+"市")){
                ArrayList<Site> sites = new SiteDbService().getSiteList();
                for(Site site : sites){
                    if (amapLocation.getCity().equals(site.getArea()+"市")){
                        showChangeSiteDialog(site);
                        break;
                    }
                }
            }
			fillHouseLikeData();
		}else {
			Logger.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
	}



    //切换站点对话框
    public void showChangeSiteDialog(final Site site) {
        String msg = "系统定位到您在" + site.getArea() + " ，需要切换至" + site.getArea() + "吗？";
        final ConfirmDialog confirmDialog = new ConfirmDialog(getActivity(), msg, "确定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                modelApp.setSite(site);
                changeSiteData();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }

	@Override
	public void onPause() {
		super.onPause();
		
		home_viewpager.stopAutoScroll();
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
	}
	
	
	 private List<String> getTagsList(String originalText) {
	        if (originalText == null || originalText.equals("")) {
	            return null;
	        }
	        List<String> tags = new ArrayList<String>();
	        int indexOfComma = originalText.indexOf(',');
	        String tag;
	        while (indexOfComma != -1) {
	            tag = originalText.substring(0, indexOfComma);
	            tags.add(tag);

	            originalText = originalText.substring(indexOfComma + 1);
	            indexOfComma = originalText.indexOf(',');
	        }

	        tags.add(originalText);
	        return tags;
	 }
}
