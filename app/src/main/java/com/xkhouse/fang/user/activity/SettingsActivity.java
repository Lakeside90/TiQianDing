package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.entity.AppUpgrade;
import com.xkhouse.fang.app.task.VersionCheckRequest;
import com.xkhouse.fang.widget.AppUpdateDialog;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.File;
import java.util.Properties;

/**
* @Description: 设置
* @author wujian  
* @date 2015-10-10 上午9:29:17
 */
public class SettingsActivity extends AppBaseActivity {
	
	private ImageView iv_head_left;
	private TextView tv_head_title;
	
	private ImageView switch_on_off;
	private LinearLayout share_lay;
	private LinearLayout feed_back_lay;
	private LinearLayout disclaimer_lay;
	private LinearLayout about_lay;
	private LinearLayout cache_clear_lay;
	private LinearLayout upgrade_lay;
	
	private TextView version_txt;
	private TextView cache_txt;

	private TextView logout_txt;
	private LinearLayout logout_lay;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		version_txt.setText(getVersion());
		 if(Preference.getInstance().readIsPushClose()){
			 switch_on_off.setImageResource(R.drawable.switch_off);
		 }else{
			 switch_on_off.setImageResource(R.drawable.switch_on);
		 }


        File imageCache = ImageLoader.getInstance().getDiskCache().getDirectory();
        if(imageCache != null){
            double size  = getDirSize(imageCache);
            String sizeStr =  String.format("%.2f", size);

            cache_txt.setText(sizeStr + "M");

        }else{
            cache_txt.setText("0.00M");
        }

	}

    @Override
    protected void onResume() {
        super.onResume();

        if(Preference.getInstance().readIsLogin()){
            logout_lay.setVisibility(View.VISIBLE);
        }else{
            logout_lay.setVisibility(View.GONE);
        }
    }

    @Override
	protected void setContentView() {
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void findViews() {
        initTitle();
		
		switch_on_off = (ImageView) findViewById(R.id.switch_on_off);
		share_lay = (LinearLayout) findViewById(R.id.share_lay);
		feed_back_lay = (LinearLayout) findViewById(R.id.feed_back_lay);
		disclaimer_lay = (LinearLayout) findViewById(R.id.disclaimer_lay);
		about_lay = (LinearLayout) findViewById(R.id.about_lay);
		cache_clear_lay = (LinearLayout) findViewById(R.id.cache_clear_lay);
		upgrade_lay = (LinearLayout) findViewById(R.id.upgrade_lay);
		logout_lay = (LinearLayout) findViewById(R.id.logout_lay);
		
		version_txt = (TextView) findViewById(R.id.version_txt);
        cache_txt = (TextView) findViewById(R.id.cache_txt);
		logout_txt = (TextView) findViewById(R.id.logout_txt);
	}

	private void initTitle() {
		iv_head_left = (ImageView) findViewById(R.id.iv_head_left);
		tv_head_title = (TextView) findViewById(R.id.tv_head_title);
		tv_head_title.setText("设置");
		iv_head_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}
	
	@Override
	protected void setListeners() {
		share_lay.setOnClickListener(this);
		feed_back_lay.setOnClickListener(this);
		disclaimer_lay.setOnClickListener(this);
		logout_txt.setOnClickListener(this);
		about_lay.setOnClickListener(this);
		cache_clear_lay.setOnClickListener(this);
		upgrade_lay.setOnClickListener(this);
		switch_on_off.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_on_off:
			if(Preference.getInstance().readIsPushClose()){
				switch_on_off.setImageResource(R.drawable.switch_on);
				
				PushManager.startWork(getApplicationContext(), 
						PushConstants.LOGIN_TYPE_API_KEY,
						"1wK0jleZjhG4fee33xXiNGtz");
				Preference.getInstance().writeIsPushClose(false);
			}else{
				switch_on_off.setImageResource(R.drawable.switch_off);
				PushManager.stopWork(mContext);
				Preference.getInstance().writeIsPushClose(true);
			}
			break;
			
		case R.id.share_lay:
			startActivity(new Intent(mContext, AppShareActivity.class));
			break;

		case R.id.logout_txt:
            logout();
			Preference.getInstance().writeIsLogin(false);
			finish();
			break;
			
		case R.id.feed_back_lay:
			startActivity(new Intent(mContext, FeedBackActivity.class));
			break;
			
		case R.id.disclaimer_lay:
			startActivity(new Intent(mContext, DisclaimerActivity.class));
			break;
			
		case R.id.about_lay:
			startActivity(new Intent(mContext, AboutUsActivity.class));
			break;
			
		case R.id.cache_clear_lay:
            Toast.makeText(mContext, "缓存清理中...", Toast.LENGTH_SHORT).show();
			ImageLoader.getInstance().clearDiskCache();
            String sdcardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            Properties properties = BaseConfig.getInstance().getConfig();
            String path = sdcardDir + File.separator + properties.getProperty("cachepath");
            FileUtil.delete(path);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "清除缓存成功！", Toast.LENGTH_SHORT).show();
                    File imageCache = ImageLoader.getInstance().getDiskCache().getDirectory();
                    if(imageCache != null){
                        double size  = getDirSize(imageCache);
                        String sizeStr =  String.format("%.2f", size);
                        cache_txt.setText(sizeStr + "M");
                    }else{
                        cache_txt.setText("0.00M");
                    }
                }
            }, 3000);
			break;
			
		case R.id.upgrade_lay:
           appUpdate();
            break;
		}
	}


    private void appUpdate(){
        if (NetUtil.detectAvailable(this)) {
            Toast.makeText(mContext,"正在检查版本...", Toast.LENGTH_SHORT).show();
            VersionCheckRequest request = new VersionCheckRequest(getVersionCode(), new RequestListener() {
                @Override
                public void sendMessage(Message message) {
                    switch (message.what) {
                        case Constants.ERROR_DATA_FROM_NET:
                            Toast.makeText(mContext, R.string.service_error, Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.NO_DATA_FROM_NET:
                            Toast.makeText(mContext,"已是最新版本", Toast.LENGTH_SHORT).show();
                            break;

                        case Constants.SUCCESS_DATA_FROM_NET:
                            final AppUpgrade appUpgrade = (AppUpgrade) message.obj;
                            if(appUpgrade == null || StringUtil.isEmpty(appUpgrade.getDownLoadUrl())) {
                                Toast.makeText(mContext,"已是最新版本", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (appUpgrade != null && !StringUtil.isEmpty(appUpgrade.getDownLoadUrl())){
                                final AppUpdateDialog confirmDialog = new AppUpdateDialog(SettingsActivity.this, appUpgrade.getContent());
                                confirmDialog.show();
                                confirmDialog.setClicklistener(new AppUpdateDialog.ClickListenerInterface() {
                                    @Override
                                    public void doConfirm() {
                                        confirmDialog.dismiss();

                                        Uri uri = Uri.parse(appUpgrade.getDownLoadUrl());
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void doCancel() {
                                        confirmDialog.dismiss();
                                    }
                                });
                            }
                            break;
                    }
                }
            });
            request.doRequest();
        }else {
            Toast.makeText(mContext, R.string.net_warn, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    private String getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = String.valueOf(info.versionCode);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }



    private double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            return 0.0;
        }
    }


	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	private String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return "V"+ version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "V1.0";
	    }
	}



    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	private void logout(){
        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, "1104857499",
                "ZNVEJATOlvNK33he");
        qqSsoHandler.addToSocialSDK();

        // 添加微信平台
        String appId = "wxa45adeb6ee24dfdb";
        String appSecret = "07e76cac282ff8fca887754f9e3917a6";
        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();

        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.deleteOauth(mContext, SHARE_MEDIA.SINA,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onComplete(int status, SocializeEntity entity) {}
                });
        mController.deleteOauth(mContext, SHARE_MEDIA.WEIXIN,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onComplete(int status, SocializeEntity entity) {}
                });
        mController.deleteOauth(mContext, SHARE_MEDIA.QQ,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onComplete(int status, SocializeEntity entity) {}
                });
    }
	
}
