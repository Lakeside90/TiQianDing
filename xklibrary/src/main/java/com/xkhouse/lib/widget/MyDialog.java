/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-27上午11:30:10
 *@所有人 CDEL
 *@文件名 MyDialog.java
 *@包名 org.cdel.chinaacc.phone.help
 */

package com.xkhouse.lib.widget;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.xkhouse.lib.utils.StringUtil;

/**
 * MyDialog类用于创建对话框；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class MyDialog {

	/**
	 * 创建系统的不确定对话框ProgressDialog对象
	 * 
	 * @param context
	 * @param msg
	 * @return ProgressDialog
	 */
	public static ProgressDialog createLoadingDialog(Context context, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		return progressDialog;
	}

	/**
	 * 关闭对话框
	 * 
	 * @param progressDialog
	 *            对话框
	 */
	public static void closeLoadingDialog(ProgressDialog progressDialog) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 创建系统的对话框AlertDialog对象
	 * 
	 * <br>
	 * 
	 * @param context
	 * @param msg
	 *            消息
	 * @param title
	 *            标题
	 * @param needOk
	 *            确定按钮
	 * @param needCancel
	 *            取消按钮
	 * @return AlertDialog
	 */
	public static AlertDialog.Builder createAlertDialog(Context context,
			String title, String msg, boolean needOk, boolean needCancel) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		if (StringUtil.isNotNull(title)) {
			ab.setTitle(title);
		}
		if (StringUtil.isNotNull(msg)) {
			ab.setMessage(msg);
		}
		if (needOk) {
			ab.setPositiveButton("确定", null);
		}
		if (needCancel) {
			ab.setNegativeButton("确定", null);
		}

		return ab;
	}

	/**
	 * 创建系统的对话框AlertDialog对象
	 * 
	 * <br>
	 * 
	 * @param context
	 * @param msg
	 *            消息
	 * @param title
	 *            标题
	 * @return AlertDialog
	 */
	public static AlertDialog.Builder createAlertDialog(Context context,
			String title, String msg) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		if (StringUtil.isNotNull(title)) {
			ab.setTitle(title);
		}
		if (StringUtil.isNotNull(msg)) {
			ab.setMessage(msg);
		}
		return ab;
	}

	/**
	 * 关闭对话框
	 * 
	 * @param ab
	 *            对话框
	 */
	public static void closeAlertDialog(AlertDialog ab) {
		if (ab != null && ab.isShowing()) {
			ab.cancel();
		}
	}

	/**
	 * 创建显示进度的对话框；
	 * 
	 * @param @param context
	 * @param @param msg
	 * @param @return
	 * @return ProgressDialog
	 */
	public static ProgressDialog createProgressDialog(Context context,
			String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMessage(msg);
		progressDialog.setMax(100);
		progressDialog.setProgress(0);
		//progressDialog.setSecondaryProgress(10);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		return progressDialog;
	}

}
