package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.house.view.NewHouseListView;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;


/**
 * 新房列表
 * wujian  2016/8/3
 */
public class NewHouseListActivity extends AppBaseActivity implements AMapLocationListener {

    //顶部导航栏
    private ImageView back_iv;
    private ImageView map_iv;
    private LinearLayout house_search_bar;	//搜索
    private TextView house_search_txt;

    private TextView discount_txt;
    private TextView new_txt;
    private TextView custom_txt;
    private LinearLayout content_lay;

    private NewHouseListView allListView;
    private FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);

    //定位
    private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
    private AMapLocation mAmapLocation = null;			//当前定位的位置
    private LatLng startLatlng; //当前经纬度

    private String keyword;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allListView = new NewHouseListView(this);
        content_lay.addView(allListView.getView(), lp);
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_wjnew_house_list);
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
        if (getIntent().getExtras() != null){
            keyword = getIntent().getExtras().getString("keyword");
        }
    }


    @Override
    protected void findViews() {
        super.findViews();

        new_txt = (TextView) findViewById(R.id.new_txt);
        custom_txt = (TextView) findViewById(R.id.custom_txt);
        discount_txt = (TextView) findViewById(R.id.discount_txt);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
    }

    @Override
    protected void setListeners() {
        super.setListeners();

        initTitleView();
        new_txt.setOnClickListener(this);
        discount_txt.setOnClickListener(this);
        custom_txt.setOnClickListener(this);
    }


    private void initTitleView(){
        back_iv = (ImageView) findViewById(R.id.back_iv);
        map_iv = (ImageView) findViewById(R.id.map_iv);
        house_search_bar = (LinearLayout) findViewById(R.id.house_search_bar);
        house_search_txt = (TextView) findViewById(R.id.house_search_txt);
        if(StringUtil.isEmpty(keyword)){
            house_search_txt.setText("请输入楼盘名或区域等");
        }else {
            house_search_txt.setText(keyword);
        }

        back_iv.setOnClickListener(this);
        map_iv.setOnClickListener(this);
        house_search_bar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        Intent intent;
        Bundle data;

        switch (v.getId()){
            case R.id.back_iv:
                this.finish();
                break;

            case R.id.map_iv:
                data = new Bundle();
                data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
                intent = new Intent(mContext, MapHousesActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                break;

            case R.id.house_search_bar:
                data = new Bundle();
                data.putString("keyword", keyword);
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                if(!StringUtil.isEmpty(keyword)){
                    finish();
                }
                break;

            case R.id.new_txt:
                startActivity(new Intent(NewHouseListActivity.this, NewHouseRecentListActivity.class));
                break;

            case R.id.custom_txt:
                startActivity(new Intent(NewHouseListActivity.this, CustomHouseListActivity.class));
                break;

            case R.id.discount_txt:
                startActivity(new Intent(NewHouseListActivity.this, NewHouseDiscountListActivity.class));
                break;
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

            if (allListView != null) allListView.fillNewHouseData();

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (allListView != null && allListView.closeTypeView())  return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    public LatLng getStartLatlng() {
        return startLatlng;
    }

    public String getKeyword() {
        return keyword;
    }
}
