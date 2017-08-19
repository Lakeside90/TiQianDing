/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  AppInfo.java   
 * @Package com.cdel.frame.tool 
 * @Author nieshuting     
 * @Date   2014-5-30 下午5:02:58   
 * @Version V1.0     
 */
package com.xkhouse.frame.tool;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.xkhouse.frame.app.AppUtil;
import com.xkhouse.lib.utils.PhoneUtil;

/**
 * // 应用名称 // 应用包名 // 安装时间 // appkey // 内部版本号 // 外部版本号 // 数据库路径 // 缓存目录 // 缓存大小
 * // 服务service // 使用流量 // 未下载完课件 // 已下载完课件 // 最近登录用户 // 下载路径 // 导入路径 // 存储目录
 * 
 * @Author nieshuting
 * @ClassName AppInfo
 * @Date 2014-5-30下午5:02:58
 */
public class AppInfo {

	/** 应用名称 */
	public String appName = "";

	/** 应用包名 */
	public String packageName = "";

	/** 外部版本号 */
	public String versionName = "";

	/** 内部版本号 */
	public int versionCode = 0;

	/** 应用图标 */
	public Drawable appIcon = null;

	/** 首次安装时间 */
	public String firstInstallTime = "";

	/** appkey */
	public String appkey = "";

	/**
	 * 打印应用信息
	 * 
	 * @param @param context
	 * @param @return
	 * @return String
	 */
	public static String print(Context context) {
		AppInfo app = getAppInfo(context);
		String info = "应用信息 appName:" + app.appName + " ";
		info += "packageName:" + app.packageName + " ";// 包名；
		info += "versionName:" + app.versionName + " ";// 外部版本号；
		info += "versionCode:" + app.versionCode + " ";// 内部版本号
		info += "firstInstallTime:" + app.firstInstallTime + " ";// 首次安装时间
		info += "appkey:" + app.appkey;// appkey
		return info;
	}

	/**
	 * 获取应用对象数据
	 * 
	 * @param @param context
	 * @param @return
	 * @return AppInfo
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static AppInfo getAppInfo(Context context) {
		PackageInfo pi = AppUtil.getPackageInfo(context);
		AppInfo info = new AppInfo();
		info.appName = pi.applicationInfo
				.loadLabel(context.getPackageManager()).toString();
		info.packageName = pi.packageName;
		info.versionCode = pi.versionCode;
		info.versionName = pi.versionName;
		//info.firstInstallTime = DateUtil
				//.getString(new Date(pi.firstInstallTime));
//		info.appkey = PhoneUtil.getAppKey(context);
		info.appIcon = pi.applicationInfo.loadIcon(context.getPackageManager());
		return info;
	}
}
