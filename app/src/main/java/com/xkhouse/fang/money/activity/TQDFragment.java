package com.xkhouse.fang.money.activity;

import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseFragment;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.adapter.BookedInfoAdapter;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.money.task.XKBHouseListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;

/**
 * 提前订
 */
public class TQDFragment extends AppBaseFragment implements OnClickListener, AMapLocationListener{
	
	private View rootView;


    private LinearLayout content_lay;   //显示的内容
    private ImageView blank_iv;         //没有数据视图
    private LinearLayout error_lay;         //加载失败，点击重新加载
    private RotateLoading rotate_loading;   //加载框

	//房源列表
	private XListView house_listView;
	private BookedInfoAdapter adapter;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 20; //每次请求20条数据
	private boolean isPullDown = false; // 下拉
	
	private XKBHouseListRequest houseListRequest;
	private ArrayList<House> houseList = new ArrayList<House>();
	
	private ModelApplication modelApp;
	private String siteID;
	
	private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
	private AMapLocation mAmapLocation = null;			//当前定位的位置
	private LatLng startLatlng; //当前经纬度
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		modelApp = (ModelApplication) getActivity().getApplication();
		siteID = modelApp.getSite().getSiteId();
		
		rootView = inflater.inflate(R.layout.fragment_money, container, false);
		findViews();
		setListeners();
		//开始定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
		
//		getNewHouseSearchTitleData();
		startHouseListTask(1, true);
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		

	}
	
	private void findViews() {
		initTitleView();
//		initSearchTitleView();
		initHouseListView();

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        blank_iv = (ImageView) rootView.findViewById(R.id.blank_iv);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);

    }

	private void initTitleView(){
	}


	
	private void initHouseListView() {
		house_listView = (XListView) rootView.findViewById(R.id.house_listView);
		house_listView.setPullLoadEnable(true);
		house_listView.setPullRefreshEnable(true);
		house_listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startHouseListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startHouseListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

	}
	
	
	private void setListeners() {
        error_lay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {



            case R.id.house_search_bar:
                // TODO go search activity
                break;



            case R.id.error_lay:
                startHouseListTask(1, true);

                break;

		}
	}
	
	

	
	
	


	

	
	
	
	
	/************************  列表数据   ***********************/
	private String _areaId; 
	private String _type; 
	private String _order; 
	
	private String getParams(){
		StringBuilder sb = new StringBuilder();
		
		if(!StringUtil.isEmpty(_areaId)){
			sb.append("&areaId=" + _areaId);
		}
		if(!StringUtil.isEmpty(_type)){
			sb.append("&type=" + _type);
		}
		if(!StringUtil.isEmpty(_order)){
			sb.append("&order=" + _order);
		}
		
		if(startLatlng != null){
			sb.append("&longitude=" + startLatlng.longitude);
			sb.append("&latitude=" + startLatlng.latitude);
		}
		return sb.toString();
	}

    //是否点击的筛选条件
    private boolean isSelectParam(){
        if(getParams().contains("&areaId=") || getParams().contains("&type=") ||
                getParams().contains("&order=") || getParams().contains("&k=")){
            return true;
        }else{
            return false;
        }
    }

	private void startHouseListTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(getActivity())) {
			if(houseListRequest == null){
				houseListRequest = new XKBHouseListRequest(modelApp.getSite().getSiteId(), page, pageSize,
						getParams(), new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {

						rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }

						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            if (houseList == null || houseList.size() < 1){
                                fillCacheData();
                            }else{
                                Toast.makeText(getContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.NO_DATA_FROM_NET:
                            error_lay.setVisibility(View.GONE);
                            if(!isSelectParam() && currentPageIndex == 1){
                                content_lay.setVisibility(View.GONE);
                                blank_iv.setVisibility(View.VISIBLE);
                            }else{
                                content_lay.setVisibility(View.VISIBLE);
                                if (currentPageIndex == 1){
                                    Toast.makeText(getActivity(), "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                                    house_listView.setVisibility(View.INVISIBLE);
                                }
                            }
							break;

						case Constants.SUCCESS_DATA_FROM_NET:
                            content_lay.setVisibility(View.VISIBLE);
                            blank_iv.setVisibility(View.GONE);
                            error_lay.setVisibility(View.GONE);

							ArrayList<House> temp = (ArrayList<House>) message.obj;
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								house_listView.setPullLoadEnable(false);
							}else{
								house_listView.setPullLoadEnable(true);
							}
							
							//如果是下拉刷新则索引恢复到1，并且清除掉之前数据
							if(isPullDown && houseList != null){
								houseList.clear();
								currentPageIndex = 1;
							}
							houseList.addAll(temp);
							fillHouseData();
                            currentPageIndex++;
							break;
						}
						isPullDown = false;
						house_listView.stopRefresh();
						house_listView.stopLoadMore();
					}
				});
			}else {
				houseListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, getParams());
			}
			if (showLoading) {
                content_lay.setVisibility(View.GONE);
                blank_iv.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
			houseListRequest.doRequest();

		}else {

			isPullDown = false;
			house_listView.stopRefresh();
			house_listView.stopLoadMore();

            if (houseList == null || houseList.size() < 1){
                fillCacheData();
            }else{
                Toast.makeText(getContext(), R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
		}
	}
	
	
	private void fillHouseData() {
		if(houseList == null) return;
        house_listView.setVisibility(View.VISIBLE);
		if(adapter == null){
//			adapter = new XKBHouseAdapter(getActivity(), houseList, startLatlng);
            adapter = new BookedInfoAdapter(getActivity());
			house_listView.setAdapter(adapter);
		}else{
//			adapter.setData(houseList, startLatlng);
		}
	}


    public void fillCacheData(){
        XKBHouseListRequest request = new XKBHouseListRequest();
        request.parseResult(AppCache.readMoneyHouseListJson(modelApp.getSite().getSiteId()));
        houseList = request.getHouseList();

        if (houseList == null || houseList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillHouseData();
        }

    }




	/******************** 定位相关回调   ********************/
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
			fillHouseData();
		}else {
			Logger.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
		}
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mLocationManagerProxy.removeUpdates(this);
		mLocationManagerProxy.destroy();
	}
	



}
