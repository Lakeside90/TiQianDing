package com.xkhouse.fang.user.activity;

import android.os.Message;
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
import com.xkhouse.fang.user.task.ResetPasswordRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;


/** 
 * @Description: 重置密码
 * @author wujian  
 */
public class ResetPswActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

	private TextView phone_txt;
	private TextView get_code_txt;
	private EditText code_txt;


	private EditText new_psw_txt;
	private EditText re_psw_txt;
	private TextView commit_txt;

	//参数
	private String token;
	private String phone;	//手机号
	private String verif;  	//验证码
	private String passWord; //密码

	private ResetPasswordRequest request;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_reset_psw);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

		phone_txt = (TextView) findViewById(R.id.phone_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);

		new_psw_txt = (EditText) findViewById(R.id.new_psw_txt);
		re_psw_txt = (EditText) findViewById(R.id.re_psw_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("重置密码");
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
		commit_txt.setOnClickListener(this);
		get_code_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.commit_txt:
				startFindPswTask();
				break;

			case R.id.get_code_txt:
				// TODO: 2017/9/14   验证码
				break;
	
		}
	}
	
	
	
	private void startFindPswTask() {

		verif = code_txt.getText().toString();

		passWord = new_psw_txt.getText().toString();
		String rePassWord = re_psw_txt.getText().toString();
		if(StringUtil.isEmpty(passWord) || StringUtil.isEmpty(rePassWord) ||
				StringUtil.isEmpty(phone) || StringUtil.isEmpty(verif)){

			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!passWord.equals(rePassWord)){
			Toast.makeText(mContext, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(passWord.trim().length() < 6){
			Toast.makeText(mContext, "密码长度6位以上", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			if(request == null){
				request = new ResetPasswordRequest(token, phone,
						 passWord, verif, new RequestListener() {
					
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
							finish();
							break;
						}
					}
				});
			}else {
				request.setData(phone, passWord, verif);
			}
			showLoadingDialog("密码设置中...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

}
