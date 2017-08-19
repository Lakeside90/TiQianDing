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
import com.xkhouse.fang.house.task.OldHouseListRequest;
import com.xkhouse.fang.house.task.OldRentConditionListRequest;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.user.activity.LoginActivity;
import com.xkhouse.fang.user.activity.SellInActivity;
import com.xkhouse.fang.user.activity.SellReleaseActivity;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 二手房房源列表
 */
public class OldHouseListActivity extends AppBaseActivity {

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
    private OldHouseAdapter oldHouseAdapter;
    private ArrayList<OldHouse> oldHouseList = new ArrayList<OldHouse>();

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
    private TextView sell_in_txt;		//我要买房
    private TextView sell_release_txt;		//我要出售

    private String keyword;


    private LinearLayout content_lay;
    private RotateLoading rotate_loading;
    private LinearLayout error_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startOldHouseListTask(1, true);
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
        setContentView(R.layout.activity_old_house_list);
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
        sell_in_txt = (TextView) findViewById(R.id.sell_in_txt);
        sell_release_txt = (TextView) findViewById(R.id.sell_release_txt);

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

        sell_in_txt.setOnClickListener(this);
        sell_release_txt.setOnClickListener(this);

        house_listView.setPullLoadEnable(true);
        house_listView.setPullRefreshEnable(true);
        house_listView.setXListViewListener(new XListView.IXListViewListener() {

            @Override
            public void onRefresh() {
                isPullDown = true;
                startOldHouseListTask(1, false);
            }

            @Override
            public void onLoadMore() {
                startOldHouseListTask(currentPageIndex, false);
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

        Intent intent;
        Bundle data;

        switch (v.getId()) {
            case R.id.back_iv:
                this.finish();
                break;

            case R.id.map_iv:
                data = new Bundle();
                data.putInt("houseType", MapHousesActivity.HOUSE_TYPE_OLD);
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

            case R.id.sell_in_txt:
                if(Preference.getInstance().readIsLogin()){
                    if(Constants.USER_GE_REN.equals(modelApp.getUser().getMemberType())){
                        startActivity(new Intent(OldHouseListActivity.this, SellInActivity.class));
                    }else{
                        Toast.makeText(OldHouseListActivity.this, "您不是个人用户，无法发布求购信息", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(OldHouseListActivity.this, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OldHouseListActivity.this, LoginActivity.class));
                }
                break;

            case R.id.sell_release_txt:
                if(Preference.getInstance().readIsLogin()){
                    startActivity(new Intent(OldHouseListActivity.this, SellReleaseActivity.class));
                }else {
                    Toast.makeText(OldHouseListActivity.this, "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OldHouseListActivity.this, LoginActivity.class));
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
                startOldHouseListTask(1, true);
                if(areas == null && areas.size() == 0){
                    startOldRentConditionTask();
                }
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

                    if("不限".equals(area.getName())){
                        category_area_txt.setText("区域");
                    }else{
                        category_area_txt.setText(area.getName());
                    }

                    o_area = area.getId();
                    isPullDown = true;
                    startOldHouseListTask(1, true);
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
                        category_type_txt.setText("类型");
                    }else{
                        category_type_txt.setText(type.getName());
                    }

                    o_type = type.getId();
                    isPullDown = true;
                    startOldHouseListTask(1, true);
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
                        category_price_txt.setText("单价");
                    }else{
                        category_price_txt.setText(price.getName());
                    }

                    o_price = price.getId();
                    isPullDown = true;
                    startOldHouseListTask(1, true);
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
                    o_sort = "";
                    o_houseType = "";
                    o_areaZone = "";
                    o_roomFace = "";
                    o_buildingera = "";
                    o_membertype= "";
                    o_fitment = "";

                    //根据选中类型的索引，给参数赋值
                    for(HashMap.Entry<String, String> entry : selectIndex.entrySet()){
                        //找到选中的不是第一个的分类的索引 ,key为分类的索引，value为分类下选中的索引
                        if(!"0".equals(entry.getValue())){
                            int index = Integer.parseInt(entry.getKey());
                            int childIndex = Integer.parseInt(entry.getValue());
                            String typeName = mores.get(index).getName();
                            if("排序".equals(typeName)){
                                o_sort = mores.get(index).getChild().get(childIndex).getId();
                            }else if("户型".equals(typeName)){
                                o_houseType = mores.get(index).getChild().get(childIndex).getId();
                            }else if("面积".equals(typeName)){
                                o_areaZone = mores.get(index).getChild().get(childIndex).getId();
                            }else if("朝向".equals(typeName)){
                                o_roomFace = mores.get(index).getChild().get(childIndex).getId();
                            }else if("房龄".equals(typeName)){
                                o_buildingera = mores.get(index).getChild().get(childIndex).getId();
                            }else if("来源".equals(typeName)){
                                o_membertype = mores.get(index).getChild().get(childIndex).getId();
                            }else if("装修".equals(typeName)){
                                o_fitment = mores.get(index).getChild().get(childIndex).getId();
                            }
                        }
                    }
                    isPullDown = true;
                    startOldHouseListTask(1, true);
                }

                @Override
                public void onCancelClick() {
                    //重置更多参数
                    o_sort = "";
                    o_houseType = "";
                    o_areaZone = "";
                    o_roomFace = "";
                    o_buildingera = "";
                    o_membertype= "";
                    o_fitment= "";

                    isPullDown = true;
                    startOldHouseListTask(1, true);
                }
            });
        }
        moreView.fillParentData(mores);

        if(typeView != null && typeView.isShowing())  typeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(areaView != null && areaView.isShowing())  areaView.dismiss();
        moreView.showAsDropDown(search_title_view);
    }


    /*************************************** 获取二手房筛选条件   ********************************************/
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
                                    getOldHouseSearchTitleData(message.getData());
                                    break;
                            }
                        }
                    });
            request.doRequest();
        }
    }

    private void getOldHouseSearchTitleData(Bundle conditionData) {

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

        //物业类型
        ArrayList<CommonType> houseTypeList = (ArrayList<CommonType>) conditionData.getSerializable("houseTypeList");
        if(houseTypeList != null && houseTypeList.size() > 0){
            for(CommonType houseType : houseTypeList){
                CommonType type = new CommonType();
                type.setId(houseType.getId());
                type.setName(houseType.getName());
                types.add(type);
            }
        }

        //单价
        ArrayList<CommonType> priceZoneList = (ArrayList<CommonType>) conditionData.getSerializable("priceZoneList");
        if(priceZoneList != null && priceZoneList.size() > 0){
            for(CommonType priceZone : priceZoneList){
                CommonType type = new CommonType();
                type.setId(priceZone.getId());
                type.setName(priceZone.getName());
                prices.add(type);
            }
        }

        //更多（排序，户型，面积，房龄，朝向，来源,装修）
        ArrayList<CommonType> sortList = (ArrayList<CommonType>) conditionData.getSerializable("sortList");
        if(sortList != null && sortList.size() > 0){
            CommonType sort = new CommonType();
            sort.setId("");
            sort.setName("排序");
            sort.setChild(sortList);
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
        ArrayList<CommonType> buildingAgeList = (ArrayList<CommonType>) conditionData.getSerializable("buildingAgeList");
        if(buildingAgeList != null && buildingAgeList.size() > 0){
            CommonType buildingAge = new CommonType();
            buildingAge.setId("");
            buildingAge.setName("房龄");
            buildingAge.setChild(buildingAgeList);
            mores.add(buildingAge);
        }
        ArrayList<CommonType> roomFaceList = (ArrayList<CommonType>) conditionData.getSerializable("roomFaceList");
        if(roomFaceList != null && roomFaceList.size() > 0){
            CommonType roomFace = new CommonType();
            roomFace.setId("");
            roomFace.setName("朝向");
            roomFace.setChild(roomFaceList);
            mores.add(roomFace);
        }
        ArrayList<CommonType> memberTypeList = (ArrayList<CommonType>) conditionData.getSerializable("memberTypeList");
        if(memberTypeList != null && memberTypeList.size() > 0){
            CommonType memberType = new CommonType();
            memberType.setId("");
            memberType.setName("来源");
            memberType.setChild(memberTypeList);
            mores.add(memberType);
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

    private OldHouseListRequest oldHouseListRequest;
    //请求参数
    private String o_area;			//区域
    private String o_type;			//房屋类型
    private String o_price;			//价格
    private String o_sort;			//排序
    private String o_houseType;		//户型
    private String o_areaZone;		//面积
    private String o_roomFace;		//朝向
    private String o_buildingera;	//房龄
    private String o_membertype;	//来源
    private String o_fitment;		//装修

    private String getOldParams(){
        StringBuilder sb = new StringBuilder();
        if(!StringUtil.isEmpty(o_area)){
            sb.append("&area=" + o_area);
        }
        if(!StringUtil.isEmpty(o_type)){
            sb.append("&type=" + o_type);
        }
        if(!StringUtil.isEmpty(o_price)){
            sb.append("&price=" + o_price);
        }
        if(!StringUtil.isEmpty(o_sort)){
            sb.append("&sort=" + o_sort);
        }
        if(!StringUtil.isEmpty(o_houseType)){
            sb.append("&houseType=" + o_houseType);
        }
        if(!StringUtil.isEmpty(o_areaZone)){
            sb.append("&areaZone=" + o_areaZone);
        }
        if(!StringUtil.isEmpty(o_roomFace)){
            sb.append("&roomFace=" + o_roomFace);
        }
        if(!StringUtil.isEmpty(o_buildingera)){
            sb.append("&buildingera=" + o_buildingera);
        }
        if(!StringUtil.isEmpty(o_membertype)){
            sb.append("&membertype=" + o_membertype);
        }
        if(!StringUtil.isEmpty(o_fitment)){
            sb.append("&fitment=" + o_fitment);
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

    private void startOldHouseListTask(final int page, boolean showLoading){
        if (NetUtil.detectAvailable(mContext)) {
            if(oldHouseListRequest == null){
                oldHouseListRequest = new OldHouseListRequest(modelApp.getSite().getSiteId(), page, pageSize,
                        getOldParams(), new RequestListener() {

                    @Override
                    public void sendMessage(Message message) {
                        rotate_loading.stop();
                        rotate_loading.setVisibility(View.GONE);

                        if (isPullDown){
                            currentPageIndex = 1;
                        }
                        switch (message.what) {
                            case Constants.ERROR_DATA_FROM_NET:
                                if (oldHouseList == null || oldHouseList.size() == 0){
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
                                    if(oldHouseList != null){
                                        oldHouseList.clear();
                                        fillOldHouseData();
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
                                if(isPullDown && oldHouseList != null){
                                    if(oldHouseList.size() > 0){
                                        house_listView.smoothScrollToPosition(0);
                                    }
                                    oldHouseList.clear();
                                    currentPageIndex = 1;
                                }
                                oldHouseList.addAll(temp);
                                fillOldHouseData();
                                if (currentPageIndex > 1 && message.arg1 == oldHouseList.size()){
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
                oldHouseListRequest.setData(modelApp.getSite().getSiteId(), page, pageSize, getOldParams());
            }
            if(showLoading){
                content_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                rotate_loading.setVisibility(View.VISIBLE);
                rotate_loading.start();
            }
            oldHouseListRequest.doRequest();

        }else {
            isPullDown = false;
            house_listView.stopRefresh();
            house_listView.stopLoadMore();
            if (oldHouseList == null || oldHouseList.size() == 0){
                fillCacheData();
            }else{
                Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fillOldHouseData() {
        if(oldHouseList == null) return;
        if(oldHouseAdapter == null){
            oldHouseAdapter = new OldHouseAdapter(mContext, oldHouseList, MapHousesActivity.HOUSE_TYPE_OLD);
            house_listView.setAdapter(oldHouseAdapter);
        }else {
            oldHouseAdapter.setData(oldHouseList);
        }
    }


    private void fillCacheData(){
        OldHouseListRequest request = new OldHouseListRequest();
        request.parseResult(AppCache.readOldHouseListJson(modelApp.getSite().getSiteId()));
        Bundle data = request.getCacheModel();
        house_count_txt.setText(String.valueOf(data.getInt("count")));
        oldHouseList = (ArrayList<OldHouse>) data.getSerializable("houseList");
        if (oldHouseList == null || oldHouseList.size() ==0 ){
            content_lay.setVisibility(View.GONE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }else{
            content_lay.setVisibility(View.VISIBLE);
            rotate_loading.setVisibility(View.GONE);
            error_lay.setVisibility(View.GONE);
            currentPageIndex++;
            fillOldHouseData();
        }
    }


}
