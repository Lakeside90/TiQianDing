/*
 *
 *@作者 nieshuting
 *@创建日期 2011-10-26上午09:42:00
 *@所有人 CDEL
 *@文件名 CrashHandler.java
 *@包名 com.cdel.lib..help
 */

package com.xkhouse.frame.tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.xkhouse.frame.app.AppUtil;
import com.xkhouse.frame.log.Logger;

/**
 * Crash类用于捕获全局异常并处理；
 * 
 * @author nieshuting
 * @version 0.2
 */
public class Crash implements UncaughtExceptionHandler {

	// 系统默认的UncaughtException处理类
	private UncaughtExceptionHandler defaultHandler;

	// CrashHandler实例
	private static Crash INSTANCE;

	// 程序的Context对象
	private Context context;

	private static final String TAG = "Crash";

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Logger.e(TAG, "捕获全局异常");
		if (!handleException(ex) && defaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			defaultHandler.uncaughtException(thread, ex);
		} else {
		    //程序崩溃后 Kill本进程
			AppUtil.closeApp(context);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static Crash getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Crash();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param context
	 *            上下文
	 * @param api
	 *            接口
	 */
	public void init(Context context) {
		this.context = context;
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		// 发送错误消息至服务器；
		saveCrash(ex);
		return true;
	}

	/**
	 * 保存日志；
	 * 
	 * @param e
	 */
	private void saveCrash(Throwable e) {
		Writer info = new StringWriter();
		PrintWriter printWriter = new PrintWriter(info);
		e.printStackTrace(printWriter);
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		String report = info.toString();
		printWriter.close();
		Logger.e(TAG, "程序崩溃，日志=" + report);
	}
}
