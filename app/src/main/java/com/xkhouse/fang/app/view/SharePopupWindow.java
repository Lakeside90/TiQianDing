package com.xkhouse.fang.app.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xkhouse.fang.R;
import com.xkhouse.lib.utils.StringUtil;


/**
* @Description: 分享第三方平台面板
 * 微信好友、QQ好友、QQ空间分享 规则：
        楼盘
        楼盘名 、页面描述、楼盘图片
        资讯、专题、图集、直播、活动
        资讯标题去掉后缀、描述、图片（没有图片的用星房惠logo）

        朋友圈分享
        楼盘
        楼盘名 、楼盘图片
        资讯、专题、图集、直播、活动
        资讯标题去掉后缀、图片（没有图片的用星房惠logo）

        微博分享
        楼盘
        楼盘名 页面URL、楼盘图片
        资讯、专题、图集、直播、活动
        资讯标题去掉后缀、页面URL、图片（没有图片的用星房惠logo）
 *
 * MD,日了狗了
 *
* @author wujian  
* @date 2015-11-3 下午5:41:57
 */

public class SharePopupWindow extends PopupWindow implements OnClickListener {
	
	private Context mContext;
	private View rootView;
	
	private TextView wechat_txt;
	private TextView qq_txt;
	private TextView wechat_circle_txt;
	private TextView weibo_txt;
	private TextView qqzone_txt;
	private TextView tencet_txt;
	private TextView cancel_btn;
	
	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	private String title = "星房惠";
	private String content = "星房惠客户端，您口袋中的置业管家。提供全城楼盘搜索，独家购房优惠，热门楼盘火爆抢购等多重服务，安家路上，与您同行。";
	private String url = "http://www.hfhouse.com";
    private String imageUrl;  //分享图片地址
    private boolean isNews = true;  //是否是分享资讯


	private UMImage image = new UMImage(mContext, R.mipmap.ic_launcher);
	
	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public SharePopupWindow(Context context) {
		super(context);
		this.mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootView = inflater.inflate(R.layout.share_popup, null);
		this.setContentView(rootView);
		init();
		findView();
		setListener();
	}

	private void init() {
		// wxa45adeb6ee24dfdb是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wxa45adeb6ee24dfdb";
		String appSecret = "07e76cac282ff8fca887754f9e3917a6";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(mContext,appId, appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId,appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((Activity) mContext, "1104857499",
		                "ZNVEJATOlvNK33he");
		qqSsoHandler.addToSocialSDK(); 
		
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) mContext, "1104857499",
		                "ZNVEJATOlvNK33he");
		qZoneSsoHandler.addToSocialSDK();
		
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		
		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		
		
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0x44000000);
		this.setBackgroundDrawable(dw);
		
	}

	private void findView() {
		wechat_txt = (TextView) rootView.findViewById(R.id.wechat_txt);
		qq_txt = (TextView) rootView.findViewById(R.id.qq_txt);
		wechat_circle_txt = (TextView) rootView.findViewById(R.id.wechat_circle_txt);
		weibo_txt = (TextView) rootView.findViewById(R.id.weibo_txt);
		qqzone_txt = (TextView) rootView.findViewById(R.id.qqzone_txt);
		tencet_txt = (TextView) rootView.findViewById(R.id.tencet_txt);
		
		cancel_btn = (TextView) rootView.findViewById(R.id.cancel_btn);
	}

	private void setListener() {
		wechat_txt.setOnClickListener(this);
		qq_txt.setOnClickListener(this);
		wechat_circle_txt.setOnClickListener(this);
		weibo_txt.setOnClickListener(this);
		qqzone_txt.setOnClickListener(this);
		tencet_txt.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
	}
	
	
	public void showAtLocation(String title, String content, String imageUrl, String url, boolean isNews,
                               View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);

		this.url = url;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.isNews = isNews;

        if (StringUtil.isEmpty(this.content)) this.content = this.title;
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wechat_txt:

			WeiXinShareContent weixinContent = new WeiXinShareContent();
//            if (isNews){
//                weixinContent.setShareContent(title);
//                weixinContent.setTitle("星房惠");
//            }else{
                weixinContent.setShareContent(content);
                weixinContent.setTitle(title);
//            }
			weixinContent.setTargetUrl(url);
            if (StringUtil.isEmpty(imageUrl)){
                weixinContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
            }else{
                weixinContent.setShareImage(new UMImage(mContext, imageUrl));
            }
			mController.setShareMedia(weixinContent);
			performShare(SHARE_MEDIA.WEIXIN);
			break;

		case R.id.qq_txt:
			QQShareContent qqShareContent = new QQShareContent();
//            if (isNews){
//                qqShareContent.setShareContent(title);
//                qqShareContent.setTitle("星房惠");
//            }else{
                qqShareContent.setShareContent(content);
                qqShareContent.setTitle(title);
//            }
            if (StringUtil.isEmpty(imageUrl)){
                qqShareContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
            }else{
                qqShareContent.setShareImage(new UMImage(mContext, imageUrl));
            }
			qqShareContent.setTargetUrl(url);
			mController.setShareMedia(qqShareContent);
			performShare(SHARE_MEDIA.QQ);
			break;
		
		case R.id.wechat_circle_txt:
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(title);
			circleMedia.setTitle(title);
			circleMedia.setTargetUrl(url);
            if (StringUtil.isEmpty(imageUrl)){
                circleMedia.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
            }else{
                circleMedia.setShareImage(new UMImage(mContext, imageUrl));
            }
			mController.setShareMedia(circleMedia);
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		
		case R.id.weibo_txt:
			SinaShareContent sinaContent = new SinaShareContent();
            sinaContent.setShareContent(title + url);
            sinaContent.setTitle(title);
            sinaContent.setTargetUrl(url);
            if (StringUtil.isEmpty(imageUrl) || imageUrl.contains("weixin_340100")){
                //news/images/images/weixin/weixin_340100.jpg  这张图片微博分享失败5004
                sinaContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
            }else{
                sinaContent.setShareImage(new UMImage(mContext, imageUrl));
            }
			mController.setShareMedia(sinaContent);
			performShare(SHARE_MEDIA.SINA);
			break;
		
		case R.id.qqzone_txt:
			QZoneShareContent qzone = new QZoneShareContent();
//            if (isNews){
//                qzone.setShareContent(title);
//                qzone.setTitle("星房惠");
//            }else{
                qzone.setShareContent(content);
                qzone.setTitle(title);
//            }
			qzone.setTargetUrl(url);
            if (StringUtil.isEmpty(imageUrl)){
                qzone.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
            }else{
                qzone.setShareImage(new UMImage(mContext, imageUrl));
            }
            mController.setShareMedia(qzone);
			performShare(SHARE_MEDIA.QZONE);
			break;
			
		case R.id.tencet_txt:
			TencentWbShareContent tencentContent = new TencentWbShareContent();
			tencentContent.setShareContent(content);
			tencentContent.setTitle(title);
			tencentContent.setTargetUrl(url);
			tencentContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(tencentContent);
			performShare(SHARE_MEDIA.TENCENT);
			break;
			
		case R.id.cancel_btn:
			dismiss();
			break;
			 
		}
	}

	
	private void performShare(SHARE_MEDIA platform) {
		mController.postShare(mContext, platform, new SnsPostListener() {
			@Override
			public void onStart() {
//				Toast.makeText(mContext, "开始分享", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				if (eCode == 200) {
//					Toast.makeText(mContext, "分享成功", Toast.LENGTH_SHORT)
//							.show();
				} else {
					String eMsg = "";
					if (eCode == -101) {
						eMsg = "没有授权";
					}
//					Toast.makeText(mContext, "分享失败[" + eCode + "] " + eMsg,
//							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
}
