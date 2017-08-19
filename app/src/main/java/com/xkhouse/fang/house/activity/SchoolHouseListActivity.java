package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
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
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.house.adapter.SchoolHouseAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.SchoolHouse;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.SchoolHouseConfigRequest;
import com.xkhouse.fang.house.task.SchoolHouseListRequest;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow;
import com.xkhouse.fang.house.view.SchoolNoHouseView;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 2016/6/17 wujian
 * 学区房列表
 */
public class SchoolHouseListActivity extends AppBaseActivity implements AMapLocationListener {

    //导航栏
    private ImageView back_iv;
    private LinearLayout house_search_bar;	//搜索
    private TextView house_search_txt;

    private ImageView scroll_top_iv;

    //筛选条件
    private LinearLayout search_title_view;
    private TextView category_area_txt;		//区域
    private TextView category_type_txt;		//类型(新房，二手房) 来源（租房）
    private TextView category_key_txt;	//重点（新房）单价（二手房）租金（租房）
    private TextView category_more_txt;		//更多

    private TypeListPopupWindow areaView;
    private TypeListPopupWindow typeView;
    private TypeListPopupWindow keyView;
    private TypeListPopupWindow orderView;

    private List<CommonType> areas = new ArrayList<>();
    private List<CommonType> types = new ArrayList<>();
    private List<CommonType> keys = new ArrayList<>();
    private List<CommonType> orders = new ArrayList<>();

    private CommonType area;
    private CommonType type;
    private CommonType key;
    private CommonType order;


    //列表
    private XListView school_listView;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 10; //每次请求10条数据
    private boolean isPullDown = false; // 下拉
    private SchoolHouseAdapter schoolHouseAdapter;
    private ArrayList<SchoolHouse> schoolHouseList = new ArrayList<SchoolHouse>();


    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;
    private LinearLayout no_data_lay;
    private SchoolNoHouseView schoolNoHouseView;    //没有搜索到结果的提示页面


    private String keyword;

    //定位
    private LocationManagerProxy mLocationManagerProxy;  //高德定位代理类
    private AMapLocation mAmapLocation = null;			//当前定位的位置
    private LatLng startLatlng; //当前经纬度





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startSchoolHouseListTask(1, true);
        getSchoolHouseSearchTitleData();
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
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_school_house_list);
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitleView();

        search_title_view = (LinearLayout) findViewById(R.id.search_title_view);
        category_area_txt = (TextView) findViewById(R.id.category_area_txt);
        category_type_txt = (TextView) findViewById(R.id.category_type_txt);
        category_key_txt = (TextView) findViewById(R.id.category_key_txt);
        category_more_txt = (TextView) findViewById(R.id.category_more_txt);

        school_listView = (XListView) findViewById(R.id.school_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);

        no_data_lay = (LinearLayout) findViewById(R.id.no_data_lay);
    }

    private void initTitleView(){
        back_iv = (ImageView) findViewById(R.id.back_iv);
        house_search_bar = (LinearLayout) findViewById(R.id.house_search_bar);
        house_search_txt = (TextView) findViewById(R.id.house_search_txt);
        if(StringUtil.isEmpty(keyword)){
            house_search_txt.setText("请输入学校名称");
        }else {
            house_search_txt.setText(keyword);
        }
        back_iv.setOnClickListener(this);
        house_search_bar.setOnClickListener(this);
    }


    @Override
    protected void setListeners() {
        super.setListeners();

        error_lay.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);

        category_area_txt.setOnClickListener(this);
        category_type_txt.setOnClickListener(this);
        category_key_txt.setOnClickListener(this);
        category_more_txt.setOnClickListener(this);

        school_listView.setPullLoadEnable(true);
        school_listView.setPullRefreshEnable(true);
        school_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startSchoolHouseListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startSchoolHouseListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        school_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && school_listView.getEnablePullLoad()) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1
                            && !school_listView.getPullLoading()) {
                        school_listView.startLoadMore();
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

        Intent intent;
        Bundle data;

        switch (v.getId()) {
            case R.id.back_iv:
                this.finish();
                break;

            case R.id.category_area_txt:
                showAreaTypeView();
                break;

            case R.id.category_type_txt:
                showTypeView();
                break;

            case R.id.category_key_txt:
                showPriceView();
                break;

            case R.id.category_more_txt:
                showOrderTypeView();
                break;


            case R.id.house_search_bar:
                data = new Bundle();
                data.putString("keyword", keyword);
                intent = new Intent(mContext, SchoolHouseSearchActivity.class);
                intent.putExtras(data);
                startActivity(intent);
                if(!StringUtil.isEmpty(keyword)){
                    finish();
                }
                break;

            case R.id.scroll_top_iv:
                school_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        school_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startSchoolHouseListTask(1, true);
                break;
        }
    }


    //区域分类
    private void showAreaTypeView(){
        if (areas == null || areas.size() < 1) return;
        if(areaView == null){
            areaView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    areaView.dismiss();
                    area = areas.get(position);
                    _areaId = area.getId();
                    isPullDown = true;
                    startSchoolHouseListTask(1, true);
                }
            });
        }

        List<String> areaNames = new ArrayList<String>();
        for (CommonType type : areas) {
            areaNames.add(type.getName());
        }
        areaView.fillData(areaNames);

        areaView.showAsDropDown(search_title_view);
    }

    //类型分类
    private void showTypeView(){
        if (types == null || types.size() < 1) return;

        if(typeView == null){
            typeView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    typeView.dismiss();
                    type = types.get(position);

                    _type = "";

                    if(!StringUtil.isEmpty(type.getId())){   //不限的id是空的
                        _type = type.getId();
                        category_type_txt.setText(type.getName());
                    }else{
                        category_type_txt.setText("类型");
                    }
                    isPullDown = true;

                    startSchoolHouseListTask(1, true);
                }
            });
        }

        List<String> typeNames = new ArrayList<String>();
        for (CommonType type : types) {
            typeNames.add(type.getName());
        }
        typeView.fillData(typeNames);

        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        if(keyView != null && keyView.isShowing())  keyView.dismiss();
        if(orderView != null && orderView.isShowing())  orderView.dismiss();
        typeView.showAsDropDown(search_title_view);
    }

    //重点分类
    private void showPriceView() {
        if (keys == null || keys.size() < 1) return;

        if(keyView == null){
            keyView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    keyView.dismiss();
                    key = keys.get(position);
                    _key = "";
                    if(!StringUtil.isEmpty(key.getId())) {
                        _key = key.getId();
                        category_key_txt.setText(key.getName());
                    }else{
                        category_key_txt.setText("重点");
                    }
                    isPullDown = true;
                    startSchoolHouseListTask(1, true);
                }
            });
        }

        List<String> keyNames = new ArrayList<String>();
        for (CommonType type : keys) {
            keyNames.add(type.getName());
        }
        keyView.fillData(keyNames);

        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(orderView != null && orderView.isShowing())  orderView.dismiss();
        keyView.showAsDropDown(search_title_view);
    }

    //更多分类
    private void showOrderTypeView(){
        if (orders == null || orders.size() < 1) return;

        if(orderView == null){
            orderView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    orderView.dismiss();

                    order = orders.get(position);
                    _order = "";
                    if(!StringUtil.isEmpty(order.getId())) {
                        _order = order.getId();
                        category_more_txt.setText(order.getName());
                    }else{
                        category_more_txt.setText("排序");
                    }
                    isPullDown = true;
                    startSchoolHouseListTask(1, true);
                }
            });
        }

        List<String> orderNames = new ArrayList<String>();
        for (CommonType type : orders) {
            orderNames.add(type.getName());
        }
        orderView.fillData(orderNames);

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(keyView != null && keyView.isShowing())  keyView.dismiss();
        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        orderView.showAsDropDown(search_title_view);
    }




    private SchoolHouseListRequest schoolHouseListRequest;
    //请求参数
    private String _areaId;		 //区域ID
    private String _type;       //类型
    private String _key;		//重点
    private String _order;		//排序方式

    private String getParams(){
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isEmpty(_areaId)){
            sb.append("&aid=" + _areaId);
        }
        if(!StringUtil.isEmpty(_type)){
            sb.append("&type=" + _type);
        }
        if(!StringUtil.isEmpty(_key)){
            sb.append("&isImportant=" + _key);
        }
        if(!StringUtil.isEmpty(keyword)){
            try {
                sb.append("&key=" + URLEncoder.encode(keyword, "utf-8"));
            } catch (Exception e) {
                sb.append("&key=" + keyword);
            }
        }

        if(!StringUtil.isEmpty(_order)){
            if("2".equals(_order) && startLatlng == null){
                Toast.makeText(mContext, "定位失败，不能按距离排序", Toast.LENGTH_SHORT).show();
            }else{
                sb.append("&moretag=" + _order);
            }
        }
        if(startLatlng != null){
            sb.append("&longitude=" + startLatlng.longitude);
            sb.append("&latitude=" + startLatlng.latitude);
        }


        return sb.toString();
    }

    private void startSchoolHouseListTask(final int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(schoolHouseListRequest == null){
                schoolHouseListRequest = new SchoolHouseListRequest(modelApp.getSite().getSiteId(), page, pageSize,
                        getParams(), new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        school_listView.setVisibility(View.VISIBLE);
                        search_title_view.setVisibility(View.VISIBLE);
                        no_data_lay.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (schoolHouseList == null || schoolHouseList.size() == 0){
                                    fillCacheData();
                                }else{
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                content_lay.setVisibility(View.VISIBLE);
                                if (currentPageIndex == 1){

                                    if(schoolHouseList != null){
                                        schoolHouseList.clear();
                                        fillSchoolHouseData();
                                    }
                                    // 显示推荐的学区
                                    school_listView.setVisibility(View.GONE);
                                    scroll_top_iv.setVisibility(View.GONE);
                                    no_data_lay.setVisibility(View.VISIBLE);
                                    //没有选择筛选的条件下，搜索无结果
                                    if(!StringUtil.isEmpty(keyword) && schoolNoHouseView == null){
                                        search_title_view.setVisibility(View.GONE);
                                    }

                                    ArrayList<SchoolHouse> temp = (ArrayList<SchoolHouse>) message.obj;
                                    ArrayList<SchoolHouse> list = new ArrayList<>();
                                    list.addAll(temp);
                                    if (schoolNoHouseView == null){
                                        schoolNoHouseView = new SchoolNoHouseView(mContext, list, startLatlng);
                                    }else {
                                        schoolNoHouseView.refreshView(list, startLatlng);
                                    }
                                    no_data_lay.removeAllViews();
                                    no_data_lay.addView(schoolNoHouseView.getView());
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);

                                ArrayList<SchoolHouse> temp = (ArrayList<SchoolHouse>) message.obj;

                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && schoolHouseList != null){
                                    if(schoolHouseList.size() > 0){
                                        school_listView.smoothScrollToPosition(0);
                                    }
                                    schoolHouseList.clear();
                                    currentPageIndex = 1;
                                }
                                schoolHouseList.addAll(temp);
                                fillSchoolHouseData();

                                if (message.arg1 == schoolHouseList.size()){
                                    if(currentPageIndex > 1 ){
                                        Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                                    }
                                    school_listView.setPullLoadEnable(false);
                                }else{
                                    school_listView.setPullLoadEnable(true);
                                }

                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        school_listView.stopRefresh();
                        school_listView.stopLoadMore();
                    }
                });
            }else {
                schoolHouseListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, getParams());
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            schoolHouseListRequest.doRequest();

        }else {
            isPullDown = false;
            school_listView.stopRefresh();
            school_listView.stopLoadMore();
            if (schoolHouseList == null || schoolHouseList.size() == 0){
                fillCacheData();
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillSchoolHouseData() {
        if(schoolHouseList == null) return;
        if(schoolHouseAdapter == null){
            schoolHouseAdapter = new SchoolHouseAdapter(mContext, schoolHouseList, startLatlng);
            school_listView.setAdapter(schoolHouseAdapter);
        }else {
            schoolHouseAdapter.setData(schoolHouseList, startLatlng);
        }
    }


    private void fillCacheData(){
        SchoolHouseListRequest request = new SchoolHouseListRequest();
        request.parseResult(AppCache.readSchoolHouseListJson(modelApp.getSite().getSiteId()));
        Bundle data = request.getCacheModel();
        schoolHouseList = (ArrayList<SchoolHouse>) data.getSerializable("schoolList");
        if (schoolHouseList == null || schoolHouseList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillSchoolHouseData();
        }
    }





    public boolean closeTypeView(){
        if (areaView != null && areaView.isShowing()){
            areaView.dismiss();
            return true;
        }
        if (typeView != null && typeView.isShowing()){
            typeView.dismiss();
            return true;
        }
        if (keyView != null && keyView.isShowing()){
            keyView.dismiss();
            return true;
        }
        if (orderView != null && orderView.isShowing()){
            orderView.dismiss();
            return true;
        }
        return false;
    }



    /*************************************** 获取新房筛选条件   ********************************************/

    private HouseConfigDbService configDbService = new HouseConfigDbService();
    //获取新房的筛选条件
    public void getSchoolHouseSearchTitleData() {

        getAreaSearchTitleData();

        //类型， 重点， 排序 （这几个数据同一个接口）
        if (types != null && types.size() > 0
                && keys != null && keys.size() > 0
                && orders != null && orders.size() > 0) return;

        ArrayList<CommonType> keyList = configDbService.getSchoolKeyListBySite(modelApp.getSite().getSiteId());
        ArrayList<CommonType> typeList = configDbService.getSchoolTypeListBySite(modelApp.getSite().getSiteId());
        ArrayList<CommonType> orderList = configDbService.getSchoolOrderListBySite(modelApp.getSite().getSiteId());

        if(keyList == null || keyList.size() < 1
                || orderList == null || orderList.size() < 1
                || typeList == null || typeList.size() < 1
                || orderList == null || orderList.size() < 1){

            startNewHouseMoreTask();

        }else {
            //类型
            if(typeList != null && typeList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                types.add(0, defaultType);
                for(CommonType property : typeList){
                    CommonType type = new CommonType();
                    type.setId(property.getId());
                    type.setName(property.getName());
                    types.add(type);
                }
            }

            //重点
            if(keyList != null && keyList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                keys.add(0, defaultType);
                for(CommonType price : keyList){
                    CommonType type = new CommonType();
                    type.setId(price.getId());
                    type.setName(price.getName());
                    keys.add(type);
                }
            }

            //排序
            if(orderList != null && orderList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                orders.add(0, defaultType);
                for(CommonType price : orderList){
                    CommonType type = new CommonType();
                    type.setId(price.getId());
                    type.setName(price.getName());
                    orders.add(type);
                }
            }

        }
    }

    /**
     * 获取区域（学区）
     */
    public void getAreaSearchTitleData(){

        if (areas != null && areas.size() > 1) return;

        //区域以及学校
        ArrayList<Area> areaList= new HouseConfigDbService().getAreaListBySite(modelApp.getSite().getSiteId());
        if(areaList == null || areaList.size() < 1){
            //请求站点下的行政区域
            startAreaTask();
        }else{
            //不限
            CommonType defaultType = new CommonType();
            defaultType.setId("");
            defaultType.setName("不限");
            areas.add(defaultType);
            for(int i = 0; i < areaList.size(); i++){
                CommonType area = new CommonType();
                area.setId(areaList.get(i).getAreaId());
                area.setName(areaList.get(i).getAreaName());
                areas.add(area);
            }
        }
    }


    private void startAreaTask(){
        if(NetUtil.detectAvailable(mContext)){

            AreaListRequest request = new AreaListRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            ArrayList<Area> temp = (ArrayList<Area>) message.obj;
                            new HouseConfigDbService().insertAreaList(temp, modelApp.getSite().getSiteId());
                            ArrayList<Area> areaList= new HouseConfigDbService().getAreaListBySite(modelApp.getSite().getSiteId());

                            if(areaList != null && areaList.size() > 0){
                                CommonType defaultType = new CommonType();
                                defaultType.setId("0");
                                defaultType.setName("不限");
                                areas.add(defaultType);
                                for(Area area : areaList){
                                    CommonType type = new CommonType();
                                    type.setId(area.getAreaId());
                                    type.setName(area.getAreaName());
                                    areas.add(type);
                                }
                            }
                            break;
                    }
                }
            });
            request.doRequest();
        }
    }


    private void startNewHouseMoreTask(){
        if(NetUtil.detectAvailable(mContext)){
            SchoolHouseConfigRequest request = new SchoolHouseConfigRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:

                            ArrayList<CommonType> keyList = configDbService.getSchoolKeyListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> typeList = configDbService.getSchoolTypeListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> orderList = configDbService.getSchoolOrderListBySite(modelApp.getSite().getSiteId());

                            //类型
                            if(typeList != null && typeList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                types.add(0, defaultType);
                                for(CommonType property : typeList){
                                    CommonType type = new CommonType();
                                    type.setId(property.getId());
                                    type.setName(property.getName());
                                    types.add(type);
                                }
                            }

                            //重点
                            if(keyList != null && keyList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                keys.add(0, defaultType);
                                for(CommonType price : keyList){
                                    CommonType type = new CommonType();
                                    type.setId(price.getId());
                                    type.setName(price.getName());
                                    keys.add(type);
                                }
                            }

                            //排序
                            if(orderList != null && orderList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                orders.add(0, defaultType);
                                for(CommonType price : orderList){
                                    CommonType type = new CommonType();
                                    type.setId(price.getId());
                                    type.setName(price.getName());
                                    orders.add(type);
                                }
                            }

                            break;
                    }
                }
            });
            request.doRequest();
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

            fillSchoolHouseData();

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
            if (closeTypeView())  return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
