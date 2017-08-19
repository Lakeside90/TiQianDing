package com.xkhouse.fang.house.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.house.activity.NewHouseListActivity;
import com.xkhouse.fang.house.adapter.NewHouseTypeAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.NewHouseTypeConfigRequest;
import com.xkhouse.fang.house.task.NewHouseTypeListRequest;
import com.xkhouse.fang.house.task.SchoolListRequest;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 户型
 * Created by wujian on 2016/2/18.
 */
public class NewRoomListView implements View.OnClickListener{

    private NewHouseListActivity mContext;
    private View rootView;

    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;

    //列表
    private XListView room_listView;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 20; //每次请求10条数据
    private boolean isPullDown = false; // 下拉
    private NewHouseTypeAdapter newHouseTypeAdapter;
    private ArrayList<XKRoom> newHouseTypeList = new ArrayList<>();

    //筛选条件
    private LinearLayout bottom_bar_lay;
    private TextView category_area_txt;		//区域
    private TextView category_type_txt;		//类型(新房，二手房) 来源（租房）
    private TextView category_price_txt;	//价格（新房）单价（二手房）租金（租房）
    private TextView category_more_txt;		//更多

    private NHTypeListPopupWindow areaView;
    private NHTypeListPopupWindow typeView;
    private NHTypeListPopupWindow priceView;
    private NHMultyTypeListPopupWindow moreView;

    private List<CommonType> areas = new ArrayList<>();
    private List<CommonType> types = new ArrayList<>();
    private List<CommonType> prices = new ArrayList<>();
    private List<CommonType> mores = new ArrayList<>();

    private CommonType area;
    private CommonType type;
    private CommonType price;

    //楼盘个数
    private TextView house_count_txt;

    private ImageView scroll_top_iv;
    private int lastVisiblePosition;

    private ModelApplication modelApp;


    public View getView() {
        return rootView;
    }

    public NewRoomListView(NewHouseListActivity context){
        this.mContext = context;
        modelApp = (ModelApplication) ((Activity) context).getApplication();

        initView();
        setListeners();
        startNewHouseTypeListTask(1, true);
        getNewHouseSearchTitleData();
    }

    private void initView(){
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_new_room_list, null);

        bottom_bar_lay = (LinearLayout) rootView.findViewById(R.id.bottom_bar_lay);
        category_area_txt = (TextView) rootView.findViewById(R.id.category_area_txt);
        category_type_txt = (TextView) rootView.findViewById(R.id.category_type_txt);
        category_price_txt = (TextView) rootView.findViewById(R.id.category_price_txt);
        category_more_txt = (TextView) rootView.findViewById(R.id.category_more_txt);

        house_count_txt = (TextView) rootView.findViewById(R.id.house_count_txt);
        room_listView = (XListView) rootView.findViewById(R.id.room_listView);
        scroll_top_iv = (ImageView) rootView.findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);

    }

    private void setListeners(){
        scroll_top_iv.setOnClickListener(this);
        error_lay.setOnClickListener(this);

        category_area_txt.setOnClickListener(this);
        category_type_txt.setOnClickListener(this);
        category_price_txt.setOnClickListener(this);
        category_more_txt.setOnClickListener(this);

        room_listView.setPullLoadEnable(true);
        room_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {

                startNewHouseTypeListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        room_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //滑到底部自动加载下一页数据
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && room_listView.getEnablePullLoad()) {
                    if (view.getLastVisiblePosition() == view.getCount() - 1
                            && !room_listView.getPullLoading()) {
                        room_listView.startLoadMore();
                    }
                }

                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        lastVisiblePosition = room_listView.getLastVisiblePosition();
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        if (room_listView.getLastVisiblePosition() > lastVisiblePosition){
                            bottom_bar_lay.setVisibility(View.INVISIBLE);
                        }else{
                            bottom_bar_lay.setVisibility(View.VISIBLE);
                        }
                        break;

                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        bottom_bar_lay.setVisibility(View.VISIBLE);
                        break;
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
        switch (v.getId()) {

            case R.id.category_area_txt:
                showAreaTypeView();
                break;

            case R.id.category_type_txt:
                showTypeView();
                break;

            case R.id.category_price_txt:
                showPriceView();
                break;

            case R.id.category_more_txt:
                showMoreTypeView();
                break;

            case R.id.scroll_top_iv:
                room_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        room_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startNewHouseTypeListTask(1, true);
                getAreaSearchTitleData();
                break;
        }
    }

    //区域分类
    private void showAreaTypeView(){
        if (areas == null || areas.size() < 1) return;
        //新房两级列表（区域，学区）
        if(areaView == null){
            areaView = new NHTypeListPopupWindow(mContext, false, new NHTypeListPopupWindow.ChildTypeListClickListener() {

                @Override
                public void onParentClick(int position) {
                }

                @Override
                public void onChildClick(int parent, int child) {
                    areaView.dismiss();
                    area = areas.get(parent).getChild().get(child);
                    _schoolId = "";
                    _areaId = "";
                    if(!StringUtil.isEmpty(area.getId())){
                        if("区域".equals(areas.get(parent).getName())){
                            _areaId = area.getId();
                        }else if("学区".equals(areas.get(parent).getName())){
                            _schoolId = area.getId();
                        }
                        category_area_txt.setText(area.getName());
                    }else{
                        category_area_txt.setText("区域");
                    }

                    isPullDown = true;
                    startNewHouseTypeListTask(1, true);
                }
            });
            areaView.fillParentData(areas);
        }

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismiss();
        areaView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }


    //类型分类
    private void showTypeView(){
        if (types == null || types.size() < 1) return;

        if(typeView == null){
            typeView = new NHTypeListPopupWindow(mContext, new NHTypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    typeView.dismiss();
                    type = types.get(position);

                    _propertyType = "";
                    //新房
                    if(!StringUtil.isEmpty(type.getId())){   //不限的id是空的
                        _propertyType = type.getId();
                        category_type_txt.setText(type.getName());
                    }else{
                        category_type_txt.setText("类型");
                    }
                    isPullDown = true;

                    startNewHouseTypeListTask(1, true);
                }
            });
        }

        List<String> typeNames = new ArrayList<String>();
        for (CommonType type : types) {
            typeNames.add(type.getName());
        }
        typeView.fillData(typeNames);

        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismiss();
        typeView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    //价格分类
    private void showPriceView() {
        if (prices == null || prices.size() < 1) return;

        if(priceView == null){
            priceView = new NHTypeListPopupWindow(mContext, new NHTypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    priceView.dismiss();
                    price = prices.get(position);
                    _price = "";
                    if(!StringUtil.isEmpty(price.getId())) {
                        _price = price.getId();
                        category_price_txt.setText(price.getName());
                    }else{
                        category_price_txt.setText("价格");
                    }
                    isPullDown = true;
                    startNewHouseTypeListTask(1, true);
                }
            });
        }

        List<String> priceNames = new ArrayList<String>();
        for (CommonType type : prices) {
            priceNames.add(type.getName());
        }
        priceView.fillData(priceNames);

        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismiss();
        priceView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    //更多分类
    private void showMoreTypeView(){
        if (mores == null || mores.size() < 1) return;

        if(moreView == null){
            moreView = new NHMultyTypeListPopupWindow(mContext, new NHMultyTypeListPopupWindow.NHMultyTypeListClickListener() {

                @Override
                public void onConfirmClick(HashMap<String, String> selectIndex) {
                    //重置更多参数
                    _order = "";
                    _feature = "";
                    _houseType = "";
                    _areaSize = "";
                    _developers = "";
                    _saleState = "";

                    //根据选中类型的索引，给参数赋值
                    for(HashMap.Entry<String, String> entry : selectIndex.entrySet()){
                        //找到选中的不是第一个的分类的索引 ,key为分类的索引，value为分类下选中的索引
                        if(!"0".equals(entry.getValue())){
                            int index = Integer.parseInt(entry.getKey());
                            int childIndex = Integer.parseInt(entry.getValue());
                            String typeName = mores.get(index).getName();
                            if("排序".equals(typeName)){
                                _order = mores.get(index).getChild().get(childIndex).getId();
                            }else if("楼盘特色".equals(typeName)){
                                _feature = mores.get(index).getChild().get(childIndex).getId();
                            }else if("户型".equals(typeName)){
                                _houseType = mores.get(index).getChild().get(childIndex).getId();
                            }else if("面积".equals(typeName)){
                                _areaSize = mores.get(index).getChild().get(childIndex).getId();
                            }else if("开发商".equals(typeName)){
                                _developers = mores.get(index).getChild().get(childIndex).getName();
                            }else if("销售状态".equals(typeName)){
                                _saleState = mores.get(index).getChild().get(childIndex).getId();
                            }
                        }
                    }

                    isPullDown = true;
                    startNewHouseTypeListTask(1, true);
                }
            });
        }
        moreView.fillData(mores);

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        moreView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    private NewHouseTypeListRequest newHouseTypeListRequest;

    //请求参数
    private String _areaId;		 //区域ID
    private String _schoolId;	 //学校ID
    private String _propertyType; //物业类型
    private String _price;		//均价区间
    private String _houseType;	//户型
    private String _areaSize;	//面积
    private String _developers; //开发商
    private String _feature;		//楼盘特色
    private String _saleState;	//销售状态
    private String _order;		//排序方式

    private String getParams(){
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isEmpty(_areaId)){
            sb.append("&areaId=" + _areaId);
        }
        if(!StringUtil.isEmpty(_schoolId)){
            sb.append("&schoolId=" + _schoolId);
        }
        if(!StringUtil.isEmpty(_propertyType)){
            sb.append("&propertyType=" + _propertyType);
        }
        if(!StringUtil.isEmpty(_price)){
            sb.append("&price=" + _price);
        }
        if(!StringUtil.isEmpty(_houseType)){
            sb.append("&houseType=" + _houseType);
        }
        if(!StringUtil.isEmpty(_areaSize)){
            sb.append("&areaSize=" + _areaSize);
        }
        if(!StringUtil.isEmpty(_developers)){
            try {
                sb.append("&developers=" + URLEncoder.encode(_developers, "utf-8"));
            } catch (Exception e) {
                sb.append("&developers=" + _developers);
            }
        }
        if(!StringUtil.isEmpty(_feature)){
            sb.append("&feature=" + _feature);
        }
        if(!StringUtil.isEmpty(_saleState)){
            sb.append("&saleState=" + _saleState);
        }
        if(!StringUtil.isEmpty(mContext.getKeyword())){
            try {
                sb.append("&keyword=" + URLEncoder.encode(mContext.getKeyword(), "utf-8"));
            } catch (Exception e) {
                sb.append("&keyword=" + mContext.getKeyword());
            }
        }
        if(!StringUtil.isEmpty(_order)){
            if("1".equals(_order) && mContext.getStartLatlng() == null){
                Toast.makeText(mContext, "定位失败，不能按距离排序", Toast.LENGTH_SHORT).show();
            }else{
                sb.append("&order=" + _order);
            }
        }
        if(mContext.getStartLatlng() != null){
            sb.append("&longitude=" + mContext.getStartLatlng().longitude);
            sb.append("&latitude=" + mContext.getStartLatlng().latitude);
        }

        return sb.toString();
    }

    private void startNewHouseTypeListTask(int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(newHouseTypeListRequest == null){
                newHouseTypeListRequest = new NewHouseTypeListRequest(modelApp.getSite().getSiteId(), page, pageSize,
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
                                if (newHouseTypeList == null || newHouseTypeList.size() < 1){
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
                                    if(newHouseTypeList != null){
                                        newHouseTypeList.clear();
                                        fillNewHouseTypeData();
                                    }
                                    house_count_txt.setText("0");
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);

                                house_count_txt.setText(String.valueOf(message.arg1));
                                ArrayList<XKRoom> temp = (ArrayList<XKRoom>) message.obj;
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    room_listView.setPullLoadEnable(false);
                                }else{
                                    room_listView.setPullLoadEnable(true);
                                }

                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && newHouseTypeList != null){
                                    if(newHouseTypeList.size() > 0){
                                        room_listView.smoothScrollToPosition(0);
                                    }
                                    newHouseTypeList.clear();
                                    currentPageIndex = 1;
                                }
                                newHouseTypeList.addAll(temp);
                                fillNewHouseTypeData();
                                if (currentPageIndex > 1 && message.arg1 == newHouseTypeList.size()){
                                    Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                                }
                                currentPageIndex++;
                                break;
                        }
                        isPullDown = false;
                        room_listView.stopRefresh();
                        room_listView.stopLoadMore();
                    }
                });
            }else {
                newHouseTypeListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, getParams());
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            newHouseTypeListRequest.doRequest();
        }else {

            isPullDown = false;
            room_listView.stopRefresh();
            room_listView.stopLoadMore();

            if (newHouseTypeList == null || newHouseTypeList.size() < 1){
                fillCacheData();
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void fillNewHouseTypeData() {
        if(newHouseTypeList == null) return;
        if(newHouseTypeAdapter == null){
            newHouseTypeAdapter = new NewHouseTypeAdapter(mContext, newHouseTypeList, mContext.getStartLatlng());
            room_listView.setAdapter(newHouseTypeAdapter);
        }else {
            newHouseTypeAdapter.setData(newHouseTypeList, mContext.getStartLatlng());
        }
    }

    public void fillCacheData(){
        NewHouseTypeListRequest request = new NewHouseTypeListRequest();
        request.parseResult(AppCache.readNewHouseTypeListJson(modelApp.getSite().getSiteId()));
        Bundle data = request.getCacheModel();
        house_count_txt.setText(String.valueOf(data.getInt("count")));
        newHouseTypeList = (ArrayList<XKRoom>) data.getSerializable("houseTypeList");
        if (newHouseTypeList == null || newHouseTypeList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillNewHouseTypeData();
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
        if (priceView != null && priceView.isShowing()){
            priceView.dismiss();
            return true;
        }
        if (moreView != null && moreView.isShowing()){
            moreView.dismiss();
            return true;
        }
        return false;
    }

    /*************************************** 获取新房筛选条件   ********************************************/

    private HouseConfigDbService configDbService = new HouseConfigDbService();
    //获取新房的筛选条件
    public void getNewHouseSearchTitleData() {

        getAreaSearchTitleData();

        //类型， 价格， 更多（排序 楼盘特色  户型 面积  开发商  销售状态）（这几个数据同一个接口）
        if (types != null && types.size() > 0) return;

        ArrayList<CommonType> developerList = configDbService.getDeveloperListBySite(modelApp.getSite().getSiteId());
        if(developerList == null || developerList.size() < 1){
            startNewHouseMoreTask();
        }else {
            ArrayList<CommonType> featureList = configDbService.getFeatureListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> houseTypeList = configDbService.getHouseTypeListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> spaceList = configDbService.getSpaceListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> orderList = configDbService.getOrderListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> saleStateList = configDbService.getSaleStateListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> propertyList = configDbService.getPropertyListBySite(modelApp.getSite().getSiteId());
            ArrayList<CommonType> priceList = configDbService.getPriceListBySite(modelApp.getSite().getSiteId());

            //类型
            if(propertyList != null && propertyList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                types.add(0, defaultType);
                for(CommonType property : propertyList){
                    CommonType type = new CommonType();
                    type.setId(property.getId());
                    type.setName(property.getName());
                    types.add(type);
                }
            }

            //价格
            if(priceList != null && priceList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                prices.add(0, defaultType);
                for(CommonType price : priceList){
                    CommonType type = new CommonType();
                    type.setId(price.getId());
                    type.setName(price.getName());
                    prices.add(type);
                }
            }

            //更多
            if(orderList != null && orderList.size() > 0){
                CommonType order = new CommonType();
                order.setId("");
                order.setName("排序");
                order.setChild(orderList);
                mores.add(order);
            }

            if(houseTypeList != null && houseTypeList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                houseTypeList.add(0, defaultType);
                CommonType houseType = new CommonType();
                houseType.setId("");
                houseType.setName("户型");
                houseType.setChild(houseTypeList);
                mores.add(houseType);
            }

            if(spaceList != null && spaceList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                spaceList.add(0, defaultType);
                CommonType space = new CommonType();
                space.setId("");
                space.setName("面积");
                space.setChild(spaceList);
                mores.add(space);
            }

            if(featureList != null && featureList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                featureList.add(0, defaultType);
                CommonType feature = new CommonType();
                feature.setId("");
                feature.setName("楼盘特色");
                feature.setChild(featureList);
                mores.add(feature);
            }

            if(developerList != null && developerList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                developerList.add(0, defaultType);
                CommonType developer = new CommonType();
                developer.setId("");
                developer.setName("开发商");
                developer.setChild(developerList);
                mores.add(developer);
            }

            if(saleStateList != null && saleStateList.size() > 0){
                //不限
                CommonType defaultType = new CommonType();
                defaultType.setId("");
                defaultType.setName("不限");
                saleStateList.add(0, defaultType);
                CommonType saleState = new CommonType();
                saleState.setId("");
                saleState.setName("销售状态");
                saleState.setChild(saleStateList);
                mores.add(saleState);
            }

        }
    }

    /**
     * 获取区域（学区）
     */
    public void getAreaSearchTitleData(){
        if (areas != null && areas.size() > 1) return;

        //区域以及学校
        ArrayList<Area> areaList= configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
        if(areaList == null || areaList.size() < 1){
            //请求站点下的行政区域
            startAreaTask();
        }else{
            CommonType cArea = new CommonType();
            cArea.setId("");
            cArea.setName("区域");
            List<CommonType> cAreaList = new ArrayList<CommonType>();
            //不限
            CommonType defaultType = new CommonType();
            defaultType.setId("");
            defaultType.setName("不限");
            cAreaList.add(defaultType);
            for(int i = 0; i < areaList.size(); i++){
                CommonType area = new CommonType();
                area.setId(areaList.get(i).getAreaId());
                area.setName(areaList.get(i).getAreaName());
                cAreaList.add(area);
            }
            cArea.setChild(cAreaList);
            areas.add(cArea);
        }

        ArrayList<CommonType> schoolList = configDbService.getSchoolListBySite(modelApp.getSite().getSiteId());
        if(schoolList == null || schoolList.size() < 1){
            // 请求站点下的学区
            startNewHouseSchoolTask();

        }else{
            //不限
            CommonType defaultType = new CommonType();
            defaultType.setId("");
            defaultType.setName("不限");
            schoolList.add(0, defaultType);
            CommonType school = new CommonType();
            school.setId("");
            school.setName("学区");
            school.setChild(schoolList);
            areas.add(school);
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
                            configDbService.insertAreaList(temp, modelApp.getSite().getSiteId());
                            ArrayList<Area> areaList= configDbService.getAreaListBySite(modelApp.getSite().getSiteId());

                            if(areaList != null && areaList.size() > 0){
                                CommonType cArea = new CommonType();
                                cArea.setId("");
                                cArea.setName("区域");
                                List<CommonType> cAreaList = new ArrayList<CommonType>();
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                cAreaList.add(defaultType);
                                for(int i = 0; i < areaList.size(); i++){
                                    CommonType area = new CommonType();
                                    area.setId(areaList.get(i).getAreaId());
                                    area.setName(areaList.get(i).getAreaName());
                                    cAreaList.add(area);
                                }
                                cArea.setChild(cAreaList);
                                areas.add(cArea);
                            }
                            break;
                    }
                }
            });
            request.doRequest();
        }
    }

    private void startNewHouseSchoolTask() {
        if(NetUtil.detectAvailable(mContext)){
            SchoolListRequest request = new SchoolListRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
                            configDbService.insertSchoolList(temp, modelApp.getSite().getSiteId());
                            ArrayList<CommonType> schoolList = configDbService.getSchoolListBySite(modelApp.getSite().getSiteId());

                            if(schoolList != null && schoolList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                schoolList.add(0, defaultType);

                                CommonType school = new CommonType();
                                school.setId("");
                                school.setName("学区");
                                school.setChild(schoolList);
                                areas.add(school);
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
            NewHouseTypeConfigRequest request = new NewHouseTypeConfigRequest(modelApp.getSite().getSiteId(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:

                            ArrayList<CommonType> propertyList = configDbService.getPropertyListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> priceList = configDbService.getPriceListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> orderList = configDbService.getOrderListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> featureList = configDbService.getFeatureListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> houseTypeList = configDbService.getHouseTypeListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> spaceList = configDbService.getSpaceListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> developerList = configDbService.getDeveloperListBySite(modelApp.getSite().getSiteId());
                            ArrayList<CommonType> saleStateList = configDbService.getSaleStateListBySite(modelApp.getSite().getSiteId());

                            //类型
                            if(propertyList != null && propertyList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                types.add(0, defaultType);
                                for(CommonType property : propertyList){
                                    CommonType type = new CommonType();
                                    type.setId(property.getId());
                                    type.setName(property.getName());
                                    types.add(type);
                                }
                            }

                            //价格
                            if(priceList != null && priceList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                prices.add(0, defaultType);
                                for(CommonType price : priceList){
                                    CommonType type = new CommonType();
                                    type.setId(price.getId());
                                    type.setName(price.getName());
                                    prices.add(type);
                                }
                            }

                            //更多
                            if(orderList != null && orderList.size() > 0){
                                CommonType order = new CommonType();
                                order.setId("");
                                order.setName("排序");
                                order.setChild(orderList);
                                mores.add(order);
                            }

                            if(houseTypeList != null && houseTypeList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                houseTypeList.add(0, defaultType);
                                CommonType houseType = new CommonType();
                                houseType.setId("");
                                houseType.setName("户型");
                                houseType.setChild(houseTypeList);
                                mores.add(houseType);
                            }

                            if(spaceList != null && spaceList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                spaceList.add(0, defaultType);
                                CommonType space = new CommonType();
                                space.setId("");
                                space.setName("面积");
                                space.setChild(spaceList);
                                mores.add(space);
                            }

                            if(featureList != null && featureList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                featureList.add(0, defaultType);
                                CommonType feature = new CommonType();
                                feature.setId("");
                                feature.setName("楼盘特色");
                                feature.setChild(featureList);
                                mores.add(feature);
                            }

                            if(developerList != null && developerList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                developerList.add(0, defaultType);
                                CommonType developer = new CommonType();
                                developer.setId("");
                                developer.setName("开发商");
                                developer.setChild(developerList);
                                mores.add(developer);
                            }

                            if(saleStateList != null && saleStateList.size() > 0){
                                //不限
                                CommonType defaultType = new CommonType();
                                defaultType.setId("");
                                defaultType.setName("不限");
                                saleStateList.add(0, defaultType);
                                CommonType saleState = new CommonType();
                                saleState.setId("");
                                saleState.setName("销售状态");
                                saleState.setChild(saleStateList);
                                mores.add(saleState);
                            }

                            break;
                    }
                }
            });
            request.doRequest();
        }
    }
}
