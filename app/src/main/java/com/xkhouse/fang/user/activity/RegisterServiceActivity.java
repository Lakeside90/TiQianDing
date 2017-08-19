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
* @Description: 注册协议
* @author wujian  
* @date 2015-10-29 下午1:44:31
 */
public class RegisterServiceActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView title_txt;
	private TextView content_txt;
	
	
	private String title = "星房惠用户注册协议";  	
	private String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一、	服务条款的确认和接纳<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在您决定成为星房惠会员前，请仔细阅读本会员服务条款。您必须在完全同意如下条款的前提下，才能进行会员注册程序，通过完成注册程序，" +
			"便表明您接受了本服务协议的条款，成为会员，您只有在成为会员后，才能使用我们所提供的服务。用户在享受星房惠会服务时必须完全、严格遵守本服务条款。 <br/><br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;二、服务条款的完善和修改<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;根据互联网的发展和中华人民共和国有关法律、法规的变化，星房惠将不断地完善服务质量并依此修改会员服务条款。用户如果不同意服务条款的修改，" +
			"可以主动取消已经获得的网络服务，如果用户继续享用网络服务，则视为用户已经接受服务条款的修改。<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户的权利以及义务的表述，均以最新的服务条款为准。星房惠会保留随时修改或中断服务而不需通知您的权利，星房惠会行使修改或中断服务的权利，" +
			"不需对您或第三方负责。<br/><br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;三、会员服务条款说明<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;星房惠运用自己的操作系统通过国际互联网向用户提供丰富的网上资源，包括各种信息工具、网上论坛、个性化内容等。同时，会员必须遵守：<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）自行配备上网所必需的设备，包括计算机、数据机，其他存取装置或接受服务所需的其它设备；<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）自行支付与此服务有关的费用，如电话费用、网络费用等； <br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（3）会员在使用星房惠提供的服务时需对自己在星房惠的行为、言论负责。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为了能使用本服务，用户需同意以下事项：<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）依本服务注册表的提示提供您本人真实、正确、最新及完整的资料；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）会员在注册会员资格时，所提交的资料必须真实有效，否则星房惠有权拒绝其申请或者撤销其会员资格，" +
			"并不予任何赔偿或者退还任何已缴纳的收费服务费用。会员的个人资料发生变化时，应及时修改注册个人资料，" +
			"否则由此造成的会员权利不能全面有效地行使的责任由会员自己承担，星房惠有权因此取消其会员资格，并不予任何赔偿或者退还任何已缴纳的收费服务费用。<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（3）本会员资格只限本用户名使用，不得转让到其他用户名上。<br/><br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;四、会员账号、密码条款<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、用户一旦注册成功，便成为星房惠的合法会员，将得到一个密码和帐号。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、会员有义务保证密码和帐号的安全，并对利用该账号和密码所进行的一切活动负全责，因此所衍生的任何损失或损害，星房惠无法也不承担任何责任。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、会员因忘记密码或密码被盗向星房惠查询密码时，必须提供完全正确的注册信息，否则星房惠有权本着为会员保密的原则不予告知。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4、会员用户名中不得含有任何威胁、恐吓、漫骂、庸俗、亵渎、色情、淫秽、非法、反动、前后矛盾、攻击性、伤害性、骚扰性、诽谤性、辱骂性的或侵害他人知识产权的文字。<br/><br/>  " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;五、用户隐私制度<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;尊重会员的个人隐私是星房惠的一项基本政策，在未经会员授权时，星房惠保证不公开或披露会员的个人信息，也不会私自更改会员的注册信息。 除非有下列情况：<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（1）遵守有关法律法规的规定，向国家有关机关提供；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（2）保持维护星房惠的知识产权和其他重要权利；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（3）为维护社会公众利益、维护第三方合法权益；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（4）在紧急情况下维护用户个人和社会大众的隐私安全；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（5）其他需要公开、编辑或透露个人信息的情况，如 <br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a.在进行促销或抽奖时，星房惠可能会与赞助商共享会员的个人信息，在这些情况下星房惠会在发送会员信息之前进行提示，并且会员可以通过不参与来终止传送过程。<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. 星房惠可以将会员信息与第三方数据匹配。<br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;c. 星房惠会通过透露合计会员统计数据，向未来的合作伙伴、广告商及其他第三方以及为了其他合法目的而描述星房惠的服务。<br/> " +
			"星房惠会向会员发送客户订制的信息或者星房惠认为会员会感兴趣的其他信息。如果会员不希望收到这样的信息，可以回复信息退订。" +
			"星房惠对会员及其他任何第三方的上述行为，不承担任何责任。<br/><br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;六、拒绝提供担保<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;会员对网络服务的使用承担风险。星房惠对此不作任何类型的担保，不论是明确的或隐含的，但是不对商业性的隐含担保、特定目的和不违反规定的适当担保作限制。<br/><br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;七、关于中断或终止服务<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如发生下列任何一种情形，星房惠有权随时中断或终止服务而无需对会员或任何第三方承担任何责任： <br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1、会员提供的个人资料不真实；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2、会员违反本协议中规定的使用规则；<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3、会员在使用收费网络服务时未按规定向星房惠支付相应的服务费。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4、因发生不可抗拒的事由，如政府行为、不可抗力，导致会员服务无法继续提供，星房惠将尽快通知您，但不承担由此对您造成的任何损失或退还任何已缴纳的收费服务费用。<br/><br/> " +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;八、服务内容的版权<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;星房惠的会员服务内容（包括但不限于：文字、照片、图形、图像、图表、声音、FLASH 动画、视频、音频等〕，均受版权、商标或其它财产所有权法律的保护，" +
			"未经相关权利人同意，会员不能擅自复制、再造这些内容、或创造与内容有关的派生产品。<br/>" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本网站声明及修改权、更新权、最终解释权均属星房惠所有。 请仔细阅读以上条款，阅读结束后同意请按“我接受”按钮。<br/><br/>";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		title_txt.setText(title);
		content_txt.setText(Html.fromHtml(content));
	}
	
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_register_service);
	}

	@Override
	protected void init() {
		super.init();
		
	}

	@Override
	protected void findViews() {
		initTitle();
		title_txt = (TextView) findViewById(R.id.title_txt);
		content_txt = (TextView) findViewById(R.id.content_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("注册协议");
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
