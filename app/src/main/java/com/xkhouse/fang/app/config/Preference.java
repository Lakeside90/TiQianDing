package com.xkhouse.fang.app.config;

import android.content.SharedPreferences.Editor;

import com.xkhouse.frame.db.AppPreference;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.StringUtil;

public class Preference extends AppPreference {

	protected static Preference INSTANCE;

	// 用户ID
	private static final String UID = "uid";

	// 用户名
	private static final String USER_NAME = "user_name";
	
	//密码
	private static final String PASSWORD = "password";
	

	/**
	 * 生成单例；
	 * 
	 * @return 返回单例对象
	 */
	public static Preference getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Preference();
		}
		return INSTANCE;
	}

	/**
	 * 用户ID
	 * 
	 * @return
	 */
	public void writeUID(String uid) {
		Editor editor = mSP.edit();
		editor.putString(UID, uid);
		editor.commit();
	}

	public String readUID() {
		return mSP.getString(UID, "");
	}

	public void delUID() {
		Editor editor = mSP.edit();
		editor.remove(UID);
		editor.commit();
	}


	/**
	 * 用户名
	 * @return
	 */
	public void writeUsername(String username) {
		Editor editor = mSP.edit();
		editor.putString(USER_NAME, username);
		editor.commit();
	}

	public String readUsername() {
		return mSP.getString(USER_NAME, "");
	}

	

	/**
	 * 密码
	 * @return
	 */
	public void writePassword(String password) {
		Editor editor = mSP.edit();
		editor.putString(PASSWORD, password);
		editor.commit();
	}

	public String readPassword() {
		String currentDate = DateUtil.getCurrentDate();
		return mSP.getString(PASSWORD, currentDate.substring(0, 7));
	}

	public void delPassword() {
		Editor editor = mSP.edit();
		editor.remove(PASSWORD);
		editor.commit();
	}


	// 是否第一次启动
	private static final String APP_FIRST_START = "app_first_start";

	/**
	 * 是否第一次进入app
	 */
	public boolean isAppFirstStart() {
		return mSP.getBoolean(APP_FIRST_START, true);
	}

	/**
	 * 设置是否第一次进入app
	 */
	public void writeAppFirstStart(boolean isFirst) {
		Editor editor = mSP.edit();
		editor.putBoolean(APP_FIRST_START, isFirst);
		editor.commit();
	}

	// 上一次登录的UID
	private static final String LAST_UID = "uid";

	/**
	 * 读取上次登录的uid；
	 * 
	 * @return 默认为""
	 */
	public String readLastUID() {
		return mSP.getString(LAST_UID, "");
	}

	/**
	 * 写入上次登录的uid
	 * 
	 * @param uid
	 */
	public void writeLastUID(String uid) {
		Editor editor = mSP.edit();
		editor.putString(LAST_UID, uid);
		editor.commit();
	}

	// 用户头像
	private static final String IMAGE_URL = "image_url";

	public void writeImageUrl(String uid, String url) {
		if (StringUtil.isNotNull(url) && !"null".equals(url)) {
			Editor editor = mSP.edit();
			editor.putString(uid.trim() + IMAGE_URL, url);
			editor.commit();
		}
	}

	public String readImageUrl(String uid) {
		String url = mSP.getString(uid.trim() + IMAGE_URL, "");
		return "null".equals(url) ? "" : url;
	}

	// 是否登录
	private static final String IS_LOGIN = "is_login";

	/**
	 * 
	 * @Title: readIsLogin
	 * @Description: 读取登录状态
	 * @param: @return
	 * @return: boolean
	 * @throws
	 */
	public boolean readIsLogin() {
		return mSP.getBoolean(IS_LOGIN, false);
	}

	/**
	 * 
	 * @Title: writeIsLogin
	 * @Description: 写入登录状态
	 * @param: @param islogin
	 * @return: void
	 * @throws
	 */
	public void writeIsLogin(boolean islogin) {
		Editor editor = mSP.edit();
		editor.putBoolean(IS_LOGIN, islogin);
		editor.commit();
	}
	
	//是否记住密码
	private static final String IS_REMEMBER_PSW = "is_remember_psw";
	
	public boolean readIsRememberPsw() {
		return mSP.getBoolean(IS_REMEMBER_PSW, false);
	}
	
	public void writeIsRememberPsw(boolean isRemember) {
		Editor editor = mSP.edit();
		editor.putBoolean(IS_REMEMBER_PSW, isRemember);
		editor.commit();
	}
	
	//是否记关闭消息推送
	private static final String IS_PUSH_CLOSE = "is_push_close";
	
	public boolean readIsPushClose() {
		return mSP.getBoolean(IS_PUSH_CLOSE, false);
	}
	
	public void writeIsPushClose(boolean isClose) {
		Editor editor = mSP.edit();
		editor.putBoolean(IS_PUSH_CLOSE, isClose);
		editor.commit();
	}

	// 站点城市
	private static final String CITY = "city";

	public String readCity() {
		return mSP.getString(CITY, "");
	}

	public void writeCity(String city) {
		Editor editor = mSP.edit();
		editor.putString(CITY, city);
		editor.commit();
	}
	
	
	// 站点城市
	private static final String AD_SPLASH = "ad_splash";

	public String readADSplash() {
		return mSP.getString(AD_SPLASH, "");
	}

	public void writeADSplash(String adUrl) {
		Editor editor = mSP.edit();
		editor.putString(AD_SPLASH, adUrl);
		editor.commit();
	}
	
	
	
	private static final String BD_USER_ID = "baidu_user_id";
	/**
	 * 百度消息推送   推送用户id
	 * @return
	 */
	public void writeBDUserId(String userid) {
		Editor editor = mSP.edit();
		editor.putString(BD_USER_ID, userid);
		editor.commit();
	}

	public String readBDUserId() {
		return mSP.getString(BD_USER_ID, "");
	}
	
	private static final String BD_CHANNEL_ID = "baidu_channel_id";
	/**
	 * 百度消息推送 推送设备id
	 * @return
	 */
	public void writeBDChannelId(String channelId) {
		Editor editor = mSP.edit();
		editor.putString(BD_CHANNEL_ID, channelId);
		editor.commit();
	}

    /**
     * 当前版本号
     */
    private static final String VERSION_CODE = "version_code";
    public String readVersionCode(){
        return mSP.getString(VERSION_CODE, "0");
    }
    public void writeVersionCode(String versionCode){
        Editor editor = mSP.edit();
        editor.putString(VERSION_CODE, versionCode);
        editor.commit();
    }


	public String readBDChannelId() {
		return mSP.getString(BD_CHANNEL_ID, "");
	}
}
