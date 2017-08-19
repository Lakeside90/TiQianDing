/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  AppUtil.java   
 * @Package com.cdel.chinaacc.phone.main.util 
 * @Author: nieshuting     
 * @Date:   2012-12-4 下午2:12:01   
 * @Version V1.0     
 */
package com.xkhouse.frame.app;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;


/**
 * 应用程序全局工具类
 * 
 * @Author nieshuting
 * @ClassName AppUtil
 * @Date 2012-12-4下午2:12:01
 */
public class AppUtil {

	private static final String TAG = "AppUtil";

	/**
	 * 关闭整个应用程序
	 * 
	 * @param: @param activity
	 * @return: void
	 */
	public static void closeApp(Context context) {
//		BaseApplication application = (BaseApplication) context
//				.getApplicationContext();
//		application.getActivityManager().popAllActivity();
//		context.stopService(new Intent(context, AppService.class));
//		DBHelper.getInstance().closeDatabase();
		Logger.i(TAG, "已关闭所有activity");
		// Process.killProcess(Process.myPid());
	}

	/**
	 * 获取签名
	 * 
	 * @param: context dd
	 * @return: String
	 */
	public static String getPublicKey(Context context) {
		if (context == null) {
			return "";
		}
		try {
			String packageName = context.getPackageName();
			// 得到安装包名称
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(sign
							.toByteArray()));
			// 统一格式
			String publickey = cert.getPublicKey().toString()
					.replace("\n", ",").replace(" ", "");
			publickey = publickey.substring(publickey.indexOf("modulus") + 8,
					publickey.indexOf(",public"));
			Logger.i("sign:", publickey);
			return publickey;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e("sign:", e.toString());
		}
		return "";
	}

	/**
	 * 判断当前act是否为顶层，需要添加android.permission.GET_TASKS权限
	 * 
	 * @param: activity
	 * @return: boolean
	 */
	public static boolean isTopActivity(Activity activity) {
		if (activity != null) {
			String packageName = getPackageInfo(activity).packageName;
			ActivityManager activityManager = (ActivityManager) activity
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = activityManager
					.getRunningTasks(1);
			if (tasksInfo.size() > 0) {
				System.out.println("---------------包名-----------"
						+ tasksInfo.get(0).topActivity.getPackageName());
				// 应用程序位于堆栈的顶层
				if (packageName.equals(tasksInfo.get(0).topActivity
						.getPackageName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 从apk获取应用信息
	 * 
	 * @param: @param context
	 * @param: @param path
	 * @param: @return
	 * @return: ApplicationInfo
	 */
	public static ApplicationInfo getApplicationInfo(Context context,
			String path) {
		if (context == null || !StringUtil.isNotNull(path)) {
			return null;
		}
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			return info.applicationInfo;
		}
		return null;
	}

	/**
	 * 获取包信息
	 * 
	 * @param: context
	 * @param: @return
	 * @return: PackageInfo
	 */
	public static PackageInfo getPackageInfo(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过Service的类名来判断是否启动某个服务
	 * 
	 * @param className
	 * @param context
	 * @return
	 */
	public static boolean serviceIsStart(String className, Context context) {
		return serviceIsStart(className, context, 30);
	}
	public static boolean serviceIsStart(String className, Context context,
			int size) {

		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(size);
		for (int i = 0; i < mServiceList.size(); i++) {
			if (className.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
