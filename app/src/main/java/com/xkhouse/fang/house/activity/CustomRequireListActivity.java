package com.xkhouse.fang.house.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.house.entity.CommonType;

/** 
 * @Description: 帮你找房--定制需求的区域、价格、面积选择页
 * @author wujian  
 * @date 2015-9-18 上午8:58:44  
 */
public class CustomRequireListActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private String title;		
	private List<CommonType> requirements;	
	
	private ListView requirement_listview;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fillData();
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_custom_requirement_list);
	}
	
	@Override
	protected void init() {
		super.init();
		
		title = getIntent().getExtras().getString("title");
		requirements = (List<CommonType>) getIntent().getExtras().getSerializable("requirements");
		
		
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		requirement_listview = (ListView) findViewById(R.id.requirement_listview);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText(title);
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	@Override
	protected void setListeners() {
		super.setListeners();
		
		requirement_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("require", requirements.get(position));
				intent.putExtras(bundle);
				setResult(CustomHouseActivity.RESULT_CODE, intent);
				CustomRequireListActivity.this.finish();
			}
		});
	}
	
	
	private void fillData() {
		if (requirements != null && requirements.size() > 0) {
			requirement_listview.setAdapter(new RequirementAdapter());
		}
		
	}
	
	
	
	
	
	/*********************** 适配器  ***********************/
	class RequirementAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return requirements.size();
		}

		@Override
		public Object getItem(int position) {
			return requirements.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.item_requirement_list, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.name_txt.setText(requirements.get(position).getName());
			
			return convertView;
		}
		
		
		public class ViewHolder{
			TextView name_txt;
			
			public ViewHolder(View view){
				name_txt = (TextView) view.findViewById(R.id.name_txt);
			}
		}
	}
	
	
}
