package com.xkhouse.fang.app.activity;

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
import com.xkhouse.fang.app.adapter.ShoppingIntentionAdapter;
import com.xkhouse.fang.house.entity.CommonType;

/**
* @Description: 购买意向
* @author wujian  
* @date 2015-10-20 下午3:13:45
 */
public class JJShoppingIntentionActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	private ListView intention_listview;
	private ShoppingIntentionAdapter adapter;
    private ArrayList<CommonType> typeList;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fillData();
		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_jj_shopping_intention);
	}
	
	@Override
	protected void init() {
		super.init();
		typeList = (ArrayList<CommonType>) getIntent().getExtras().getSerializable("typeList");
		
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		intention_listview = (ListView) findViewById(R.id.intention_listview);
		
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		intention_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				CommonType type = typeList.get(position);
				if(type.isSelected()){
					type.setSelected(false);
				}else {
					type.setSelected(true);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		
		tv_head_title.setText("选择购买意向");
		tv_head_right.setText("确认");
		tv_head_right.setVisibility(View.VISIBLE);
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putSerializable("typeList", typeList);
				intent.putExtras(data);
				setResult(JJAppointmentActivity.RESULT_CODE, intent);
				finish();
			}
		});
		
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private void fillData(){
		if(typeList == null) return;
		
		if(adapter == null){
			adapter = new ShoppingIntentionAdapter(typeList, mContext);
			intention_listview.setAdapter(adapter);
		}else{
			adapter.setData(typeList);
		}
	}
	
	
}
