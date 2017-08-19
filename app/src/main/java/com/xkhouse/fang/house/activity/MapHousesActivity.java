package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapTouchListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.VisibleRegion;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.entity.House;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.fang.house.adapter.MapOldHouseAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.entity.OldHouse;
import com.xkhouse.fang.house.entity.XKCommunity;
import com.xkhouse.fang.house.entity.XKRoom;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.MapNewHouseCountRequest;
import com.xkhouse.fang.house.task.MapOldHouseCommunityCountRequest;
import com.xkhouse.fang.house.task.MapOldHouseCountRequest;
import com.xkhouse.fang.house.task.MapOldHouseListRequest;
import com.xkhouse.fang.house.task.MapRentHouseCountRequest;
import com.xkhouse.fang.house.task.MapNewHouseConfigRequest;
import com.xkhouse.fang.house.task.NewHouseListRequest;
import com.xkhouse.fang.house.task.OldRentConditionListRequest;
import com.xkhouse.fang.house.task.RentHouseListRequest;
import com.xkhouse.fang.house.task.SchoolListRequest;
import com.xkhouse.fang.house.view.MapTypeListPopupWindow;
import com.xkhouse.fang.house.view.MapTypeListPopupWindow.MapCommonTypeListClickListener;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow;
import com.xkhouse.fang.house.view.MultyTypeListPopupWindow.MultyTypeListClickListener;
import com.xkhouse.fang.house.view.TypeListPopupWindow;
import com.xkhouse.fang.house.view.TypeListPopupWindow.ChildTypeListClickListener;
import com.xkhouse.fang.house.view.TypeListPopupWindow.CommonTypeListClickListener;
import com.xkhouse.fang.widget.xlist.XListView;
import com.xkhouse.fang.widget.xlist.XListView.IXListViewListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * @Description: 地图找房 
 * @author wujian  
 * @date 2015-9-14 下午5:52:16  
 */
public class MapHousesActivity extends AppBaseActivity implements OnMarkerClickListener, 
							OnMapTouchListener, OnMapClickListener, OnCameraChangeListener{

	//title
	private ImageView back_iv;
	private LinearLayout house_search_bar;
	private EditText house_search_txt;
	
	//筛选
	private LinearLayout search_title_view;
	private TextView category_area_txt;
	private TextView category_type_txt;
	private TextView category_price_txt;
	private TextView category_more_txt;
	
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
	
	private ArrayList<House> newHouseList = new ArrayList<House>();
	private ArrayList<XKCommunity> oldCommunityList = new ArrayList<XKCommunity>();
	private ArrayList<OldHouse> rentHouseList = new ArrayList<OldHouse>();
	
	//地图相关
	private MapView mapView;
	private AMap aMap;
	private LatLng northeast;  //地图可视范围内东北角坐标
	private LatLng southwest;  //地图可视范围内西南角坐标
	
	
	private final float zoomTag = 13.0f;  //当地图的缩放级别大于等于13时，按楼盘显示，小于13时显示每个区域的楼盘个数
	private final float zoomTag_old = 16.0f;  //二手房，租房房源太多，地图缩放级别太小的话，出现OOM，所以设置成16
	
	private float currentZoom = 11.0f;
	private List<MarkerOptions> houseMarkers = new ArrayList<MarkerOptions>();  //楼盘标记
	private List<MarkerOptions> areaMarkers = new ArrayList<MarkerOptions>();  //行政区域楼盘数标记
	
	/** 新房 **/
	public static final int HOUSE_TYPE_NEW = 0;
	/** 二手房 **/
	public static final int HOUSE_TYPE_OLD = 1;
	/** 租房 **/
	public static final int HOUSE_TYPE_RENT = 2;
	
	private int houseType = HOUSE_TYPE_NEW;
	
	/**  筛选条件的区域 **/
	private ArrayList<Area> areaList = new ArrayList<Area>();
	
	
	
	//地图下面的房源列表
	private LinearLayout map_house_lay;
	private TextView map_house_name_txt;
	private TextView map_house_type_txt;
	private XListView map_house_listview;
	private int currentPageIndex = 1;  //分页索引
	private int pageSize = 10; //每次请求10条数据
	private MapOldHouseAdapter mapOldHouseAdapter;
	
	private MapTypeListPopupWindow mapTypeView;
	private ArrayList<CommonType> roomTypeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		initMapView();
		
		if(houseType == HOUSE_TYPE_NEW){
			startNewHouseCountTask();
			getNewHouseSearchTitleData();
			
		} else if(houseType == HOUSE_TYPE_OLD) {
			startOldRentConditionTask();
			startOldHouseCountTask();
			
		} else if(houseType == HOUSE_TYPE_RENT) {
			startOldRentConditionTask();
			startRentHouseCountTask();
		}
		
		
		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_map_houses);
	}
	
	@Override
	protected void init() {
		super.init();
		houseType = getIntent().getExtras().getInt("houseType");
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		back_iv = (ImageView) findViewById(R.id.back_iv);
		house_search_bar = (LinearLayout) findViewById(R.id.house_search_bar);
		house_search_txt = (EditText) findViewById(R.id.house_search_txt);
        //软键盘按下搜索键
		house_search_txt.setOnEditorActionListener(new OnEditorActionListener() {
					
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
					
					closeSoftInput();
					
					if(houseType == HOUSE_TYPE_NEW){
						dispatchNewHouseTask();
					}else if(houseType == HOUSE_TYPE_OLD){
						dispatchOldHouseTask();
					}
                    return true;
                }
				return false;
			}
		});
				
		search_title_view = (LinearLayout) findViewById(R.id.search_title_view);
		category_area_txt = (TextView) findViewById(R.id.category_area_txt);
		category_type_txt = (TextView) findViewById(R.id.category_type_txt);
		category_price_txt = (TextView) findViewById(R.id.category_price_txt);
		category_more_txt = (TextView) findViewById(R.id.category_more_txt);
		
		initMapHouseView();
		
		if(houseType == HOUSE_TYPE_NEW){
			house_search_txt.setHint("请输入楼盘名");
		}else{
			house_search_txt.setHint("请输入小区名");
		}
		
	}
	
	private void initMapView() {
		
		if(aMap == null){
			aMap = mapView.getMap();
			aMap.setOnMapTouchListener(this);
			aMap.setOnMarkerClickListener(this);
			aMap.setOnMapClickListener(this);
			aMap.setOnCameraChangeListener(this);
		}
	
		LatLng currentLatLng = new LatLng(Double.parseDouble(modelApp.getSite().getLatitude()),
				Double.parseDouble(modelApp.getSite().getLongitude())); 
		aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11));
	}
	
	private void initMapHouseView() {
		map_house_lay = (LinearLayout) findViewById(R.id.map_house_lay);
		map_house_name_txt = (TextView) findViewById(R.id.map_house_name_txt);
		map_house_type_txt = (TextView) findViewById(R.id.map_house_type_txt);
		map_house_listview = (XListView) findViewById(R.id.map_house_listview);
		
		if(houseType == HOUSE_TYPE_NEW){
			map_house_lay.setVisibility(View.GONE);
			category_type_txt.setText("类型");
			category_price_txt.setText("价格");
			
		}else if(houseType == HOUSE_TYPE_OLD) {
			map_house_lay.setVisibility(View.GONE);
			map_house_type_txt.setText("户型");
			category_type_txt.setText("类型");
			category_price_txt.setText("单价");
			
		}else if(houseType == HOUSE_TYPE_RENT) {
			map_house_lay.setVisibility(View.GONE);
			map_house_type_txt.setText("租金");
			category_type_txt.setText("来源");
			category_price_txt.setText("租金");
			
		}
		
		map_house_type_txt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMapTypeView();
			}
		});
		
		map_house_listview.setPullLoadEnable(true);
		map_house_listview.setPullRefreshEnable(false);
		map_house_listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {}
			
			@Override
			public void onLoadMore() {
				// TODO
			}
		}, R.id.map_house_listview);
	}
	
	private void showAreaMarks() {
		if(areaMarkers == null) return;
		aMap.clear();
		for (int i = 0; i < areaMarkers.size(); i++) {
			MarkerOptions options = areaMarkers.get(i); 
			View view = LayoutInflater.from(mContext).inflate(R.layout.map_area_item, null);
			TextView map_area_name_txt = (TextView) view.findViewById(R.id.map_area_name_txt);
			TextView map_area_count_txt = (TextView) view.findViewById(R.id.map_area_count_txt);
			map_area_name_txt.setText(options.getTitle());
			map_area_count_txt.setText(options.getSnippet());
			options.icon(BitmapDescriptorFactory.fromView(view));
			aMap.addMarker(options);
		}
		
	}
	
	private void showHouseMarks() {
		if(houseMarkers == null) return;
		aMap.clear();
		for(MarkerOptions options : houseMarkers){
			View view = LayoutInflater.from(mContext).inflate(R.layout.map_house_item, null);
			TextView map_house_txt = (TextView) view.findViewById(R.id.map_house_txt);
			map_house_txt.setText(options.getTitle());
			options.icon(BitmapDescriptorFactory.fromView(view));
			aMap.addMarker(options);
		}
	}	
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		back_iv.setOnClickListener(this);
		house_search_bar.setOnClickListener(this);
		search_title_view.setOnClickListener(this);
		category_area_txt.setOnClickListener(this);
		category_type_txt.setOnClickListener(this);
		category_price_txt.setOnClickListener(this);
		category_more_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back_iv:
			if(map_house_lay.getVisibility() == View.VISIBLE) {
				closeMapHouseLay();
				return;
			}
            closeSoftInput();
            //解決黑屏問題
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
			break;

		case R.id.house_search_bar:
			
			break;

		case R.id.search_title_view:
	
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
		
		}
	}
	
	//区域分类
	private void showAreaTypeView(){
		if (areas == null || areas.size() < 1) return;
		if(houseType == HOUSE_TYPE_NEW){
			//新房两级列表（区域，学区）
			if(areaView == null){
				areaView = new TypeListPopupWindow(mContext, false, new ChildTypeListClickListener() {
					
					@Override
					public void onParentClick(int position) {
					}
					
					@Override
					public void onChildClick(int parent, int child) {
						areaView.dismiss();
						area = areas.get(parent).getChild().get(child);
						_areaId = "";
						if(!StringUtil.isEmpty(area.getId())){
							if("区域".equals(areas.get(parent).getName())){
								_areaId = area.getId();
								//跳转到指定区域
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getAreaLatLng(_areaId), zoomTag));
							}
						}else{
							//不限区域
							if("区域".equals(areas.get(parent).getName())){
								_areaId = "";
								LatLng currentLatLng = new LatLng(Double.parseDouble(modelApp.getSite().getLatitude()),
										Double.parseDouble(modelApp.getSite().getLongitude())); 
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11));
								showAreaMarks();
							}
						}
						
					}
				});
				areaView.fillParentData(areas);
			}
		}else {
			//二手房，租房一级列表
			if(areaView == null){
				areaView = new TypeListPopupWindow(mContext, new CommonTypeListClickListener() {
					
					@Override
					public void onTypeClick(int position) {
						areaView.dismiss();
						area = areas.get(position);
						
						if(houseType == HOUSE_TYPE_OLD){
							
							//坑爹的不限
							if("不限".equals(area.getName())){
								o_area = "";
								LatLng currentLatLng = new LatLng(Double.parseDouble(modelApp.getSite().getLatitude()),
										Double.parseDouble(modelApp.getSite().getLongitude())); 
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11));
								showAreaMarks();
							}else{
								o_area = area.getId();
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getAreaLatLng(o_area), zoomTag_old));
							}
							
						}else if(houseType == HOUSE_TYPE_RENT){
							if("不限".equals(area.getName())){
								o_area = "";
								LatLng currentLatLng = new LatLng(Double.parseDouble(modelApp.getSite().getLatitude()),
										Double.parseDouble(modelApp.getSite().getLongitude())); 
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 11));
								showAreaMarks();
							}else {
								r_area = area.getId();
								aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getAreaLatLng(r_area), zoomTag_old));
							}
							
						}
						
					}
				});
			}
			
			List<String> areaNames = new ArrayList<String>();
			for (CommonType type : areas) {
				areaNames.add(type.getName());
			}
			areaView.fillData(areaNames);
		}
			
		if(typeView != null && typeView.isShowing())  typeView.dismiss();
		if(priceView != null && priceView.isShowing())  priceView.dismiss();
		if(moreView != null && moreView.isShowing())  moreView.dismissWithResetData();
		areaView.showAsDropDown(search_title_view);
	}
			
			
	//类型分类
	private void showTypeView(){
		if (types == null || types.size() < 1) return;
		
		if(typeView == null){
			typeView = new TypeListPopupWindow(mContext, new CommonTypeListClickListener() {
				
				@Override
				public void onTypeClick(int position) {
					typeView.dismiss();
					type = types.get(position);
					
					if(houseType == HOUSE_TYPE_NEW){
						//新房
						if(!StringUtil.isEmpty(type.getId())){   //不限的id是空的
							_propertyType = type.getId();
						}else{
							_propertyType = "";
						}
						dispatchNewHouseTask();
						
					}else if(houseType == HOUSE_TYPE_OLD){
						//二手房
						o_type = type.getId();
						dispatchOldHouseTask();
						
					}else if(houseType == HOUSE_TYPE_RENT){
						//租房
						r_membertype = type.getId();
						startRentHouseListTask();
					}
					
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
			priceView = new TypeListPopupWindow(mContext, new CommonTypeListClickListener() {
				
				@Override
				public void onTypeClick(int position) {
					priceView.dismiss();
					price = prices.get(position);
					if(houseType == HOUSE_TYPE_NEW){
						//新房
						if(!StringUtil.isEmpty(price.getId())) {
							_price = price.getId();
						}else{
							_price = "";
						}
						dispatchNewHouseTask();
						
					}else if(houseType == HOUSE_TYPE_OLD){
						//二手房
						o_price = price.getId();
						dispatchOldHouseTask();
						
					}else if(houseType == HOUSE_TYPE_RENT){
						//租房
						r_price = price.getId();
						startRentHouseListTask();
						
					}
					
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
			moreView = new MultyTypeListPopupWindow(mContext, new MultyTypeListClickListener() {
				
				@Override
				public void onConfirmClick(HashMap<String, String> selectIndex) {
					if(houseType == HOUSE_TYPE_NEW){
						//新房
						//重置更多参数
						_order = "";
						_feature = "";
						_houseType = "";
						_areaSize = "";
						_renovateState = "";
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
								}else if("装修状态".equals(typeName)){
									_renovateState = mores.get(index).getChild().get(childIndex).getId();
								}else if("销售状态".equals(typeName)){
									_saleState = mores.get(index).getChild().get(childIndex).getId();
								}
							}   
						}  
						dispatchNewHouseTask();
						
					}else if(houseType == HOUSE_TYPE_OLD){
						//二手房
						//重置更多参数
						o_sort = "";
						o_houseType = "";	
						o_areaZone = ""; 	
						o_roomFace = "";		
						o_buildingera = "";	
						o_membertype= "";	
						
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
						dispatchOldHouseTask();
						
					}else if(houseType == HOUSE_TYPE_RENT){
						//租房
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
						startRentHouseListTask();
					}
					
				}
				
				@Override
				public void onCancelClick() {
					if(houseType == HOUSE_TYPE_NEW){
						//新房
						//重置更多参数
						_order = "";
						_feature = "";
						_houseType = "";
						_areaSize = "";
						_renovateState = "";
						_saleState = "";
						
						dispatchNewHouseTask();
						
					}else if(houseType == HOUSE_TYPE_OLD){
						//二手房
						//重置更多参数
						o_sort = "";
						o_houseType = "";	
						o_areaZone = ""; 	
						o_roomFace = "";		
						o_buildingera = "";	
						o_membertype= "";	
						
						startOldHouseCommunityCountTask();
						
					}else if(houseType == HOUSE_TYPE_RENT){
						//租房
						//重置更多参数
						r_sort = "";			
						r_houseType = "";		
						r_areaZone = "";	
						r_isShare = "";		
						r_fitment = "";	
						
						startRentHouseListTask();
						
					}
					
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
	
	//租金，户型筛选条件
	private void showMapTypeView() {
		if(mapTypeView != null && mapTypeView.isShowing()) {
			mapTypeView.dismiss();
			return;
		}
		//二手房 户型
		if(houseType == HOUSE_TYPE_OLD){
			if(roomTypeList == null || roomTypeList.size() < 1) return;
			List<String> mapTypes = new ArrayList<String>();
			for (CommonType type : roomTypeList) {
				mapTypes.add(type.getName());
			}
			
			if(mapTypeView == null) {
				mapTypeView = new MapTypeListPopupWindow(mContext, new MapCommonTypeListClickListener() {
					
					@Override
					public void onTypeClick(int position) {
						mapTypeView.dismiss();
						searchContent = "&roomType="+roomTypeList.get(position).getId();
						startOldHouseListTask(1, true);
					}
				});
				mapTypeView.fillData(mapTypes);
			}
		}
		
		
		mapTypeView.showAsDropDown(map_house_type_txt, 0, DisplayUtil.dip2px(mContext, 10));
	}
	
	
	
	
	/***********************************  地图相关  ***********************************/
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		
		if(houseType == HOUSE_TYPE_NEW){
			if(aMap.getCameraPosition().zoom < zoomTag){
				//点击的是区域marker
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomTag));
				
			}else {
				//点击的是楼盘marker  新房跳转到房源详情页
				Intent intent = new Intent(mContext, HouseDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("projectId",  marker.getSnippet());
				bundle.putString("projectName", "房源详情");
				bundle.putInt("houseType", MapHousesActivity.HOUSE_TYPE_NEW);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}else {
			if(aMap.getCameraPosition().zoom < zoomTag_old){
				//点击的是区域marker
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomTag_old));
				
			}else {
				//二手房，租房地图下面展示列表
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), currentZoom));
				projname = marker.getSnippet();
				houseMarkerClicked();
			}
		}
		
		currentZoom = aMap.getCameraPosition().zoom;
		return true;
	}

	@Override
	public void onTouch(MotionEvent arg0) {
		if(houseType == HOUSE_TYPE_NEW){
			if(aMap.getCameraPosition().zoom < zoomTag && currentZoom >= zoomTag){
				showAreaMarks();
			}
		}else {
			if(aMap.getCameraPosition().zoom < zoomTag_old && currentZoom >= zoomTag_old){
				showAreaMarks();
			}
		}
		
		currentZoom = aMap.getCameraPosition().zoom;
	}
	
	
	private void houseMarkerClicked() {
		if(houseType == HOUSE_TYPE_NEW){
			
		}else{
			map_house_lay.setVisibility(View.VISIBLE);
			search_title_view.setVisibility(View.GONE);
			map_house_name_txt.setText(projname);
			startOldHouseListTask(1, true);
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		closeMapHouseLay();
	}
	
	
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
	}
	

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		Logger.d("", "  onCameraChangeFinish -------");
		
		if(houseType == HOUSE_TYPE_NEW){
			if(cameraPosition.zoom >= zoomTag){
				//获取地图可视范围内的东北角和西南角的经纬度
				VisibleRegion region = aMap.getProjection().getVisibleRegion();
				northeast = region.latLngBounds.northeast;
				southwest = region.latLngBounds.southwest;
				
				startNewHouseListTask();
			}
		}else {
			if(cameraPosition.zoom >= zoomTag_old){
				
				//获取地图可视范围内的东北角和西南角的经纬度
				VisibleRegion region = aMap.getProjection().getVisibleRegion();
				northeast = region.latLngBounds.northeast;
				southwest = region.latLngBounds.southwest;
				
				startOldHouseCommunityCountTask();
			}
		}
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(map_house_lay.getVisibility() == View.VISIBLE) {
				closeMapHouseLay();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void closeMapHouseLay() {
		if(typeView != null && typeView.isShowing()) typeView.dismiss();
		map_house_lay.setVisibility(View.GONE);
		search_title_view.setVisibility(View.VISIBLE);
		
	}
	
	
	private LatLng getAreaLatLng(String areaId){
		LatLng latLng = null;
		for(Area area : areaList ){
			if(areaId.equals(area.getAreaId())){
				latLng = new LatLng(Double.parseDouble(area.getLatitude()), 
						Double.parseDouble(area.getLongitude()));
			}
		}
		return latLng;
	}
	
	
	/***************************************** 新房相关  *****************************************/
	/**
	 * 处理逻辑：1.根据可视区域的经纬度（东北，西南）获取该经纬度范围内的楼盘
	 * 		   2. 点击筛选条件，都是在可视范围内的楼盘中筛选(地图找房中没有 学区，和排序)
	 * 		   3. 点击楼盘跳转到楼盘详情页
	 */
	
	private void dispatchNewHouseTask(){
		if(aMap.getCameraPosition().zoom < zoomTag){
			startNewHouseCountTask();
		}else{
			startNewHouseListTask();
		}
	}
	
	private void startNewHouseCountTask() {
		if(NetUtil.detectAvailable(mContext)){
			MapNewHouseCountRequest request = new MapNewHouseCountRequest(modelApp.getSite().getSiteId(), getParams(),
					new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						break;
						
					case Constants.NO_DATA_FROM_NET:
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						ArrayList<Area> areaList = (ArrayList<Area>) message.obj;
						for(Area area : areaList){
							MarkerOptions areaMarker = new MarkerOptions();
							areaMarker.position(new LatLng(Double.parseDouble(area.getLatitude()), 
									Double.parseDouble(area.getLongitude())));
							
							if(StringUtil.isEmpty(area.getCount())){
								areaMarker.title(area.getAreaName()).snippet("0个楼盘");
							}else{
								areaMarker.title(area.getAreaName()).snippet(area.getCount() + "个楼盘");
							}
							
							areaMarkers.add(areaMarker);
						}
						showAreaMarks();
						break;
					}
				}
			});
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
		}
	}
	
	
	//获取新房的筛选条件
	private HouseConfigDbService configDbService = new HouseConfigDbService();
	
	private void getNewHouseSearchTitleData() {
		//区域以及学校
		areaList = configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
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
		
//		ArrayList<CommonType> schoolList = configDbService.getSchoolListBySite(modelApp.getSite().getSiteId());
//		if(schoolList == null || schoolList.size() < 1){
//			// 请求站点下的学区
//			startNewHouseSchoolTask();
//			
//		}else{
//			//不限
//			CommonType defaultType = new CommonType();
//			defaultType.setId("");
//			defaultType.setName("不限");
//			schoolList.add(0, defaultType);
//			CommonType school = new CommonType();
//			school.setId("");
//			school.setName("学区");
//			school.setChild(schoolList);
//			areas.add(school);
//		}
		
		//类型， 价格， 更多（排序 楼盘特色  户型 面积  装修状态  销售状态）（这几个数据同一个接口）
		ArrayList<CommonType> orderList = configDbService.getOrderListBySite(modelApp.getSite().getSiteId());
		if(orderList == null || orderList.size() < 1){
			startNewHouseMoreTask();
		}else {
			ArrayList<CommonType> featureList = configDbService.getFeatureListBySite(modelApp.getSite().getSiteId());
			ArrayList<CommonType> houseTypeList = configDbService.getHouseTypeListBySite(modelApp.getSite().getSiteId());
			ArrayList<CommonType> spaceList = configDbService.getSpaceListBySite(modelApp.getSite().getSiteId());
			ArrayList<CommonType> renovateList = configDbService.getRenovateListBySite(modelApp.getSite().getSiteId());
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
//			if(orderList != null && orderList.size() > 0){
//				CommonType order = new CommonType();
//				order.setId("");
//				order.setName("排序");
//				order.setChild(orderList);
//				mores.add(order);
//			}
			
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
			
			if(renovateList != null && renovateList.size() > 0){
				//不限
				CommonType defaultType = new CommonType();
				defaultType.setId("");
				defaultType.setName("不限");
				renovateList.add(0, defaultType);
				CommonType renovate = new CommonType();
				renovate.setId("");
				renovate.setName("装修状态");
				renovate.setChild(renovateList);
				mores.add(renovate);
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
						areaList = configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
						
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
			MapNewHouseConfigRequest request = new MapNewHouseConfigRequest(modelApp.getSite().getSiteId(), new RequestListener() {
				
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
						ArrayList<CommonType> renovateList = configDbService.getRenovateListBySite(modelApp.getSite().getSiteId());
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
//						if(orderList != null && orderList.size() > 0){
//							CommonType order = new CommonType();
//							order.setId("");
//							order.setName("排序");
//							order.setChild(orderList);
//							mores.add(order);
//						}
						
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
						
						if(renovateList != null && renovateList.size() > 0){
							//不限
							CommonType defaultType = new CommonType();
							defaultType.setId("");
							defaultType.setName("不限");
							renovateList.add(0, defaultType);
							CommonType renovate = new CommonType();
							renovate.setId("");
							renovate.setName("装修状态");
							renovate.setChild(renovateList);
							mores.add(renovate);
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
	
	private NewHouseListRequest newHouseListRequest;
	
	//请求参数
	private String _areaId;		 //区域ID
	private String _schoolId;	 //学校ID
	private String _propertyType; //物业类型
	private String _price;		//均价区间
	private String _houseType;	//户型
	private String _areaSize;	//面积
	private String _renovateState; //装修状态
	private String _feature;		//楼盘特色
	private String _saleState;	//销售状态
	private String _order;		//排序方式
	private String _keyword;		//排序方式
	
	private String getParams(){
		_keyword = house_search_txt.getText().toString();
		
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
		if(!StringUtil.isEmpty(_renovateState)){
			sb.append("&renovateState=" + _renovateState);
		}
		if(!StringUtil.isEmpty(_feature)){
			sb.append("&feature=" + _feature);
		}
		if(!StringUtil.isEmpty(_saleState)){
			sb.append("&saleState=" + _saleState);
		}
		if(!StringUtil.isEmpty(_order)){
			sb.append("&order=" + _order);
		}
		if(!StringUtil.isEmpty(_keyword)){
			try {
				sb.append("&keyword=" + URLEncoder.encode(_keyword, "utf-8"));
			} catch (Exception e) {
				
			}
		}
		
		if(northeast != null && southwest != null){
			sb.append("&swlng=" + southwest.longitude);
			sb.append("&swlat=" + southwest.latitude);
			sb.append("&nelng=" + northeast.longitude);
			sb.append("&nelat=" + northeast.latitude);
		}
		return sb.toString();
	}
	
	private void startNewHouseListTask(){
		if (NetUtil.detectAvailable(mContext)) {
			if(newHouseListRequest == null){
				newHouseListRequest = new NewHouseListRequest(modelApp.getSite().getSiteId(), 
						getParams(), new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

						case Constants.NO_DATA_FROM_NET:
                            if (currentPageIndex == 1){
                                Toast.makeText(mContext, "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							ArrayList<House> temp = (ArrayList<House>) message.obj;
							if(newHouseList != null){
								newHouseList.clear();
							}
							newHouseList.addAll(temp);
							fillNewHouseData();
							break;
						}
					}
				});
			}else {
				newHouseListRequest.setData(modelApp.getSite().getSiteId(), getParams());
			}
			
			showLoadingDialog(R.string.data_loading);
			newHouseListRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void fillNewHouseData() {
		if(houseMarkers != null) houseMarkers.clear();
		for(House house : newHouseList){
			MarkerOptions houseMarker = new MarkerOptions();
			houseMarker.position(new LatLng(Double.parseDouble(house.getLatitude()), 
					Double.parseDouble(house.getLongitude())));
			houseMarker.title(house.getProjectName() + "  " + house.getAveragePrice())
						.snippet(house.getProjectId());
			houseMarkers.add(houseMarker);
		}
		showHouseMarks();
	}
		
	
	
	/***************************************** 二手房相关  *****************************************/
	/**
	 * 处理逻辑：1.根据可视区域的经纬度（东北，西南）获取该经纬度范围内的小区（xxx套）
	 * 		   2. 点击筛选条件，都是在可视范围内的小区中筛选
	 * 		   3. 点击小区底部展示小区内出售的房源（可按户型筛选）
	 * 		   4. 点击房源进去房源详情页
	 */
	
	private void dispatchOldHouseTask(){
		if(aMap.getCameraPosition().zoom < zoomTag_old){
			startOldHouseCountTask();
		}else{
			startOldHouseCommunityCountTask();
		}
	}
	
	//获取某个站点下所有区域的楼盘个数
	private void startOldHouseCountTask() {
		if(NetUtil.detectAvailable(mContext)){
			MapOldHouseCountRequest oldHouseCountRequest = new MapOldHouseCountRequest(modelApp.getSite().getSiteId(),
					getOldCountParams(), new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						break;
						
					case Constants.NO_DATA_FROM_NET:
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						ArrayList<Area> temp = (ArrayList<Area>) message.obj;
						if(areaList != null){
							areaList.clear();
						}else{
							areaList = new ArrayList<Area>();
						}
						areaList.addAll(temp);
						
						for(Area area : areaList){
							MarkerOptions areaMarker = new MarkerOptions();
							areaMarker.position(new LatLng(Double.parseDouble(area.getLatitude()), 
									Double.parseDouble(area.getLongitude())));
							if(StringUtil.isEmpty(area.getCount())){
								areaMarker.title(area.getAreaName()).snippet("0套房源");
							}else {
								areaMarker.title(area.getAreaName()).snippet(area.getCount() + "套房源");
							}
							
							areaMarkers.add(areaMarker);
						}
						showAreaMarks();
						break;
					}
				}
			});
			showLoadingDialog(R.string.data_loading);
			oldHouseCountRequest.doRequest();
		}
	}
	
	private String getOldCountParams(){
		o_projname = house_search_txt.getText().toString();
		
		StringBuilder sb = new StringBuilder();
		
		if(!StringUtil.isEmpty(o_type)){
			sb.append("&houseType=" + o_type);
		}
		if(!StringUtil.isEmpty(o_price)){
			sb.append("&pricezone=" + o_price);
		}
		if(!StringUtil.isEmpty(o_houseType)){
			sb.append("&roomType=" + o_houseType);
		}
		if(!StringUtil.isEmpty(o_areaZone)){
			sb.append("&areazone=" + o_areaZone);
		}
		if(!StringUtil.isEmpty(o_roomFace)){
			sb.append("&roomface=" + o_roomFace);
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
		if(!StringUtil.isEmpty(o_projname)){
			sb.append("&projname=" + o_projname);
		}
		
		return sb.toString();
	}
	
	
	private void getOldHouseSearchTitleData(Bundle conditionData) {
		
		if(conditionData == null) return;
		
		//区域
		areaList = (ArrayList<Area>) conditionData.getSerializable("areaList");
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
		
		//更多（排序，户型，面积，房龄，朝向，来源）
//		ArrayList<CommonType> sortList = (ArrayList<CommonType>) conditionData.getSerializable("sortList");
//		if(sortList != null && sortList.size() > 0){
//			CommonType sort = new CommonType();
//			sort.setId("");
//			sort.setName("排序");
//			sort.setChild(sortList);
//			mores.add(sort);
//		}
		roomTypeList = (ArrayList<CommonType>) conditionData.getSerializable("roomTypeList");
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
	
	private MapOldHouseCommunityCountRequest oldHouseCommunityCountRequest;
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
	private String o_projname;		//小区名搜索
	
	private String getOldParams(){
		o_projname = house_search_txt.getText().toString();
		
		StringBuilder sb = new StringBuilder();
//		if(!StringUtil.isEmpty(o_area)){
//			sb.append("&area=" + o_area);
//		}
		if(!StringUtil.isEmpty(o_type)){
			sb.append("&houseType=" + o_type);
		}
		if(!StringUtil.isEmpty(o_price)){
			sb.append("&pricezone=" + o_price);
		}
//		if(!StringUtil.isEmpty(o_sort)){
//			sb.append("&sort=" + o_sort);
//		}
		if(!StringUtil.isEmpty(o_houseType)){
			sb.append("&roomType=" + o_houseType);
		}
		if(!StringUtil.isEmpty(o_areaZone)){
			sb.append("&areazone=" + o_areaZone);
		}
		if(!StringUtil.isEmpty(o_roomFace)){
			sb.append("&roomface=" + o_roomFace);
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
		if(!StringUtil.isEmpty(o_projname)){
			try {
				sb.append("&projname=" + URLEncoder.encode(o_projname, "utf-8"));
			} catch (Exception e) {
				sb.append("&projname=" + o_projname);
			}
			
		}
		if(northeast != null && southwest != null){
			sb.append("&swlongitude=" + southwest.longitude);
			sb.append("&swlatitude=" + southwest.latitude);
			sb.append("&nelongitude=" + northeast.longitude);
			sb.append("&nelatitude=" + northeast.latitude);
		}
		
		return sb.toString();
	}
	
	private void startOldHouseCommunityCountTask(){
		if (NetUtil.detectAvailable(mContext)) {
			if(oldHouseCommunityCountRequest == null){
				oldHouseCommunityCountRequest = new MapOldHouseCommunityCountRequest(modelApp.getSite().getSiteId(),
						getOldParams(), new RequestListener() {
							
							@Override
							public void sendMessage(Message message) {
								hideLoadingDialog();
								switch (message.what) {
								case Constants.ERROR_DATA_FROM_NET:
									break;
									
								case Constants.NO_DATA_FROM_NET:
									break;
									
								case Constants.SUCCESS_DATA_FROM_NET:
									ArrayList<XKCommunity> temp = (ArrayList<XKCommunity>) message.obj;
									if(oldCommunityList != null){
										oldCommunityList.clear();
									}else{
										oldCommunityList = new ArrayList<XKCommunity>();
									}
									oldCommunityList.addAll(temp);
									fillOldCommunityData();
									break;
								}
							}
						});
			}else {
				oldHouseCommunityCountRequest.setData(modelApp.getSite().getSiteId(), getOldParams());
			}
			showLoadingDialog(R.string.data_loading);
			oldHouseCommunityCountRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void fillOldCommunityData() {
		if(houseMarkers != null) houseMarkers.clear();
		for(XKCommunity house : oldCommunityList){
			MarkerOptions houseMarker = new MarkerOptions();
			houseMarker.position(new LatLng(Double.parseDouble(house.getLatitude()), 
					Double.parseDouble(house.getLongitude())));
			if(StringUtil.isEmpty(house.getCountNum())){
				houseMarker.title(house.getBuildName() + "  0套")
				.snippet(house.getBuildName());
			}else{
				houseMarker.title(house.getBuildName() + "  " + house.getCountNum() + "套")
				.snippet(house.getBuildName());
			}
			
			houseMarkers.add(houseMarker);
		}
		showHouseMarks();
	}
	
	// 小区具体房源列表
	private MapOldHouseListRequest oldHouseListRequest;
	private ArrayList<XKRoom> oldHouseList = new ArrayList<XKRoom>();
	private String projname;
	private String searchContent;  // 户型搜索条件
	
	private void startOldHouseListTask(int page, boolean showLoading){
		if (NetUtil.detectAvailable(this)) {
			
			if(page == 1) {
				oldHouseList.clear();
				currentPageIndex = 1;
			}
			
			if(oldHouseListRequest == null){
				oldHouseListRequest = new MapOldHouseListRequest(modelApp.getSite().getSiteId(), projname, page, pageSize,
						searchContent, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

						case Constants.NO_DATA_FROM_NET:
                            if (currentPageIndex == 1){
                                Toast.makeText(mContext, "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							
							ArrayList<XKRoom> temp = (ArrayList<XKRoom>) message.obj;
							//根据返回的数据量判断是否隐藏加载更多
							if(temp.size() < pageSize){
								map_house_listview.setPullLoadEnable(false);
							}else{
								map_house_listview.setPullLoadEnable(true);
							}
							oldHouseList.addAll(temp);
							fillOldHouseListData();
                            if (currentPageIndex > 1 && message.arg1 == oldHouseList.size()){
                                Toast.makeText(mContext, R.string.data_load_end, Toast.LENGTH_SHORT).show();
                            }
                            currentPageIndex++;
							break;
						}
						map_house_listview.stopLoadMore();
					}
				});
			}else {
				oldHouseListRequest.setData(modelApp.getSite().getSiteId(), projname, page, pageSize, searchContent);
			}
			if (showLoading) {
                showLoadingDialog(R.string.data_loading);
            }
			oldHouseListRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
			map_house_listview.stopLoadMore();
		}
	}
	
	
	private void fillOldHouseListData(){
		if(oldHouseList == null) return;
		if(mapOldHouseAdapter == null) {
			mapOldHouseAdapter = new MapOldHouseAdapter(mContext, oldHouseList);
			map_house_listview.setAdapter(mapOldHouseAdapter);
		}else{
			mapOldHouseAdapter.setData(oldHouseList);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***************************************** 租房相关  *****************************************/
	/**
	 * 处理逻辑：1.根据可视区域的经纬度（东北，西南）获取该经纬度范围内的小区（xxx套）
	 * 		   2. 点击筛选条件，都是在可视范围内的小区中筛选
	 * 		   3. 点击小区底部展示小区内出租的房源（可按租金筛选）
	 * 		   4. 点击房源进去房源详情页
	 */
	
	private void startRentHouseCountTask() {
		if(NetUtil.detectAvailable(mContext)){
			MapRentHouseCountRequest request = new MapRentHouseCountRequest(modelApp.getSite().getSiteId(),
					new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						break;
						
					case Constants.NO_DATA_FROM_NET:
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						ArrayList<Area> areaList = (ArrayList<Area>) message.obj;
						for(Area area : areaList){
							MarkerOptions areaMarker = new MarkerOptions();
							areaMarker.position(new LatLng(Double.parseDouble(area.getLatitude()), 
									Double.parseDouble(area.getLongitude())));
							if(StringUtil.isEmpty(area.getCount())){
								areaMarker.title(area.getAreaName()).snippet("0套房源");
							}else{
								areaMarker.title(area.getAreaName()).snippet(area.getCount() + "套房源");
							}
							areaMarkers.add(areaMarker);
						}
						showAreaMarks();
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
//		ArrayList<CommonType> sortHireList = (ArrayList<CommonType>) conditionData.getSerializable("sortHireList");
//		if(sortHireList != null && sortHireList.size() > 0){
//			CommonType sort = new CommonType();
//			sort.setId("");
//			sort.setName("排序");
//			sort.setChild(sortHireList);
//			mores.add(sort);
//		}
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
		
		return sb.toString();
	}
	
	private void startRentHouseListTask(){
		if (NetUtil.detectAvailable(mContext)) {
			if(rentHouseListRequest == null){
				rentHouseListRequest = new RentHouseListRequest(modelApp.getSite().getSiteId(),
						getRentParams(), new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

						case Constants.NO_DATA_FROM_NET:
                            if (currentPageIndex == 1){
                                Toast.makeText(mContext, "没有符合条件的房源,您可以换个条件试试", Toast.LENGTH_SHORT).show();
                            }
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							ArrayList<OldHouse> temp = (ArrayList<OldHouse>) message.obj;
							
							if(rentHouseList != null){
								rentHouseList.clear();
							}
							rentHouseList.addAll(temp);
							fillRentHouseData();
							break;
						}
					}
				});
			}else {
				rentHouseListRequest.setData(modelApp.getSite().getSiteId(), getRentParams());
			}
			showLoadingDialog(R.string.data_loading);
			rentHouseListRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void fillRentHouseData() {
		// TODO  在地图上显示marker
		
	}
	
	
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
								Bundle conditionData = message.getData();
								if(houseType == HOUSE_TYPE_OLD) {
									getOldHouseSearchTitleData(conditionData);
								}else {
									getRentHouseSearchTitleData(conditionData);
								}
								
								break;
							}
						}
					});
			request.doRequest();
		}
	}
	

	
}
