package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.house.entity.CommonType;
import com.xkhouse.fang.money.activity.CustomerAddActivity;
import com.xkhouse.fang.money.adapter.ExpectHouseAdapter;
import com.xkhouse.fang.money.adapter.ExpectHouseAdapter.ExpectHouseItemClickListener;
import com.xkhouse.fang.money.entity.XKBArea;
import com.xkhouse.fang.money.entity.XKBHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * @Description: 期望区域（求租，求购）
 * @author wujian  
 * @date 2016-04-12
 */
public class ExpectStreetActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	private TextView tv_head_right;
	
	private ExpandableListView area_house_list;
	private ExpectHouseAdapter adapter;
	private List<XKBArea> groupAreas = new ArrayList<XKBArea>();
    private HashMap<String, List<XKBHouse>> childHouses = new HashMap<String, List<XKBHouse>>();
    
    private ArrayList<CommonType> areaHouseList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		fillData();
		
	}
	
	@Override
	protected void setContentView() {
		super.setContentView();
		
		setContentView(R.layout.activity_expect_street);
	}
	
	@Override
	protected void init() {
		super.init();
		areaHouseList = (ArrayList<CommonType>) getIntent().getExtras().getSerializable("areaHouseList");
		String houseID = getIntent().getExtras().getString("houseID");
		String[] houseIDs = null;
		if(houseID != null){
			houseIDs = houseID.split(",");
		}
		
		if(areaHouseList != null){
			for(int i = 0; i < areaHouseList.size(); i++){
				CommonType area = areaHouseList.get(i);
				XKBArea xkbArea = new XKBArea();
				xkbArea.setId(area.getId());
				xkbArea.setName(area.getName());
				xkbArea.setSelected(false);
				groupAreas.add(xkbArea);
				
				List<CommonType> houseList = area.getChild();
				List<XKBHouse> houses = new ArrayList<XKBHouse>();
				if(houseList != null){
					for(int j = 0; j < houseList.size(); j++ ){
						CommonType house = houseList.get(j);
						XKBHouse xkbHouse = new XKBHouse();
						xkbHouse.setId(house.getId());
						xkbHouse.setName(house.getName());
						xkbHouse.setSelected(false);
						if(houseIDs != null){
							for(String houseId : houseIDs){
								if(houseId.equals(house.getId())){
									xkbHouse.setSelected(true);
									break;
								}
							}
						}
						
						houses.add(xkbHouse);
					}
				}
				childHouses.put(area.getId(), houses);
			}
		}
		
	}
	
	
	@Override
	protected void findViews() {
		super.findViews();
		
		initTitle();
		
		area_house_list = (ExpandableListView) findViewById(R.id.area_house_list);
		
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_right = (TextView) findViewById(R.id.tv_head_right);
		
		tv_head_title.setText("选择楼盘");
		tv_head_right.setText("确认");
		tv_head_right.setVisibility(View.VISIBLE);
		tv_head_right.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle data = new Bundle();
				data.putSerializable("childHouses", childHouses);
				intent.putExtras(data);
				setResult(CustomerAddActivity.RESULT_CODE, intent);
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
	
	
	
	private ExpectHouseItemClickListener clickListener = new ExpectHouseItemClickListener() {
		
		@Override
		public void onHouseClick(int groupPosition, int childPosition) {
			XKBHouse house = childHouses.get(groupAreas.get(groupPosition).getId()).get(childPosition);
			if(house.isSelected()){
				house.setSelected(false);
			}else{
				house.setSelected(true);
			}
			adapter.notifyDataSetChanged();
		}
	};
	
	private void fillData(){
		if(groupAreas == null || childHouses == null) return;
		
		if(adapter == null){
			adapter = new ExpectHouseAdapter(mContext, groupAreas, childHouses, clickListener);
			area_house_list.setAdapter(adapter);
		}else{
			adapter.refreshData(groupAreas, childHouses);
		}
		
	}
	
	
}
