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
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.app.task.JJCommitRequest;
import com.xkhouse.fang.money.activity.XKBCitySelectActivity;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 家居--公益保障 
 * @author wujian  
 * @date 2015-10-19 下午3:48:38  
 */
public class JJFreeServiceActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	private ImageView yf_iv;
	private ImageView yf_check_iv;
	private ImageView sj_iv;
	private ImageView sj_check_iv;
	private ImageView fj_iv;
	private ImageView fj_check_iv;
	private ImageView jc_iv;
	private ImageView jc_check_iv;
	
	private EditText name_txt;
	private EditText phone_txt;
	private EditText verify_code_txt;
	private TextView get_code_txt;
	private TextView city_txt;
	private EditText community_txt;
	
	private TextView room_one_txt;
	private TextView room_two_txt;
	private TextView room_three_txt;
	private TextView room_three_more_txt;
	
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
	private String houseType = "1";  //默认一室
//	private String intention;
	private String content;
	private String type = "1";		//类别（1=公益保障、2=预约陪购）
	private String pick;
	
	public static final int RESULT_CODE_CITY = 102;
	public static final int REQUEST_CODE = 100;
	
	private int index = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		freshHouseTypeView(1);
		if(index == 2){
			yf_check_iv.setVisibility(View.INVISIBLE);
			sj_check_iv.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void init() {
		super.init();
		Bundle data = getIntent().getExtras();
		if(data != null){
			index = data.getInt("index");
		}
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_jj_free_service);
		
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		yf_iv = (ImageView) findViewById(R.id.yf_iv);
		yf_check_iv = (ImageView) findViewById(R.id.yf_check_iv);
		sj_iv = (ImageView) findViewById(R.id.sj_iv);
		sj_check_iv = (ImageView) findViewById(R.id.sj_check_iv);
		fj_iv = (ImageView) findViewById(R.id.fj_iv);
		fj_check_iv = (ImageView) findViewById(R.id.fj_check_iv);
		jc_iv = (ImageView) findViewById(R.id.jc_iv);
		jc_check_iv = (ImageView) findViewById(R.id.jc_check_iv);
		
		name_txt = (EditText) findViewById(R.id.name_txt);
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		verify_code_txt = (EditText) findViewById(R.id.verify_code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		city_txt = (TextView) findViewById(R.id.city_txt);
		community_txt = (EditText) findViewById(R.id.community_txt);
		
		room_one_txt = (TextView) findViewById(R.id.room_one_txt);
		room_two_txt = (TextView) findViewById(R.id.room_two_txt);
		room_three_txt = (TextView) findViewById(R.id.room_three_txt);
		room_three_more_txt = (TextView) findViewById(R.id.room_three_more_txt);
		
		remark_txt = (EditText) findViewById(R.id.remark_txt);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_title.setText("公益保障");
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
		
		yf_iv.setOnClickListener(this);
		sj_iv.setOnClickListener(this);
		fj_iv.setOnClickListener(this);
		jc_iv.setOnClickListener(this);
		
		get_code_txt.setOnClickListener(this);
		city_txt.setOnClickListener(this);
		
		room_one_txt.setOnClickListener(this);
		room_two_txt.setOnClickListener(this);
		room_three_txt.setOnClickListener(this);
		room_three_more_txt.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.yf_iv:
			if(View.VISIBLE == yf_check_iv.getVisibility()){
				yf_check_iv.setVisibility(View.INVISIBLE);
			}else{
				yf_check_iv.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.sj_iv:
			if(View.VISIBLE == sj_check_iv.getVisibility()){
				sj_check_iv.setVisibility(View.INVISIBLE);
			}else{
				sj_check_iv.setVisibility(View.VISIBLE);
			}		
			break;
					
		case R.id.fj_iv:
			if(View.VISIBLE == fj_check_iv.getVisibility()){
				fj_check_iv.setVisibility(View.INVISIBLE);
			}else{
				fj_check_iv.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.jc_iv:
			if(View.VISIBLE == jc_check_iv.getVisibility()){
				jc_check_iv.setVisibility(View.INVISIBLE);
			}else{
				jc_check_iv.setVisibility(View.VISIBLE);
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
			
		case R.id.room_one_txt:
			freshHouseTypeView(1);
			break;
			
		case R.id.room_two_txt:
			freshHouseTypeView(2);
			break;
				
		case R.id.room_three_txt:
			freshHouseTypeView(3);
			break;
					
		case R.id.room_three_more_txt:
			freshHouseTypeView(4);	
			break;
			
		}
	}
	
	
	private void freshHouseTypeView(int index) {
		switch (index) {
		case 1:
			houseType = "1";
			room_one_txt.setTextColor(getResources().getColor(R.color.white));
			room_one_txt.setBackgroundColor(getResources().getColor(R.color.common_green));
			room_two_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_two_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_more_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_more_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			
			break;
			
		case 2:
			houseType = "2";
			room_two_txt.setTextColor(getResources().getColor(R.color.white));
			room_two_txt.setBackgroundColor(getResources().getColor(R.color.common_green));
			room_one_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_one_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_more_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_more_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			break;
			
		case 3:
			houseType = "3";
			room_three_txt.setTextColor(getResources().getColor(R.color.white));
			room_three_txt.setBackgroundColor(getResources().getColor(R.color.common_green));
			room_two_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_two_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_one_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_one_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_more_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_more_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			break;
			
		case 4:
			houseType = "4";
			room_three_more_txt.setTextColor(getResources().getColor(R.color.white));
			room_three_more_txt.setBackgroundColor(getResources().getColor(R.color.common_green));
			room_two_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_two_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_three_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_three_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			room_one_txt.setTextColor(getResources().getColor(R.color.common_black_txt));
			room_one_txt.setBackgroundResource(R.drawable.gray_border_btn_bg);
			break;

		}
	}
	
	//类型选择（1公益验房，2免费设计，3免费工地飞检，4免费空气检测，5公益陪购，6南京宜家行）
	private String getPickData(){
		StringBuilder sb = new StringBuilder();
		
		if(View.VISIBLE == yf_check_iv.getVisibility()){
			sb.append("1,");
		}
		if(View.VISIBLE == sj_check_iv.getVisibility()){
			sb.append("2,");
		}
		if(View.VISIBLE == fj_check_iv.getVisibility()){
			sb.append("3,");
		}
		if(View.VISIBLE == jc_check_iv.getVisibility()){
			sb.append("4,");
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
		if(!StringUtil.isEmpty(houseType)){
			sb.append("&houseType=" + houseType);
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
			Toast.makeText(mContext, "您的手机号忘记填了", Toast.LENGTH_SHORT).show();
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
				
		if(NetUtil.detectAvailable(mContext)){
			if(commitRequest == null){
				commitRequest = new JJCommitRequest(userName, phone, verifyCode,
						siteName, village, "",
						houseType, content, type, pick, new RequestListener() {
					
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
						siteName, village, "",
						houseType, content, type, pick);
			}
			
			showLoadingDialog("表单提交中...");
			commitRequest.doRequest();
			
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
        }
	}
	
	

}
