package com.xkhouse.fang.house.activity;

import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.entity.XKAd;
import com.xkhouse.fang.house.adapter.NewHouseAdapter;
import com.xkhouse.fang.house.task.NewHouseListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;


import java.util.ArrayList;


/**
 * 最新开盘
 *  wujian 2016/8/3
 */
public class NewHouseRecentListActivity extends AppBaseActivity implements AMapLocationListener {


    private ImageView iv_head_left;
    private TextView tv_head_title;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    //列表
    private XListView house_listView;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 20; //每次请求20条数据
    private boolean isPullDown = false; // 下拉
    private NewHouseAdapter newHouseAdapter;
    private ArrayList<House> newHouseList = new ArrayList<House>();
    private ArrayList<XKAd> adList = new ArrayList<XKAd>();
    private int adIndex;        //在第几条数据后面插广告

    private NewHouseListRequest newHouseListRequest;


    private ImageView scroll_top_iv;


    //定位
    private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
    private AMapLocation mAmapLocation = null;			//当前定位的位置
    private LatLng startLatlng; //当前经纬度






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startNewHouseListTask(1, true);

    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_new_house_recent_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开始定位
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(false);
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, this);
    }

    @Override
    protected void init() {
        super.init();
    }


    @Override
    protected void findViews() {
        super.findViews();

        initTitleView();

        house_listView = (XListView) findViewById(R.id.house_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);

    }

    private void initTitleView(){
        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("最新开盘");
        iv_head_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewHouseRecentListActivity.this.finish();
            }
        });
    }

    @Override
    protected void setListeners() {
        super.setListeners();

        scroll_top_iv.setOnClickListener(this);
        error_lay.setOnClickListener(this);


        house_listView.setPullLoadEnable(true);
        house_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {

                startNewHouseListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        house_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滑到底部自动加载下一页数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && house_listView.getEnablePullLoad()) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1
                            && !house_listView.getPullLoading()) {
                        house_listView.startLoadMore();
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 5) {
                    if (scroll_top_iv.getVisibility() == View.GONE) {
                        scroll_top_iv.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (scroll_top_iv.getVisibility() == View.VISIBLE) {
                        scroll_top_iv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }





    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {

            case R.id.scroll_top_iv:
                house_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        house_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startNewHouseListTask(1, true);
                break;
        }

    }




    private void startNewHouseListTask(int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(newHouseListRequest == null){
                newHouseListRequest = new NewHouseListRequest(modelApp.getSite().getSiteId(), page, pageSize,
                        "&new=1", new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {

                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);
                        if (isPullDown){
                            currentPageIndex = 1;
                        }
                        switch (message.what) {

                            case Constants.ERROR_DATA_FROM_NET:
                                if (newHouseList == null || newHouseList.size() == 0){
                                    fillCacheData();
                                }else{
                                    error_lay.setVisibility(View.VISIBLE);
                                    content_lay.setVisibility(View.GONE);
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                content_lay.setVisibility(View.VISIBLE);
                                if (currentPageIndex == 1){
                                    Toast.makeText(mContext, "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                                    if(newHouseList != null){
                                        newHouseList.clear();
                                        fillNewHouseData();
                                    }
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);

                                if(currentPageIndex == 1){
                                    adList = (ArrayList<XKAd>) message.getData().getSerializable("adList");
                                    adIndex = message.getData().getInt("adIndex");
                                }
                                ArrayList<House> temp = (ArrayList<House>) message.obj;
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    house_listView.setPullLoadEnable(false);
                                }else{
                                    house_listView.setPullLoadEnable(true);
                                }

                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && newHouseList != null){
                                    if(newHouseList.size() > 0){
                                        house_listView.smoothScrollToPosition(0);
                                    }
                                    newHouseList.clear();
                                    currentPageIndex = 1;
                                }
                                newHouseList.addAll(temp);
                                fillNewHouseData();
                                if (currentPageIndex > 1 && message.arg1 == newHouseList.size()){
                                    Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                                }
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        house_listView.stopRefresh();
                        house_listView.stopLoadMore();
                    }
                });
            }else {
                newHouseListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, "&new=1");
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            newHouseListRequest.doRequest();

        }else {
            isPullDown = false;
            house_listView.stopRefresh();
            house_listView.stopLoadMore();

            if (newHouseList == null || newHouseList.size() == 0){
                fillCacheData();
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void fillNewHouseData() {
        if(newHouseList == null) return;
        if(newHouseAdapter == null){
            newHouseAdapter = new NewHouseAdapter(mContext, newHouseList, adList, adIndex, startLatlng);
            house_listView.setAdapter(newHouseAdapter);
        }else {
            newHouseAdapter.setData(newHouseList, adList, adIndex, startLatlng);
        }
    }

    public void fillCacheData(){
        NewHouseListRequest request = new NewHouseListRequest();
        request.parseResult(AppCache.readNewHouseNListJson(modelApp.getSite().getSiteId()));
        Bundle data = request.getCacheModel();
        newHouseList = (ArrayList<House>) data.getSerializable("houseList");
        adIndex = data.getInt("adIndex");
        adList = (ArrayList<XKAd>) data.getSerializable("adList");
        if (newHouseList == null || newHouseList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillNewHouseData();
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
            fillNewHouseData();
        }else {
            Logger.e("AmapErr", "Location ERR:" + amapLocation.getAMapException().getErrorCode());
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        mLocationManagerProxy.removeUpdates(this);
        mLocationManagerProxy.destroy();
    }


}
