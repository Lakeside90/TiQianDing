package com.xkhouse.fang.user.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.activity.JJAppointmentActivity;
import com.xkhouse.fang.app.adapter.ShoppingIntentionAdapter;
import com.xkhouse.fang.user.adapter.CashBankListAdapter;
import com.xkhouse.fang.user.entity.XKBank;
import com.xkhouse.fang.user.view.CashBankView;

/**
* @Description: 星空宝--选择银行 
* @author wujian  
* @date 2015-10-22 上午10:33:28
 */
public class CashBankListActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private ListView bank_listview;
	private CashBankListAdapter adapter;
    private ArrayList<XKBank> bankList;
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fillData();
		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_cash_bank_list);
	}
	
	@Override
	protected void init() {
		super.init();
		bankList = (ArrayList<XKBank>) getIntent().getExtras().getSerializable("bankList");
		
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		bank_listview = (ListView) findViewById(R.id.bank_listview);
		
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		bank_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putSerializable("bank", bankList.get(position));
				intent.putExtras(data);
				setResult(CashBankView.RESULT_CODE, intent);
				finish();
			}
		});
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("选择银行");
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void fillData(){
		if(bankList == null) return;
		
		if(adapter == null){
			adapter = new CashBankListAdapter(mContext, bankList);
			bank_listview.setAdapter(adapter);
		}else{
			adapter.setData(bankList);
		}
	}
	
	
}
