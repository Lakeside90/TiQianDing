package com.xkhouse.fang.user.activity;

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
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.MainActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.LoginPhoneRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 验证码登陆
 */
public class LoginByCodeActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;

    //手机号登录
    private EditText phone_txt;
    private EditText code_txt;
    private TextView get_code_txt;

	private TextView login_txt;
	private TextView login_psw_txt;


	// 使用软件过程中 触发登录的动作，在进入到登录界面，然后又进到这里进行注册，注册完返回后执行的动作和登录后返回执行的动作一致
	private Class clazz;
	private Bundle bundle;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    }
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_login_by_code);

	}

	@Override
	protected void init() {
		super.init();
		
		clazz = (Class) getIntent().getSerializableExtra("classStr");
		bundle = getIntent().getExtras();

	}

	@Override
	protected void findViews() {
		initTitle();


        phone_txt = (EditText) findViewById(R.id.phone_txt);
        code_txt = (EditText) findViewById(R.id.code_txt);
        get_code_txt = (TextView) findViewById(R.id.get_code_txt);

		login_txt = (TextView) findViewById(R.id.login_txt);
		login_psw_txt = (TextView) findViewById(R.id.login_psw_txt);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		tv_head_right.setVisibility(View.VISIBLE);
		tv_head_right.setText("新用户");
		tv_head_title.setText("验证码登录");
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
//                Intent intent = new Intent(mContext, RegisterActivity.class);
//                if (clazz != null) {
//                    intent.putExtra("classStr", clazz);
//                    if (bundle != null) {
//                        intent.putExtras(bundle);
//                    }
//                }
//                startActivity(intent);
            }
        });
		
	}
	
	@Override
	protected void setListeners() {
		login_txt.setOnClickListener(this);
        login_psw_txt.setOnClickListener(this);
        get_code_txt.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

            case R.id.login_txt:
                startPhoneTask();
                break;

            case R.id.login_psw_txt:
                Intent intent = new Intent(mContext, LoginActivity.class);
                if (clazz != null) {
                    intent.putExtra("classStr", clazz);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                }
                startActivity(intent);
                finish();
                break;

            case R.id.get_code_txt:
                startVerityCodeTask();
                break;
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
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					break;
					
				case Constants.SUCCESS_DATA_FROM_NET:
					User user = (User) message.obj;
					if(user != null){
						Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
						Preference.getInstance().writeIsLogin(true);
						Preference.getInstance().writeUID(user.getUid());
						Preference.getInstance().writePassword(user.getPassword());
                        Preference.getInstance().writeToken(user.getToken());
						modelApp.setUser(user);
						startToTargetAct();
					}else {
						Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
					}
					
					break;
				}
		}
	};
	
	
    private void startPhoneTask(){
        String phone = phone_txt.getText().toString();
        String code = code_txt.getText().toString();
        if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(code)){
            Toast.makeText(this, "请填写完整!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (NetUtil.detectAvailable(mContext)) {
            showLoadingDialog("正在登录...");
            LoginPhoneRequest request = new LoginPhoneRequest(phone, code, requestListener);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

}
