/**  
 * @Copyright (c),2011~2012 ,cdel
 * @Title:  DownFileRequest.java   
 * @Package com.cdel.frame.tool.update 
 * @Author: nieshuting     
 * @Date:   2013-12-17 下午8:03:03   
 * @Version V1.0     
 */
package com.xkhouse.frame.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;

import com.xkhouse.frame.activity.IRelease;
import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.FileUtil;
import com.xkhouse.lib.utils.StringUtil;
import com.xkhouse.lib.widget.MyDialog;

/**
 * 从服务器下载文件
 * 
 * @Author nieshuting
 * @ClassName FileDownloader
 * @Date 2013-12-17下午8:03:03
 */
public class FileDownloader implements ICallbackListener, IRelease {

	private Context context;

	private Handler handler;

	/** 下载地址 */
	private String url;

	/** 保存路径 */
	private String path;

	/** 文件名称 */
	private String name;

	private File file;

	/** 是否取消 */
	private boolean isCanceled = false;

	// 加载对话框；
	private ProgressDialog progressDialog;

	/** 成功回调 */
	protected ISuccessListener onSucessListener;

	/** 失败回调 */
	protected IErrorListener onErrorListener;

	private static final String TAG = "FileDownloader";

	/** 下载进度 */
	public final static int DOWNLOAD_PROGRESS = 0x4001;;

	/** 下载成功 */
	public final static int DOWNLOAD_SUCCESS = 0x4002;;

	/** 下载失败 */
	public final static int DOWNLOAD_FAULT = 0x40013;

	public FileDownloader(Context context, String url, String path, String name) {
		this.context = context;
		this.url = url;
		this.path = path;
		this.name = name;
		handleMessage();
	}

	/**
	 * 开始下载
	 * 
	 * @param
	 * @return void
	 */
	public void startDownlaod() {
		if (StringUtil.isEmpty(url) || StringUtil.isEmpty(path)) {
			if (onErrorListener != null) {
				onErrorListener.onError("地址为空");
			}
			return;
		}
		FileUtil.createFolder(path);
		file = new File(path + name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				if (onErrorListener != null) {
					onErrorListener.onError("无法生成文件" + e.toString());
				}
				return;
			}
		}
		showLoadingDialog("正在下载，请稍候");
		DownloadThread thread = new DownloadThread();
		thread.start();
	}

	/**
	 * 更新界面；
	 */
	private void handleMessage() {
		handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FileDownloader.DOWNLOAD_FAULT:
					// 下载安装文件失败；
					hideLoadingDialog();
					if (onErrorListener != null) {
						onErrorListener.onError("下载失败,url=" + url);
					}
					break;
				case FileDownloader.DOWNLOAD_SUCCESS:
					// 安装文件下载成功，则开始升级；
					hideLoadingDialog();
					if (onSucessListener != null) {
						onSucessListener.onSucess();
					}
					break;
				case FileDownloader.DOWNLOAD_PROGRESS:
					if (context != null) {
						Logger.i(TAG, String.valueOf(msg.obj));
						progressDialog.setProgress((Integer) msg.obj);
					}
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void showLoadingDialog(String msg) {
		if (context != null) {
			progressDialog = MyDialog.createProgressDialog(context, msg);
			progressDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			progressDialog.show();
		}
	}

	private void hideLoadingDialog() {
		if (context != null && progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	private void removeMessage() {
		if (handler != null) {
			handler.removeMessages(DOWNLOAD_FAULT);
			handler.removeMessages(DOWNLOAD_PROGRESS);
			handler.removeMessages(DOWNLOAD_SUCCESS);
		}
	}

	/**
	 * 
	 * 
	 * @param listener
	 * @see com.xkhouse.frame.download.ISuccessListener
	 * (com.xkhouse.frame.download.ISuccessListener)
	 */
	@Override
	public void setOnSucess(ISuccessListener listener) {
		this.onSucessListener = listener;
	}

	/**
	 * 
	 * 
	 * @param listener
	 * @see com.xkhouse.frame.download.ICallbackListener#setOnError
	 * (com.xkhouse.frame.network.IErrorListener)
	 */
	@Override
	public void setOnError(IErrorListener listener) {
		this.onErrorListener = listener;
	}

	class DownloadThread extends Thread {
		@Override
		public void run() {
			if (isCanceled) {
				return;
			}
			DefaultHttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					5000);
			HttpGet get = null;
			HttpResponse response = null;
			long fileSize = -1;
			int downFileSize = 0;
			int progress = 0;
			try {
				get = new HttpGet(url);
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
				fileSize = entity.getContentLength();
				InputStream is;
				is = entity.getContent();
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					Message msg = null;
					while ((ch = is.read(buf)) != -1) {
						downFileSize = downFileSize + ch;
						// 下载进度
						progress = (int) (downFileSize * 100 / fileSize);
						fileOutputStream.write(buf, 0, ch);
						if (!isCanceled) {
							msg = handler.obtainMessage(DOWNLOAD_PROGRESS,
									progress);
							handler.sendMessage(msg);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					if (!isCanceled) {
						if (file.exists()) {
							handler.sendMessage(handler
									.obtainMessage(DOWNLOAD_SUCCESS));
						} else {
							handler.sendEmptyMessage(DOWNLOAD_FAULT);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Logger.e(TAG, e.toString());
				if (!isCanceled) {
					handler.sendEmptyMessage(DOWNLOAD_FAULT);
				}
			} finally {
				client.getConnectionManager().shutdown();
			}
		}
	}

	/**
	 * 
	 * 
	 * @see com.xkhouse.frame.activity.IRelease#release()
	 */
	@Override
	public void release() {
		context = null;
		handler = null;
		onErrorListener = null;
		onSucessListener = null;
		isCanceled = true;
		hideLoadingDialog();
		removeMessage();
	}

}
