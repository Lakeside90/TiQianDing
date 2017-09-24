package com.xkhouse.fang.money.activity;

import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.xkhouse.fang.app.entity.BookedInfo;
import com.xkhouse.fang.app.task.BookedInfoListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;

import java.util.ArrayList;

/**
 * 提前订
 */
public class TQDFragment extends AppBaseFragment implements OnClickListener, AMapLocationListener{
	
	private View rootView;

    private TextView search_bar;

    private LinearLayout content_lay;   //显示的内容
    private LinearLayout error_lay;         //加载失败，点击重新加载
    private RotateLoading rotate_loading;   //加载框


	//列表
	private XListView listView;
	private BookedInfoAdapter adapter;
    private ArrayList<BookedInfo> bookedInfoList = new ArrayList<>();
    private String isRecommend = BookedInfoListRequest.LIST_ALL;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 20; //每次请求20条数据
	private boolean isPullDown = false; // 下拉

    private BookedInfoListRequest request;


	private ModelApplication modelApp;

	private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
	private AMapLocation mAmapLocation = null;			//当前定位的位置
	private LatLng startLatlng; //当前经纬度
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		modelApp = (ModelApplication) getActivity().getApplication();

		rootView = inflater.inflate(R.layout.fragment_tqd, container, false);

		findViews();
		setListeners();

		//开始定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);

//        fillCacheData();

		startListTask(1, true);
		
		return rootView;
	}


	private void findViews() {
		initTitleView();

		initListView();

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);

    }

	private void initTitleView(){
        search_bar = (TextView) rootView.findViewById(R.id.search_bar);
        search_bar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO go search
            }
        });
    }


	
	private void initListView() {
		listView = (XListView) rootView.findViewById(R.id.listView);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(true);
		listView.setXListViewListener(new IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startListTask(currentPageIndex, false);
            }
        }, R.id.listView);

	}
	
	
	private void setListeners() {
        error_lay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.error_lay:
                startListTask(1, true);
                break;
		}
	}
	
	

	
	private void fillData() {
        if(bookedInfoList == null) return;
        if(adapter == null){
            adapter = new BookedInfoAdapter(getContext(), bookedInfoList);
            listView.setAdapter(adapter);
        }else {
            adapter.setData(bookedInfoList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载缓存
     */
    public void fillCacheData(){
        if (request == null) {
            request = new BookedInfoListRequest();
        }
        request.parseResult(AppCache.readBookInfoListJson(modelApp.getSite().getSiteId()));
        bookedInfoList = request.getBookedInfoList();

        if (bookedInfoList == null || bookedInfoList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillData();
        }

    }

	
	
	
	/************************  列表数据   ***********************/

    private void startListTask(int page, boolean showLoading){

        if (NetUtil.detectAvailable(getActivity())) {

            if (request == null) {
                request = new BookedInfoListRequest(isRecommend,
                        modelApp.getSite().getSiteId(),
                        pageSize, page, bookedInfoListener);
            } else {
                request.setData(isRecommend, modelApp.getSite().getSiteId(), pageSize, page, bookedInfoListener);
            }

            if (showLoading) {
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }

            request.doRequest();

        }else {

            isPullDown = false;
            listView.stopRefresh();
            listView.stopLoadMore();

            if (bookedInfoList == null || bookedInfoList.size() ==0 ){
                content_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.GONE);
                error_lay.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(getContext(), R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private RequestListener bookedInfoListener = new RequestListener() {

        @Override
        public void sendMessage(Message message) {

            rotate_loading.stop();
            rotate_loading.setVisibility(View.GONE);
            if (isPullDown){
                currentPageIndex = 1;
            }

            if(!modelApp.getSite().getSiteId().equals(message.getData().getString("siteId"))){
                return;
            }

            switch (message.what) {
                case Constants.ERROR_DATA_FROM_NET:
                    if (bookedInfoList == null || bookedInfoList.size() == 0){
                        content_lay.setVisibility(View.GONE);
                        error_lay.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(getContext(), R.string.service_error, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case Constants.NO_DATA_FROM_NET:
                    error_lay.setVisibility(View.GONE);
                    content_lay.setVisibility(View.VISIBLE);

                    if (bookedInfoList != null) {
                        bookedInfoList.clear();
                        fillData();
                    }
                    break;

                case Constants.SUCCESS_DATA_FROM_NET:
                    content_lay.setVisibility(View.VISIBLE);
                    error_lay.setVisibility(View.GONE);

                    ArrayList<BookedInfo> temp = (ArrayList<BookedInfo>) message.getData().getSerializable("bookedInfoList");
                    //根据返回的数据量判断是否隐藏加载更多
                    if(temp.size() < pageSize){
                        listView.setPullLoadEnable(false);
                    }else{
                        listView.setPullLoadEnable(true);
                    }
                    //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                    if(isPullDown && bookedInfoList != null){
                        bookedInfoList.clear();
                        currentPageIndex = 1;
                    }
                    currentPageIndex++;
                    bookedInfoList.addAll(temp);
                    fillData();
                    break;
            }

            isPullDown = false;
            listView.stopRefresh();
            listView.stopLoadMore();
        }
    };







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
			fillData();
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
