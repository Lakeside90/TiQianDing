/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  ApiCache.java   
 * @Package com.cdel.frame.cache 
 * @Author nieshuting     
 * @Date   2014-1-15 上午8:37:38   
 * @Version V1.0     
 */
package com.xkhouse.frame.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.StringUtil;

/**
 * 缓存接口
 * 
 * @Author nieshuting
 * @ClassName ApiCache
 * @Date 2014-1-15上午8:37:38
 */
public class ApiCache {

	protected static SharedPreferences mSP;

	public static final String name = "ApiCache";

	public static void init(Context context) {
		mSP = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	/**
	 * 清除缓存
	 * 
	 * @param
	 * @return void
	 */
	public static void clearAllCache() {
		if (mSP != null) {
			mSP.edit().clear().commit();
		}
	}

	/**
	 * 清除api的缓存
	 * 
	 * @param api
	 * @return void
	 */
	public static void clearCache(String api) {
		if (mSP != null && StringUtil.isNotNull(api)) {
			Editor editor = mSP.edit();
			editor.putString(getMD5Api(api), "");
			editor.commit();
			Logger.i(name, "清空接口缓存,api=%s", api);
		}
	}

	/**
	 * 设置缓存
	 * 
	 * @param
	 * @return void
	 */
	public static void setNowCacheFlag(String api) {
		if (mSP != null && StringUtil.isNotNull(api)) {
			Editor editor = mSP.edit();
			editor.putString(getMD5Api(api), DateUtil.getCurrentDate());
			editor.commit();
			Logger.i(name, "更新接口缓存至当前时间,api=%s", api);
		}
	}

	/**
	 * 得到md5api
	 * 
	 * @param api
	 * @return String
	 */
	private static String getMD5Api(String api) {
		return new Md5FileNameGenerator().generate(api);
	}

	/**
	 * 是否缓存；
	 * 
	 * @param @param level
	 * @param @param api
	 * @param @return
	 * @return boolean
	 */
	public static boolean isUpdateCache(int level, String api) {
		if (level == CacheLevel.LEVEL0) {
			return true;// 0实时更新
		}
		if (mSP != null && StringUtil.isNotNull(api)) {
			String dateStr = mSP.getString(getMD5Api(api), "");
			int day = DateUtil.getDayNums(dateStr);
			if (day == -1) {
				// -1为第一次必须更新
				return true;
			}
			if (day == 0) {
				// 0为当天不更新
				return false;
			}
			if (day >= level) {
				return true;
			}
		}
		return false;
	}
}
