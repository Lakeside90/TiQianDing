/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-28上午10:48:00
 *@所有人 CDEL
 *@文件名 MyPreference.java
 *@包名 org.cdel.chinaacc.phone.sharedPreferences
 */

package com.xkhouse.frame.db;

import java.util.Properties;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.lib.utils.StringUtil;

/**
 * AppPreference类用于存储用户偏好；
 * 
 * @author nieshuting
 * @version 0.2
 */
public class AppPreference {

	/**
	 * 静态的Properties
	 */
	public static SharedPreferences mSP;

	private static Context context;

	private static String appname;

	private static AppPreference INSTANCE;

	public static final String IP = "ip";

	public static final String UPDATE_LEVEL = "updateLevel";

	/**
	 * 统计中记录应用程序启动时间；
	 */
	private static final String APP_START_TIME = "app_start_time";

	/**
	 * 讲义背景颜色
	 */
	public static final String BACKGROUND_COLOR = "background_color";

	/**
	 * 记录应用程序启动次数；
	 */
	private static final String APP_RUN = "app_run";

	/**
	 * 上一次登录的UID;
	 */
	private static final String LAST_UID = "uid";

	/**
	 * 应用程序内部版本号；
	 */
	private static final String APP_VER_CODE = "app_ver_code";

	public AppPreference() {

	}

	/**
	 * 初始化；
	 * 
	 * @param _context
	 */
	public static void init(Context _context) {
		context = _context;
		Properties property = BaseConfig.getInstance().getConfig();
		if (property != null) {
			appname = property.getProperty("appname");
			if (StringUtil.isNotNull(appname)) {
				mSP = context.getSharedPreferences(appname,
						Context.MODE_PRIVATE);
			}
		}
	}

	/**
	 * 生成单例；
	 * 
	 * @return 返回单例对象
	 */
	public static AppPreference getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AppPreference();
		}
		return INSTANCE;
	}

	/**
	 * 读kv
	 * 
	 * @param @param name
	 * @param @return
	 * @return int
	 */
	public String read(String name) {
		return mSP.getString(name, "");
	}

	/**
	 * 写kv
	 * 
	 * @param @param name
	 * @param @param value
	 * @return void
	 */
	public void write(String name, String value) {
		Editor editor = mSP.edit();
		editor.putString(name, value);
		editor.commit();
	}

	/**
	 * 读取更新联盟号的标识
	 * 
	 * @return boolean
	 */
	public boolean readIsUpdateLevel() {
		return mSP.getBoolean(UPDATE_LEVEL, true);
	}

	/**
	 * 写更新联盟号
	 * 
	 * @param value
	 * @return void
	 */
	public void writeIsUpdateLevel(boolean value) {
		Editor editor = mSP.edit();
		editor.putBoolean(UPDATE_LEVEL, value);
		editor.commit();
	}

	/**
	 * 读取应用程序启动时间；
	 * 
	 * @return 默认为０
	 */
	public long readAppStartTime() {
		return mSP.getLong(APP_START_TIME, 0);
	}

	/**
	 * 写入应用启动时间
	 * 
	 * @param time
	 */
	public void writeAppStartTime(long time) {
		try {
			Editor editor = mSP.edit();
			editor.putLong(APP_START_TIME, time);
			editor.commit();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写入讲义背景颜色
	 * 
	 * @return
	 */
	public void setBackgroundColor(String color) {
		Editor editor = mSP.edit();
		editor.putString(BACKGROUND_COLOR, color);
		editor.commit();
	}

	/**
	 * 读取讲义背景颜色
	 * 
	 * @param version
	 */
	public String getBackgroundColor() {
		return mSP.getString(BACKGROUND_COLOR, "#ecedee");// 默认背景颜色
	}

	/**
	 * 读取应用程序启动信息；
	 * 
	 * @return 默认为""
	 */
	public String readAppRun() {
		return mSP.getString(APP_RUN, "");
	}

	/**
	 * 写入应用启动信息
	 * 
	 * @param time
	 */
	public void writeAppRun(String run) {
		if (StringUtil.isEmpty(run)) {
			return;
		}
		Editor editor = mSP.edit();
		String last = readAppRun();
		if (StringUtil.isEmpty(last)) {
			editor.putString(APP_RUN, run);
		} else {
			if (last.length() > 32768) {
				editor.putString(APP_RUN, "");
			} else {
				editor.putString(APP_RUN, last + "," + run);
			}
		}
		editor.commit();
	}

	/**
	 * 读取UID；
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

	/**
	 * 读取内部版本号
	 * 
	 * @return 默认为""
	 */
	public int readAppVerCode() {
		return mSP.getInt(APP_VER_CODE, 0);
	}

	/**
	 * 写入内部版本号
	 * 
	 * @param uid
	 */
	public void writeAppVerCode(int code) {
		Editor editor = mSP.edit();
		editor.putInt(APP_VER_CODE, code);
		editor.commit();
	}
}
