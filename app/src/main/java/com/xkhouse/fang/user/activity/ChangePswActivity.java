package com.xkhouse.fang.user.activity;

import android.os.Bundle;
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
import com.xkhouse.fang.user.task.ChangePasswordRequest;
import com.xkhouse.fang.user.task.SetPasswordRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/** 
 * @Description: 修改密码 
 * @author wujian  
 * @date 2015-10-9 上午11:14:46  
 */
public class ChangePswActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;

    private LinearLayout change_psw_lay;
	private EditText old_psw_txt;
	private EditText new_psw_txt;
	private EditText re_psw_txt;

    private LinearLayout set_psw_lay;
    private EditText set_psw_txt;
    private EditText set_repsw_txt;

	private TextView commit_txt;
	
	
	private ChangePasswordRequest request;
	//参数
	 
	private String oldpassword;  	//旧密码
	private String passWord; 		//新密码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if("0".equals(modelApp.getUser().getPassportstatus())){
            //设置密码
            change_psw_lay.setVisibility(View.GONE);
            set_psw_lay.setVisibility(View.VISIBLE);
            tv_head_title.setText("设置密码");
        }else{
            //修改密码
            change_psw_lay.setVisibility(View.VISIBLE);
            set_psw_lay.setVisibility(View.GONE);
            tv_head_title.setText("修改密码");
        }

    }

    @Override
	protected void setContentView() {
		setContentView(R.layout.activity_changepsw);
	}

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	protected void findViews() {
		initTitle();

        change_psw_lay = (LinearLayout) findViewById(R.id.change_psw_lay);
        new_psw_txt = (EditText) findViewById(R.id.new_psw_txt);
		old_psw_txt = (EditText) findViewById(R.id.old_psw_txt);
		re_psw_txt = (EditText) findViewById(R.id.re_psw_txt);

        set_psw_lay = (LinearLayout) findViewById(R.id.set_psw_lay);
        set_psw_txt = (EditText) findViewById(R.id.set_psw_txt);
        set_repsw_txt = (EditText) findViewById(R.id.set_repsw_txt);

        commit_txt = (TextView) findViewById(R.id.commit_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
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
            if(set_psw_lay.getVisibility() == View.VISIBLE){
                startSetPswTask();
            }else{
                startFindPswTask();
            }
			break;
	
		}
	}
	
	
	
	private void startFindPswTask() {
		passWord = new_psw_txt.getText().toString();
		oldpassword = old_psw_txt.getText().toString();
		String rePassWord = re_psw_txt.getText().toString();
		
		if(StringUtil.isEmpty(passWord) || StringUtil.isEmpty(oldpassword) 
				|| StringUtil.isEmpty(rePassWord)){
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
				request = new ChangePasswordRequest(modelApp.getUser().getUid(), 
						oldpassword, passWord, new RequestListener() {
					
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
				request.setData(modelApp.getUser().getUid(), oldpassword, passWord);
			}
			showLoadingDialog("密码修改中...");
			request.doRequest();
			
		}else {
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}



    private void startSetPswTask() {
        passWord = set_psw_txt.getText().toString();
        String rePassWord = set_repsw_txt.getText().toString();

        if(StringUtil.isEmpty(passWord) || StringUtil.isEmpty(rePassWord)){
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

            SetPasswordRequest  request = new SetPasswordRequest(modelApp.getUser().getUid(), passWord,
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
                        modelApp.getUser().setPassportstatus("1");
                        finish();
                        break;
                }
            }
        });

            showLoadingDialog("密码设置中...");
            request.doRequest();

        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }
    }


}
