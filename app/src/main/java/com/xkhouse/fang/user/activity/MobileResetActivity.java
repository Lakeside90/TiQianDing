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
import com.xkhouse.fang.app.task.GetVerifyCodeRequest;
import com.xkhouse.fang.user.task.MobileResetRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 修改手机号
 */
public class MobileResetActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView old_phone_txt;
	private TextView get_old_code_txt;
	private EditText code_old_txt;


	private EditText phone_txt;
	private TextView get_code_txt;
	private EditText code_txt;


	private TextView commit_txt;
	

	private GetVerifyCodeRequest codeRequest;
	private Timer timer;
	private int duration = 60;
	
	//参数
	private String oldphone;
	private String oldverif;
	private String newphone;
	private String newverif;

	private MobileResetRequest mobileResetRequest;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_reset_mobile);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

		old_phone_txt = (TextView) findViewById(R.id.old_phone_txt);
		get_old_code_txt = (TextView) findViewById(R.id.get_old_code_txt);
		code_old_txt = (EditText) findViewById(R.id.code_old_txt);

		phone_txt = (EditText) findViewById(R.id.phone_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		code_txt = (EditText) findViewById(R.id.code_txt);


	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("修改手机号");

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
		get_old_code_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.commit_txt:
				startMobileResetTask();
				break;

			case R.id.get_old_code_txt:
				startVerityCodeTask(old_phone_txt.getText().toString());
				break;

			case R.id.get_code_txt:
				startVerityCodeTask(phone_txt.getText().toString());
				break;
		}
	}
	
	
	
	private void startMobileResetTask() {

		oldphone = old_phone_txt.getText().toString();
		oldverif = code_old_txt.getText().toString();
		newphone = phone_txt.getText().toString();
		newverif = code_txt.getText().toString();
		
		if(StringUtil.isEmpty(oldphone) || StringUtil.isEmpty(oldverif) ||
				StringUtil.isEmpty(newphone) || StringUtil.isEmpty(newverif)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}

		if(NetUtil.detectAvailable(mContext)){
			if(mobileResetRequest == null){
                mobileResetRequest = new MobileResetRequest(modelApp.getUser().getToken(),
						oldphone, oldverif, newphone, newverif,
                        new RequestListener() {
                            @Override
                            public void sendMessage(Message message) {
                                hideLoadingDialog();
                                switch (message.what) {
                                    case Constants.ERROR_DATA_FROM_NET:
                                        Toast.makeText(mContext, "修改手机号失败！", Toast.LENGTH_SHORT).show();
                                        break;

                                    case Constants.NO_DATA_FROM_NET:
                                        String msgStr = (String) message.obj;
                                        Toast.makeText(mContext, msgStr, Toast.LENGTH_SHORT).show();
                                        break;

                                    case Constants.SUCCESS_DATA_FROM_NET:
                                        Toast.makeText(mContext, "修改手机号成功！", Toast.LENGTH_SHORT).show();
                                        finish();
                                        break;
                                }
                            }
                        });
            }else {
                mobileResetRequest.setData(oldphone, oldverif, newphone, newverif);
            }
            showLoadingDialog(R.string.data_loading);
            mobileResetRequest.doRequest();
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

	/*********************  获取验证码    ****************************/
	
	private void startVerityCodeTask(String mobile) {
        mobile = phone_txt.getText().toString();
		if (StringUtil.isEmpty(mobile)){
            Toast.makeText(MobileResetActivity.this, "请填写手机号", Toast.LENGTH_SHORT).show();
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
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}
	

}
