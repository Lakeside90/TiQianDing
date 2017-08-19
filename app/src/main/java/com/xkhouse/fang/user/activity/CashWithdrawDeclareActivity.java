package com.xkhouse.fang.user.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;

/**
* @Description: 提现说明
* @author wujian  
* @date 2015-10-29 下午1:44:31
 */
public class CashWithdrawDeclareActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView content_txt;
	
	private String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一、佣金审核申请：<br/>"
							+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、已推荐客户通过星房惠平台成功购房后，经过审核每套房源可领取相对应的佣金，" +
							"购买多套房源可以领取相对应数量佣金。客户成交后通过售楼部和星房惠平台审核和确认，审核通过后方可进行提现申请。<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、推荐人需提前12小时在系统平台提交购房者数据，如未能在规定时间提交数据，即使成交则视为无效客户；<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、申请兑现审核时间：<br/>" +
							" A、 被推荐人成交后，工作人员会在7个工作日提交审核结果，推荐人可根据审核结果进行提现申请。<br/>" +
							"B、 审核通过后佣金会默认发放至个人账户—我的佣金内。<br/><br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;二、提现<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、提现金额累计在100元以上方可进行提现申请；<br/>" +
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、佣金提现前需要设置提现密码，提现密码设置好可直接提现至绑定的银行卡和支付宝账户。<br/>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		content_txt.setText(Html.fromHtml(content));
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_about_us);
	}

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	protected void findViews() {
		initTitle();
		content_txt = (TextView) findViewById(R.id.content_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("提现说明");
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
		
	}
	
	
}
