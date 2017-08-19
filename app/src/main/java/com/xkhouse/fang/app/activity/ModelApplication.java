package com.xkhouse.fang.app.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.tencent.bugly.crashreport.CrashReport;
import com.xkhouse.fang.app.config.Constants;
import com.xkhouse.fang.app.config.Preference;
import com.xkhouse.fang.app.entity.Site;
import com.xkhouse.fang.app.service.SiteDbService;
import com.xkhouse.fang.user.entity.User;
import com.xkhouse.fang.user.service.UserService;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.config.DoaminConstants;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.KeyUtil;
import com.xkhouse.lib.utils.SDCardUtil;
import com.xkhouse.lib.utils.StringUtil;

import java.io.File;
import java.util.Properties;


public class ModelApplication extends BaseApplication {
	
	/** 当前站点   */
	private Site site;
	
	/**  当前登录的用户 **/
	private User user;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initGlobal();
		initImage();
        //bugly 初始化
        CrashReport.initCrashReport(getApplicationContext(), "900025669", false);
    }
	
	@Override
	protected void initDirs() {

		if (SDCardUtil.detectAvailable()) {
			String sdcardDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			Properties properties = BaseConfig.getInstance().getConfig();
			createDir(sdcardDir, properties.getProperty("dbpath"));
			createDir(sdcardDir, properties.getProperty("downloadpath"));
			createDir(sdcardDir, properties.getProperty("imagepath"));
			createDir(sdcardDir, properties.getProperty("hidepath"));
			createDir(sdcardDir, properties.getProperty("cachepath"));
		} else {
			Logger.i(TAG, "没有SD卡!");
		}
	}

	private void createDir(String sdcardDir, String path) {
		if (FileUtil.createFolder(sdcardDir + File.separator + path)) {
			Logger.i(TAG, "成功创建SD卡目录" + path);
		}
	}

	@Override
	protected void initDomain() {
		BaseConfig.getInstance().init(mContext,
				DoaminConstants.XKHOUSE + ".properties");
	}

	/**
	 * 初始化
	 */
	private void initGlobal() {
		Constants.ANDROID_ID = KeyUtil.getKeyAndroidId(mContext);
	}

	
	
	
	
	
	
	
	
	public static Context getAppContext() {
	    return mContext;
	}
	
	public void setSite(Site site) {
		this.site = site;
		if(site != null){
			Preference.getInstance().writeCity(site.getArea());
		}
	}
	
	public Site getSite() {
        if(site == null){
            SharedPreferences mSP = getSharedPreferences("xkhousefang", Context.MODE_PRIVATE);
            String city = mSP.getString("city", "");
            site = new SiteDbService().getSiteByName(city);
        }
		return site;
	}
	
	
	public User getUser() {
        //系统内存不足的时候，user可能会被回收
        if(user == null){
            SharedPreferences mSP = getSharedPreferences("xkhousefang", Context.MODE_PRIVATE);
            String uid = mSP.getString("uid", "");
            user = new UserService().queryUser(uid);
        }
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	/**********************************  web页url地址 ***********************************/
	
	public String getWebHost(){
		String host = "";
		if(getSite() != null){
			host = getSite().getDomain();
		}
		if(StringUtil.isEmpty(host)){
			host = "http://m.hfhouse.com";
		}
		return host;
	}
	
	/**  资讯首页（头条）  **/
	public String getNewsIndex(){
		 return getWebHost() + "/news/";
	}

    /**  资讯首页（图集）  **/
    public String getNewsPhoto(){
        return getWebHost() + "/news/photo/";
    }

    /**  资讯首页（视频）  **/
    public String getNewsVideo(){
        return getWebHost() + "/news/video/";
    }

	/**  资讯导购  **/
	public String getNewsDG(){
		 return getWebHost() + "/news/daogou/";
	}
	
	/**  资讯行情  **/
	public String getNewsHQ(){
		return getWebHost() + "/news/hangqing/";
	}


    /**  学区房详情  **/
    public String getSchoolHouseDetail(){
        return getWebHost() + "/newhouse/xuequ/index?id=";
    }

    /**  新房详情  **/
	public String getNewHouseDetail(){
		return getWebHost() + "/newhouse/";
	} 
	
	/**  二手房详情  **/
	public String getOldHouseDetail(){
		return getWebHost() + "/oldhouse/";
	} 
	
	/**  租房详情  **/
	public String getRentHouseDetail(){
		return getWebHost() + "/zufang/";
	} 
	
	/**  网友定制详情   **/
	public String getCustomHouseDetail(){
		return getWebHost()+ "/dingzhi/";
	} 
	
	/**  活动   **/
	public String getHuoDong(){
		return getWebHost()+ "/huodong/";
	} 
	
	
	/**  家居--图集   **/
	public String getJJIndex(){
		 return getWebHost()+ "/home/index.html";
	}
	
	/**  家居--公益   **/
	public String getJJGongYi(){
		return getWebHost()+ "/home/gongyi.html";
	}
	
	/**  家居--促销   **/
	public String getJJCuXiao(){
		return getWebHost()+ "/home/cuxiao.html";
	}
	
	/**  家居--新闻   **/
	public String getJJNews(){
		return getWebHost()+ "/home/news.html";
	} 
	   
	/**  星空贷首页   **/
	public String getXKLoan(){
		return getWebHost()+ "/loan/";
	} 
	
	/**  计算器   **/
	public String getCalculator(){
		return getWebHost() + "/member/tools/counter.html";
	}

    /**  看房团  **/
    public String getKanFang(){
        return getWebHost() + "/kanfang/";
    }
}
