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
import com.xkhouse.fang.app.adapter.HomeLuckAdapter;
import com.xkhouse.fang.app.adapter.BookedInfoAdapter;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Banner;
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.app.entity.LuckInfo;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.entity.TQDad;
import com.xkhouse.fang.app.service.SiteDbService;
import com.xkhouse.fang.app.task.BannerListRequest;
import com.xkhouse.fang.app.task.BookedInfoListRequest;
import com.xkhouse.fang.app.task.HouseLikeListRequest;
import com.xkhouse.fang.app.task.LuckInfoListRequest;
import com.xkhouse.fang.app.task.SiteListRequest;
import com.xkhouse.fang.app.task.TQDadRequest;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.activity.CustomHouseListActivity;
import com.xkhouse.fang.house.activity.HouseDetailActivity;
import com.xkhouse.fang.house.activity.MapHousesActivity;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.fang.widget.CustomScrollView;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.fang.widget.ScrollXListView;
import com.xkhouse.fang.widget.autoscrollviewpager.AutoScrollViewPager;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 *
 */
public class HomeFragment extends AppBaseFragment implements OnClickListener, AMapLocationListener{
	
	
	private View rootView;
	//title bar
	private TextView city_txt;
	
	private CustomScrollView content_scroll;
    private ImageView scroll_top_iv;
	
	//轮询图
	private AutoScrollViewPager home_viewpager;
	private LinearLayout home_point_lay;
	private ArrayList<Banner> bannerList;
	private List<ImageView> pointViews;

	//广告图片
	private ImageView home_ad_img;
	private TQDad ad;


	//最新抽奖
	private ScrollGridView luck_gridview;
	private LuckInfoListRequest luckInfoListRequest;
    private ArrayList<LuckInfo> luckList;
    private HomeLuckAdapter luckAdapter;

    //预定推荐
	private ScrollXListView bookInfo_recommed_listview;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 5; //每次请求10条数据
	private boolean isPullDown = false; // 下拉
	private boolean isLoading = false; //是否正在加载数据
	private ArrayList<BookedInfo> bookedInfoList = new ArrayList<>();
	private BookedInfoAdapter bookedInfoAdapter;

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

        //滚动到顶部
        bookInfo_recommed_listview.setFocusable(false);
        luck_gridview.setFocusable(false);

        //开始定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
		
		city_txt.setText(modelApp.getSite().getArea());

        getDataFromLocal();

		getDataFromNet();

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

        content_scroll.scrollTo(0,0);
        city_txt.setText(modelApp.getSite().getArea());

        //清除上一个城市数据
        clearLastSiteData();

        getDataFromNet();
        Toast.makeText(getActivity(), "正在切换站点...", Toast.LENGTH_SHORT).show();

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
		city_txt = (TextView) rootView.findViewById(R.id.city_txt);
		
		home_viewpager = (AutoScrollViewPager) rootView.findViewById(R.id.home_viewpager);
		home_point_lay = (LinearLayout) rootView.findViewById(R.id.home_point_lay);

		content_scroll = (CustomScrollView) rootView.findViewById(R.id.content_scroll);
        scroll_top_iv = (ImageView) rootView.findViewById(R.id.scroll_top_iv);

        luck_gridview = (ScrollGridView) rootView.findViewById(R.id.luck_gridview);
		bookInfo_recommed_listview = (ScrollXListView) rootView.findViewById(R.id.bookInfo_recommed_listview);

		home_ad_img = (ImageView) rootView.findViewById(R.id.home_ad_img);
	}
	
	private void setListeners() {
		city_txt.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);



		home_viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                for (int i = 0; i < bannerList.size(); i++) {
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
		bookInfo_recommed_listview.setPullLoadEnable(true);
		bookInfo_recommed_listview.setPullRefreshEnable(false);
		bookInfo_recommed_listview.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                if (!isLoading) {
                    startBookedInfoListTask(currentPageIndex);
                }
            }
        }, R.id.bookInfo_recommed_listview);

        content_scroll.setOnBorderListener(new CustomScrollView.OnBorderListener() {
            @Override
            public void onBottom() {
                //可能会调用多次
                if (bookInfo_recommed_listview.getEnablePullLoad()) {
                    bookInfo_recommed_listview.startLoadMore();
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



	}


	

	private void fillAdData() {
		if (ad == null) return;
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nopic)   // 加载的图片
				.showImageOnFail(R.drawable.nopic) // 错误的时候的图片
				.showImageForEmptyUri(R.drawable.nopic)
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
				.cacheOnDisk(true).build();
		ImageLoader.getInstance().displayImage(ad.getImgurl(), home_ad_img, options);
	}
	
	//轮询图
	private void fillBannerData(){
		if(bannerList == null) return;

        if(bannerList.size() < 1){
			home_point_lay.setVisibility(View.GONE);
        }else{
			home_point_lay.setVisibility(View.VISIBLE);
        }

		pointViews = new ArrayList<>();
		home_point_lay.removeAllViews();
		
		LayoutParams lps = new LayoutParams(DisplayUtil.dip2px(getActivity(), 6),
				DisplayUtil.dip2px(getActivity(), 6));
		lps.leftMargin = DisplayUtil.dip2px(getActivity(), 3);
		for(int i = 0; i < bannerList.size(); i++){
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
		for (int i = 0; i < bannerList.size(); i++) {
			ImageView image = new ImageView(getActivity());
			image.setScaleType(ScaleType.FIT_XY);
			views.add(image);
			ImageLoader.getInstance().displayImage(bannerList.get(i).getImgurl(), image, options);
			image.setOnClickListener(new OnClickListener() {
				
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
        String url = bannerList.get(home_viewpager.getCurrentItem()).getLink();

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
        data.putString("url", bannerList.get(home_viewpager.getCurrentItem()).getLink());
        data.putString("isAd", NewsDetailActivity.AD_FLAG);
        intent.putExtras(data);
        getActivity().startActivity(intent);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {


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



            case R.id.custom_house_btn:
                startActivity(new Intent(getActivity(), CustomHouseListActivity.class));
                break;

            case R.id.scroll_top_iv:
                content_scroll.fullScroll(ScrollView.FOCUS_UP);
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
	


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}


	
	
	//最新活动
	private void fillLuckData() {
		if(luckList == null) return;
		
		if (luckAdapter == null) {
			luckAdapter = new HomeLuckAdapter(getActivity(), luckList, startLatlng);
			luck_gridview.setAdapter(luckAdapter);
		} else {
			luckAdapter.setData(luckList, startLatlng);
			luckAdapter.notifyDataSetChanged();
		}
	}
	
	//推荐预定
	private void fillRecommedData() {
		if(bookedInfoList == null) return;
		
		if (bookedInfoAdapter == null) {
			bookedInfoAdapter = new BookedInfoAdapter(getActivity(), bookedInfoList);
			bookInfo_recommed_listview.setAdapter(bookedInfoAdapter);
		} else {
			bookedInfoAdapter.setData(bookedInfoList);
			bookedInfoAdapter.notifyDataSetChanged();
		}
	}
	




		
	private RequestListener luckListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (luckList != null) {
					luckList.clear();
					fillLuckData();
				}
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<LuckInfo> temp = (ArrayList<LuckInfo>) message.getData().getSerializable("luckInfoList");
				//最多显示6条数据
				if(temp != null && temp.size() > 10){
					int size = temp.size();
					for(int i = 10; i < size; i++){
						temp.remove(temp.size()-1);
					}
				}
				if (luckList == null) {
					luckList = new ArrayList<LuckInfo>();
					luckList.addAll(temp);
				} else {
					luckList.clear();
					luckList.addAll(temp);
				}
				fillLuckData();

				break;
			}
		}
	};
	
	private RequestListener bookedInfoListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (bookedInfoList != null) {
					bookedInfoList.clear();
					fillRecommedData();
				}
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<BookedInfo> temp = (ArrayList<BookedInfo>) message.getData().getSerializable("bookedInfoList");
                if(temp.size() < pageSize){
                    bookInfo_recommed_listview.setPullLoadEnable(false);
                }else{
                    bookInfo_recommed_listview.setPullLoadEnable(true);
                }
				//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
				if(isPullDown && bookedInfoList != null){
					bookedInfoList.clear();
					currentPageIndex = 1;
				}
				currentPageIndex++;
				bookedInfoList.addAll(temp);
				fillRecommedData();
				break;
			}
			
			isPullDown = false;
			isLoading = false;
			bookInfo_recommed_listview.stopRefresh();
			bookInfo_recommed_listview.stopLoadMore();
		}
	};
	

	

	private RequestListener bannerListListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {

            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET: 
				break;
				
			case Constants.NO_DATA_FROM_NET:
				if (bannerList != null) {
					bannerList.clear();
					fillBannerData();
				}
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<Banner> temp = (ArrayList<Banner>) message.getData().getSerializable("bannerList");
				
				if (bannerList == null) {
					bannerList = new ArrayList<Banner>();
					bannerList.addAll(temp);
				} else {
					bannerList.clear();
					bannerList.addAll(temp);
				}
				fillBannerData();
				break;
			}
		}
	};

	private RequestListener adListener = new RequestListener() {

		@Override
		public void sendMessage(Message message) {

			if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
				return;
			}

			switch (message.what) {
				case Constants.ERROR_DATA_FROM_NET:
					break;

				case Constants.NO_DATA_FROM_NET:
					ad = new TQDad();
					fillAdData();
					break;

				case Constants.SUCCESS_DATA_FROM_NET:
					ad = (TQDad) message.getData().getSerializable("ad");
					fillAdData();
					break;
			}
		}
	};

	private void getDataFromNet(){
		if (NetUtil.detectAvailable(getActivity())) {
			//最新抽奖
            if (luckInfoListRequest == null) {
                luckInfoListRequest = new LuckInfoListRequest(modelApp.getSite().getSiteId(), 10, 1, luckListener);
                luckInfoListRequest.doRequest();
            }
            luckInfoListRequest.doRequest();


			//推荐预定
			isPullDown = true;
			currentPageIndex = 1;
			startBookedInfoListTask(1);

			//轮询图广告
            BannerListRequest bannerListRequest = new BannerListRequest(modelApp.getSite().getSiteId(), bannerListListener);
			bannerListRequest.doRequest();

			TQDadRequest adRequest = new TQDadRequest(modelApp.getSite().getSiteId(), "1", adListener);
			adRequest.doRequest();

		} else {
			Toast.makeText(getActivity(), R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

    /****************************** 获取本地缓存数据  ******************************/
	private void getDataFromLocal(){
        //轮询图广告
        BannerListRequest bannerListRequest = new BannerListRequest();
        bannerListRequest.parseResult(AppCache.readHomeBannerJson(modelApp.getSite().getSiteId()));
        bannerList = bannerListRequest.getBannerList();
        fillBannerData();

		//广告图片
		TQDadRequest adRequest = new TQDadRequest();
		adRequest.parseResult(AppCache.readIndexAdJson(modelApp.getSite().getSiteId()));
		ad = adRequest.getAd();
		fillAdData();

        //猜你喜欢
        LuckInfoListRequest luckListRequest = new LuckInfoListRequest();
        luckListRequest.parseResult(AppCache.readHomeLuckJson(modelApp.getSite().getSiteId()));
        luckList = luckListRequest.getLuckInfoList();
        fillLuckData();

        BookedInfoListRequest bookedInfoListRequest = new BookedInfoListRequest();
        bookedInfoListRequest.parseResult(AppCache.readBookInfoRecommedJson(modelApp.getSite().getSiteId()));
        bookedInfoList = bookedInfoListRequest.getBookedInfoList();
        fillRecommedData();
    }

    //清除上个站点的数据
    private void clearLastSiteData(){
        if(bannerList != null){
            bannerList.clear();
            fillBannerData();
        }

        if(luckList != null){
            luckList.clear();
            fillLuckData();
        }

        if(bookedInfoList != null){
            bookedInfoList.clear();
            fillRecommedData();
        }
    }


    //预定推荐
	private void startBookedInfoListTask(int page){

		if (NetUtil.detectAvailable(getActivity())) {
            BookedInfoListRequest bookedInfoListRequest = new BookedInfoListRequest(BookedInfoListRequest.LIST_RECOMMED,
                    modelApp.getSite().getSiteId(),
                    pageSize, page, bookedInfoListener);
			isLoading = true;
			bookedInfoListRequest.doRequest();
		}else {
			Toast.makeText(getActivity(), R.string.net_warn, Toast.LENGTH_SHORT).show();
			isPullDown = false;
			bookInfo_recommed_listview.stopRefresh();
			bookInfo_recommed_listview.stopLoadMore();
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
			fillLuckData();
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
