package com.xkhouse.fang.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Message;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xkhouse.fang.R;
import com.xkhouse.fang.app.callback.RequestListener;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.service.HouseConfigDbService;
import com.xkhouse.fang.app.service.SiteDbService;
import com.xkhouse.fang.app.service.UpdateService;
import com.xkhouse.fang.app.task.SplashADListRequest;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.frame.activity.BaseSplashActivity;
import com.xkhouse.lib.utils.NetUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 
* @Description: 启动页
* @author wujian  
* @date 2015-8-25 下午2:32:36
 */
public class SplashActivity extends BaseSplashActivity{
	
	
	public ModelApplication modelApp;
	private UserService userService;
	
	private ImageView ad_iv;
	
	DisplayImageOptions options;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_splash);
	}
	
	@Override
	protected void init() {
		modelApp = (ModelApplication)getApplication();
		
		options = new DisplayImageOptions.Builder()
	       .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
	       .cacheOnDisk(true).build();
		
		super.init();
		updateDB(new UpdateService(mContext));
	}

	@Override
	protected void findViews() {
		ad_iv = (ImageView) findViewById(R.id.ad_iv);
		loadAD();
	}

	@Override
	protected void setListeners() {}

	@Override
	protected void launchCompleteDoNext() {
		
		String user_id = Preference.getInstance().readUID();
		//判断登录状态
		if(Preference.getInstance().readIsLogin()){
			userService = new UserService();
			modelApp.setUser(userService.queryUser(user_id));
            if (modelApp.getUser() == null) {
                Preference.getInstance().writeIsLogin(false);
            }
		}
		
		//判断是否是首次启动
		Intent intent = null;
        if (Preference.getInstance().isAppFirstStart()){

            // 首次启动
            Preference.getInstance().writeCity("");
            Preference.getInstance().writeIsLogin(false);
            new SiteDbService().clearSites();
            new HouseConfigDbService().clearPriceList(); //4.1.5清除房价筛选条件
            intent = new Intent(SplashActivity.this, GuideActivity.class);
            Preference.getInstance().writeAppFirstStart(false);
            Preference.getInstance().writeVersionCode(getVersionCode());

        }else if(!getVersionCode().equals(Preference.getInstance().readVersionCode())) {

            // 首次启动
            new HouseConfigDbService().clearPriceList(); //4.1.5清除房价筛选条件
            Preference.getInstance().writeAppFirstStart(false);
            Preference.getInstance().writeVersionCode(getVersionCode());
            if(modelApp.getSite() == null){
                intent = new Intent(SplashActivity.this, CitySelectActivity.class);
            }else{
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

        } else {
        	if(modelApp.getSite() == null){
    			intent = new Intent(SplashActivity.this, CitySelectActivity.class);
    		}else{
    			intent = new Intent(SplashActivity.this, MainActivity.class);
    		}
        }
		
        
		startActivity(intent);
		this.finish();
	}
	

	@Override
	protected void loadAD() {
		super.loadAD();
		//广告
		if(NetUtil.detectAvailable(mContext)){
			String siteId = "";
			if(modelApp.getSite() != null){
				siteId = modelApp.getSite().getSiteId();
			}
			SplashADListRequest adListRequest = new SplashADListRequest(siteId, "2414", adListListener);
			adListRequest.doRequest();
		}else{
			ImageLoader.getInstance().displayImage(Preference.getInstance().readADSplash(), ad_iv, options);
		}
	}
	

	private RequestListener adListListener = new RequestListener() {
		
		@Override
		public void sendMessage(Message message) {
			switch (message.what) {
			case Constants.ERROR_DATA_FROM_NET: 
			case Constants.NO_DATA_FROM_NET:
                ImageLoader.getInstance().displayImage(Preference.getInstance().readADSplash(), ad_iv, options);
				break;
				
			case Constants.SUCCESS_DATA_FROM_NET:
				if(!isFinishing()){
					String url = (String) message.obj;
					if(!StringUtil.isEmpty(url)){
						ImageLoader.getInstance().displayImage(url, ad_iv, options);
						Preference.getInstance().writeADSplash(url);
					}
				}
				break;
			}
		}
	};

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
	
}
