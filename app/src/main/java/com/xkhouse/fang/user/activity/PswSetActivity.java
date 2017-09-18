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
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.task.PswSetRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 设置密码页
 */
public class PswSetActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;

	private TextView username_txt;
	private EditText psw_txt;

	private TextView confirm_txt;

	
	private PswSetRequest pswSetRequest;

    private String phone;
		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        username_txt.setText(phone);

    }
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_psw_set);

	}

	@Override
	protected void init() {
		super.init();
        phone = getIntent().getExtras().getString("phone");
	}

	@Override
	protected void findViews() {
		initTitle();

        username_txt = (TextView) findViewById(R.id.username_txt);
		psw_txt = (EditText) findViewById(R.id.psw_txt);

		confirm_txt = (TextView) findViewById(R.id.confirm_txt);

	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("设置密码");
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
		confirm_txt.setOnClickListener(this);
	}

	
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

            case R.id.confirm_txt:
                startPswSetTask();
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
                    String msg2 = message.obj.toString();
                    Toast.makeText(mContext, msg2, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PswSetActivity.this, LoginActivity.class));
                    finish();
					break;
				}
		}
	};
	
	
	private void startPswSetTask(){
		
		String userName = username_txt.getText().toString();
		String passWord = psw_txt.getText().toString();
		if(StringUtil.isEmpty(userName) || StringUtil.isEmpty(passWord)){
			Toast.makeText(this, "请填写完整!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (NetUtil.detectAvailable(mContext)) {
			   showLoadingDialog("正在设置密码...");
				if(pswSetRequest == null){
                    pswSetRequest = new PswSetRequest(passWord, requestListener);
				}else{
                    pswSetRequest.setData(passWord);
				}
            pswSetRequest.doRequest();
		}else{
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

}
