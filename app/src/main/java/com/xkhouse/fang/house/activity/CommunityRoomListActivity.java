package com.xkhouse.fang.house.activity;

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
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.house.adapter.OldHouseAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.OldHouse;
import com.xkhouse.fang.house.task.OldHouseListRequest;
import com.xkhouse.fang.house.task.OldRentConditionListRequest;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 小区相关房源列表
 *
 * wujian  2016-8-8
 */
public class CommunityRoomListActivity extends AppBaseActivity {

    //导航栏
    private ImageView iv_head_left;
    private TextView tv_head_title;

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
    private TextView category_space_txt;		//面积
    private TextView category_house_type_txt;	//户型
    private TextView category_price_txt;	//价格
    private TextView category_more_txt;		//更多

    private TypeListPopupWindow spaceView;
    private TypeListPopupWindow houseTypeView;
    private TypeListPopupWindow priceView;
    private MultyTypeListPopupWindow moreView;

    private List<CommonType> spaces = new ArrayList<CommonType>();
    private List<CommonType> houseTypes = new ArrayList<CommonType>();
    private List<CommonType> prices = new ArrayList<CommonType>();
    private List<CommonType> mores = new ArrayList<CommonType>();

    private CommonType space;
    private CommonType houseType;
    private CommonType price;


    private TextView house_count_txt;		//楼盘个数

    private String communityName;   //小区名


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
    }

    @Override
    protected void init() {
        super.init();
        if (getIntent().getExtras() != null){
            communityName = getIntent().getExtras().getString("communityName");
        }
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_community_room_list);
    }

    @Override
    protected void findViews() {
        super.findViews();

        initTitleView();

        search_title_view = (LinearLayout) findViewById(R.id.search_title_view);
        category_space_txt = (TextView) findViewById(R.id.category_space_txt);
        category_house_type_txt = (TextView) findViewById(R.id.category_house_type_txt);
        category_price_txt = (TextView) findViewById(R.id.category_price_txt);
        category_more_txt = (TextView) findViewById(R.id.category_more_txt);
        house_count_txt = (TextView) findViewById(R.id.house_count_txt);

        house_listView = (XListView) findViewById(R.id.house_listView);
        scroll_top_iv = (ImageView) findViewById(R.id.scroll_top_iv);

        content_lay = (LinearLayout) findViewById(R.id.content_lay);
        rotate_loading = (RotateLoading) findViewById(R.id.rotate_loading);
        error_lay = (LinearLayout) findViewById(R.id.error_lay);
    }


    private void initTitleView(){

        iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        iv_head_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityRoomListActivity.this.finish();
            }
        });

        tv_head_title.setText(communityName);
    }


    @Override
    protected void setListeners() {
        super.setListeners();

        error_lay.setOnClickListener(this);
        scroll_top_iv.setOnClickListener(this);

        category_space_txt.setOnClickListener(this);
        category_house_type_txt.setOnClickListener(this);
        category_price_txt.setOnClickListener(this);
        category_more_txt.setOnClickListener(this);


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

        switch (v.getId()) {

            case R.id.category_space_txt:
                showSpaceTypeView();
                break;

            case R.id.category_house_type_txt:
                showHouseTypeView();
                break;

            case R.id.category_price_txt:
                showPriceView();
                break;

            case R.id.category_more_txt:
                showMoreTypeView();
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
                if(spaces == null && spaces.size() == 0){
                    startOldRentConditionTask();
                }
                break;
        }
    }


    //面积分类
    private void showSpaceTypeView(){
        if (spaces == null || spaces.size() < 1) return;
        if(spaceView == null){
            spaceView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    spaceView.dismiss();
                    space = spaces.get(position);

                    if("不限".equals(space.getName())){
                        category_space_txt.setText("面积");
                    }else{
                        category_space_txt.setText(space.getName());
                    }

                    o_areaZone = space.getId();
                    isPullDown = true;
                    startOldHouseListTask(1, true);
                }
            });
        }

        List<String> spaceNames = new ArrayList<String>();
        for (CommonType type : spaces) {
            spaceNames.add(type.getName());
        }
        spaceView.fillData(spaceNames);

        if(houseTypeView != null && houseTypeView.isShowing())  houseTypeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
        spaceView.showAsDropDown(search_title_view);
    }


    //户型分类
    private void showHouseTypeView(){
        if (houseTypes == null || houseTypes.size() < 1) return;

        if(houseTypeView == null){
            houseTypeView = new TypeListPopupWindow(mContext, new TypeListPopupWindow.CommonTypeListClickListener() {

                @Override
                public void onTypeClick(int position) {
                    houseTypeView.dismiss();
                    houseType = houseTypes.get(position);

                    if("不限".equals(houseType.getName())){
                        category_house_type_txt.setText("户型");
                    }else{
                        category_house_type_txt.setText(houseType.getName());
                    }

                    o_houseType = houseType.getId();
                    isPullDown = true;
                    startOldHouseListTask(1, true);
                }
            });
        }

        List<String> typeNames = new ArrayList<String>();
        for (CommonType type : houseTypes) {
            typeNames.add(type.getName());
        }
        houseTypeView.fillData(typeNames);

        if(spaceView != null && spaceView.isShowing())  spaceView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
        houseTypeView.showAsDropDown(search_title_view);
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

        if(spaceView != null && spaceView.isShowing())  spaceView.dismiss();
        if(houseTypeView != null && houseTypeView.isShowing())  houseTypeView.dismiss();
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
                    o_roomFace = "";
                    o_buildingera = "";
                    o_membertype= "";
                    o_fitment = "";

                    isPullDown = true;
                    startOldHouseListTask(1, true);
                }
            });
        }
        moreView.fillParentData(mores);

        if(houseTypeView != null && houseTypeView.isShowing())  houseTypeView.dismiss();
        if(priceView != null && priceView.isShowing())  priceView.dismiss();
        if(spaceView != null && spaceView.isShowing())  spaceView.dismiss();
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
        //面积
        ArrayList<CommonType> spaceList = (ArrayList<CommonType>) conditionData.getSerializable("areaZoneList");
        if(spaceList != null && spaceList.size() > 0){
            //不限
            CommonType defaultType = new CommonType();
            defaultType.setId("0");
            defaultType.setName("不限");
            spaces.add(defaultType);
            for(CommonType space : spaceList){
                CommonType type = new CommonType();
                type.setId(space.getId());
                type.setName(space.getName());
                spaces.add(type);
            }
        }

        //户型
        ArrayList<CommonType> houseTypeList = (ArrayList<CommonType>) conditionData.getSerializable("roomTypeList");
        if(houseTypeList != null && houseTypeList.size() > 0){
            for(CommonType houseType : houseTypeList){
                CommonType type = new CommonType();
                type.setId(houseType.getId());
                type.setName(houseType.getName());
                houseTypes.add(type);
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

        //更多（排序，房龄，朝向，来源,装修）
        ArrayList<CommonType> sortList = (ArrayList<CommonType>) conditionData.getSerializable("sortList");
        if(sortList != null && sortList.size() > 0){
            CommonType sort = new CommonType();
            sort.setId("");
            sort.setName("排序");
            sort.setChild(sortList);
            mores.add(sort);
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
        try {
            sb.append("&projname=" + URLEncoder.encode(communityName, "utf-8"));
        } catch (Exception e) {
            sb.append("&projname=" + communityName);
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
                                Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
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

}
