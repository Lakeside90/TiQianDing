package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.house.adapter.OldHouseAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.OldHouse;
import com.xkhouse.fang.house.task.OldRentConditionListRequest;
import com.xkhouse.fang.house.task.RentHouseListRequest;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.user.activity.LoginActivity;
import com.xkhouse.fang.user.activity.RentInActivity;
import com.xkhouse.fang.user.activity.RentReleaseActivity;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RentHouseListActivity extends AppBaseActivity {

    //导航栏
    private ImageView back_iv;
    private ImageView map_iv;
    private LinearLayout house_search_bar;	//搜索
    private TextView house_search_txt;

    private ImageView scroll_top_iv;

    //列表
    private XListView house_listView;
    private int currentPageIndex = 1;  //分页索引
    private int pageSize = 10; //每次请求10条数据
    private boolean isPullDown = false; // 下拉
    private OldHouseAdapter rentHouseAdapter;
    private ArrayList<OldHouse> rentHouseList = new ArrayList<OldHouse>();

    //筛选条件
    private LinearLayout search_title_view;
    private TextView category_area_txt;		//区域
    private TextView category_type_txt;		//类型(新房，二手房) 来源（租房）
    private TextView category_price_txt;	//价格（新房）单价（二手房）租金（租房）
    private TextView category_more_txt;		//更多

    private TypeListPopupWindow areaView;
    private TypeListPopupWindow typeView;
    private TypeListPopupWindow priceView;
    private MultyTypeListPopupWindow moreView;

    private List<CommonType> areas = new ArrayList<CommonType>();
    private List<CommonType> types = new ArrayList<CommonType>();
    private List<CommonType> prices = new ArrayList<CommonType>();
    private List<CommonType> mores = new ArrayList<CommonType>();

    private CommonType area;
    private CommonType type;
    private CommonType price;
    private CommonType more;

    private TextView house_count_txt;		//楼盘个数
    private TextView rent_in_txt;		//我要买房
    private TextView rent_release_txt;		//我要出售

    private String keyword;


    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startRentHouseListTask(1, true);
        startOldRentConditionTask();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(areas == null && areas.size() == 0){
            startOldRentConditionTask();
        }
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
        setContentView(R.layout.activity_rent_house_list);
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitleView();

        search_title_view = (LinearLayout) findViewById(R.id.search_title_view);
        category_area_txt = (TextView) findViewById(R.id.category_area_txt);
        category_type_txt = (TextView) findViewById(R.id.category_type_txt);
        category_price_txt = (TextView) findViewById(R.id.category_price_txt);
        category_more_txt = (TextView) findViewById(R.id.category_more_txt);
        house_count_txt = (TextView) findViewById(R.id.house_count_txt);
        rent_in_txt = (TextView) findViewById(R.id.rent_in_txt);
        rent_release_txt = (TextView) findViewById(R.id.rent_release_txt);

        house_listView = (XListView) findViewById(R.id.house_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
    }

    private void initTitleView(){
        back_iv = (ImageView) findViewById(R.id.back_iv);
        map_iv = (ImageView) findViewById(R.id.map_iv);
        house_search_bar = (LinearLayout) findViewById(R.id.house_search_bar);
        house_search_txt = (TextView) findViewById(R.id.house_search_txt);
        if(StringUtil.isEmpty(keyword)){
            house_search_txt.setText("请输入小区名");
        }else {
            house_search_txt.setText(keyword);
        }

        back_iv.setOnClickListener(this);
        map_iv.setOnClickListener(this);
        house_search_bar.setOnClickListener(this);
    }


    @Override
    protected void setListeners() {
        super.setListeners();

        error_lay.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);

        category_area_txt.setOnClickListener(this);
        category_type_txt.setOnClickListener(this);
        category_price_txt.setOnClickListener(this);
        category_more_txt.setOnClickListener(this);

        rent_in_txt.setOnClickListener(this);
        rent_release_txt.setOnClickListener(this);

        house_listView.setPullLoadEnable(true);
        house_listView.setPullRefreshEnable(true);
        house_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startRentHouseListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startRentHouseListTask(currentPageIndex, false);
            }
        }, R.id.house_listView);

        house_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
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

        Intent intent = null;
        Bundle data = null;

        switch (v.getId()) {
            case R.id.back_iv:
                this.finish();
                break;

            case R.id.map_iv:
                data = new Bundle();
                data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_RENT);
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

            case R.id.rent_in_txt:
                if(Preference.getInstance().readIsLogin()){
                    if(Constants.USER_GE_REN.equals("")){
                        startActivity(new Intent(RentHouseListActivity.this, RentInActivity.class));
                    }else{
                        Toast.makeText(RentHouseListActivity.this, "您不是个人用户，无法发布求租信息", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RentHouseListActivity.this, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RentHouseListActivity.this, LoginActivity.class));
                }
                break;

            case R.id.rent_release_txt:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(RentHouseListActivity.this, RentReleaseActivity.class));
                }else {
                    Toast.makeText(RentHouseListActivity.this, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RentHouseListActivity.this, LoginActivity.class));
                }
                break;

            case R.id.scroll_top_iv:
                house_listView.post(new Runnable() {
                    @Override
                    public void run() {
                        house_listView.smoothScrollToPosition(0);
                    }
                });
                break;

            case R.id.error_lay:
                startRentHouseListTask(1, true);
                if(areas == null && areas.size() == 0){
                    startOldRentConditionTask();
                }
                break;
        }
    }


    //区域分类
    private void showAreaTypeView(){
        if (areas == null || areas.size() < 1) return;
        //二手房，租房一级列表
        if(areaView == null){
            areaView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    areaView.dismiss();
                    area = areas.get(position);

                    if("不限".equals(area.getName())){
                        category_area_txt.setText("区域");
                    }else{
                        category_area_txt.setText(area.getName());
                    }

                    r_area = area.getId();
                    isPullDown = true;
                    startRentHouseListTask(1, true);
                }
            });
        }

        List<String> areaNames = new ArrayList<String>();
        for (CommonType type : areas) {
            areaNames.add(type.getName());
        }
        areaView.fillData(areaNames);

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
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

                    if("不限".equals(type.getName())){
                        category_type_txt.setText("来源");
                    }else{
                        category_type_txt.setText(type.getName());
                    }

                    r_membertype = type.getId();
                    isPullDown = true;
                    startRentHouseListTask(1, true);
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
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
        typeView.showAsDropDown(search_title_view);
    }

    //价格分类
    private void showPriceView() {
        if (prices == null || prices.size() < 1) return;

        if(priceView == null){
            priceView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    priceView.dismiss();
                    price = prices.get(position);

                    if("不限".equals(price.getName())){
                        category_price_txt.setText("租金");
                    }else{
                        category_price_txt.setText(price.getName());
                    }

                    r_price = price.getId();
                    isPullDown = true;
                    startRentHouseListTask(1, true);
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
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
        priceView.showAsDropDown(search_title_view);
    }

    //更多分类
    private void showMoreTypeView(){
        if (mores == null || mores.size() < 1) return;

        if(moreView == null){
            moreView = new MultyTypeListPopupWindow(mContext, new MultyTypeListPopupWindow.MultyTypeListClickListener() {

                @Override
                public void onConfirmClick(HashMap<String, String> selectIndex) {

                    //重置更多参数
                    r_sort = "";
                    r_houseType = "";
                    r_areaZone = "";
                    r_isShare = "";
                    r_fitment = "";

                    //根据选中类型的索引，给参数赋值
                    for(HashMap.Entry<String, String> entry : selectIndex.entrySet()){
                        //找到选中的不是第一个的分类的索引 ,key为分类的索引，value为分类下选中的索引
                        if(!"0".equals(entry.getValue())){
                            int index = Integer.parseInt(entry.getKey());
                            int childIndex = Integer.parseInt(entry.getValue());
                            String typeName = mores.get(index).getName();
                            if("排序".equals(typeName)){
                                r_sort = mores.get(index).getChild().get(childIndex).getId();
                            }else if("户型".equals(typeName)){
                                r_houseType = mores.get(index).getChild().get(childIndex).getId();
                            }else if("面积".equals(typeName)){
                                r_areaZone = mores.get(index).getChild().get(childIndex).getId();
                            }else if("整租/合租".equals(typeName)){
                                r_isShare = mores.get(index).getChild().get(childIndex).getId();
                            }else if("装修".equals(typeName)){
                                r_fitment = mores.get(index).getChild().get(childIndex).getId();
                            }
                        }
                    }
                    isPullDown = true;
                    startRentHouseListTask(1, true);

                }

                @Override
                public void onCancelClick() {
                    //重置更多参数
                    r_sort = "";
                    r_houseType = "";
                    r_areaZone = "";
                    r_isShare = "";
                    r_fitment = "";

                    isPullDown = true;
                    startRentHouseListTask(1, true);
                }
            });
        }
        moreView.fillParentData(mores);

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        moreView.showAsDropDown(search_title_view);
    }

    public void closeALLTypeView() {
        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
    }


    /*************************************** 获取租房筛选条件   ********************************************/

    private void startOldRentConditionTask() {
        if(NetUtil.detectAvailable(mContext)){
            OldRentConditionListRequest request = new OldRentConditionListRequest(modelApp.getSite().getSiteId(),
                    new RequestListener() {

                        @Override
                        public void sendMessage(Message message) {
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                case Constants.NO_DATA_FROM_NET:
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    getRentHouseSearchTitleData(message.getData());
                                    break;
                            }
                        }
                    });
            request.doRequest();
        }
    }

    private void getRentHouseSearchTitleData(Bundle conditionData) {

        if(conditionData == null) return;

        //区域
        ArrayList<Area> areaList = (ArrayList<Area>) conditionData.getSerializable("areaList");
        if(areaList != null && areaList.size() > 0){
            //不限
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

        //来源
        ArrayList<CommonType> memberTypeList = (ArrayList<CommonType>) conditionData.getSerializable("memberTypeList");
        if(memberTypeList != null && memberTypeList.size() > 0){
            for(CommonType memberType : memberTypeList){
                CommonType type = new CommonType();
                type.setId(memberType.getId());
                type.setName(memberType.getName());
                types.add(type);
            }
        }

        //租金
        ArrayList<CommonType> priceZoneHireList = (ArrayList<CommonType>) conditionData.getSerializable("priceZoneHireList");
        if(priceZoneHireList != null && priceZoneHireList.size() > 0){
            for(CommonType priceZoneHire : priceZoneHireList){
                CommonType type = new CommonType();
                type.setId(priceZoneHire.getId());
                type.setName(priceZoneHire.getName());
                prices.add(type);
            }
        }

        //更多
        ArrayList<CommonType> sortHireList = (ArrayList<CommonType>) conditionData.getSerializable("sortHireList");
        if(sortHireList != null && sortHireList.size() > 0){
            CommonType sort = new CommonType();
            sort.setId("");
            sort.setName("排序");
            sort.setChild(sortHireList);
            mores.add(sort);
        }
        ArrayList<CommonType> roomTypeList = (ArrayList<CommonType>) conditionData.getSerializable("roomTypeList");
        if(roomTypeList != null && roomTypeList.size() > 0){
            CommonType roomType = new CommonType();
            roomType.setId("");
            roomType.setName("户型");
            roomType.setChild(roomTypeList);
            mores.add(roomType);
        }
        ArrayList<CommonType> areaZoneList = (ArrayList<CommonType>) conditionData.getSerializable("areaZoneList");
        if(areaZoneList != null && areaZoneList.size() > 0){
            CommonType areaZone = new CommonType();
            areaZone.setId("");
            areaZone.setName("面积");
            areaZone.setChild(areaZoneList);
            mores.add(areaZone);
        }
        ArrayList<CommonType> isShareList = (ArrayList<CommonType>) conditionData.getSerializable("isShareList");
        if(isShareList != null && isShareList.size() > 0){
            CommonType isShare = new CommonType();
            isShare.setId("");
            isShare.setName("整租/合租");
            isShare.setChild(isShareList);
            mores.add(isShare);
        }
        ArrayList<CommonType> fitmentList = (ArrayList<CommonType>) conditionData.getSerializable("fitmentList");
        if(fitmentList != null && fitmentList.size() > 0){
            CommonType fitment = new CommonType();
            fitment.setId("");
            fitment.setName("装修");
            fitment.setChild(fitmentList);
            mores.add(fitment);
        }
    }

    private RentHouseListRequest rentHouseListRequest;
    //请求参数
    private String r_area;			//区域
    private String r_membertype;	//来源
    private String r_price;			//租金
    private String r_sort;			//排序
    private String r_houseType;		//户型
    private String r_areaZone;		//面积
    private String r_isShare;		//整租、合租
    private String r_fitment;		//装修

    private String getRentParams(){
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isEmpty(r_area)){
            sb.append("&area=" + r_area);
        }
        if(!StringUtil.isEmpty(r_membertype)){
            sb.append("&membertype=" + r_membertype);
        }
        if(!StringUtil.isEmpty(r_price)){
            sb.append("&price=" + r_price);
        }
        if(!StringUtil.isEmpty(r_sort)){
            sb.append("&sort=" + r_sort);
        }
        if(!StringUtil.isEmpty(r_houseType)){
            sb.append("&houseType=" + r_houseType);
        }
        if(!StringUtil.isEmpty(r_areaZone)){
            sb.append("&areaZone=" + r_areaZone);
        }
        if(!StringUtil.isEmpty(r_isShare)){
            sb.append("&isShare=" + r_isShare);
        }
        if(!StringUtil.isEmpty(r_fitment)){
            sb.append("&fitment=" + r_fitment);
        }
        if(!StringUtil.isEmpty(keyword)){
            try {
                sb.append("&projname=" + URLEncoder.encode(keyword, "utf-8"));
            } catch (Exception e) {
                sb.append("&projname=" + keyword);
            }
        }

        return sb.toString();
    }

    private void startRentHouseListTask(int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(rentHouseListRequest == null){
                rentHouseListRequest = new RentHouseListRequest(modelApp.getSite().getSiteId(), page, pageSize,
                        getRentParams(), new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (rentHouseList == null || rentHouseList.size() == 0){
                                    fillCacheData();
                                }else{
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                error_lay.setVisibility(View.GONE);
                                content_lay.setVisibility(View.VISIBLE);
                                if (currentPageIndex == 1){
                                    Toast.makeText(mContext, "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                                    if(rentHouseList != null){
                                        rentHouseList.clear();
                                        fillRentHouseData();
                                    }
                                    house_count_txt.setText("0");
                                }
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                content_lay.setVisibility(View.VISIBLE);
                                error_lay.setVisibility(View.GONE);

                                house_count_txt.setText(String.valueOf(message.arg1));
                                ArrayList<OldHouse> temp = (ArrayList<OldHouse>) message.obj;
                                //根据返回的数据量判断是否隐藏加载更多
                                if(temp.size() < pageSize){
                                    house_listView.setPullLoadEnable(false);
                                }else{
                                    house_listView.setPullLoadEnable(true);
                                }

                                //如果是下拉刷新则索引恢复到1，并且清除掉之前数据
                                if(isPullDown && rentHouseList != null){
                                    if(rentHouseList.size() > 0){
                                        house_listView.smoothScrollToPosition(0);
                                    }
                                    rentHouseList.clear();
                                    currentPageIndex = 1;
                                }
                                rentHouseList.addAll(temp);
                                fillRentHouseData();
                                if (currentPageIndex > 1 && message.arg1 == rentHouseList.size()){
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
                rentHouseListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, getRentParams());
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            rentHouseListRequest.doRequest();

        }else {
            isPullDown = false;
            house_listView.stopRefresh();
            house_listView.stopLoadMore();
            if (rentHouseList == null || rentHouseList.size() == 0){
                fillCacheData();
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillRentHouseData() {
        if(rentHouseList == null) return;
        if(rentHouseAdapter == null){
            rentHouseAdapter = new OldHouseAdapter(mContext, rentHouseList, MapHousesActivity.HOUSE_TYPE_RENT);
            house_listView.setAdapter(rentHouseAdapter);
        }else {
            rentHouseAdapter.setData(rentHouseList);
        }
    }


    private void fillCacheData(){
        RentHouseListRequest request = new RentHouseListRequest();
        request.parseResult(AppCache.readRentHouseListJson(modelApp.getSite().getSiteId()));
        Bundle data = request.getCacheModel();
        house_count_txt.setText(String.valueOf(data.getInt("count")));
        rentHouseList = (ArrayList<OldHouse>) data.getSerializable("houseList");
        if (rentHouseList == null || rentHouseList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillRentHouseData();
        }
    }

}
