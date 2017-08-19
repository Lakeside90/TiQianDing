package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.task.ChangePayPasswordRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
* @Description: 设置/修改支付密码
* @author wujian  
* @date 2015-10-27 下午2:03:02
 */
public class ChangePayPswActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText old_psw_txt;
	private EditText new_psw_txt;
	private EditText re_psw_txt;
    private LinearLayout code_lay;
	private EditText code_txt;
	private TextView get_code_txt;
	private TextView commit_txt;
	private TextView find_paypsw_txt;

	public static final String PAY_TYPE_SET = "0";
	public static final String PAY_TYPE_CHANGE = "1";
	
	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	//参数
	private String paystatus;
	private String oldpassword = "";  	//旧密码
	private String newpassword; 	//新密码
	private String mobileCode; 		//新密码
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_change_paypsw);
	}

	@Override
	protected void init() {
		super.init();
		paystatus = getIntent().getExtras().getString("paystatus");
	}

	@Override
	protected void findViews() {
		initTitle();
		new_psw_txt = (EditText) findViewById(R.id.new_psw_txt);
		old_psw_txt = (EditText) findViewById(R.id.old_psw_txt);
		re_psw_txt = (EditText) findViewById(R.id.re_psw_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
        find_paypsw_txt = (TextView) findViewById(R.id.find_paypsw_txt);
        code_lay = (LinearLayout) findViewById(R.id.code_lay);

        if(PAY_TYPE_SET.equals(paystatus)){
			old_psw_txt.setVisibility(View.GONE);
		}else{
            code_lay.setVisibility(View.GONE);
        }
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		if(PAY_TYPE_SET.equals(paystatus)){
			tv_head_title.setText("设置支付密码");
		}else {
			tv_head_title.setText("修改支付密码");
		}
		
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
        find_paypsw_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.commit_txt:
			startChangePswTask();
			break;
			
		case R.id.get_code_txt:
			startVerityCodeTask();
			break;

        case R.id.find_paypsw_txt:
            startActivity(new Intent(ChangePayPswActivity.this, FindPaypswActivity.class));
            ChangePayPswActivity.this.finish();
            break;
		}
	}
	
	
	
	private void startChangePswTask() {
		newpassword = new_psw_txt.getText().toString();
		oldpassword = old_psw_txt.getText().toString();
		mobileCode = code_txt.getText().toString();
		
		if(StringUtil.isEmpty(newpassword)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
        if (PAY_TYPE_SET.equals(paystatus) && StringUtil.isEmpty(mobileCode)){
            Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
            return;
        }
		if(PAY_TYPE_CHANGE.equals(paystatus)){
			//修改密码
			String rePassWord = re_psw_txt.getText().toString();
			if(StringUtil.isEmpty(rePassWord) || StringUtil.isEmpty(oldpassword)){
				Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
				return;
			}
			if(!newpassword.equals(rePassWord)){
				Toast.makeText(mContext, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	
		if(newpassword.trim().length() < 6){
			Toast.makeText(mContext, "密码长度6位以上", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			ChangePayPasswordRequest request = new ChangePayPasswordRequest(modelApp.getUser().getUid(),
					oldpassword, newpassword,
					paystatus, modelApp.getUser().getMobile(), mobileCode,  new RequestListener() {
					
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
			
			showLoadingDialog("密码修改中...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask() {
		String mobile = modelApp.getUser().getMobile();
		
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
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}
	

}
