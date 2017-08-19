package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.task.CommonConfigRequest;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.app.task.JJCommitRequest;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.activity.XKBCitySelectActivity;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 家居--公益保障 
 * @author wujian  
 * @date 2015-10-19 下午3:48:38  
 */
public class JJAppointmentActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	private ImageView pg_iv;
	private ImageView pg_check_iv;
	private ImageView yjx_iv;
	private ImageView yjx_check_iv;
	
	
	private EditText name_txt;
	private EditText phone_txt;
	private EditText verify_code_txt;
	private TextView get_code_txt;
	private TextView city_txt;
	private EditText community_txt;
	private TextView shopping_intent_txt;
	
	private EditText remark_txt;
	
	private JJCommitRequest commitRequest;
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	
	//参数
	private String userName;
	private String phone;
	private String verifyCode;
	private String siteName;
	private String village;
	private String intention;
	private String content;
	private String type = "1";		//类别（1=公益保障、2=预约陪购）
	private String pick;
	
	public static final int RESULT_CODE = 101;
	public static final int RESULT_CODE_CITY = 102;
	public static final int REQUEST_CODE = 100;
	
	private CommonConfigRequest configRequest;
	private ArrayList<CommonType> typeList = new ArrayList<CommonType>(); //购买意向
	
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(index == 0){
			pg_check_iv.setVisibility(View.VISIBLE);
			pick = "5,";
		}else if(index == 1){
			yjx_check_iv.setVisibility(View.VISIBLE);
			pick = "6,";
		}
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_jj_appointment);
	}
	
	@Override
	protected void init() {
		super.init();
		
		index = getIntent().getExtras().getInt("index");
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		pg_iv = (ImageView) findViewById(R.id.pg_iv);
		pg_check_iv = (ImageView) findViewById(R.id.pg_check_iv);
		yjx_iv = (ImageView) findViewById(R.id.yjx_iv);
		yjx_check_iv = (ImageView) findViewById(R.id.yjx_check_iv);
		
		
		name_txt = (EditText) findViewById(R.id.name_txt);
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		verify_code_txt = (EditText) findViewById(R.id.verify_code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		city_txt = (TextView) findViewById(R.id.city_txt);
		community_txt = (EditText) findViewById(R.id.community_txt);
		
		shopping_intent_txt = (TextView) findViewById(R.id.shopping_intent_txt);
	
		remark_txt = (EditText) findViewById(R.id.remark_txt);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_title.setText("预约陪购");
		tv_head_right.setText("提交");
		tv_head_right.setVisibility(View.VISIBLE);
		
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCommitTask();
			}
		});

		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
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
		
		pg_iv.setOnClickListener(this);
		yjx_iv.setOnClickListener(this);
		
		get_code_txt.setOnClickListener(this);
		city_txt.setOnClickListener(this);
		shopping_intent_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.pg_iv:
			if(View.VISIBLE == pg_check_iv.getVisibility()){
				pg_check_iv.setVisibility(View.INVISIBLE);
			}else{
				pg_check_iv.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.yjx_iv:
			if(View.VISIBLE == yjx_check_iv.getVisibility()){
				yjx_check_iv.setVisibility(View.INVISIBLE);
			}else{
				yjx_check_iv.setVisibility(View.VISIBLE);
			}		
			break;

		case R.id.get_code_txt:
			startVerityCodeTask();		
			break;
					
		case R.id.city_txt:
			Intent cityIntent = new Intent(mContext, XKBCitySelectActivity.class);
			Bundle location = new Bundle();
			location.putString("city",  null);
			cityIntent.putExtras(location);
			startActivityForResult(cityIntent, REQUEST_CODE);
			break;
			
		case R.id.shopping_intent_txt:
			startIntentType();
			break;
			
		}
	}
	
	
	
	//类型选择（1公益验房，2免费设计，3免费工地飞检，4免费空气检测，5公益陪购，6南京宜家行）
	private String getPickData(){
		StringBuilder sb = new StringBuilder();
		
		if(View.VISIBLE == pg_check_iv.getVisibility()){
			sb.append("5,");
		}
		if(View.VISIBLE == yjx_check_iv.getVisibility()){
			sb.append("6,");
		}
		return sb.toString();
	}
		
	public String getParams() {
		
		StringBuilder sb = new StringBuilder();
		
		if(!StringUtil.isEmpty(verifyCode)){
			sb.append("&verifyCode=" + verifyCode);
		}
		if(!StringUtil.isEmpty(siteName)){
			sb.append("&siteName=" + siteName);
		}
		if(!StringUtil.isEmpty(village)){
			sb.append("&village=" + village);
		}
		if(!StringUtil.isEmpty(intention)){
			sb.append("&intention=" + intention);
		}
		if(!StringUtil.isEmpty(content)){
			sb.append("&content=" + content);
		}
		if(!StringUtil.isEmpty(type)){
			sb.append("&type=" + type);
		}
		if(!StringUtil.isEmpty(pick)){
			sb.append("&pick=" + pick);
		}
		
		return sb.toString();
	}
		
		
		
	private void startCommitTask(){
		
		userName = name_txt.getText().toString();
		phone = phone_txt.getText().toString();
		verifyCode = verify_code_txt.getText().toString();
		siteName = city_txt.getText().toString();
		village = community_txt.getText().toString();
		content = remark_txt.getText().toString();
		pick = getPickData();
		
		//校验数据		
		if(StringUtil.isEmpty(userName)){
			Toast.makeText(mContext, "您的姓名忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, "您的手机号忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(verifyCode)){
			Toast.makeText(mContext, "您的验证码忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(siteName)){
			Toast.makeText(mContext, "您的所在城市忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(village)){
			Toast.makeText(mContext, "您的所在小区忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(pick)){
			Toast.makeText(mContext, "您的保障类型忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(intention)){
			Toast.makeText(mContext, "您的意向购买类别忘记填了", Toast.LENGTH_SHORT).show();
			return;
		}
				
		if(NetUtil.detectAvailable(mContext)){
			if(commitRequest == null){
				commitRequest = new JJCommitRequest(userName, phone, verifyCode,
						siteName, village, intention,
						"", content, type, pick, new RequestListener() {
					
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
							finish();
							break;
						}
					}
				});
			}else {
				commitRequest.setData(userName, phone, verifyCode,
						siteName, village, intention,
						"", content, type, pick);
			}
			
			showLoadingDialog("表单提交中...");
			commitRequest.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
		
	
		
	
	//购买意向
	private RequestListener typeListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			hideLoadingDialog();
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET:
				Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.NO_DATA_FROM_NET:
				Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				ArrayList<CommonType> temp  = (ArrayList<CommonType>) message.obj;
				if(typeList != null){
					typeList.clear();
				}else{
					typeList = new ArrayList<CommonType>();
				}
				typeList.addAll(temp);
				
				Intent intent = new Intent(mContext, JJShoppingIntentionActivity.class);
				Bundle data = new Bundle();
				data.putSerializable("typeList", typeList);
				intent.putExtras(data);
				startActivityForResult(intent, REQUEST_CODE);
				break;
			}
		}
	};
	
	
	private void startIntentType() {
		if(typeList != null && typeList.size() > 0) {
			Intent intent = new Intent(mContext, JJShoppingIntentionActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("typeList", typeList);
			intent.putExtras(data);
			startActivityForResult(intent, REQUEST_CODE);
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			if(configRequest == null){
				configRequest = new CommonConfigRequest(modelApp.getSite().getSiteId(), "HOME_YIXIANG", typeListener);
			}
			showLoadingDialog(R.string.data_loading);
			configRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
		
	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask() {
		phone = phone_txt.getText().toString();
		if(StringUtil.isEmpty(phone)){
			Toast.makeText(mContext, R.string.input_phone, Toast.LENGTH_SHORT).show();
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
		
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//选择的期望楼盘
        if (requestCode == REQUEST_CODE) {
        	if(resultCode == RESULT_CODE_CITY){
				Site site = (Site) data.getExtras().getSerializable("site");
				city_txt.setText(site.getArea());
			}
        	if(resultCode == RESULT_CODE){
        		typeList = (ArrayList<CommonType>) data.getExtras().getSerializable("typeList");
        		StringBuilder sbName = new StringBuilder();
        		StringBuilder sbId = new StringBuilder();
        		
        		for(CommonType type : typeList){
        			if(type.isSelected()){
        				sbName.append(type.getName() + "  ");
        				sbId.append(type.getId() + ",");
        			}
        		}
        		shopping_intent_txt.setText(sbName.toString());
        		intention = sbId.toString();
        	}
        }
	}
	

}
