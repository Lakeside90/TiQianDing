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
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.xkhouse.fang.user.task.QQLoginRequest;
import com.xkhouse.fang.user.task.QQRebindRequest;
import com.xkhouse.fang.user.task.SinaRebindRequest;
import com.xkhouse.fang.user.task.WXLoginRequest;
import com.xkhouse.fang.user.task.WXRebindRequest;
import com.xkhouse.fang.user.task.WeiboLoginRequest;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
* @Description:  第三方登录绑定手机号
* @author wujian  
* @date 2016-04-05 上午8:35:22
 */
public class SFPhoneBindActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;

    private TextView bind_desc_txt;
	private EditText phone_txt;
	private EditText code_txt;
	private TextView get_code_txt;
	

	private TextView register_bind_txt;

	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	//参数
	private String openid;
	private String profileImage;
	private String mobile;
	private String mobileCode;
//	private String passWord;
	
	private String loginType;

    private String DEVICE_ID;      //设备ID
    private int READ_PHONE_STATE_REQUEST_CODE = 100;

    private String description = "您的QQ微信微博账号已通过认证<br/>手机号将用来登录和便捷升级其他服务，星房惠APP保证会员信息不被泄露和用作其他用途";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        bind_desc_txt.setText(Html.fromHtml(description));
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_sf_phone_bind);
	}

	@Override
	protected void init() {
		super.init();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);

        } else {
            //执行获取权限后的操作
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = tm.getDeviceId();
        }

		openid = getIntent().getExtras().getString("openid");
		profileImage = getIntent().getExtras().getString("profileImage");
		loginType = getIntent().getExtras().getString("loginType");
		
	}

	@Override
	protected void findViews() {
		initTitle();
        bind_desc_txt = (TextView) findViewById(R.id.bind_desc_txt);

        phone_txt = (EditText) findViewById(R.id.phone_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);

		register_bind_txt = (TextView) findViewById(R.id.register_bind_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("绑定账号");
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
		register_bind_txt.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
            case R.id.get_code_txt:
                startVerityCodeTask();
                break;


            case R.id.register_bind_txt:
                if (Constants.LOGIN_TYPE_QQ.equals(loginType)) {
                    startQQLoginTask();
                } else if (Constants.LOGIN_TYPE_WEIBO.equals(loginType)) {
                    startWeiboLoginTask();
                } else if (Constants.LOGIN_TYPE_WX.equals(loginType)) {
                    startWXLoginTask();
                }
                break;
        }
	}



    /******************************** QQ登录相关 ********************************/
	private void startQQLoginTask() {
		mobile = phone_txt.getText().toString();
		mobileCode = code_txt.getText().toString();

		if(StringUtil.isEmpty(mobile) || StringUtil.isEmpty(mobileCode)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (NetUtil.detectAvailable(mContext)) {
			QQLoginRequest request = new QQLoginRequest(openid, Preference.getInstance().readBDUserId(),
					Preference.getInstance().readBDChannelId(), 
					new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					 hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.NO_DATA_FROM_NET:
                        String msg = message.obj.toString();
                        if("106".equals(msg)){
                            showQQBindDialog();
                        }else{
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        }
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						User user = (User) message.obj;
						if(user != null){
							Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
							Preference.getInstance().writeIsLogin(true);
							Preference.getInstance().writeUID(user.getUid());
							Preference.getInstance().writePassword(user.getPassword());
							modelApp.setUser(user);
							startActivity(new Intent(mContext, MainActivity.class));
							finish();
						}else {
							Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					}
				}
			});
			showLoadingDialog("正在绑定...");
			request.doBindRequest(mobile, mobileCode, profileImage, DEVICE_ID);
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

    //重新绑定QQ
    private void startQQRebindTask(){
        if (NetUtil.detectAvailable(mContext)) {
            QQRebindRequest request = new QQRebindRequest(mobile, openid, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:

                            String msg = (String) message.obj;
                            if(StringUtil.isEmpty(msg)){
                                msg = "绑定失败";
                            }
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            User user = (User) message.obj;
                            if(user != null){
                                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                                Preference.getInstance().writeIsLogin(true);
                                Preference.getInstance().writeUID(user.getUid());
                                Preference.getInstance().writePassword(user.getPassword());
                                modelApp.setUser(user);
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            });
            showLoadingDialog("正在绑定...");
            request.doRequest();
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //qq绑定对话框
    public void showQQBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "该账号已被绑定，是否解绑旧账号绑定新帐号？", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                startQQRebindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }




    /******************************** 微信登录相关 ********************************/
    private void startWXLoginTask() {
        mobile = phone_txt.getText().toString();
        mobileCode = code_txt.getText().toString();
        if(StringUtil.isEmpty(mobile) || StringUtil.isEmpty(mobileCode)){
            Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            WXLoginRequest request = new WXLoginRequest(openid, Preference.getInstance().readBDUserId(),
                    Preference.getInstance().readBDChannelId(),
                    new RequestListener() {

                        @Override
                        public void sendMessage(Message message) {
                            hideLoadingDialog();
                            switch (message.what) {
                                case Constants.ERROR_DATA_FROM_NET:
                                    Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                                    break;

                                case Constants.NO_DATA_FROM_NET:
                                    String msg = message.obj.toString();
                                    if("106".equals(msg)){
                                        showWXBindDialog();
                                    }else{
                                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                                    }
                                    break;

                                case Constants.SUCCESS_DATA_FROM_NET:
                                    User user = (User) message.obj;
                                    if(user != null){
                                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                                        Preference.getInstance().writeIsLogin(true);
                                        Preference.getInstance().writeUID(user.getUid());
                                        Preference.getInstance().writePassword(user.getPassword());
                                        modelApp.setUser(user);
                                        startActivity(new Intent(mContext, MainActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                            }
                        }
                    });
            showLoadingDialog("正在绑定...");
            request.doBindRequest(mobile, mobileCode, DEVICE_ID);
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

    //重新绑定微信
    private void startWXRebindTask(){
        if (NetUtil.detectAvailable(mContext)) {
            WXRebindRequest request = new WXRebindRequest(mobile, openid, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if (StringUtil.isEmpty(msg)){
                                msg = "绑定失败";
                            }
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            User user = (User) message.obj;
                            if(user != null){
                                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                                Preference.getInstance().writeIsLogin(true);
                                Preference.getInstance().writeUID(user.getUid());
                                Preference.getInstance().writePassword(user.getPassword());
                                modelApp.setUser(user);
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            });
            showLoadingDialog("正在绑定...");
            request.doRequest();
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //微信绑定对话框
    public void showWXBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "该账号已被绑定，是否解绑旧账号绑定新帐号？", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                startWXRebindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }




    /******************************** 微博登录相关 ********************************/
	private void startWeiboLoginTask() {
		mobile = phone_txt.getText().toString();
		mobileCode = code_txt.getText().toString();
		if(StringUtil.isEmpty(mobile) || StringUtil.isEmpty(mobileCode)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (NetUtil.detectAvailable(mContext)) {
			WeiboLoginRequest request = new WeiboLoginRequest(openid, Preference.getInstance().readBDUserId(),
					Preference.getInstance().readBDChannelId(),
					new RequestListener() {
				
				@Override
				public void sendMessage(Message message) {
					 hideLoadingDialog();
					switch (message.what) {
					case Constants.ERROR_DATA_FROM_NET:
						Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
						break;
						
					case Constants.NO_DATA_FROM_NET:
                        String msg = message.obj.toString();
                        if("106".equals(msg)){
                            showSinaBindDialog();
                        }else{
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        }
						
						break;
						
					case Constants.SUCCESS_DATA_FROM_NET:
						User user = (User) message.obj;
						if(user != null){
							Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
							Preference.getInstance().writeIsLogin(true);
							Preference.getInstance().writeUID(user.getUid());
							Preference.getInstance().writePassword(user.getPassword());
							modelApp.setUser(user);
							startActivity(new Intent(mContext, MainActivity.class));
							finish();
						}else {
							Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					}
				}
			});
			showLoadingDialog("正在绑定...");
			request.doBindRequest(mobile, mobileCode, DEVICE_ID);
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

    //重新绑定微博
    private void startSinaRebindTask(){
        if (NetUtil.detectAvailable(mContext)) {
            SinaRebindRequest request = new SinaRebindRequest(mobile, openid, new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if (StringUtil.isEmpty(msg)){
                                msg = "绑定失败";
                            }
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            User user = (User) message.obj;
                            if(user != null){
                                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                                Preference.getInstance().writeIsLogin(true);
                                Preference.getInstance().writeUID(user.getUid());
                                Preference.getInstance().writePassword(user.getPassword());
                                modelApp.setUser(user);
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            });
            showLoadingDialog("正在绑定...");
            request.doRequest();
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


    //sina绑定对话框
    public void showSinaBindDialog() {

        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "该账号已被绑定，是否解绑旧账号绑定新帐号？", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                startSinaRebindTask();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
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
