package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.entity.EmployeeInfo;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.task.EmployeeInfoRequest;
import com.xkhouse.fang.user.task.UserInfoRequest;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 我是员工
 */
public class EmployeeInfoActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;

	private TextView name_txt;
	private ImageView icon_iv;
	private TextView money_txt;
	private TextView num_txt;

	private TextView account_txt;
	private TextView withdraw_txt;
	private TextView comment_txt;

	private EmployeeInfo employeeInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.employee_blue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        startTask();
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_employee_info);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();

		name_txt = (TextView) findViewById(R.id.name_txt);
		money_txt = (TextView) findViewById(R.id.money_txt);
		num_txt = (TextView) findViewById(R.id.num_txt);
		icon_iv = (ImageView) findViewById(R.id.icon_iv);

		account_txt = (TextView) findViewById(R.id.account_txt);
		withdraw_txt = (TextView) findViewById(R.id.withdraw_txt);
		comment_txt = (TextView) findViewById(R.id.comment_txt);


    }

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
        iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {

        account_txt.setOnClickListener(this);
		withdraw_txt.setOnClickListener(this);
		comment_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){

            case R.id.account_txt:
                startActivity(new Intent(this, EmployeeAccountInfoListActivity.class));
                break;

			case R.id.withdraw_txt:
				startActivity(new Intent(this, TXRecordListActivity.class));
				break;

			case R.id.comment_txt:
//				startActivity(new Intent(this, TXRecordListActivity.class));
				// TODO: 2017/10/9  web page
				break;
        }


	}
	
	
	private void fillData(){

		if (employeeInfo == null) return;

		name_txt.setText("早上好：" + employeeInfo.getName());
		money_txt.setText(employeeInfo.getReward_balance() + "元");
		withdraw_txt.setText("本月剩余提现次数：" + employeeInfo.getWithdrawals_num());

	}


	private void startTask() {
		if(NetUtil.detectAvailable(this)){
			EmployeeInfoRequest request = new EmployeeInfoRequest(modelApp.getUser().getToken(), new RequestListener() {

				@Override
				public void sendMessage(Message message) {
					hideLoadingDialog();
					switch (message.what) {
						case Constants.ERROR_DATA_FROM_NET:
						case Constants.NO_DATA_FROM_NET:
							break;

						case Constants.SUCCESS_DATA_FROM_NET:
							employeeInfo = (EmployeeInfo) message.obj;
							fillData();
							break;
					}
				}
			});
			request.doRequest();

		}else {
			Toast.makeText(this, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}


}
