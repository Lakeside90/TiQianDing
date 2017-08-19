package com.xkhouse.fang.money.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.model.LatLng;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseFragment;
import com.xkhouse.fang.app.activity.ModelApplication;
import com.xkhouse.fang.app.cache.AppCache;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.MapNewHouseConfigRequest;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.house.view.TypeListPopupWindow.CommonTypeListClickListener;
import com.xkhouse.fang.money.adapter.XKBHouseAdapter;
import com.xkhouse.fang.money.task.XKBHouseListRequest;
import com.xkhouse.fang.money.task.XKBOrderRequest;
import com.xkhouse.fang.user.activity.LoginActivity;
import com.xkhouse.fang.widget.loading.RotateLoading;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/** 
 * @Description:  赚佣(星空宝)
 * @author wujian  
 * @date 2015-8-25 下午4:28:25  
 */
public class MoneyFragment extends AppBaseFragment implements OnClickListener, AMapLocationListener{
	
	private View rootView;
	
	private ImageView make_money_iv;		//我要赚佣
	private LinearLayout house_search_bar;	//搜索
	private EditText house_search_txt;		

    private LinearLayout content_lay;   //显示的内容
    private ImageView blank_iv;         //没有数据视图
    private LinearLayout error_lay;         //加载失败，点击重新加载
    private RotateLoading rotate_loading;   //加载框

	//筛选条件
	private LinearLayout search_title_view;
	private TextView category_area_txt;		//区域
	private TextView category_type_txt;		//类型
	private TextView category_sort_txt;		//排序
	     
	private TypeListPopupWindow areaView;
	private TypeListPopupWindow typeView;
	private TypeListPopupWindow sortView;
	
	private List<CommonType> areas = new ArrayList<CommonType>();
	private List<CommonType> types = new ArrayList<CommonType>();
	private List<CommonType> sorts = new ArrayList<CommonType>();
	
	private CommonType area;
	private CommonType type;
	private CommonType sort;

	//房源列表
	private XListView house_listView;
	private XKBHouseAdapter adapter;
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
		
		getNewHouseSearchTitleData();
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
		
		if(!hidden){
			if(!modelApp.getSite().getSiteId().equals(siteID)){

                //清除上个站点数据以及选择的筛选条件
                if (houseList != null){
                    houseList.clear();
                    fillHouseData();
                }
                _areaId = "";
                _type = "";
                _order = "";
                house_search_txt.setText("");
                areaView = null;
                typeView = null;
                sortView = null;

				isPullDown = true;
				startHouseListTask(1, true);

                //初始化筛选条件
                category_area_txt.setText("区域");
                category_type_txt.setText("类型");
                category_sort_txt.setText("排序");
                area = null;
                type = null;
                sort = null;
                if (areas != null) areas.clear();
                if (types != null) types.clear();
                if (sorts != null) sorts.clear();
                getNewHouseSearchTitleData();

				siteID = modelApp.getSite().getSiteId();
			}else if (houseList == null || houseList.size() < 1 ){
                isPullDown = true;
                startHouseListTask(1, true);
                siteID = modelApp.getSite().getSiteId();
            }

		}
	}
	
	private void findViews() {
		initTitleView();
		initSearchTitleView();
		initHouseListView();

        content_lay = (LinearLayout) rootView.findViewById(R.id.content_lay);
        blank_iv = (ImageView) rootView.findViewById(R.id.blank_iv);
        error_lay = (LinearLayout) rootView.findViewById(R.id.error_lay);
        rotate_loading = (RotateLoading) rootView.findViewById(R.id.rotate_loading);

    }

	private void initTitleView(){
		make_money_iv = (ImageView) rootView.findViewById(R.id.make_money_iv);
		house_search_bar = (LinearLayout) rootView.findViewById(R.id.house_search_bar);
		house_search_txt = (EditText) rootView.findViewById(R.id.house_search_txt);

        make_money_iv.setOnClickListener(this);
		house_search_bar.setOnClickListener(this);
		//软键盘按下搜索键
		house_search_txt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

					closeSoftInput();

					isPullDown = true;
					startHouseListTask(1, true);

                    return true;
                }
				return false;
			}
		});
	}

	//筛选条件控件
	private void initSearchTitleView() {
		search_title_view = (LinearLayout) rootView.findViewById(R.id.search_title_view);
		category_area_txt = (TextView) rootView.findViewById(R.id.category_area_txt);
		category_type_txt = (TextView) rootView.findViewById(R.id.category_type_txt);
		category_sort_txt = (TextView) rootView.findViewById(R.id.category_sort_txt);
		
		category_area_txt.setOnClickListener(this);
		category_type_txt.setOnClickListener(this);
		category_sort_txt.setOnClickListener(this);
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

        house_listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeSoftInput();
                return false;
            }
        });
	}
	
	
	private void setListeners() {
        error_lay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

            case R.id.make_money_iv:
                if(Preference.getInstance().readIsLogin()){
                    getActivity().startActivity(new Intent(getActivity(), CustomerAddActivity.class));
                }else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("classStr", CustomerAddActivity.class);
                    getActivity().startActivity(intent);
                }

                break;

            case R.id.house_search_bar:
                // TODO go search activity
                break;

            case R.id.category_area_txt:
                showAreaTypeView();
                break;

            case R.id.category_type_txt:
                showTypeView();
                break;

            case R.id.category_sort_txt:
                showSortView();
                break;

            case R.id.error_lay:
                startHouseListTask(1, true);
                //重新获取筛选条件
                area = null;
                type = null;
                sort = null;
                if (areas != null) areas.clear();
                if (types != null) types.clear();
                if (sorts != null) sorts.clear();
                getNewHouseSearchTitleData();
                break;

		}
	}
	
	
	//区域分类
	private void showAreaTypeView(){
		if (areas == null || areas.size() < 1) return;
		
		if(areaView == null){
			areaView = new TypeListPopupWindow(getActivity(),  new CommonTypeListClickListener(){
                @Override
                public void onTypeClick(int position) {
                    areaView.dismiss();
					area = areas.get(position);
					_areaId = "";
					if(!StringUtil.isEmpty(area.getId())){
                        _areaId = area.getId();
					}
                    if("不限".equals(area.getName())){
                        category_area_txt.setText("区域");
                    }else{
                        category_area_txt.setText(area.getName());
                    }

                    if (houseList != null){
                        houseList.clear();
                        fillHouseData();
                    }
					isPullDown = true;
					startHouseListTask(1, true);
                }
            });
		}
        List<String> areaNames = new ArrayList<String>();
        for (CommonType area : areas) {
            areaNames.add(area.getName());
        }
        areaView.fillData(areaNames);
			
		if(typeView != null && typeView.isShowing())  typeView.dismiss();
		if(sortView != null && sortView.isShowing())  sortView.dismiss();
		areaView.showAsDropDown(search_title_view);
	}
			
			
	//类型分类
	private void showTypeView(){
		if (types == null || types.size() < 1) return;
		
		if(typeView == null){
			typeView = new TypeListPopupWindow(getActivity(), new CommonTypeListClickListener() {
				
				@Override
				public void onTypeClick(int position) {
					
					typeView.dismiss();
					type = types.get(position);
					_type = "";
					if(!StringUtil.isEmpty(type.getId())){   //不限的id是空的
						_type = type.getId();
					}

                    if("不限".equals(type.getName())){
                        category_type_txt.setText("类型");
                    }else{
                        category_type_txt.setText(type.getName());
                    }

                    if (houseList != null){
                        houseList.clear();
                        fillHouseData();
                    }
					isPullDown = true;
					startHouseListTask(1, true);
				}
			});
		}
		
		List<String> typeNames = new ArrayList<String>();
		for (CommonType type : types) {
			typeNames.add(type.getName());
		}
		typeView.fillData(typeNames);
		
		if(areaView != null && areaView.isShowing())  areaView.dismiss();
		if(sortView != null && sortView.isShowing())  sortView.dismiss();
		typeView.showAsDropDown(search_title_view);
	}
	
	//排序分类
	private void showSortView() {
		if (sorts == null || sorts.size() < 1) return;
		
		if(sortView == null){
			sortView = new TypeListPopupWindow(getActivity(), new CommonTypeListClickListener() {
				
				@Override
				public void onTypeClick(int position) {
					
					if("1".equals(sorts.get(position).getId()) && startLatlng == null){
						// 定位失败不能按距离排序
						Toast.makeText(getActivity(), "定位失败，不能按距离排序", Toast.LENGTH_SHORT).show();
						return;
					}

					sortView.dismiss();
					sort = sorts.get(position);
					_order = sort.getId();

                    if("默认排序".equals(sort.getName())){
                        category_sort_txt.setText("排序");
                    }else{
                        category_sort_txt.setText(sort.getName());
                    }

                    if (houseList != null){
                        houseList.clear();
                        fillHouseData();
                    }
					isPullDown = true;
					startHouseListTask(1, true);
				}
			});
		}
		
		List<String> priceNames = new ArrayList<String>();
		for (CommonType type : sorts) {
			priceNames.add(type.getName());
		}
		sortView.fillData(priceNames);
		
		if(areaView != null && areaView.isShowing())  areaView.dismiss();
		if(typeView != null && typeView.isShowing())  typeView.dismiss();
		sortView.showAsDropDown(search_title_view);
	}
	
	
	
	/*******************************  排序数据   ******************************/
	private HouseConfigDbService configDbService = new HouseConfigDbService();
	
	private void getNewHouseSearchTitleData() {
		//区域以及学校
		ArrayList<Area> areaList= configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
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
		
		//类型， 价格， 更多（排序 楼盘特色  户型 面积  装修状态  销售状态）（这几个数据同一个接口）
		ArrayList<CommonType> orderList = configDbService.getOrderListBySite(modelApp.getSite().getSiteId());
		if(orderList == null || orderList.size() < 1){
			startTypeTask();
		}else {
			ArrayList<CommonType> propertyList = configDbService.getPropertyListBySite(modelApp.getSite().getSiteId());
			
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
		}

        //排序
        if(sorts == null || sorts.size() < 1){
            startXKBOrderTask();
        }
	}
	
	private void startAreaTask(){
		if(NetUtil.detectAvailable(getActivity())){

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
	
	private void startTypeTask(){
		if(NetUtil.detectAvailable(getActivity())){
			MapNewHouseConfigRequest request = new MapNewHouseConfigRequest(modelApp.getSite().getSiteId(), "PROPERTY_TYPE",
					new RequestListener() {

				@Override
				public void sendMessage(Message message) {
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						break;

					case Constants.NO_DATA_FROM_NET:
						break;

					case Constants.SUCCESS_DATA_FROM_NET:

						ArrayList<CommonType> propertyList = configDbService.getPropertyListBySite(modelApp.getSite().getSiteId());

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

						break;
					}
				}
			});
			request.doRequest();
		}
	}
	
	
	private void startXKBOrderTask() {
		if(NetUtil.detectAvailable(getActivity())){
			
			XKBOrderRequest request = new XKBOrderRequest(new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						break;
						
					case Constants.NO_DATA_FROM_NET:
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						
						ArrayList<CommonType> orderList = (ArrayList<CommonType>) message.obj;
						//排序
						if(orderList != null && orderList.size() > 0){
							for(CommonType order : orderList){
								CommonType type = new CommonType();
								type.setId(order.getId());
								type.setName(order.getName());
								sorts.add(type);
							}
						}
						break;
					}
				}
			});
			request.doRequest();
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
		if(!StringUtil.isEmpty(house_search_txt.getText().toString())){
			sb.append("&k=" + house_search_txt.getText().toString());
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
			adapter = new XKBHouseAdapter(getActivity(), houseList, startLatlng);
			house_listView.setAdapter(adapter);
		}else{
			adapter.setData(houseList, startLatlng);
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


    public boolean closeTypeView(){
        if (areaView != null && areaView.isShowing()){
            areaView.dismiss();
            return true;
        }
        if (typeView != null && typeView.isShowing()){
            typeView.dismiss();
            return true;
        }
        if (typeView != null && typeView.isShowing()){
            typeView.dismiss();
            return true;
        }
        if (sortView != null && sortView.isShowing()){
            sortView.dismiss();
            return true;
        }
        return false;
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
	
	private void closeSoftInput(){
		InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(house_search_txt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}


}
