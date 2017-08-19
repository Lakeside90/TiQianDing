/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  DeviceInfo.java   
 * @Package com.cdel.frame.tool 
 * @Author nieshuting     
 * @Date   2014-5-30 下午5:12:57   
 * @Version V1.0     
 */
package com.xkhouse.frame.tool;

import android.content.Context;

import com.xkhouse.lib.utils.KeyUtil;
import com.xkhouse.lib.utils.PhoneUtil;

/**
 * 
 * @Author nieshuting
 * @ClassName DeviceInfo
 * @Date 2014-5-30下午5:12:57
 */
public class DeviceInfo {

	// 设备型号
	public String brand = "";

	// 系统版本
	public String version = "";

	// 手机号
	public String phoneNumber = "";

	// CPU型号
	public String cpu = "";

	// 屏幕分辨率
	public String resolution = "";

	// 运营商
	public String operator = "";

	// 网络
	public String network = "";

	// android_id
	public String androidid = "";

	/**
	 * 打印设备信息
	 * 
	 * @param @param context
	 * @param @return
	 * @return String
	 */
	public static String print(Context context) {
		DeviceInfo device = getInfo(context);
		String info = "设备信息 brand:" + device.brand + " ";// 牌子
		info += "version:" + device.version + " ";// 系统版本；
		info += "phoneNumber:" + device.phoneNumber + " ";// 手机号；
		info += "cpu:" + device.cpu + " ";// cpu
		info += "resolution:" + device.resolution + " ";// 分辨率
		info += "operator:" + device.operator + " ";// 运营商
		info += "network:" + device.network + " ";// 网络
		info += "androidid:" + device.androidid;// androidid
		return info;
	}

	/**
	 * 
	 * 获取设备信息
	 * 
	 * @param @param context
	 * @param @return
	 * @return String
	 */
	public static DeviceInfo getInfo(Context context) {
		DeviceInfo info = new DeviceInfo();
		info.brand = PhoneUtil.getBrandModel(context);// 品牌和型号；
		info.version = PhoneUtil.getPhoneVersion(context);// 系统版本；
		info.phoneNumber = PhoneUtil.getPhoneNumber(context);// 手机号
		info.resolution = PhoneUtil.getResolution(context);// 分辨率
		info.network = PhoneUtil.getNetWork(context);// 网络
		info.operator = PhoneUtil.getOperator(context);// 运营商
		info.androidid = KeyUtil.getKeyAndroidId(context);// android_id
		info.cpu = PhoneUtil.getCpuName();// cpu
		return info;
	}

}
