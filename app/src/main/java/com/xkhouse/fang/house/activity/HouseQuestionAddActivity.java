package com.xkhouse.fang.house.activity;

import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.xkhouse.fang.house.task.HouseQuestionAddRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 我要提问
 * @author wujian  
 * @date 2016-6-20
 */
public class HouseQuestionAddActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private EditText content_txt;
	private TextView content_length_txt;
	private TextView commit_txt;

	
	private HouseQuestionAddRequest request;
	private String content;  	
	private String uid = "";

    private String pid;


	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_house_question_add);
	}

	@Override
	protected void init() {
		super.init();
        pid = getIntent().getExtras().getString("pid");
	}

	@Override
	protected void findViews() {
		initTitle();
        content_txt = (EditText) findViewById(R.id.content_txt);
        content_length_txt = (TextView) findViewById(R.id.content_length_txt);
		commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("我要提问");
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

        //监听搜索框的文字变化
        content_txt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = content_txt.getText().toString().trim().length();
                if (count > 200){
                    Toast.makeText(mContext, "提问内容字数在200以内！", Toast.LENGTH_SHORT).show();
                }
                content_length_txt.setText(count + "/200");
            }
        });
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
		
		content = content_txt.getText().toString().trim();

		if(StringUtil.isEmpty(content)){
			Toast.makeText(mContext, "请填写提问内容！", Toast.LENGTH_SHORT).show();
			return;
		}

        if (content_txt.getText().toString().trim().length() > 200){
            Toast.makeText(mContext, "提问内容字数在200以内！", Toast.LENGTH_SHORT).show();
            return;
        }
		
		if(NetUtil.detectAvailable(mContext)){
			if(request == null){
				if(Preference.getInstance().readIsLogin()){
					uid = modelApp.getUser().getId();
				}
				
				request = new HouseQuestionAddRequest(uid, pid, modelApp.getSite().getSiteId(), modelApp.getUser().getPhone(), content, new RequestListener() {
					
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
				request.setData(uid, pid, modelApp.getSite().getSiteId(), modelApp.getUser().getPhone(), content);
			}
			showLoadingDialog("意见提交中...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}

	

}
