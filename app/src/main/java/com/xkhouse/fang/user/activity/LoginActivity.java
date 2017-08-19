package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.MainActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.LoginPhoneRequest;
import com.xkhouse.fang.user.task.LoginRequest;
import com.xkhouse.fang.user.task.QQLoginRequest;
import com.xkhouse.fang.user.task.WXLoginRequest;
import com.xkhouse.fang.user.task.WeiboLoginRequest;
import com.xkhouse.fang.widget.ConfirmDialog;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 登录页
 * @author wujian  
 * @date 2015-10-8 下午8:06:07  
 */
public class LoginActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;

    //切换
    private LinearLayout login_username_lay;
    private TextView login_username_txt;
    private View login_username_line;

    private LinearLayout login_phone_lay;
    private TextView login_phone_txt;
    private View login_phone_line;


    //普通登录
    private LinearLayout login_one_lay;
	private EditText username_txt;
	private EditText psw_txt;
	private ImageView psw_see_iv;

    //手机号登录
    private LinearLayout login_two_lay;
    private EditText phone_txt;
    private EditText code_txt;
    private TextView get_code_txt;

	private TextView login_txt;
	private TextView find_psw_txt;

	private TextView login_notice_txt;
	private ImageView login_qq_iv;
	private ImageView login_wx_iv;
	private ImageView login_weibo_iv;
	
	private LoginRequest loginRequest;
	
	// 使用软件过程中 触发登录的动作，在进入到登录界面，然后又进到这里进行注册，注册完返回后执行的动作和登录后返回执行的动作一致
	private Class clazz;
	private Bundle bundle;
		
	private boolean isHidden=true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        String loginNotice = "用户系统升级，1月8日前使用PC端QQ登录绑定账号的用户请在" +
                "<a href='http://passport.xkhouse.com/old.html'>passport.xkhouse.com/old.html</a>此页面进行重新绑定。";
        login_notice_txt.setText(Html.fromHtml(loginNotice));
        login_notice_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_login);

	}

	@Override
	protected void init() {
		super.init();
		
		clazz = (Class) getIntent().getSerializableExtra("classStr");
		bundle = getIntent().getExtras();
		
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, "1104857499",
		                "ZNVEJATOlvNK33he");
		qqSsoHandler.addToSocialSDK();

        // 添加微信平台
        String appId = "wxa45adeb6ee24dfdb";
        String appSecret = "07e76cac282ff8fca887754f9e3917a6";
        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();

		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		
	}

	@Override
	protected void findViews() {
		initTitle();

        login_username_lay = (LinearLayout) findViewById(R.id.login_username_lay);
        login_username_txt = (TextView) findViewById(R.id.login_username_txt);
        login_username_line = findViewById(R.id.login_username_line);

        login_phone_lay = (LinearLayout) findViewById(R.id.login_phone_lay);
        login_phone_txt = (TextView) findViewById(R.id.login_phone_txt);
        login_phone_line = findViewById(R.id.login_phone_line);

        login_one_lay = (LinearLayout) findViewById(R.id.login_one_lay);
        username_txt = (EditText) findViewById(R.id.username_txt);
		psw_txt = (EditText) findViewById(R.id.psw_txt);
		psw_see_iv = (ImageView) findViewById(R.id.psw_see_iv);

        login_two_lay = (LinearLayout) findViewById(R.id.login_two_lay);
        phone_txt = (EditText) findViewById(R.id.phone_txt);
        code_txt = (EditText) findViewById(R.id.code_txt);
        get_code_txt = (TextView) findViewById(R.id.get_code_txt);

		login_txt = (TextView) findViewById(R.id.login_txt);
		find_psw_txt = (TextView) findViewById(R.id.find_psw_txt);

		login_qq_iv = (ImageView) findViewById(R.id.login_qq_iv);
		login_wx_iv = (ImageView) findViewById(R.id.login_wx_iv);
		login_weibo_iv = (ImageView) findViewById(R.id.login_weibo_iv);
        login_notice_txt = (TextView) findViewById(R.id.login_notice_txt);

        login_one_lay.setVisibility(View.VISIBLE);
        login_two_lay.setVisibility(View.GONE);
        login_username_txt.setTextColor(getResources().getColor(R.color.common_blue));
        login_username_line.setBackgroundResource(R.color.common_blue);
        login_phone_txt.setTextColor(getResources().getColor(R.color.c_666666));
        login_phone_line.setBackgroundResource(R.color.common_diver_line);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_right.setVisibility(View.VISIBLE);
		tv_head_right.setText("新用户");
		tv_head_title.setText("登录");
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
		tv_head_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                if (clazz != null) {
                    intent.putExtra("classStr", clazz);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                }
                startActivity(intent);
            }
        });
		
	}
	
	@Override
	protected void setListeners() {
		psw_see_iv.setOnClickListener(this);
		login_txt.setOnClickListener(this);
		find_psw_txt.setOnClickListener(this);
		login_qq_iv.setOnClickListener(this);
		login_wx_iv.setOnClickListener(this);
		login_weibo_iv.setOnClickListener(this);

        login_username_lay.setOnClickListener(this);
        login_phone_lay.setOnClickListener(this);
        get_code_txt.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
            case R.id.psw_see_iv:
                pswSeeStatusChange();
                break;

            case R.id.login_txt:
                if(login_one_lay.getVisibility() == View.VISIBLE){
                    startLoginTask();
                }else{
                    startPhoneTask();
                }
                break;

            case R.id.find_psw_txt:
                startActivity(new Intent(mContext, FindPswActivity.class));
                break;

            case R.id.login_qq_iv:
                QQLogin();
                break;

            case R.id.login_wx_iv:
                WXLogin();
                break;

            case R.id.login_weibo_iv:
                SinaLogin();
                break;

            case R.id.login_username_lay:
                login_one_lay.setVisibility(View.VISIBLE);
                login_two_lay.setVisibility(View.GONE);
                login_username_txt.setTextColor(getResources().getColor(R.color.common_blue));
                login_username_line.setBackgroundResource(R.color.common_blue);
                login_phone_txt.setTextColor(getResources().getColor(R.color.c_666666));
                login_phone_line.setBackgroundResource(R.color.common_diver_line);
                break;

            case R.id.login_phone_lay:
                login_one_lay.setVisibility(View.GONE);
                login_two_lay.setVisibility(View.VISIBLE);
                login_username_txt.setTextColor(getResources().getColor(R.color.c_666666));
                login_username_line.setBackgroundResource(R.color.common_diver_line);
                login_phone_txt.setTextColor(getResources().getColor(R.color.common_blue));
                login_phone_line.setBackgroundResource(R.color.common_blue);
                break;

            case R.id.get_code_txt:
                startVerityCodeTask();
                break;
		}
	}
	
	public void pswSeeStatusChange(){
		if (isHidden) {
            //设置EditText文本为可见的
			psw_txt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            //设置EditText文本为隐藏的
        	psw_txt.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        isHidden = !isHidden;
        psw_txt.postInvalidate();
        //切换后将EditText光标置于末尾
        CharSequence charSequence = psw_txt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
	}
	

	private RequestListener requestListener = new RequestListener() {
		
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
						showSaveDialog();
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
						startToTargetAct();
					}else {
						Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
					}
					
					break;
				}
		}
	};
	
	
	private void startLoginTask(){
		
		String userName = username_txt.getText().toString();
		String passWord = psw_txt.getText().toString();
		if(StringUtil.isEmpty(userName) || StringUtil.isEmpty(passWord)){
			Toast.makeText(this, "请填写完整!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (NetUtil.detectAvailable(mContext)) {
			   showLoadingDialog("正在登录...");
				if(loginRequest == null){
					loginRequest = new LoginRequest(userName, passWord, 
							Preference.getInstance().readBDUserId(),
							Preference.getInstance().readBDChannelId(), requestListener);
				}else{
					loginRequest.setData(userName, passWord, Preference.getInstance().readBDUserId(),
							Preference.getInstance().readBDChannelId());
				}
				loginRequest.doRequest();
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}


    private void startPhoneTask(){
        String phone = phone_txt.getText().toString();
        String code = code_txt.getText().toString();
        if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
            Toast.makeText(this, "请填写完整!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            showLoadingDialog("正在登录...");
            LoginPhoneRequest request = new LoginPhoneRequest(phone, code,
                    Preference.getInstance().readBDUserId(),
                    Preference.getInstance().readBDChannelId(), requestListener);
            request.doRequest();
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }




    /*********************  获取验证码    ****************************/
    private GetVerifyCodeRequest codeRequest;
    private Timer timer;
    private int duration = 60;

    private void startVerityCodeTask() {
        String mobile = phone_txt.getText().toString();
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




	
	public void showSaveDialog() {
		
        final ConfirmDialog confirmDialog = new ConfirmDialog(this, "首次登录，请绑定账号！", "绑定", "取消");
        confirmDialog.show();
        confirmDialog.setClicklistener(new ConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                
               //老用户登录，未绑定手机号
				Intent intent = new Intent(mContext, ChangeMobileActivity.class);
				Bundle data = new Bundle();
				data.putString("userName", username_txt.getText().toString());
				intent.putExtras(data);
				startActivity(intent);
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }

	
	
	private void startToTargetAct() {
		Intent intent = null;
		if (clazz != null) {
			intent = new Intent(mContext, clazz);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
		} else {
			intent = new Intent(mContext, MainActivity.class);
		}
		startActivity(intent);
		finish();
	}
	
	



	
	
	
	
	
	/*********************************** 第三方登录     ****************************************/
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	
	private void QQLogin(){
        if(!isApkInstalled("com.tencent.mobileqq")){
            Toast.makeText(LoginActivity.this, "请安装qq客户端!", Toast.LENGTH_SHORT).show();
            return;
        }
		mController.doOauthVerify(mContext, SHARE_MEDIA.QQ, new UMAuthListener() {
		    @Override
		    public void onStart(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
		    }
		    @Override
		    public void onError(SocializeException e, SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
		    }
		    @Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();
				openid = value.getString("openid");
				
				// 获取相关授权信息
				mController.getPlatformInfo(mContext, SHARE_MEDIA.QQ,
						new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(mContext, "获取平台数据开始...",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(int status,
									Map<String, Object> info) {
								if (status == 200 && info != null) {
									StringBuilder sb = new StringBuilder();
									Set<String> keys = info.keySet();
									for (String key : keys) {
										sb.append(key + "=" + info.get(key).toString() + "\r\n");
									}
									Logger.d("TestData", sb.toString());
									profileImage = info.get("profile_image_url").toString();
									startQQLoginTask();
								} else {
									Logger.d("TestData", "发生错误：" + status);
								}
							}
						});
			}
		    @Override
		    public void onCancel(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
		    }
		} );
	}
	
	
	private void WXLogin(){
        if(!isApkInstalled("com.tencent.mm")){
            Toast.makeText(LoginActivity.this, "请安装微信客户端!", Toast.LENGTH_SHORT).show();
            return;
        }
		mController.doOauthVerify(mContext, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
		    @Override
		    public void onStart(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
		    }
		    @Override
		    public void onError(SocializeException e, SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
		    }
		    @Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();
                openid = value.getString("openid");

				// 获取相关授权信息
				mController.getPlatformInfo(mContext, SHARE_MEDIA.WEIXIN,
						new UMDataListener() {
							@Override
							public void onStart() {
								Toast.makeText(mContext, "获取平台数据开始...",
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onComplete(int status,
									Map<String, Object> info) {
								if (status == 200 && info != null) {
									StringBuilder sb = new StringBuilder();
									Set<String> keys = info.keySet();
									for (String key : keys) {
										sb.append(key + "=" + info.get(key).toString() + "\r\n");
									}
									Logger.d("TestData", sb.toString());
									
									//TODO
                                    startWXLoginTask();
								} else {
									Logger.d("TestData", "发生错误："+ status);
								}
							}
						});
			}
		    @Override
		    public void onCancel(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
		    }
		} );
	}
	
	
	private void SinaLogin(){
		mController.doOauthVerify(mContext, SHARE_MEDIA.SINA, new UMAuthListener() {
		    @Override
		    public void onStart(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权开始", Toast.LENGTH_SHORT).show();
		    }
		    @Override
		    public void onError(SocializeException e, SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权错误", Toast.LENGTH_SHORT).show();
		    }
		    @Override
			public void onComplete(Bundle value, SHARE_MEDIA platform) {
				Toast.makeText(mContext, "授权完成", Toast.LENGTH_SHORT).show();
				
				openid = value.getString("uid");
				startWeiboLoginTask();

			}
		    @Override
		    public void onCancel(SHARE_MEDIA platform) {
		        Toast.makeText(mContext, "授权取消", Toast.LENGTH_SHORT).show();
		    }
		} );
		
	}

	
	private String openid;			//第三方平台ID
	private String profileImage;   //qq头像
	
	private void startQQLoginTask() {
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
                            Intent intent = new Intent(mContext, SFPhoneBindActivity.class);
                            Bundle data = new Bundle();
                            data.putString("openid", openid);
                            data.putString("profileImage", profileImage);
                            data.putString("loginType", Constants.LOGIN_TYPE_QQ);
                            intent.putExtras(data);
                            startActivity(intent);
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
							startToTargetAct();
						}else {
							Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					}
				}
			});
			showLoadingDialog("正在检测账号绑定...");
			request.doCheckRequest();
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

    private void startWXLoginTask() {
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
                                        Intent intent = new Intent(mContext, SFPhoneBindActivity.class);
                                        Bundle data = new Bundle();
                                        data.putString("openid", openid);
                                        data.putString("loginType", Constants.LOGIN_TYPE_WX);
                                        intent.putExtras(data);
                                        startActivity(intent);
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
                                        startToTargetAct();
                                    }else {
                                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                                    }

                                    break;
                            }
                        }
                    });
            showLoadingDialog("正在检测账号绑定...");
            request.doCheckRequest();
        }else{
            Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }

	private void startWeiboLoginTask() {
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
                            Intent intent = new Intent(mContext, SFPhoneBindActivity.class);
                            Bundle data = new Bundle();
                            data.putString("openid", openid);
                            data.putString("loginType", Constants.LOGIN_TYPE_WEIBO);
                            intent.putExtras(data);
                            startActivity(intent);
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
							startToTargetAct();
						}else {
							Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					}
				}
			});
			showLoadingDialog("正在检测账号绑定...");
			request.doCheckRequest();
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */  
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}


    public boolean isApkInstalled(String packageName) {
        try {
            getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }


	
}
