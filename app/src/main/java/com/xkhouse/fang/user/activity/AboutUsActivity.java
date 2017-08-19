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
* @Description: 关于我们
* @author wujian  
* @date 2015-10-29 下午1:44:31
 */
public class AboutUsActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView content_txt;
	
	
	private String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;星空传媒控股是一家致力于打造亚洲地产领袖传媒的多元化企业，有着覆盖全国的市场版图，" +
            "是全国唯一一家覆盖安徽全省20个地市分站的房地产网络平台，已经在北京、南京、南昌、南宁、香港等地建立分站，" +
            "旗下合房网、安房网、星空地产网、星空汽车网正以其昂首的姿态，走在行业的前沿，绘制出一幅壮丽的星空版图。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“星房惠”是星空传媒控股在互联网+时代下自主研发的新一代移动端房产APP，由原合房网、安房网、星空地产网、" +
            "星空宝四大房产APP融合升级而成。产品集新房导购、定制购房、在线贷款、荐房赚佣、二手房、热门活动、" +
            "家居等诸多强大功能与一身，也是房地产O2O电商平台，实现线上线下买房新体验，全方位解决用户买房置业种种难题。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;通过星房惠APP上43个城市站点的信息互通与共享，平台将充分满足用户对房产置业、安家装修、买房贷款等需求。" +
            "“星房惠”也将凭借强大的复合功能以及潜在千万级的用户，成为全国领先的房产类APP之一。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;第一手房产资讯  实时掌握市场变动<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“星房惠”专业的运营团队每天24小时及时为全国置业者推送最新鲜、最具买房参考价值的地产资讯，" +
            "精准的楼盘动态，详细的户型评析，准确的价格数据，令置业者选取满意住宅不再烦心。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;来自全国的丰富房源  独家星空团买房更便宜<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“星房惠”将全国千万楼盘悉数汇聚其中，智能化的定位将为购房者时时推送实地楼盘信息。" +
            "全国百大品牌房企全线合作，建立“星级”房源库，囊括全方位的置业需求。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;买房首付有困难  一切交给星房惠<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“星房惠”无缝嫁接星空传媒控股金融平台，为购房者提供首付贷、信用贷等5大金融服务，" +
            "一键“申请贷款”，专业工作人员即刻到位一对一服务，审核仅需3-7个工作日，高效率助年轻购房者轻松安家。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;全民经纪人  推荐买房赚佣金赚不停<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为打通买房与卖房的互通，“星房惠” 不仅给买房者提供更多信息，" +
            "同样可通过荐房赚佣版块让用户在买房之余化身半个置业顾问，为楼盘推荐客户将有机会给予相应高额佣金作为奖励。<br/>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;作为星空传媒控股布局“互联网+”的全新产品，星房惠将成为购房者与地产商之间的完美平台。" +
            "从最初对楼盘的选择，再到买房希望获取的独家优惠，最后置业安家搞装修，" +
            "“星房惠”提供星级房源，星级优惠，更有星级专属服务，让用户在整个购房置业过程中变得轻松惬意。<br/>";
	
	
	
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
		tv_head_title.setText("关于我们");
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
