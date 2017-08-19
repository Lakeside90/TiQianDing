package com.xkhouse.frame.update;

import java.io.File;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.json.JSONObject;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.WindowManager;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.xkhouse.frame.activity.BaseApplication;
import com.xkhouse.frame.activity.IRelease;
import com.xkhouse.frame.app.AppUtil;
import com.xkhouse.frame.cache.ApiCache;
import com.xkhouse.frame.db.AppPreference;
import com.xkhouse.frame.download.FileDownloader;
import com.xkhouse.frame.download.ICallbackListener;
import com.xkhouse.frame.download.IErrorListener;
import com.xkhouse.frame.download.ISuccessListener;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.PhoneUtil;
import com.xkhouse.lib.utils.StringUtil;
import com.xkhouse.lib.widget.MyToast;

/**
 * 
 * 升级类，用于检测本地版本，已封装获取升级信息、下载新安装包及启动升级。调用方法checkUpdate()并实现回调即可
 * 
 * @Author nieshuting
 * @ClassName Updater
 * @Date 2013-5-24上午11:29:09
 */

public class Updater implements ICallbackListener, IRelease {

	private Context context;

	/**
	 * 忽略升级
	 */
	private boolean ignoreUpdate = true;

	/**
	 * 错误回调
	 */
	private IErrorListener errorListener;

	/**
	 * 成功回调
	 */
	private ISuccessListener successListener;

	/** 升级对话框; */
	private AlertDialog updateDialog;

	/**
	 * update对象；
	 */
	private UpdateInfo updateInfo = null;

	/**
	 * 接口地址，用于移动班和移动课堂，会根据UID判断关课日期强制升级；
	 */
	public static String CLASS_API = "/upgrade/constraintUpgrade.shtm";

	/**
	 * 接口地址，用于其它应用，只通过后台选择强制升级项控制；
	 */
	public static String API = "/getUpdateInfo.shtm";

	private FileDownloader downloader;

	/**
	 * 临时生成文件
	 */
	public static String TEMPAPK = "/temp.apk";

	private final String TAG = "Updater";

	/**
	 * 构造器
	 * 
	 * @param context
	 *            上下文
	 * @param isIgnore
	 *            是否显示忽略按钮
	 */
	public Updater(Context context, boolean isIgnore) {
		this.context = context;
		this.ignoreUpdate = isIgnore;
	}

	/**
	 * 获取update实例对象
	 * 
	 * @return update对象
	 */
	public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	/**
	 * 下载apk安装包
	 */
	private void downFile() {
		if (updateInfo != null && StringUtil.isNotNull(updateInfo.getPath())) {
			File cacheDir = context.getCacheDir();
			String path = cacheDir.getAbsolutePath();
			downloader = new FileDownloader(context, updateInfo.getPath(),
					path, Updater.TEMPAPK);
			downloader.setOnError(new IErrorListener() {

				@Override
				public void onError(String msg) {
					sendErrorCallBack(msg);
				}
			});
			downloader.setOnSucess(new ISuccessListener() {

				@Override
				public void onSucess(String... strings) {
					startUpdate();
				}
			});
			downloader.startDownlaod();
		} else {
			sendErrorCallBack("updateInfo对象为空");
		}
	}

	/**
	 * 显示升级对话框;
	 */
	private void showUpdateDialog(CharSequence update) {
		if (context != null) {
			if (updateDialog == null) {
				Builder builder = new Builder(context);
				builder.setTitle("版本更新");
				if (update != null) {
					builder.setMessage(Html.fromHtml(update.toString()));
				}
				builder.setCancelable(false);
				builder.setPositiveButton("更新", updateOkClickListener);
				if (this.ignoreUpdate) {
					builder.setNeutralButton("忽略", updateNeutralClickListener);
				} else {
					builder.setNegativeButton("取消", updateCancelClickListener);
				}
				updateDialog = builder.create();
				updateDialog.getWindow().setType(
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			}
			if (!updateDialog.isShowing() && context != null) {
				updateDialog.show();
			}
		}
	}

	private void cancelDialog() {
		if (context != null) {
			updateDialog.cancel();
		}
	}

	// 升级确认；
	private DialogInterface.OnClickListener updateOkClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			updateDialog.dismiss();
			downFile();
		}
	};

	// 升级忽略；
	private DialogInterface.OnClickListener updateNeutralClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			updateDialog.dismiss();
			// 0表示选择安装，１表示强制安装；
			if (updateInfo != null) {
				if (UpdateInfo.FORCE_UPDATE.equals(updateInfo.getIsForce())) {
					AppUtil.closeApp(context);
				} else {
					int remoteCode = Integer.parseInt(updateInfo.getVerCode());
					int localCode = AppPreference.getInstance()
							.readAppVerCode();
					if (localCode < remoteCode) {
						AppPreference.getInstance().writeAppVerCode(remoteCode);
					}
					MyToast.showAtCenter(context, "忽略后仍可在设置中手动升级");
					sendSuccessCallBack(String.valueOf(remoteCode),
							updateInfo.getNewInfo());
				}
			}
		}
	};

	// 升级取消；
	private DialogInterface.OnClickListener updateCancelClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			updateDialog.dismiss();
			// 0表示选择安装，１表示强制安装；
			if (updateInfo != null) {
				sendSuccessCallBack(updateInfo.getVerCode(),
						updateInfo.getNewInfo());
			}
		}
	};

	/**
	 * 开始升级
	 * 
	 *  安装包路径
	 */
	@SuppressWarnings("unused")
	private void startUpdate(String path) {
		if (StringUtil.isNotNull(path)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(path)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			AppUtil.closeApp(context);
		}
	}

	private void startUpdate() {
		try {
			File cacheDir = context.getCacheDir();
			String cachePath = cacheDir.getAbsolutePath() + TEMPAPK;
			FileUtil.chmod("777", cachePath);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + cachePath),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
			AppUtil.closeApp(context);
		} catch (Exception e) {
			e.printStackTrace();
			sendErrorCallBack(e.toString());
		}

	}

	/**
	 * 解析xml，获取版本
	 * 
	 * @param jsonObject 服务器上的json升级信息
	 * @return version实体对象，解析异常返回null
	 */
	public UpdateInfo parseUpdate(JSONObject jsonObject) {
		if (jsonObject != null) {
			try {
				UpdateInfo update = new UpdateInfo();
				update.setPath(jsonObject.getString("downloadpath"));
				update.setIsForce(jsonObject.getString("forceupdate"));
				update.setVerName(jsonObject.getString("vername"));
				update.setVerCode(jsonObject.getString("vercode"));
				update.setNewInfo(jsonObject.getString("info"));
				return update;
			} catch (Exception e) {
				e.printStackTrace();
				Logger.e(TAG, e.toString());
				return null;
			}
		}
		return null;
	}

	/**
	 * 检测本地程序是否需要更新,比较本地版本号，用于新版本的appkey 新增强制升级接口，新增uid\version，用于移动课堂和移动班
	 * 
	 * @param
	 */
	public void checkUpdate() {
		// TODO 请求接口检查更新， 暂且使用友盟的升级sdk

	}



	/**
	 * 请求成功监听
	 * 
	 * @author gonglong
	 * 
	 */
	class SuccessListener implements Listener<String> {

		@Override
		public void onResponse(String result) {

			if (StringUtil.isEmpty(result)) {
				return;
			}

			try {
				JSONObject json = new JSONObject(result);
				if (!"1".equals(json.getString("code"))) {
					sendErrorCallBack("返回值中code不等于１");
					return;
				}

				updateInfo = parseUpdate(json);
				if (updateInfo == null) {
					sendErrorCallBack("updateInfo对象为空");
					return;
				}

				ApiCache.setNowCacheFlag(API);
				String info = updateInfo.getNewInfo();
				int remoteCode = Integer.parseInt(updateInfo.getVerCode());

				boolean needUpdate = remoteCode > PhoneUtil.getVerCode(context);

				if (!needUpdate) {
					if (!ignoreUpdate) {
						MyToast.showAtCenter(context.getApplicationContext(),
								"已是最新版本");
					}
					sendSuccessCallBack(String.valueOf(remoteCode), info);
					return;
				}

				if (!ignoreUpdate) {
					showUpdateDialog(info);
					return;
				}

				int localCode = AppPreference.getInstance().readAppVerCode();
				if (remoteCode > localCode) {
					showUpdateDialog(info);
				} else {
					sendSuccessCallBack(String.valueOf(remoteCode), info);
				}
			} catch (Exception e) {
				e.printStackTrace();
				sendErrorCallBack(e.toString());
			}
		}

	}

	/**
	 * 请求错误监听
	 * 
	 * @author gonglong
	 * 
	 */
	class UpdateErrorListener implements ErrorListener {

		public void onErrorResponse(VolleyError error) {
			Logger.e(TAG, error.toString());
			sendErrorCallBack(error.toString());
		}

		@Override
		public void warning(TransformerException exception)
				throws TransformerException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void error(TransformerException exception)
				throws TransformerException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void fatalError(TransformerException exception)
				throws TransformerException {
			// TODO Auto-generated method stub
			
		}

	}

	/**
	 * 发送错误回调
	 */
	private void sendErrorCallBack(String msg) {
		if (errorListener != null) {
			errorListener.onError(msg);
		}
	}

	/**
	 * 成功信息回调
	 * 
	 * @param code
	 * @param msg
	 */
	private void sendSuccessCallBack(String code, String msg) {
		if (successListener != null) {
			successListener.onSucess(code, msg);
		}
	}

	/**
	 * 成功回调
	 * 
	 * @param listener
	 * @see com.xkhouse.frame.download.ICallbackListener#setOnSucess
	 * (com.xkhouse.frame.download.ISuccessListener)
	 */
	@Override
	public void setOnSucess(ISuccessListener listener) {
		this.successListener = listener;
	}

	/**
	 * 错误回调
	 * 
	 * @param listener
	 * @see com.xkhouse.frame.download.IErrorListener
	 */
	@Override
	public void setOnError(IErrorListener listener) {
		this.errorListener = listener;
	}

	/**
	 * 
	 * 
	 * @see com.xkhouse.frame.activity.IRelease#release()
	 */
	@Override
	public void release() {
		context = null;
		successListener = null;
		errorListener = null;
		cancelDialog();
		if (downloader != null) {
			downloader.release();
		}
		BaseApplication.getInstance().cancelPendingRequests(TAG);
	}
}
