/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  SplashActivity.java   
 * @Package com.cdel.frame.activity 
 * @Author: nieshuting     
 * @Date:   2013-12-12 下午6:53:57   
 * @Version V1.0     
 */
package com.xkhouse.frame.activity;

import android.content.Intent;
import android.os.Handler;

import com.xkhouse.frame.app.AppService;
import com.xkhouse.frame.db.AppPreference;
import com.xkhouse.frame.db.BaseUpdateDBService;
import com.xkhouse.frame.db.UpdateDBThread;
import com.xkhouse.lib.utils.DateUtil;
import com.xkhouse.lib.utils.PhoneUtil;

import java.util.Date;

/**
 * 继续自BaseActivity，封装启动时常用功能；
 * 
 * @Author nieshuting
 * @ClassName SplashActivity
 * @Date 2013-12-12下午6:53:57
 */
public abstract class BaseSplashActivity extends BaseActivity {

	/**
	 * 延迟时长，默认为1.5秒
	 */
	public static int DELAY_TIME = 4000;

	/**
	 * 实现检测签名，提交手机信息及离线延迟
	 * 
	 * @see com.xkhouse.frame.activity.BaseActivity#init()
	 */
	@Override
	protected void init() {
		// 添加创建文件夹
		BaseApplication app = (BaseApplication) mContext.getApplication();
		app.initDirs();
//		saveRun();
//		startUpdateAppService();
		delayLaunch();
//		MobclickAgent.updateOnlineConfig( mContext );
	}

	
	/**
	 * 在后台启动升级和统计的程序
	 * 
	 * @param
	 * @return void
	 */
	private void startUpdateAppService() {
		Intent intent = new Intent(mContext, AppService.class);
		mContext.startService(intent);
	}

	/**
	 * 延迟启动
	 */
	private void delayLaunch() {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				launchCompleteDoNext();
			}
		}, DELAY_TIME);
	}

	/**
	 * 启动完成，子类实现具体的下一步操作；
	 * 
	 * @param:
	 * @return: void
	 */
	protected abstract void launchCompleteDoNext();

	/**
	 * 欢迎界面禁用back键
	 * 
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

	}

	/**
	 *
	 *
	 * @see com.xkhouse.frame.activity.BaseActivity#release()
	 */
	@Override
	protected void release() {

	}

	/**
	 * 初始表结构，包括创建和修改表
	 * 
	 * @param:
	 * @return: void
	 */
	public void updateDB(BaseUpdateDBService service) {
		if (service != null) {
			new Thread(new UpdateDBThread(service)).start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		/* 极光 */
//		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		/* 极光 */
//		JPushInterface.onPause(this);
		super.onPause();
	}

	/**
	 * 保存启动信息
	 * 
	 * void
	 */
	private void saveRun() {
		String network = PhoneUtil.getNetWork(mContext);
		String runtime = DateUtil.getString(new Date());
		String run = network + "#" + runtime;
		AppPreference.getInstance().writeAppRun(run);
	}
	
	protected void loadAD() {
		
	}

}
