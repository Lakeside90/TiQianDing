package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.task.MobileCodeRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/** 
 * @Description: 找回密码 
 * @author wujian  
 * @date 2015-10-9 上午11:14:46  
 */
public class FindPswActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText phone_txt;
	private EditText code_txt;
	private TextView get_code_txt;
	private TextView find_next_txt;
	private TextView content_txt;
	
	
	private String content = "<font color='#ff3100'>·</font>  为了您的账号安全，必须验证身份才能重设密码。<br/>"+
			"<font color='#ff3100'>·</font>  如果手机号或邮箱更换停用，你可以拨打400-887-1216(9:00-24:00)找回密码。<br/>" +
			"<font color='#ff3100'>·</font>  当天您最多可申请5次找回密码，如果5次均未成功找回，请您次日再来尝试。";
	
	
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	private MobileCodeRequest mobileCodeRequest;
	//参数
	private String mobile;	//手机号
	private String mobileCode; //验证码
		
		
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_findpsw);
	}

	@Override
	protected void findViews() {
		initTitle();
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		find_next_txt = (TextView) findViewById(R.id.find_next_txt);
		content_txt = (TextView) findViewById(R.id.content_txt);
		content_txt.setText(Html.fromHtml(content));
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("找回密码");
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
		find_next_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.get_code_txt:
			startVerityCodeTask();
			break;

		case R.id.find_next_txt:
			startFindPswTask();
			break;
		}
	}
	
	
	
	
	private void startFindPswTask() {
		mobile = phone_txt.getText().toString();
		mobileCode = code_txt.getText().toString();
		
		if(StringUtil.isEmpty(mobile) || StringUtil.isEmpty(mobileCode)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			if(mobileCodeRequest == null){
				mobileCodeRequest = new MobileCodeRequest(mobile, mobileCode, new RequestListener() {
					
					@Override
					public void sendMessage(Message message) {
						hideLoadingDialog();
						switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
							Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.NO_DATA_FROM_NET:
							Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
							break;
							
						case Constants.SUCCESS_DATA_FROM_NET:
							Toast.makeText(mContext, message.obj.toString(), Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(mContext, ResetPswActivity.class);
							Bundle data = new Bundle();
							data.putString("mobile", mobile);
							intent.putExtras(data);
							startActivity(intent);
							finish();
							break;
						}
					}
				});
			}else {
				mobileCodeRequest.setData(mobile, mobileCode);
			}
			showLoadingDialog("");
			mobileCodeRequest.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
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

	

}
