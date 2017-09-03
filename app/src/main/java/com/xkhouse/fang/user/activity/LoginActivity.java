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
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.LoginRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 登录页
 * @author wujian  
 * @date 2015-10-8 下午8:06:07  
 */
public class LoginActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;

	private EditText username_txt;
	private EditText psw_txt;

	private TextView login_txt;
	private TextView login_code_txt;

	
	private LoginRequest loginRequest;
	
	// 使用软件过程中 触发登录的动作，在进入到登录界面，然后又进到这里进行注册，注册完返回后执行的动作和登录后返回执行的动作一致
	private Class clazz;
	private Bundle bundle;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	}

	@Override
	protected void findViews() {
		initTitle();

        username_txt = (EditText) findViewById(R.id.username_txt);
		psw_txt = (EditText) findViewById(R.id.psw_txt);
		login_txt = (TextView) findViewById(R.id.login_txt);
		login_code_txt = (TextView) findViewById(R.id.login_code_txt);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
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

	}
	
	@Override
	protected void setListeners() {
		login_txt.setOnClickListener(this);
		login_code_txt.setOnClickListener(this);

	}

	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

            case R.id.login_txt:
                startLoginTask();
                break;

            case R.id.login_code_txt:
                Intent intent = new Intent(mContext, PswSetActivity.class);
                if (clazz != null) {
                    intent.putExtra("classStr", clazz);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                }
                startActivity(intent);
                finish();
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
					loginRequest = new LoginRequest(userName, passWord, requestListener);
				}else{
					loginRequest.setData(userName, passWord);
				}
				loginRequest.doRequest();
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
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

}
