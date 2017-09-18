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
import com.xkhouse.fang.user.task.ChangeNickNameRequest;
import com.xkhouse.fang.user.task.UserInfoEditRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;


/**
* @Description: 修改昵称
* @author wujian  
 */
public class ChangeNameActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText name_txt;
	private TextView commit_txt;

	//参数
	private String name;	//手机号
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_change_name);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();
		name_txt = (EditText) findViewById(R.id.name_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("修改昵称");
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
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.commit_txt:
			startChangeNameTask();
			break;
	
		}
	}

	
	private void startChangeNameTask() {
		name = name_txt.getText().toString();
		if(StringUtil.isEmpty(name)){
			Toast.makeText(mContext, "请填写完整！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(NetUtil.detectAvailable(mContext)){
			ChangeNickNameRequest request = new ChangeNickNameRequest(
					modelApp.getUser().getToken(), name,
					new RequestListener() {
				
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

			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

}
