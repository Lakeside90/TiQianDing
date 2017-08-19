package com.xkhouse.frame.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;


import com.umeng.analytics.MobclickAgent;
import com.xkhouse.frame.config.BaseConfig;
import com.xkhouse.frame.log.Logger;

import java.util.Properties;


/**
 * 基类activity，所有activity均继承该activity. 定义常用抽象方法和view点击接口,已添加友盟。
 * 
 * @Copyright (c),2012~ ,cdel
 * @Author nieshuting
 * @ClassName BaseActivity
 * @Package com.cdel.frame.activity
 * @Date 2012-8-20上午10:32:36
 * @Verstion V1.0
 * @History
 */

public abstract class BaseActivity extends Activity implements OnClickListener {

	/**
	 * 全局应用Context
	 */
	protected BaseActivity mContext;

	/**
	 * TAG，动态生成类名
	 */
	protected String TAG = "BaseActivity";

	/**
	 * handler
	 */
	protected Handler mHandler;

	/** 配置 */
	protected Properties property;

	/** 显示时间点 */
	protected long resumeTime = 0;

	/**
	 * 可重写onCreate
	 * 
	 * @param savedInstanceState
	 * @see Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
		TAG = this.getClass().getName();
		mContext = this;
//		BaseApplication application = (BaseApplication) this.getApplication();
//		application.getActivityManager().pushActivity(this);
		property = BaseConfig.getInstance().getConfig();
		init();
		findViews();
		setListeners();
		Logger.i(TAG, "创建");
	}

	/**
	 * 设置布局
	 */
	protected abstract void setContentView();

	/**
	 * 初始化，setContentView后调用
	 */
	protected abstract void init();

	/**
	 * 查找视图。init后调用；
	 */
	protected abstract void findViews();

	/**
	 * 设置侦听器，findViews后调用
	 */
	protected abstract void setListeners();

	/**
	 * ondestory中销毁activity前,释放activity中的message\request\thread\注册侦听等占用资源的东西，
	 * 避免空指针或内存泄露
	 */
	protected abstract void release();

	/**
	 * <p>
	 * Title: onResume
	 * </p>
	 * <p>
	 * Description:添加友盟统计
	 * </p>
	 * 
	 * @see Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "重新显示");
		MobclickAgent.onResume(mContext);
	}

	/**
	 * <p>
	 * Title: onPause
	 * </p>
	 * <p>
	 * Description:添加友盟统计
	 * </p>
	 * 
	 * @see Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "暂停");
		MobclickAgent.onPause(mContext);
	}

	/**
	 * <p>
	 * Title: onDestroy
	 * </p>
	 * <p>
	 * Description:销毁activity，取消中止线程消息等异步执行
	 * </p>
	 * 
	 * @see Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		release();
		Logger.i(TAG, "销毁");
	}

	/**
	 * <p>
	 * Title: onClick
	 * </p>
	 * <p>
	 * Description:点击事件逻辑处理
	 * </p>
	 * 
	 * @param v
	 * @see OnClickListener#onClick(View)
	 */
	@Override
	public void onClick(View v) {

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		BaseApplication application = (BaseApplication) getApplication();
//		application.getActivityManager().popActivity(this);
	}

}
