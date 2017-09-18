package com.xkhouse.fang.money.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.entity.XKBHouse;
import com.xkhouse.fang.money.task.CustomerAddRequest;
import com.xkhouse.fang.money.task.XKBExpectHouseRequest;
import com.xkhouse.fang.money.view.CenterListPopupWindow;
import com.xkhouse.fang.money.view.CenterListPopupWindow.CommonTypeListClickListener;
import com.xkhouse.fang.widget.AppUpdateDialog;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @Description: 客户推荐页 
 * @author wujian  
 * @date 2015-9-9 下午1:49:43  
 */
public class CustomerAddActivity extends AppBaseActivity {

	private View title_view;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	//表单相关
	private ImageView contact_iv;
	private EditText customer_name_txt;
	private EditText customer_phone_txt;
	private TextView city_txt;		//城市
	private TextView project_txt;	//楼盘
	private TextView category_txt;	//物业类型
	private TextView price_txt;		//均价
	private EditText remark_txt;	//备注
	private TextView customer_submit_txt;	//提交
	
	private CenterListPopupWindow categoryTypeView;
	private CommonConfigRequest categoryRequest;	//
	private ArrayList<CommonType> categoryList = new ArrayList<CommonType>();
	private CommonType category;
	
	private CenterListPopupWindow priceTypeView;
	private CommonConfigRequest priceRequest;	//价格
	private ArrayList<CommonType> priceList = new ArrayList<CommonType>();
	private CommonType price;
	
	
	private ArrayList<CommonType> areaHouseList;
	
	public static final int RESULT_CODE = 101;
	public static final int RESULT_CODE_CITY = 102;
	public static final int REQUEST_CODE = 100;
	
	private String houseName;
	private String houseID;
	private String siteId;

    private int READ_CONTACTS_REQUEST_CODE = 11;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!StringUtil.isEmpty(houseName)){
			project_txt.setText(houseName);
		}
		
	}
	
		
	@Override
	protected void init() {
		super.init();
		Bundle data = getIntent().getExtras();
		if(data != null){
			houseID = data.getString("houseID");
			houseName = data.getString("houseName");
		}
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_customer_add);
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		contact_iv = (ImageView) findViewById(R.id.contact_iv);
		customer_name_txt = (EditText) findViewById(R.id.customer_name_txt);
		customer_phone_txt = (EditText) findViewById(R.id.customer_phone_txt);
		city_txt = (TextView) findViewById(R.id.city_txt);
		project_txt = (TextView) findViewById(R.id.project_txt);
		category_txt = (TextView) findViewById(R.id.category_txt);
		price_txt = (TextView) findViewById(R.id.price_txt);
		remark_txt = (EditText) findViewById(R.id.remark_txt);
		customer_submit_txt = (TextView) findViewById(R.id.customer_submit_txt);
		
	}
	
	private void initTitle() {
		title_view = findViewById(R.id.title_view);
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		
		tv_head_title.setText("客户推荐");
		tv_head_right.setText("重置");
		tv_head_right.setVisibility(View.VISIBLE);
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clearTextContent();
			}
		});
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(categoryTypeView != null && categoryTypeView.isShowing()){
					categoryTypeView.dismiss();
					return ;
				}
				if(priceTypeView != null && priceTypeView.isShowing()){
					priceTypeView.dismiss();
					return ;
				}
				closeSoftInput();
				//解決黑屏問題
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
	}
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		contact_iv.setOnClickListener(this);
		customer_submit_txt.setOnClickListener(this);
		city_txt.setOnClickListener(this);
		project_txt.setOnClickListener(this);
		category_txt.setOnClickListener(this);
		price_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.contact_iv:
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_REQUEST_CODE);
            } else {
                //执行获取权限后的操作
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), 0);
            }
			break;
			
		case R.id.customer_submit_txt:
			startAddTask();
			break;
			
		case R.id.city_txt:
			Intent cityIntent = new Intent(mContext, XKBCitySelectActivity.class);
			Bundle location = new Bundle();
			location.putString("city",  null);
			cityIntent.putExtras(location);
			startActivityForResult(cityIntent, REQUEST_CODE);
			break;
					
		case R.id.project_txt:
			if(areaHouseList != null && areaHouseList.size() > 0){
				startExpectHouseActivity();
			}else{
				startExpectHouseTask();
			}
			
			break;
			
		case R.id.category_txt:
			startCategoryListTask();
			break;
			
		case R.id.price_txt:
			startPriceListTask();
			break;
		}
	}
	
	
	private CommonTypeListClickListener categoryListClickListener = new CommonTypeListClickListener() {
		
		@Override
		public void onTypeClick(int position) {
			categoryTypeView.dismiss();
			category = categoryList.get(position);
			category_txt.setText(category.getName());
		}
	};
	private void showCategoryTypeView() {
		if(categoryTypeView == null){
			categoryTypeView = new CenterListPopupWindow(mContext, categoryListClickListener);
		}
		if(categoryList != null && categoryList.size() > 0){
			List<String> categoryStr = new ArrayList<String>();
			for (int i = 0; i < categoryList.size(); i++) {
				categoryStr.add(categoryList.get(i).getName());
			}
			categoryTypeView.fillData(categoryStr);
			categoryTypeView.showAsDropDown(title_view);
		}
	}
	

	private CommonTypeListClickListener priceListClickListener = new CommonTypeListClickListener() {
		
		@Override
		public void onTypeClick(int position) {
			priceTypeView.dismiss();
			price = priceList.get(position);
			price_txt.setText(price.getName());
		}
	};
	
	private void showPriceTypeView() {
		if(priceTypeView == null){
			priceTypeView = new CenterListPopupWindow(mContext, priceListClickListener);
		}
		
		if(priceList != null && priceList.size() > 0){
			List<String> priceStr = new ArrayList<String>();
			for (int i = 0; i < priceList.size(); i++) {
				priceStr.add(priceList.get(i).getName());
			}
			priceTypeView.fillData(priceStr);
			priceTypeView.showAsDropDown(title_view);
		}
	}
	
	
	
	//重置表单
	private void clearTextContent(){
		customer_name_txt.setText("");
		customer_phone_txt.setText("");
		city_txt.setText("");
		project_txt.setText("");
		category_txt.setText("");
		price_txt.setText("");
		remark_txt.setText("");
	}
	
	
	private void startAddTask(){
		String uId = modelApp.getUser().getId();
		
		String rName = customer_name_txt.getText().toString();
		String rPhone = customer_phone_txt.getText().toString();
		String projectStr = houseID;
		String propertyType = "";
		if(category != null){
			propertyType = category.getId();
		}
		String averagePrice = "";
		if(price != null){
			averagePrice = price.getId();
		}
		String remarks = remark_txt.getText().toString().trim().replaceAll("\n", "");
		
		// 校验数据
		if(StringUtil.isEmpty(rName)){
			Toast.makeText(mContext, "您推荐的客户的姓名忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
        if(StringUtil.isEmpty(rPhone)){
            Toast.makeText(mContext, "您推荐的客户的电话忘记填了", Toast.LENGTH_SHORT).show();
            return;
        }
		
		if(NetUtil.detectAvailable(mContext)){
			
			CustomerAddRequest request = new CustomerAddRequest(uId, rName, rPhone, projectStr, 
				 propertyType, averagePrice, remarks, siteId, new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					String msg = (String) message.obj;
					
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.NO_DATA_FROM_NET:
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:

                        final ConfirmDialog confirmDialog = new ConfirmDialog(CustomerAddActivity.this, "推荐成功",
                                "您可在'我的-->我的推荐'里查看推荐详细情况，被推荐人成功购房您将获得相应的佣金收入噢！",
                                "我知道了", "继续推荐");
                        confirmDialog.show();
                        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
                            @Override
                            public void doConfirm() {
                                confirmDialog.dismiss();
                                finish();
                            }

                            @Override
                            public void doCancel() {
                                confirmDialog.dismiss();
                                mContext.startActivity(new Intent(CustomerAddActivity.this, CustomerAddActivity.class));
                                finish();
                            }
                        });
						break;		
					}
				}
			});
			showLoadingDialog("正在提交数据...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	private void startExpectHouseTask() {
		if (NetUtil.detectAvailable(mContext)) {
			XKBExpectHouseRequest request = new XKBExpectHouseRequest(siteId, new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.NO_DATA_FROM_NET:
						Toast.makeText(mContext, "没有楼盘数据", Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						areaHouseList = (ArrayList<CommonType>) message.obj;
						startExpectHouseActivity();
						
						break;		
					}
				}
			});
			
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
			
		}else{
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	private void startExpectHouseActivity() {
		Intent intent = new Intent(mContext, ExpectHouseActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("areaHouseList", areaHouseList);
		data.putString("houseID", houseID);
		intent.putExtras(data);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	
	
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
        	//获取选中的联系人
            ContentResolver reContentResolverol = getContentResolver();
             Uri contactData = data.getData();
             @SuppressWarnings("deprecation")
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String phoneNum = "";
            Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                     null, 
                     ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, 
                     null, 
                     null);
             while (phone.moveToNext()) {
            	 phoneNum = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            	 break;
             }
             customer_name_txt.setText(username);
             customer_phone_txt.setText(phoneNum);
         }
        
        //选择的期望楼盘
        if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_CODE) {
				HashMap<String, List<XKBHouse>> childHouses = (HashMap<String, List<XKBHouse>>) data.getExtras().getSerializable("childHouses");
				StringBuilder houseStr = new StringBuilder();
				StringBuilder houseId = new StringBuilder();
				for(Map.Entry<String, List<XKBHouse>> entry : childHouses.entrySet()){
					List<XKBHouse> houseList = entry.getValue();
					for (XKBHouse house : houseList) {
						if(house.isSelected()){
							houseStr = houseStr.append(house.getName()).append(",");
							houseId = houseId.append(house.getId()).append(",");
							
						}
					}
				}
				project_txt.setText(houseStr.toString());
				houseID = houseId.toString();
			}
			
			if(resultCode == RESULT_CODE_CITY){
				Site site = (Site) data.getExtras().getSerializable("site");
                if(!site.getArea().equals(city_txt.getText())){
                    if(areaHouseList != null) areaHouseList.clear();
                    project_txt.setText("");
                    houseID = "";
                }
				city_txt.setText(site.getArea());
				siteId = site.getSiteId();
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
				priceList = (ArrayList<CommonType>) message.obj;
				showPriceTypeView();
				
				break;
			}
		}
	};
	
	private void startPriceListTask() {
		if(priceList != null && priceList.size() > 0){
			showPriceTypeView();
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
	
	/******************************  物业类型   ******************************/
	private RequestListener categoryListener = new RequestListener() {
		
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
				categoryList = (ArrayList<CommonType>) message.obj;
				showCategoryTypeView();
				
				break;
			}
		}
	};
	
	private void startCategoryListTask() {
		if(categoryList != null && categoryList.size() > 0){
			showCategoryTypeView();
			return;
		} 
		
		if(NetUtil.detectAvailable(mContext)){
			if(categoryRequest == null){
				categoryRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "PROPERTY_TYPE", categoryListener);
			}
			categoryRequest.doRequest();
			showLoadingDialog(R.string.data_loading);
		}else {
			Toast.makeText(mContext, "亲，您还没连接网络哦！", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(categoryTypeView != null && categoryTypeView.isShowing()){
				categoryTypeView.dismiss();
				return true;
			}
			if(priceTypeView != null && priceTypeView.isShowing()){
				priceTypeView.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_CONTACTS_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI), 0);
            } else {
                //没有取得权限
            }
        }
    }
	
	

	
}
