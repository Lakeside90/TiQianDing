package com.xkhouse.fang.user.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.MainActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.fang.user.task.RegisterRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 注册 
 * @author wujian  
 * @date 2015-10-9 上午10:17:51  
 */
public class RegisterActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText phone_txt;
	private EditText code_txt;
	private EditText psw_txt;
	private EditText invitation_code_txt;
	private TextView get_code_txt;
	
	private CheckBox agree_ckb;
	private TextView xkservice_txt;
	private TextView register_txt;
	
	
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	private RegisterRequest registerRequest;
	//参数
	private String mobile;	//手机号
	private String mobileCode; //验证码
	private String passWord;
	
	// 使用软件过程中 触发登录的动作，在进入到登录界面，然后又进到这里进行注册，注册完返回后执行的动作和登录后返回执行的动作一致
	private Class clazz;
	private Bundle bundle;

    private String DEVICE_ID;      //设备ID
    private int READ_PHONE_STATE_REQUEST_CODE = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		agree_ckb.setChecked(true);
		agree_ckb.setBackgroundResource(R.drawable.checkbox_checked);
		
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_register);
	}

	@Override
	protected void init() {
		super.init();
		
		clazz = (Class) getIntent().getSerializableExtra("classStr");
		bundle = getIntent().getExtras();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);

        } else {
            //执行获取权限后的操作
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = tm.getDeviceId();
        }
	}

	@Override
	protected void findViews() {
		initTitle();
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);
		psw_txt = (EditText) findViewById(R.id.psw_txt);
        invitation_code_txt = (EditText) findViewById(R.id.invitation_code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		agree_ckb = (CheckBox) findViewById(R.id.agree_ckb);
		xkservice_txt = (TextView) findViewById(R.id.xkservice_txt);
		register_txt = (TextView) findViewById(R.id.register_txt);
		
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("注册");
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
		get_code_txt.setOnClickListener(this);
		agree_ckb.setOnClickListener(this);
		xkservice_txt.setOnClickListener(this);
		register_txt.setOnClickListener(this);
	}

	@Override
	protected void release() {

	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.get_code_txt:
			startVerityCodeTask();
			break;

		case R.id.agree_ckb:
			if(agree_ckb.isChecked()){
				agree_ckb.setBackgroundResource(R.drawable.checkbox_checked);
			}else {
				agree_ckb.setBackgroundResource(R.drawable.checkbox_normal);
			}		
			break;
			
		case R.id.xkservice_txt:
			startActivity(new Intent(mContext, RegisterServiceActivity.class));
			break;
			
		case R.id.register_txt:
			startRegisterTask();
			break;
		}
	}
	
	
	
	private RequestListener registerListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			  hideLoadingDialog();
				switch (message.what) {
				case Constants.ERROR_DATA_FROM_NET:
					Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
					break;
					
				case Constants.NO_DATA_FROM_NET:
                    if(!StringUtil.isEmpty((String) message.obj)){
                        Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    }

					break;
					
				case Constants.SUCCESS_DATA_FROM_NET:
                    if(!StringUtil.isEmpty((String) message.obj)){
                        Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
					UserService userService = new UserService();
					User user = userService.queryUser(Preference.getInstance().readUID());
					modelApp.setUser(user);
					startToTargetAct();
					
					break;
				}
		}
	};
	
	private void startRegisterTask() {
		
		mobile = phone_txt.getText().toString();
		passWord = psw_txt.getText().toString();
		mobileCode = code_txt.getText().toString();
		
		if(StringUtil.isEmpty(mobile)){
			Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(StringUtil.isEmpty(mobileCode)){
			Toast.makeText(mContext, "请填写验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!StringUtil.isEmpty(passWord) && passWord.trim().length() < 6){
			Toast.makeText(mContext, "密码长度6位以上", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!agree_ckb.isChecked()){
			Toast.makeText(mContext, "请阅读并同意《合房网服务协议》", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			
			if(registerRequest == null){
				registerRequest = new RegisterRequest("1", mobile, passWord, mobileCode,
						Preference.getInstance().readBDUserId(),
						Preference.getInstance().readBDChannelId(),
                        invitation_code_txt.getText().toString(),
                        DEVICE_ID,
                        registerListener);
			}else {
				registerRequest.setData("1", mobile, passWord, mobileCode,
						Preference.getInstance().readBDUserId(),
						Preference.getInstance().readBDChannelId(),
                        invitation_code_txt.getText().toString(),
                        DEVICE_ID);
			}
			showLoadingDialog("正在注册...");
			registerRequest.doRequest();
		}else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
		
	}
	

	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask() {
		mobile = phone_txt.getText().toString();
		if(StringUtil.isEmpty(mobile)){
			Toast.makeText(mContext, "请填写手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(NetUtil.detectAvailable(mContext)){
			if(codeRequest == null){
				codeRequest = new GetVerifyCodeRequest(mobile, new RequestListener() {
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
				codeRequest.setData(mobile);
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
						get_code_txt.setTextColor(mContext.getResources().getColor(R.color.white));
						get_code_txt.setText("获取验证码");
					}else{
						get_code_txt.setClickable(false);
						get_code_txt.setBackgroundResource(R.drawable.gray_corner_btn_bg);
						get_code_txt.setTextColor(mContext.getResources().getColor(R.color.common_gray_txt));
						get_code_txt.setText("获取验证码("+duration+")");
					}
				}
			});
			
		}
	}
	
	
	private void startToTargetAct() {
		Intent intent = null;
		if (clazz != null) {
			intent = new Intent(mContext, clazz);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			startActivity(intent);
		} else {
			startActivity(new Intent(mContext, MainActivity.class));
		}
	}
		
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PHONE_STATE_REQUEST_CODE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                DEVICE_ID = tm.getDeviceId();

            } else {
                //没有取得权限
            }
        }
    }


}
