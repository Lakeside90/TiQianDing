package com.xkhouse.fang.money.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.adapter.CityListAdapter;
import com.xkhouse.fang.app.adapter.CityListAdapter.SiteSelectListener;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.service.SiteDbService;
import com.xkhouse.fang.app.task.SiteListRequest;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.widget.pinyin.AssortView;
import com.xkhouse.fang.widget.pinyin.AssortView.OnTouchAssortListener;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
* @Description: 推荐客户（以及其他页面选择城市） 城市选择页
* @author wujian  
* @date 2015-9-9 下午8:28:59
 */
public class XKBCitySelectActivity extends AppBaseActivity implements AMapLocationListener{

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	//快速选择城市
	private EditText city_search_txt;
	private ImageView city_search_iv;	
	
	private LinearLayout city_locate_lay;
	private TextView city_locate_txt;	//当前定位的城市
	private ImageView city_locate_iv;	//重新定位
	
	private ExpandableListView eListView;
	private AssortView assortView;
	private CityListAdapter adapter;
	private ArrayList<Site> sites;
	private SiteListRequest siteListRequest;
	private SiteDbService siteDbService;
	
	private String locationCity;
	private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类

    private String activityType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sites = siteDbService.getSiteList();
        if(sites == null || sites.size() == 0){
            startSiteListTask();
        } else {
            fillData();
        }
    }

    @Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_xkb_city_select);
	}
	
	@Override
	protected void init() {
		super.init();
		
		siteDbService = new SiteDbService();
		locationCity = getIntent().getExtras().getString("city");
        activityType = getIntent().getExtras().getString("activityType");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		initSiteListView();
		
		city_search_txt = (EditText) findViewById(R.id.city_search_txt);
		city_search_iv = (ImageView) findViewById(R.id.city_search_iv);
		
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("选择城市");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				closeSoftInput();
				//解決黑屏問題
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
	}
	
	private void initSiteListView() {
		// header view 
		View headerView = LayoutInflater.from(mContext).inflate(R.layout.view_site_list_header, null);
		city_locate_txt = (TextView) headerView.findViewById(R.id.city_locate_txt);
		city_locate_iv = (ImageView) headerView.findViewById(R.id.city_locate_iv);
		city_locate_lay = (LinearLayout) headerView.findViewById(R.id.city_locate_lay);
		
		if(StringUtil.isEmpty(locationCity)){
			city_locate_txt.setText("定位失败");
		}else{
			city_locate_txt.setText(locationCity.replaceAll("市", ""));
		}
		
		
		eListView = (ExpandableListView) findViewById(R.id.elist);
		assortView = (AssortView) findViewById(R.id.assort);
		
		eListView.addHeaderView(headerView);
		//点击group 不收缩
		eListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
	}
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		city_search_iv.setOnClickListener(this);
		city_locate_iv.setOnClickListener(this);
		city_locate_lay.setOnClickListener(this);
		
		// 字母按键回调
		assortView.setOnTouchAssortListener(new OnTouchAssortListener() {

			View layoutView = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_pinyin_menu, null);
			TextView text = (TextView) layoutView.findViewById(R.id.content);
			PopupWindow popupWindow;

			public void onTouchAssortListener(String str) {
				if(adapter == null || adapter.getAssort() == null 
						|| adapter.getAssort().getHashList() == null) return;
				
				int index = adapter.getAssort().getHashList().indexOfKey(str);
				if (index != -1) {
					eListView.setSelectedGroup(index);
				}
				if (popupWindow != null) {
					text.setText(str);
				} else {
					popupWindow = new PopupWindow(layoutView, DisplayUtil.dip2px(mContext, 50),
							DisplayUtil.dip2px(mContext, 50), false);
					// 显示在Activity的根视图中心
					popupWindow.showAtLocation(((Activity) mContext).getWindow()
							.getDecorView(), Gravity.CENTER, 0, 0);
				}
				text.setText(str);
			}

			public void onTouchAssortUP() {
				if (popupWindow != null) popupWindow.dismiss();
				popupWindow = null;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) { 
		case R.id.city_search_iv:
			fastSelectSite();
			break;
			
		case R.id.city_locate_lay:
			selectSiteByLocate();
			break;
			
		case R.id.city_locate_iv:
			startLocate();
			break;
		}
	}
	
	private void fillData() {
		if (sites == null) return;
		if(adapter == null){
			adapter = new CityListAdapter(mContext, sites, new SiteSelectListener() {
				
				@Override
				public void onSelected(String name) {
					Site site = getSiteByName(name.trim());
					if(site == null){
						Toast.makeText(mContext, "暂时还未覆盖此城市", Toast.LENGTH_SHORT).show();
					} else {
						closeSoftInput();
						commitCity(site);
					}
				}
			});
			eListView.setAdapter(adapter);
		} else {
			adapter.setData(sites);
			adapter.notifyDataSetChanged();
		}

		// 展开所有
		for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
			eListView.expandGroup(i);
		}
	}
	
	private void fastSelectSite() {
		String name = city_search_txt.getText().toString();
		if(StringUtil.isEmpty(name)) return;
		
		Site site = getSiteByName(name.trim());
		if(site == null){
			Toast.makeText(mContext, "暂时还未覆盖此城市", Toast.LENGTH_SHORT).show();
		} else {
			closeSoftInput();
			commitCity(site);
		}
	}
	
	private void selectSiteByLocate() {
		if(StringUtil.isEmpty(locationCity)){
			Toast.makeText(mContext, "定位失败", Toast.LENGTH_SHORT).show();
			return;
		}
		Site site = getSiteByName(locationCity.replaceAll("市", ""));
		if(site == null){
			Toast.makeText(mContext, "暂时还未覆盖此城市", Toast.LENGTH_SHORT).show();
		} else {
			commitCity(site);
		}
	}
	
	private Site getSiteByName(String name){
		for (Site site : sites) {
			if (name.equals(site.getArea())) {
				return site;
			}
		}
		return null;
	}
	
	/******************************  网络请求相关  ******************************/
	private RequestListener siteListListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				break;
				
			case Constants.NO_DATA_FROM_NET:
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<Site> temp = (ArrayList<Site>) message.obj;
				siteDbService.insertSites(temp);
				
				if (sites == null) {
					sites = new ArrayList<Site>();
					sites.addAll(temp);
				} else {
					sites.clear();
					sites.addAll(temp);
				}
				fillData();
				break;
			}
		}
	};
	
	private void startSiteListTask() {
		if (NetUtil.detectAvailable(mContext)) {
			if (siteListRequest == null) {
				siteListRequest = new SiteListRequest(siteListListener);
			}
			showLoadingDialog(R.string.data_loading);
			siteListRequest.doRequest();
		} else {
			Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	/******************** 定位相关回调   ********************/
	private void startLocate() {
		//开始定位
		RotateAnimation roAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		roAnimation.setRepeatCount(-1);
		roAnimation.setDuration(1000);
		city_locate_iv.startAnimation(roAnimation);
		
		city_locate_txt.setText("正在定位..");
		mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
	}
	
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
		
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			Toast.makeText(mContext, "当前定位：" + amapLocation.getAddress(), Toast.LENGTH_SHORT).show();
			locationCity = amapLocation.getCity();
			city_locate_txt.setText(locationCity.replaceAll("市", ""));
		}else {
			city_locate_txt.setText("定位失败");
		}
		city_locate_iv.clearAnimation();
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
	}
	

	
	
	private void commitCity(Site site) {

		Intent intent = new Intent();
		Bundle data = new Bundle();
		data.putSerializable("site", site);
		intent.putExtras(data);
        if(!StringUtil.isEmpty(activityType) && activityType.equals("XKD")){
            setResult(CustomerAddActivity.RESULT_CODE_CITY, intent);
        }else{
            setResult(CustomerAddActivity.RESULT_CODE_CITY, intent);
        }

		finish();
	}
}
