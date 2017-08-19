/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  Config.java   
 * @Package com.cdel.frame.activity 
 * @Author: nieshuting     
 * @Date:   2013-12-13 下午5:14:44   
 * @Version V1.0     
 */
package com.xkhouse.frame.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

import com.xkhouse.frame.log.Logger;

/**
 * 配置类，用于从assets中读取配置文件，是单例，可全局调用
 * 
 * @Author nieshuting
 * @ClassName BaseConfig
 * @Date 2013-12-13下午5:14:44
 */
public class BaseConfig {

	protected static Properties property;

	protected static BaseConfig instance;

	protected String mFileName;

	protected Context mContext;

	protected static String TAG = "BaseConfig";

	public BaseConfig() {
	}

	/**
	 * 生成单例；
	 * 
	 * @return 返回单例对象
	 */
	public static BaseConfig getInstance() {
		if (instance == null) {
			instance = new BaseConfig();
		}
		return instance;
	}

	/**
	 * 
	 * Description:获取配置信息
	 */
	public Properties getConfig() {
		if (property == null && mContext != null) {
			InputStream is = null;
			try {
				is = mContext.getAssets().open(mFileName);
				property = new Properties();
				property.load(is);
				Logger.i(TAG, "读取配置文件成功");
			} catch (IOException e) {
				Logger.e(TAG, "读取配置文件失败" + e.toString());
				e.printStackTrace();
			}
		}
		return property;
	}

	/** 初始化 */
	public void init(Context context, String fileName) {
		this.mContext = context;
		this.mFileName = fileName;
	}

}
