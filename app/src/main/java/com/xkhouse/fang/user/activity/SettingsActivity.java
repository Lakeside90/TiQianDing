package com.xkhouse.fang.user.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.AppBaseActivity;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.lib.utils.FileUtil;

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

	private LinearLayout about_lay;

    private TextView cache_txt;
	private LinearLayout cache_clear_lay;
	
	private TextView version_txt;

    private TextView server_call_txt;
	
	
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

		about_lay = (LinearLayout) findViewById(R.id.about_lay);

		cache_clear_lay = (LinearLayout) findViewById(R.id.cache_clear_lay);
        cache_txt = (TextView) findViewById(R.id.cache_txt);

        version_txt = (TextView) findViewById(R.id.version_txt);

        server_call_txt = (TextView) findViewById(R.id.server_call_txt);
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

		about_lay.setOnClickListener(this);

		cache_clear_lay.setOnClickListener(this);

		switch_on_off.setOnClickListener(this);

        server_call_txt.setOnClickListener(this);
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

            case R.id.server_call_txt:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:400-887-1216")));
                break;
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
	        return "当前版本V"+ version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "当前版本V1.0";
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
