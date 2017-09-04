package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;

/**
 * 我是员工
 */
public class EmployeeInfoActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;

    private TextView account_txt;

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

        fillData();
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

        account_txt = (TextView) findViewById(R.id.account_txt);


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
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){

            case R.id.account_txt:
//                startActivity(new Intent(this, EmployeeAccountInfoListActivity.class));
                startActivity(new Intent(this, TXRecordListActivity.class));
                break;
        }
	}
	
	
	private void fillData(){


	}
	


}
