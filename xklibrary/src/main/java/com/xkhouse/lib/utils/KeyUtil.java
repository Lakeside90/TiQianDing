/*
 *
 *@作者 nieshuting
 *@创建日期 2011-7-21下午05:47:37
 *@所有人 CDEL
 *@文件名 KeyGenerator.java
 *@包名 com.cdel.chinaacc.phone.help
 */

package com.xkhouse.lib.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * 密钥生成器，推荐使用android_id，但缺点是不唯一。
 * 
 * @author nieshuting
 * @version 0.1
 */
public class KeyUtil {

	public static final String DEFAULT_KEY = "008759598666173";

	/**
	 * 假设我们确实需要用到真实设备的标识，可能就需要用到DEVICE_ID。在以前，我们的Android设备是手机，
	 * 这个DEVICE_ID可以同通过TelephonyManager
	 * .getDeviceId()获取，它根据不同的手机设备返回IMEI，MEID或者ESN码，但它在使用的过程中会遇到很多问题： 非手机设备：
	 * 如果只带有Wifi的设备或者音乐播放器没有通话的硬件功能的话就没有这个DEVICE_ID 权限：
	 * 获取DEVICE_ID需要READ_PHONE_STATE权限，但如果我们只为了获取它，没有用到其他的通话功能，那这个权限有点大才小用
	 * bug：在少数的一些手机设备上，该实现有漏洞，会返回垃圾，如:zeros或者asterisks的产品
	 * 
	 * @Deprecated Implement {@link #getKeyAndroidId()}
	 * @param context
	 * @return 设备id
	 */

	public static String getKeyDeviceId(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String key = tm.getDeviceId();
			if (StringUtil.isEmpty(key)) {
				key = DEFAULT_KEY;
			}
			return key;
		} catch (Exception e) {
			return DEFAULT_KEY;
		}
	}

	/**
	 * ANDROID_ID是设备第一次启动时产生和存储的64bit的一个数，当设备被wipe后该数重置 ANDROID_ID似乎是获取Device
	 * ID的一个好选择，但它也有缺陷： 它在Android <=2.1 or Android
	 * >=2.3的版本是可靠、稳定的，但在2.2的版本并不是100%可靠的
	 * 在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
	 * 
	 * @param context
	 * @return android_id
	 */
	public static String getKeyAndroidId(Context context) {
		String key = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
		if (StringUtil.isEmpty(key)) {
			key = DEFAULT_KEY;
		}
		return key;
	}

}
