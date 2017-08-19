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
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.task.FeedBackRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 修改密码 
 * @author wujian  
 * @date 2015-10-9 上午11:14:46  
 */
public class FeedBackActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText feed_back_txt;
	private EditText feed_contact_txt;
	private TextView commit_txt;
	
	
	private FeedBackRequest request;
	private String content;  	
	private String contact;
	private String uid = "";
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_feedback);
	}

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	protected void findViews() {
		initTitle();
		feed_back_txt = (EditText) findViewById(R.id.feed_back_txt);
        feed_contact_txt = (EditText) findViewById(R.id.feed_contact_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("意见反馈");
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
			startFeedBackTask();
			break;
	
		}
	}
	
	
	
	private void startFeedBackTask() {
		
		content = feed_back_txt.getText().toString();
        contact = feed_contact_txt.getText().toString();

		if(StringUtil.isEmpty(content)){
			Toast.makeText(mContext, "请填写反馈意见！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		
		if(NetUtil.detectAvailable(mContext)){
			if(request == null){
				if(Preference.getInstance().readIsLogin()){
					uid = modelApp.getUser().getUid();
				}
				
				request = new FeedBackRequest(uid, content, contact, new RequestListener() {
					
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
				request.setData(uid, content, contact);
			}
			showLoadingDialog("意见提交中...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

}
