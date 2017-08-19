/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title  UpdateAppService.java   
 * @Package com.cdel.frame.app 
 * @Author nieshuting     
 * @Date   2014-7-16 上午10:09:42   
 * @Version V1.0     
 */
package com.xkhouse.frame.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xkhouse.frame.cache.ApiCache;
import com.xkhouse.frame.cache.CacheLevel;
import com.xkhouse.frame.download.IErrorListener;
import com.xkhouse.frame.download.ISuccessListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.frame.update.Updater;
import com.xkhouse.lib.utils.NetUtil;

/**
 * 检查应用版本更新、提交统计数据等
 * 
 * @Author nieshuting
 * @ClassName UpdateAppService
 * @Date 2014-7-16上午10:09:42
 */
public class AppService extends Service {

	private Updater updater;

	private Context mContext;
	/** TAG */
	public static final String TAG = "AppService";

	/**
	 * 
	 * 
	 * @param intent
	 * @return
	 * @see Service#onBind(Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * 
	 * @see Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;
		if (NetUtil.detectAvailable(mContext)) {
			Logger.i(TAG, "开始检查更新及提交统计数据");
//			detectSign();
//			uploadPhoneInfo();
//			uploadLevel();
//			uploadUseTime();
			updater = new Updater(mContext, true);
			updater.setOnSucess(new ISuccessListener() {

				@Override
				public void onSucess(String... strings) {
					if (strings != null) {
						for (String str : strings) {
							Logger.i(TAG, "未升级，升级信息：" + str);
						}
					}
				}
			});
			updater.setOnError(new IErrorListener() {

				@Override
				public void onError(String msg) {
					Logger.w(TAG, "未升级，" + msg);
				}
			});
			if (ApiCache.isUpdateCache(CacheLevel.LEVEL3, Updater.API)) {
				updater.checkUpdate();
			} else {
				Logger.i(TAG, "缓存未过期不请求升级");
			}
		}
	}

	/**
	 * 检测签名，debug模块下无效；
	 * 
	 * @param:
	 * @return: void
	 */
//	private void detectSign() {
//		if (!BuildConfig.DEBUG) {
//			// 执行验签；
//			new ASign(mContext.getApplicationContext()).upload();
//		}
//	}

	/**
	 * 提交手机信息，debug模块下无效；
	 * 
	 * @param
	 * @return void
	 */
//	private void uploadPhoneInfo() {
//		String runSP = AppPreference.getInstance().readAppRun();
//		String[] run = runSP.split(",");
//		if (run != null && run.length > 0) {
//			new APhoneInfo(mContext.getApplicationContext()).upload(runSP);
//		}
//	}

	/**
	 * 提交level，debug模块下无效；
	 * 
	 * @param
	 * @return void
	 */
//	private void uploadLevel() {
//		new AUpdateAppMemberLevel(mContext.getApplicationContext()).upload();
//	}

	/**
	 * 提交用户时长
	 * 
	 * @param
	 * @return void
	 */
//	private void uploadUseTime() {
//		long totaltime = AppPreference.getInstance().readAppStartTime();// 单位秒
//		if (totaltime > 12 * 60 * 60) {// 超过范围则初始化为0
//			AppPreference.getInstance().writeAppStartTime(0);
//		}
//		if (totaltime > 0) {
//			new AUseTime(mContext.getApplicationContext()).upload(String
//					.valueOf(totaltime));
//		}
//	}

	/**
	 * 
	 * 
	 * @see Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (updater != null) {
			updater.release();
		}
		Logger.i(TAG, "销毁");
	}

	/**
	 * 
	 * 
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 * @see Service#onStartCommand(Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
