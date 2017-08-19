/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-25下午05:02:54
 *@所有人 CDEL
 *@文件名 NetworkUtil.java
 *@包名 org.cdel.chinaacc.phone.util
 */

package com.xkhouse.lib.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.telephony.TelephonyManager;

/**
 * NetworkUtil类用于检测手机的网络类型，如2g\3g\wifi;
 * 
 * @author nieshuting
 * @version 0.1
 */
public class NetUtil {

	/**
	 * 常量，未知网络
	 */
	public static final int NETWORK_TYPE_UNKNOWN = 1;

	/**
	 * 常量，2G网络
	 */
	public static final int NETWORK_TYPE_2G = 2;

	/**
	 * 常量，3G网络
	 */
	public static final int NETWORK_TYPE_3G = 3;

	/**
	 * 检测网络是否可用;
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean detectAvailable(Context context) {
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 启动设置网络页面，不支持Android 3.0系统；
	 * 
	 * @param context
	 */
	public static void startNetwork(Context context) {
		if (context != null) {
			Intent mIntent = new Intent("/");
			ComponentName comp = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			mIntent.setComponent(comp);
			mIntent.setAction("android.intent.action.VIEW");
			context.startActivity(mIntent);
		}
	}

	/**
	 * 获取wifi的mac地址，有wifi的设备才能取到，另外，mac地址可能为null；
	 * 
	 * @param context
	 * @return MacAddress
	 */
	public static String getMac(Context context) {
		if (context != null) {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (wifi != null) {
				WifiInfo info = wifi.getConnectionInfo();
				if (info != null) {
					return info.getMacAddress();
				}
			}
		}
		return null;
	}

	/**
	 * 判断是否为cmwap，移动的为cmwap，联通的为3gwap、uniwap；
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean detectWap(Context context) {
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					if ((info.getTypeName().toLowerCase().indexOf("mobile") != -1)
							&& info.getExtraInfo() != null) {
						// 移动网络；
						if ((info.getExtraInfo().toLowerCase().indexOf("wap") != -1)) {
							// 包含wap字符；
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否为WIFI；
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean detectWifi(Context context) {
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null
						&& info.getType() == ConnectivityManager.TYPE_WIFI) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取wifi名称
	 * 
	 * @param @param context
	 * @param @return
	 * @return String
	 */
	public static String getWifiName(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}

	/**
	 * 打开或关闭WIFI;
	 * 
	 * @param context
	 */
	public static boolean openWifi(Context context) {
		if (context != null) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			return wifiManager.setWifiEnabled(true);
		}
		return false;
	}

	/**
	 * 关闭WIFI;
	 * 
	 * @param context
	 */
	public static boolean closeWifi(Context context) {
		if (context != null) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			return wifiManager.setWifiEnabled(false);
		}
		return false;
	}

	/**
	 * 检测是否为2G，平板电脑可能没有电话service;
	 * 联通2GNETWORK_TYPE_GPRS，移动2GNETWORK_TYPE_EDGE，电信2GNETWORK_TYPE_CDMA
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean detect2G(Context context) {
		if (context != null) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 获得手机SIMType
			if (tm != null) {
				int type = tm.getNetworkType();
				if (type == TelephonyManager.NETWORK_TYPE_GPRS
						|| type == TelephonyManager.NETWORK_TYPE_EDGE
						|| type == TelephonyManager.NETWORK_TYPE_CDMA) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检测是否为3G；平板电脑可能没有电话service; 联通3GNETWORK_TYPE_UMTS，移动3GNETWORK_TYPE_HSDPA，
	 * 电信3GNETWORK_TYPE_EVDO_0NETWORK_TYPE_EVDO_A
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean detect3G(Context context) {
		if (context != null) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 获得手机SIMType
			if (tm != null) {
				int type = tm.getNetworkType();
				if (type == TelephonyManager.NETWORK_TYPE_UMTS
						|| type == TelephonyManager.NETWORK_TYPE_HSDPA
						|| type == TelephonyManager.NETWORK_TYPE_EVDO_0
						|| type == TelephonyManager.NETWORK_TYPE_EVDO_A
						|| type == TelephonyManager.NETWORK_TYPE_LTE)
					return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否为4G
	 * 
	 * @param @param context
	 * @param @return
	 * @return boolean
	 */
	public static boolean detect4G(Context context) {
		if (context != null) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			// 获得手机SIMType
			if (tm != null) {
				int type = tm.getNetworkType();
				if (type == TelephonyManager.NETWORK_TYPE_UMTS
						|| type == TelephonyManager.NETWORK_TYPE_LTE)
					return true;
			}
		}
		return false;
	}

	/**
	 * 检查信号强弱
	 */
	public static int checkWifiRssi(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		wifiInfo = wifiManager.getConnectionInfo();
		// 获得信号强度值
		// 0到-50表示信号最好，-50到-70表示信号偏差，小于-70表示最差，有可能连接不上或者掉线，一般Wifi已断则值为-200
		int level = wifiInfo.getRssi();
		// 根据获得的信号强度发送信息
		return level;
	}
}
