package com.xkhouse.fang.user.activity;

import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.fang.user.task.InvitationCodeRequest;
import com.xkhouse.fang.widget.xlist.InvitationConfirmDialog;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
* @Description: 邀请好友
* @author wujian  
* @date 2015-10-10 上午9:29:17
 */
public class AppShareActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private TextView wechat_txt;
	private TextView qq_txt;
	private TextView wechat_circle_txt;
	private TextView weibo_txt;
	private TextView qqzone_txt;
	private TextView tencet_txt;
	private TextView copy_txt;

    private TextView invitation_code_txt;   //获取邀请码

	private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	private String title = "星房惠客户端";
	private String content = "星房惠客户端，您口袋中的置业管家。提供全城楼盘搜索，独家购房优惠，热门楼盘火爆抢购等多重服务，安家路上，与您同行。";
	private String url = "http://app.xkhouse.com/";
	private UMImage image = new UMImage(mContext, R.mipmap.ic_launcher);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
	protected void setContentView() {
		setContentView(R.layout.activity_share_app);
	}

	@Override
	protected void init() {
		super.init();
		
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
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, "1104857499",
		                "ZNVEJATOlvNK33he");
		qqSsoHandler.addToSocialSDK(); 
		
		//参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, "1104857499",
		                "ZNVEJATOlvNK33he");
		qZoneSsoHandler.addToSocialSDK();
		
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());

		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
	}

	@Override
	protected void findViews() {
		initTitle();
		
		wechat_txt = (TextView) findViewById(R.id.wechat_txt);
		qq_txt = (TextView) findViewById(R.id.qq_txt);
		wechat_circle_txt = (TextView) findViewById(R.id.wechat_circle_txt);
		weibo_txt = (TextView) findViewById(R.id.weibo_txt);
		qqzone_txt = (TextView) findViewById(R.id.qqzone_txt);
		tencet_txt = (TextView) findViewById(R.id.tencet_txt);
		copy_txt = (TextView) findViewById(R.id.copy_txt);
        invitation_code_txt = (TextView) findViewById(R.id.invitation_code_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("邀请好友");
		iv_head_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	protected void setListeners() {
		wechat_txt.setOnClickListener(this);
		qq_txt.setOnClickListener(this);
		wechat_circle_txt.setOnClickListener(this);
		weibo_txt.setOnClickListener(this);
		qqzone_txt.setOnClickListener(this);
		tencet_txt.setOnClickListener(this);
		copy_txt.setOnClickListener(this);
        invitation_code_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wechat_txt:
			//设置微信好友分享内容
			WeiXinShareContent weixinContent = new WeiXinShareContent();
			//设置分享文字
			weixinContent.setShareContent(getShareContent());
			//设置title
			weixinContent.setTitle(title);
			//设置分享内容跳转URL
			weixinContent.setTargetUrl(url);
			weixinContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(weixinContent);
			performShare(SHARE_MEDIA.WEIXIN);
			break;

		case R.id.qq_txt:
			QQShareContent qqShareContent = new QQShareContent();
			//设置分享文字
			qqShareContent.setShareContent(getShareContent());
			//设置分享title
			qqShareContent.setTitle(title);
			//设置分享图片
			qqShareContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			//设置点击分享内容的跳转链接
			qqShareContent.setTargetUrl(url);
			mController.setShareMedia(qqShareContent);
			performShare(SHARE_MEDIA.QQ);
			break;
		
		case R.id.wechat_circle_txt:
			//设置微信朋友圈分享内容
			CircleShareContent circleMedia = new CircleShareContent();
			circleMedia.setShareContent(getShareContent());
			//设置朋友圈title
			circleMedia.setTitle(title);
			circleMedia.setTargetUrl(url);
			circleMedia.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(circleMedia);
			performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			break;
		
		case R.id.weibo_txt:
			SinaShareContent sianContent = new SinaShareContent();
			sianContent.setShareContent(getShareContent() + "      "+ url);
			sianContent.setTitle(title);
			sianContent.setTargetUrl(url);
			sianContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(sianContent);
			performShare(SHARE_MEDIA.SINA);
			break;
		
		case R.id.qqzone_txt:
			QZoneShareContent qzone = new QZoneShareContent();
			//设置分享文字
			qzone.setShareContent(getShareContent());
			//设置点击消息的跳转URL
			qzone.setTargetUrl(url);
			//设置分享内容的标题
			qzone.setTitle(title);
			//设置分享图片
			qzone.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(qzone);
			performShare(SHARE_MEDIA.QZONE);
			break;
			
		case R.id.tencet_txt:
			TencentWbShareContent tencentContent = new TencentWbShareContent();
			tencentContent.setShareContent(getShareContent());
			tencentContent.setTitle(title);
			tencentContent.setTargetUrl(url);
			tencentContent.setShareImage(new UMImage(mContext, R.mipmap.ic_launcher));
			mController.setShareMedia(tencentContent);
			performShare(SHARE_MEDIA.TENCENT);
			break;
			
		case R.id.copy_txt:
			ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			cmb.setText(url);
			Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
			break;

        case R.id.invitation_code_txt:
            startInvitationCodeTask();
            break;

		}
	}
	
    private String getShareContent(){
        if(Preference.getInstance().readIsLogin() && !StringUtil.isEmpty(modelApp.getUser().getNuid())){
            return "注册请填写邀请码：" + modelApp.getUser().getNuid();
        }else{
            return content;
        }
    }

    private void startInvitationCodeTask(){
        if (!Preference.getInstance().readIsLogin()){
            Intent intent = new Intent(AppShareActivity.this, LoginActivity.class);
            intent.putExtra("classStr", AppShareActivity.class);
            startActivity(intent);
            return;
        }
        if (NetUtil.detectAvailable(mContext)){
            InvitationCodeRequest request = new InvitationCodeRequest(modelApp.getUser().getUid(), new RequestListener() {

                @Override
                public void sendMessage(Message message) {
                    hideLoadingDialog();
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            String msg = (String) message.obj;
                            if(!StringUtil.isEmpty(msg)) Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            String nuid = (String) message.obj;
                            if(StringUtil.isEmpty(nuid)){
                                Toast.makeText(mContext, "获取邀请码失败", Toast.LENGTH_SHORT).show();
                            }else{
                                modelApp.getUser().setNuid(nuid);
                                new UserService().insertUser(modelApp.getUser());
                                showCopyCodeDialog(nuid);
                            }

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
	
	@Override
	protected void release() {
		// TODO Auto-generated method stub

	}

	private void performShare(SHARE_MEDIA platform) {
		mController.postShare(mContext, platform, new SnsPostListener() {
			@Override
			public void onStart() {
//				Toast.makeText(mContext, "开始分享.", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				if (eCode == 200) {
//					Toast.makeText(mContext, "分享成功.", Toast.LENGTH_SHORT)
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
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}


    //复制邀请码
    public void showCopyCodeDialog(String code) {

        final InvitationConfirmDialog confirmDialog = new InvitationConfirmDialog(this, code, "复制", "关闭");
        confirmDialog.show();
        confirmDialog.setClicklistener(new InvitationConfirmDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cmb.setText(modelApp.getUser().getNuid());
                Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
            }
        });
    }


}
