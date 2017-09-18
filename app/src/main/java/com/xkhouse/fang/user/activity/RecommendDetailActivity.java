package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.user.adapter.XKRecommendStatusListAdapter;
import com.xkhouse.fang.user.entity.XKRecommendDetail;
import com.xkhouse.fang.user.task.XKRecommendDetailRequest;
import com.xkhouse.fang.widget.ScrollListView;
import com.xkhouse.lib.utils.NetUtil;

/** 
 * @Description: 推荐客户详情 
 * @author wujian  
 * @date 2015-11-12 上午10:00:47  
 */
public class RecommendDetailActivity extends AppBaseActivity {

	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView name_txt;
	private TextView date_txt;
    private ImageView call_iv;
    private ImageView ask_iv;

	private TextView city_txt;
	private TextView project_txt;
	private TextView price_txt;
	private TextView remark_txt;
	private TextView recommended_status_txt;
	
	private ScrollListView status_listview;

	
	private XKRecommendDetail recommendDetail;
	private String recommendId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startDataTask();
	}
	
	@Override
	protected void init() {
		super.init();
		recommendId = getIntent().getExtras().getString("recommendId");
	}
	
	
	@Override
	protected void setContentView() {
		super.setContentView();
		setContentView(R.layout.activity_recommend_detail);
	}
	
	@Override
	protected void findViews() {
		super.findViews();
		initTitle();
		
		name_txt = (TextView) findViewById(R.id.name_txt);
		date_txt = (TextView) findViewById(R.id.date_txt);
		city_txt = (TextView) findViewById(R.id.city_txt);
		project_txt = (TextView) findViewById(R.id.project_txt);
		price_txt = (TextView) findViewById(R.id.price_txt);
		remark_txt = (TextView) findViewById(R.id.remark_txt);
		recommended_status_txt = (TextView) findViewById(R.id.recommended_status_txt);
		call_iv = (ImageView) findViewById(R.id.call_iv);
		ask_iv = (ImageView) findViewById(R.id.ask_iv);
		status_listview = (ScrollListView) findViewById(R.id.status_listview);
	}
	
	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("客户详情");
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
		call_iv.setOnClickListener(this);
		ask_iv.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.call_iv:
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+recommendDetail.getPhone()))); 
			break;

		case R.id.ask_iv:
			Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + recommendDetail.getPhone()));
			intent.putExtra("sms_body", "");
			startActivity(intent);
			break;
		}
	}
	
	private void fillData(){
		if(recommendDetail == null) return;
		name_txt.setText(recommendDetail.getName() + " " + recommendDetail.getPhone());
		date_txt.setText(recommendDetail.getDate());
		city_txt.setText(recommendDetail.getCity());
		project_txt.setText(recommendDetail.getProject());
		price_txt.setText(recommendDetail.getAveragePrice());
		remark_txt.setText(recommendDetail.getRemark());
		
		if("0".equals(recommendDetail.getStatus())){
			recommended_status_txt.setText("[推荐中]");
			recommended_status_txt.setTextColor(getResources().getColor(R.color.common_gray_txt));
		}else if("1".equals(recommendDetail.getStatus())){
			recommended_status_txt.setText("[推荐成功]");
			recommended_status_txt.setTextColor(getResources().getColor(R.color.common_green));
		}else if("2".equals(recommendDetail.getStatus())){
			recommended_status_txt.setText("[推荐失败]");
			recommended_status_txt.setTextColor(getResources().getColor(R.color.common_red_txt));
		}
		
		if(recommendDetail.getStatusList() != null){
			status_listview.setAdapter(new XKRecommendStatusListAdapter(mContext,
					recommendDetail.getStatusList()));
		}
	}
	
	private void startDataTask(){
		if(NetUtil.detectAvailable(mContext)){
			XKRecommendDetailRequest request = new XKRecommendDetailRequest(recommendId,
					modelApp.getUser().getId(),
					new RequestListener() {
						
						@Override
						public void sendMessage(Message message) {
							hideLoadingDialog();
							switch (message.what) {
							case Constants.ERROR_DATA_FROM_NET:
							case Constants.NO_DATA_FROM_NET:
								break;
								
							case Constants.SUCCESS_DATA_FROM_NET:
								recommendDetail = (XKRecommendDetail) message.obj;
								fillData();
								break;
							}
						}
					});
			showLoadingDialog(R.string.data_loading);
			request.doRequest();
		}else{
			Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
		}
	}
}
