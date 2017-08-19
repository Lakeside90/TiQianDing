/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-24上午09:03:44
 *@所有人 CDEL
 *@文件名 MyToast.java
 *@包名 com.cdel.lib.view
 *
 * Copyright (C) 2011 The Chinaacc Mobile Project
 *
 */

package com.xkhouse.lib.widget;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * MyToast类用于显示提示文字，其简化了Android自带的类。
 * 
 * @author nieshuting
 * @version 0.1
 */
public class MyToast {

	/**
	 * 显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串资源ID
	 */
	public static void show(Context context, int msg) {
		if (isMainThread()) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串
	 */
	public static void show(Context context, CharSequence msg) {
		if (isMainThread()) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串资源ID
	 */
	public static void showLong(Context context, int msg) {
		if (isMainThread()) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串
	 */
	public static void showLong(Context context, CharSequence msg) {
		if (isMainThread()) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 居中显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串
	 */
	public static void showAtCenter(Context context, CharSequence msg) {
		if (isMainThread()) {
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/**
	 * 居中显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串
	 */
	public static void showAtCenter(Context context, int msg) {
		if (isMainThread()) {
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/**
	 * 自定义显示图示；
	 * 
	 * @param context
	 * @param msg
	 *            字符串
	 * @param layout
	 *            自定义布局
	 */
	public static void showCustom(Context context, int msg, View layout) {
		if (isMainThread()) {
			Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(layout);
			toast.show();
		}
	}

	/**
	 * 判断是否在主线程，用于防止在toast时出现异常
	 * 
	 * @return
	 */
	private static boolean isMainThread() {
		Looper looper = Looper.myLooper();
		return looper != null && looper == Looper.getMainLooper();
	}
}
