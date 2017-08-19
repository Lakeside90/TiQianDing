package com.xkhouse.fang.house.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.house.view.wheel.SelectDatePopWindow;
import com.xkhouse.fang.house.view.wheel.SelectDatePopWindow.DateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @Description: 预约看房 
 * @author wujian  
 * @date 2015-9-10 下午2:38:16  
 */
public class LookHouseOrderActivity extends AppBaseActivity {

	private View rootview;
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	//表单
	private EditText name_txt;
	private EditText phone_txt;
	private EditText verify_code_txt;
	private TextView get_code_txt;
	private TextView date_txt;
	private EditText remark_txt;
	
	private TextView submit_txt;
	
	private SelectDatePopWindow dateView;
	private DateSelectedListener dateListener;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_look_house_order);
		
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		name_txt = (EditText) findViewById(R.id.name_txt);
		phone_txt = (EditText) findViewById(R.id.phone_txt);
		verify_code_txt = (EditText) findViewById(R.id.verify_code_txt);
		get_code_txt = (TextView) findViewById(R.id.get_code_txt);
		date_txt = (TextView) findViewById(R.id.date_txt);
		remark_txt = (EditText) findViewById(R.id.remark_txt);
		
		submit_txt = (TextView) findViewById(R.id.submit_txt);
		
		initDateView();
		
	}
	
	private void initTitle() {
		rootview = findViewById(R.id.rootview);
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("预约看房");
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(dateView != null && dateView.isShowing()){
					dateView.dismiss();
					return ;
				}
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

	private void initDateView() {
		Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = df.format(date);
        String currentYear = dateStr.split("-")[0];
        String currentMonth = dateStr.split("-")[1];
        String currentDay = dateStr.split("-")[2];
        
        dateListener = new DateSelectedListener() {
			
			@Override
			public void onSelected(String year, String month, String day) {
				date_txt.setText(year + "-" + month + "-" + day);	
			}
		};
		dateView = new SelectDatePopWindow(mContext, currentYear, currentMonth, currentDay,
        										dateListener);
	}
	
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		get_code_txt.setOnClickListener(this);
		date_txt.setOnClickListener(this);
		submit_txt.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		
		case R.id.get_code_txt:
			
			break;
			
		case R.id.date_txt:
			dateView.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
			break;
			
		case R.id.submit_txt:
	
			break;
		
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(dateView != null && dateView.isShowing()){
				dateView.dismiss();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

}
