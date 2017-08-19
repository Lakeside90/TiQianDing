package com.xkhouse.fang.house.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Area;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.house.adapter.RequirementGridAdapter;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.house.task.AreaListRequest;
import com.xkhouse.fang.house.task.CustomHouseAddRequest;
import com.xkhouse.fang.widget.ScrollGridView;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 定制需求--定制购房 
 * @author wujian  
 * @date 2015-9-17 上午9:41:52  
 */
public class CustomHouseActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView area_txt;
	private TextView price_txt;
	private TextView space_txt;
	
	private ScrollGridView requirement_grid;
	private RequirementGridAdapter adapter;
	private EditText other_require_txt;

	private EditText phone_txt;
	private EditText verify_code_txt;
	private TextView get_code_txt;
	
	private TextView submit_txt;
	
	private AreaListRequest areaRequest;	//地区
	private ArrayList<CommonType> areaList = new ArrayList<CommonType>();
	
	private CommonConfigRequest priceRequest;	//价格
	private ArrayList<CommonType> priceList = new ArrayList<CommonType>();
	
	private CommonConfigRequest spaceRequest;	//面积
	private ArrayList<CommonType> spaceList = new ArrayList<CommonType>();
	
	private CommonConfigRequest featureRequest;	//特色
	private ArrayList<CommonType> featureList = new ArrayList<CommonType>();
	
	//请求参数
	private String areaId;		//区域ID
	private String priceRange;	//价格范围
	private String areaRange;	//面积范围
	private String feature;		//楼盘特色
	private String remark;	//需求描述
	private String phone;		//手机号码
	private String verifyCode;	//短信验证码
	
	private CustomHouseAddRequest addRequest;
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	private HouseConfigDbService configDbService = new HouseConfigDbService();
	
	public static final int REQUEST_CODE_AREA = 101;
	public static final int REQUEST_CODE_PRICE = 102;
	public static final int REQUEST_CODE_SPACE = 103;
	public static final int RESULT_CODE = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startFeatureListTask();
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_custom_house);
	}
	
	@Override
	protected void init() {
		super.init();
		
		priceList = configDbService.getPriceListBySite(modelApp.getSite().getSiteId());
		spaceList = configDbService.getSpaceListBySite(modelApp.getSite().getSiteId());
		getAreaListFromDB();
		featureList = configDbService.getFeatureListBySite(modelApp.getSite().getSiteId());
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		area_txt = (TextView) findViewById(R.id.area_txt);
		price_txt = (TextView) findViewById(R.id.price_txt);
		space_txt = (TextView) findViewById(R.id.space_txt);
		
		requirement_grid = (ScrollGridView) findViewById(R.id.requirement_grid);
		
		other_require_txt = (EditText) findViewById(R.id.other_require_txt);
		
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		verify_code_txt = (EditText) findViewById(R.id.verify_code_txt);
		
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		submit_txt = (TextView) findViewById(R.id.submit_txt);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("定制需求");
		iv_head_left.setOnClickListener(this);
	}
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		area_txt.setOnClickListener(this);
		price_txt.setOnClickListener(this);
		space_txt.setOnClickListener(this);
		get_code_txt.setOnClickListener(this);
		submit_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.iv_head_left:
			finish();
			break;
			
		case R.id.area_txt:
			startAreaListTask();
			break;
			
		case R.id.price_txt:
			startPriceListTask();
			break;
			
		case R.id.space_txt:
			startSpaceListTask();
			break;
			
		case R.id.get_code_txt:
			startVerityCodeTask();
			break;
			
		case R.id.submit_txt:
			startCustomHouseAddTask();
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_CODE){
			CommonType require = (CommonType) data.getExtras().getSerializable("require");
			
			if(requestCode == REQUEST_CODE_AREA){
				areaId = require.getId();
				area_txt.setText(require.getName());
			}else if(requestCode == REQUEST_CODE_PRICE){
				priceRange = require.getId();
				price_txt.setText(require.getName());
			} else if(requestCode == REQUEST_CODE_SPACE) {
				areaRange = require.getId();
				space_txt.setText(require.getName());
			}
		}
	}
	
	
	
	
	/******************************  行政区域   ******************************/
	private RequestListener areaListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<Area> temp = (ArrayList<Area>) message.obj;
				configDbService.insertAreaList(temp, modelApp.getSite().getSiteId());
				getAreaListFromDB();
				startAreaListActivity();
				break;
			}
		}
	};
	
	
	/******************************  价格   ******************************/
	private void startAreaListTask() {
		if(areaList != null && areaList.size() > 0){
			startAreaListActivity();
			return;
		} 
		
		if(NetUtil.detectAvailable(mContext)){
			if(areaRequest == null){
				areaRequest = new AreaListRequest(modelApp.getSite().getSiteId(), areaListener);
			}
			areaRequest.doRequest();
			showLoadingDialog(R.string.data_loading);
		}else {
			Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startAreaListActivity(){
		Intent intent = new Intent(mContext, CustomRequireListActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("requirements", areaList);
		data.putString("title", "选择区域");
		intent.putExtras(data);
		startActivityForResult(intent, REQUEST_CODE_AREA);
	}
	
	private void getAreaListFromDB(){
		ArrayList<Area> areaTypes = configDbService.getAreaListBySite(modelApp.getSite().getSiteId());
		if(areaTypes != null && areaTypes.size() > 0){
			
			for (Area area : areaTypes) {
				CommonType type = new CommonType();
				type.setId(area.getAreaId());
				type.setName(area.getAreaName());
				areaList.add(type);
			}
		}
	}
	
	
	/******************************  价格   ******************************/
	private RequestListener priceListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
				configDbService.insertPriceList(temp, modelApp.getSite().getSiteId());
				priceList = configDbService.getPriceListBySite(modelApp.getSite().getSiteId());
				startPriceListActivity();
				break;
			}
		}
	};
	
	private void startPriceListTask() {
		if(priceList != null && priceList.size() > 0){
			startPriceListActivity();
			return;
		} 
		
		if(NetUtil.detectAvailable(mContext)){
			if(priceRequest == null){
				priceRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "PRICE_RANGE", priceListener);
			}
			priceRequest.doRequest();
			showLoadingDialog(R.string.data_loading);
		}else {
			Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startPriceListActivity(){
		Intent intent = new Intent(mContext, CustomRequireListActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("requirements", priceList);
		data.putString("title", "选择价格");
		intent.putExtras(data);
		startActivityForResult(intent, REQUEST_CODE_PRICE);
	}
	
	
	
	
	/******************************  面积   ******************************/
	private RequestListener spaceListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
				configDbService.insertSpaceList(temp, modelApp.getSite().getSiteId());
				spaceList = configDbService.getSpaceListBySite(modelApp.getSite().getSiteId());
				startSpaceListActivity();
				break;
			}
		}
	};
	
	private void startSpaceListTask() {
		if(spaceList != null && spaceList.size() > 0){
			startSpaceListActivity();
			return;
		} 
		
		if(NetUtil.detectAvailable(mContext)){
			if(spaceRequest == null){
				spaceRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "AREA_TYPE", spaceListener);
			}
			spaceRequest.doRequest();
			showLoadingDialog(R.string.data_loading);
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startSpaceListActivity() {
		Intent intent = new Intent(mContext, CustomRequireListActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("requirements", spaceList);
		data.putString("title", "选择面积");
		intent.putExtras(data);
		startActivityForResult(intent, REQUEST_CODE_SPACE);
	}
	
	
	
	/******************************  特色   ******************************/
	private RequestListener featureListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "亲，没有数据哦！", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<CommonType> temp = (ArrayList<CommonType>) message.obj;
				configDbService.insertFeatureList(temp, modelApp.getSite().getSiteId());
				featureList = configDbService.getFeatureListBySite(modelApp.getSite().getSiteId());
				fillFeatureData();
				break;
			}
		}
	};
	
	private void startFeatureListTask() {
		if(featureList != null && featureList.size() > 0){
			fillFeatureData();
			return;
		} 
		
		if(NetUtil.detectAvailable(mContext)){
			if(featureRequest == null){
				featureRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "FEATURE_LIST", featureListener);
			}
			featureRequest.doRequest();
			showLoadingDialog(R.string.data_loading);
		}else {
			Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void fillFeatureData() {
		if(featureList == null) return;
		if(adapter == null) {
			adapter = new RequirementGridAdapter(featureList, mContext);
			requirement_grid.setAdapter(adapter);
		}
		adapter.notifyDataSetChanged();
	}
	
	
	private void startCustomHouseAddTask() {
		//校验表单
		if(StringUtil.isEmpty(areaId)){
			Toast.makeText(mContext, "您的购买区域忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
        if(StringUtil.isEmpty(priceRange)){
            Toast.makeText(mContext, "您的购房预算忘记填了", Toast.LENGTH_SHORT).show();
            return;
        }
        if(StringUtil.isEmpty(areaRange)){
            Toast.makeText(mContext, "您的购买面积忘记填了", Toast.LENGTH_SHORT).show();
            return;
        }
		
		remark = other_require_txt.getText().toString();
		if(!StringUtil.isEmpty(remark) && remark.length() > 200){
			Toast.makeText(mContext, "其他需求字数在200以内哦", Toast.LENGTH_SHORT).show();
			return;
		}
		
		phone = phone_txt.getText().toString();
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, R.string.input_phone, Toast.LENGTH_SHORT).show();
			return;
		}
		
		verifyCode = verify_code_txt.getText().toString();
		if(StringUtil.isEmpty(verifyCode)){
			Toast.makeText(mContext, R.string.input_code, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(adapter != null){
			feature = adapter.getfeatureIdsSelect();
		}
		
		
		if(NetUtil.detectAvailable(mContext)){
			if(addRequest == null){
				addRequest = new CustomHouseAddRequest(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(),
						phone, verifyCode, areaId, priceRange, areaRange, feature, remark,
						new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							String msg = (String) message.obj;
							Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							String msgs = (String) message.obj;
							Toast.makeText(mContext, msgs, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, MyCustomHouseListActivity.class));
							finish();
							break;
						}
					}
				});
			}else{
				addRequest.setData(modelApp.getUser().getUid(), modelApp.getSite().getSiteId(), phone, verifyCode,
						areaId, priceRange, areaRange, feature, remark);
			}
			showLoadingDialog("表单提交中...");
			addRequest.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void startVerityCodeTask() {
		phone = phone_txt.getText().toString();
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			if(codeRequest == null){
				codeRequest = new GetVerifyCodeRequest(phone, new RequestListener() {
                    @Override
                    public void sendMessage(Message message) {
                        switch (message.what){
                            case Constants.ERROR_DATA_FROM_NET:
                                Toast.makeText(mContext, R.string.sms_code_error, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.NO_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                            case Constants.SUCCESS_DATA_FROM_NET:
                                Toast.makeText(mContext, (String)message.obj, Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });
			}else {
				codeRequest.setData(phone);
			}
			codeRequest.doRequest();
			duration = 60;
			if(timer !=null){
				timer.cancel();
				timer= null;
			}
			timer = new Timer();
			timer.schedule(new MyTimerTask(), 0, 1000);
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			duration = duration - 1;
			runOnUiThread(new Runnable() {
				public void run() {
					if(duration < 0 ){
						get_code_txt.setClickable(true);
						get_code_txt.setBackgroundResource(R.drawable.green_corner_btn_bg);
						get_code_txt.setTextColor(getResources().getColor(R.color.white));
						get_code_txt.setText("获取验证码");
					}else{
						get_code_txt.setClickable(false);
						get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
						get_code_txt.setTextColor(getResources().getColor(R.color.common_gray_txt));
						get_code_txt.setText("获取验证码("+duration+")");
					}
				}
			});
			
		}
	}
}
