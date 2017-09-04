package com.xkhouse.fang.user.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.user.adapter.DSDetailAdapter;
import com.xkhouse.fang.user.adapter.TXListAdapter;
import com.xkhouse.fang.user.adapter.TXRecordAdapter;
import com.xkhouse.fang.widget.ScrollListView;

/**
 * 提现
 */
public class EmployeeTXlActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;


    private ScrollListView listView;

    private TXListAdapter adapter;



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
		setContentView(R.layout.activity_employee_tx);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
		initTitle();


        listView = (ScrollListView) findViewById(R.id.listView);


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

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
        switch (v.getId()){


        }
	}
	
	
	private void fillData(){

        if (adapter == null) {

            adapter = new TXListAdapter(mContext, null);
            listView.setAdapter(adapter);
        }

	}
	


}
