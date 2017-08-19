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
* @Description: 免责申明 
* @author wujian  
* @date 2015-10-29 下午1:44:31
 */
public class DisclaimerActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView content_txt;
	
	private String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、一切网民在进入星房惠主页及各层页面时均被视为已经仔细看过本条款并 完全同意。凡以任何方式登陆本营业，" +
			"或直接、间接使用本应用资料者，均被 视为自愿接受本应用相关声明和用户服务协议的约束。<br/><br/>"+ 
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、星房惠转载的内容并不代表星房惠之意见及观点，也不意味着本应用赞 同其观点或证实其内容的真实性。<br/><br/>"+
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、星房惠转载的文字、图片、音视频等资料均由本应用用户提供，其真实 性、准确性和合法性由信息发布人负责。" +
			"星房惠不提供任何保证，并不承担任 何法律责任。<br/><br/>"+ 
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4、星房惠所转载的文字、图片、音视频等资料，如果侵犯了第三方的知识 产权或其他权利，" +
			"责任由作者或转载者本人承担，本应用对此不承担责任。<br/><br/>" + 
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5、星房惠不保证为向用户提供便利而设置的外部链接的准确性和完整性， 同时，" +
			"对于该外部链接指向的不由星房惠实际控制的任何网页上的内容，星房惠不承担任何责任。<br/><br/>"+ 
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6、用户明确并同意其使用星房惠网络服务所存在的风险将完全由其本人承 担；" +
			"因其使用星房惠网络服务而产生的一切后果也由其本人承担，星房惠对此 不承担任何责任。<br/><br/>"+
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7、除星房惠注明之服务条款外，其它因不当使用本应用而导致的任何意外 、疏忽、合约毁坏、诽谤、" +
			"版权或其他知识产权侵犯及其所造成的任何损失（ 包括因不当下载而感染手机病毒等），星房惠概不负责，亦不承担任何法律责 任。<br/><br/>"+
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8、对于因不可抗力或因黑客攻击、通讯线路中断等星房惠不能控制的原因 造成的网络服务中断或其他缺陷，导致用户不能正常使用"+
			"城星房惠，星房惠 不承担任何责任，但将尽力减少因此给用户造成的损失或影响。<br/><br/>"+
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9、本声明未涉及的问题请参见国家有关法律法规，当本声明与国家有关法 律法规冲突时，以国家法律法规为准。<br/><br/>"+
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;10、本网站相关声明版权及其修改权、更新权和最终解释权均属星房惠所有 。<br/><br/>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		content_txt.setText(Html.fromHtml(content));
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_disclaimer);
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
		tv_head_title.setText("免责申明");
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
