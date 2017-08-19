package com.xkhouse.fang.user.activity;

import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.task.UserInfoEditRequest;
import com.xkhouse.lib.utils.NetUtil;


/**
* @Description: 修改用户名，姓名 
* @author wujian  
* @date 2015-10-28 上午8:49:40
 */
public class ChangeSexActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView male_txt;
	private TextView female_txt;
	
	//参数
	private String name;	//手机号
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_change_sex);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();
		male_txt = (TextView) findViewById(R.id.male_txt);
		female_txt = (TextView) findViewById(R.id.female_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		
		tv_head_title.setText("修改性别");
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		male_txt.setOnClickListener(this);
		female_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.male_txt:
			startChangeSexTask("1");
			break;
			
		case R.id.female_txt:
			startChangeSexTask("2");
			break;
		}
	}
	
	
	
	private void startChangeSexTask(String sex) {
		
		if(NetUtil.detectAvailable(mContext)){
			UserInfoEditRequest request = new UserInfoEditRequest(modelApp.getUser().getUid(), new RequestListener() {
				
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
			
			showLoadingDialog("修改中...");
			request.doSexRequest(sex);
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
	
}
