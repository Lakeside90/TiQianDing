/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  BaseApplication.java   
 * @Package com.cdel.frame.activity 
 * @Author: nieshuting     
 * @Date:   2013-12-13 下午12:58:00   
 * @Version V1.0     
 */
package com.xkhouse.frame.activity;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.xkhouse.frame.activity.ActivityManager;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xkhouse.BuildConfig;
import com.xkhouse.frame.cache.ApiCache;
import com.xkhouse.frame.config.DoaminConstants;
import com.xkhouse.frame.db.AppPreference;
import com.xkhouse.frame.db.DBHelper;
import com.xkhouse.frame.log.FileLogger;
import com.xkhouse.frame.log.LogCatLogger;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.frame.tool.AppInfo;
import com.xkhouse.frame.tool.Crash;
import com.xkhouse.frame.tool.DeviceInfo;

/**
 * 继承自Application，封装了常见方法如创建文件、初始化数据库、初始化配置信息等；
 * 
 * @Author nieshuting
 * @ClassName BaseApplication
 * @Date 2013-12-13下午12:58:00
 */
public abstract class BaseApplication extends Application {

	public static Context mContext;

	/**
	 * 
	 */
	private static ActivityManager activityManager = null;
	/**
	 * Volley全局请求队列
	 */
	private static RequestQueue mRequestQueue;

	/**
	 * Application单例
	 */
	private static BaseApplication sInstance;
	/**
	 * TAG
	 */
	public final String TAG = "BaseApplication";
	/**
	 * request TAG
	 */
	private static final String REQUEST_TAG = "VolleyPatterns";

	/**
	 * 域名
	 * 
	 */
	public static String domain = DoaminConstants.XKHOUSE;

	/**
	 * 
	 * 创建目录
	 * 
	 * @param
	 * @return void
	 */
	protected abstract void initDirs();

	/**
	 * @return 单例
	 */
	public static synchronized BaseApplication getInstance() {
		return sInstance;
	}

	/**
	 * <p>
	 * Title: onCreate
	 * </p>
	 * <p>
	 * Description:序列化了常用方法
	 * </p>
	 * 
	 * @see Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		mContext = this;
		initDomain();
		initSP();
		initLog();
		initDirs();
		initCrash();
		initCache();
		initDB();
		initActivity();
//		initJPush();
		Logger.i(TAG, "创建");
	}

	/**
	 * 初始化自定义Activity管理器
	 * 
	 * @param
	 * @return void
	 */
	protected void initActivity() {
		if (activityManager == null) {
			activityManager = ActivityManager.getScreenManager();
		}
	}

	/**
	 * 初始化数据库单例
	 * 
	 * @param
	 * @return void
	 */
	protected void initDB() {
		DBHelper.getInstance().init(mContext);
	}

	/**
	 * 
	 * @param
	 * @return void
	 */
	protected void initCache() {
		ApiCache.init(mContext);
	}

	/**
	 * 初始化ImageLoader类，用于全局缓存图片
	 * 
	 * @param
	 * @return void
	 */
	protected void initImage() {
		ImageLoader mImageLoader = ImageLoader.getInstance();
		Builder builder = new ImageLoaderConfiguration.Builder(mContext);
		builder.threadPriority(Thread.MIN_PRIORITY);// 设置线程的优先级
		builder.denyCacheImageMultipleSizesInMemory();
		builder.discCacheFileNameGenerator(new Md5FileNameGenerator());// 通过Md5将url生产文件的唯一名字
		builder.tasksProcessingOrder(QueueProcessingType.LIFO); // 设置图片下载和显示的工作队列排序
		builder.discCacheSize(100 * 1024 * 1024);// 设置缓存的大小
//		if (BuildConfig.DEBUG) {
//			builder.enableLogging();
//		}
		mImageLoader.init(builder.build());
		Logger.i(TAG, "初始化imageLoader");
	}

	/**
	 * 取得请求队列
	 * 
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mContext);
		}
		return mRequestQueue;
	}

	/**
	 * 添加请求
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? REQUEST_TAG : tag);
		Logger.i(TAG, "添加请求队列: %s", req.getUrl());
		getRequestQueue().add(req);
	}

	/**
	 * 添加请求，并使用默认标记
	 * 
	 * @param req
	 * @param tag
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	/**
	 * 用标记取消未完成的请求
	 * 
	 * @param tag
	 */
	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
			Logger.i(TAG, "取消请求队列: %s", String.valueOf(tag));
		}
	}

	public ActivityManager getActivityManager() {
		initActivity();
		return activityManager;
	}

	public void setActivityManager(ActivityManager _activityManager) {
		activityManager = _activityManager;
	}

	/**
	 * 初始化域名；
	 * 
	 * @param
	 * @return void
	 */
	protected abstract void initDomain();

	/**
	 * 初始化偏好单例
	 * 
	 * @param
	 * @return void
	 */
	protected void initSP() {
		AppPreference.init(mContext);
	}

	/**
	 * 捕获闪退异常，在release版中，将日志写入本地并提交服务器，用于异常追踪
	 * 
	 * @param:
	 * @return: void
	 */
	protected void initCrash() {
		Crash.getInstance().init(mContext);
	}

	/**
	 * 调试模式，将日志同时输出到SD卡和logcat中，否则，只输出logcat
	 * 
	 * @param:
	 * @return: void
	 */
	@SuppressWarnings("unused")
	protected void initLog() {
		if (BuildConfig.DEBUG || Logger.isDebug) {
			Logger.addLogger(new FileLogger(mContext));
			Logger.addLogger(new LogCatLogger());
			Logger.i(TAG, DeviceInfo.print(mContext));
			Logger.i(TAG, AppInfo.print(mContext));
		}
	}

//	/**
//	 * 极光推送
//	 * 
//	 * @param
//	 * @return void
//	 */
//	protected void initJPush() {
//		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
//		JPushInterface.init(mContext); // 初始化 JPush
//	}
}
