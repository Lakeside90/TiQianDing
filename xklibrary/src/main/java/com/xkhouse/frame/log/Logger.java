package com.xkhouse.frame.log;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xkhouse.BuildConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * TALogger是一个日志打印类
 * 
 * @Title Logger
 * @Package com.cdel.frame.log
 * @author nieshuting
 * @date 2013-1-16 14:20
 * @version V1.0
 */
public class Logger {

	/**
	 * Priority constant for the println method; use TALogger.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use TALogger.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use TALogger.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use TALogger.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use TALogger.e.
	 */
	public static final int ERROR = 6;
	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;

	/** 是否为调试模式 */
	public static boolean isDebug = false;

	private static HashMap<String, ILogger> loggerHashMap = new HashMap<String, ILogger>();

	// private static final ILogger defaultLogger = new LogCatLogger();

	public static void addLogger(ILogger logger) {
		String loggerName = logger.getClass().getName();
		// String defaultLoggerName = defaultLogger.getClass().getName();
		if (!loggerHashMap.containsKey(loggerName)) {
			logger.open();
			loggerHashMap.put(loggerName, logger);
		}

	}

	public static void removeLogger(ILogger logger) {
		String loggerName = logger.getClass().getName();
		if (loggerHashMap.containsKey(loggerName)) {
			logger.close();
			loggerHashMap.remove(loggerName);
		}

	}

	/**
	 * 输出调试消息
	 * 
	 * @param @param object
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void d(Object object, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(DEBUG, object, message);
		}
	}

	/**
	 * 输出错误消息
	 * 
	 * @param @param object
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void e(Object object, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(ERROR, object, message);
		}
	}

	/**
	 * 输出正常信息
	 * 
	 * @param @param object
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void i(Object object, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(INFO, object, message);
		}
	}

	/**
	 * 输出所有信息
	 * 
	 * @param @param object
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void v(Object object, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(VERBOSE, object, message);
		}
	}

	/**
	 * 输出警告信息
	 * 
	 * @param @param object
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void w(Object object, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(WARN, object, message);
		}
	}

	/**
	 * 输出调试信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void d(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(DEBUG, tag, format, args);
		}
	}

	/**
	 * 输出调试信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void d(String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(DEBUG, tag, message);
		}
	}

	/**
	 * 输出异常信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void e(String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(ERROR, tag, message);
		}
	}

	/**
	 * 输出异常信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void e(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(ERROR, tag, format, args);
		}
	}

	/**
	 * 输出正常信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void i(String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(INFO, tag, message);
		}
	}

	/**
	 * 输出正常信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void i(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(INFO, tag, format, args);
		}
	}

	/**
	 * 输出所有信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void v(String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(VERBOSE, tag, message);
		}
	}

	/**
	 * 输出所有信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void v(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(VERBOSE, tag, format, args);
		}
	}

	/**
	 * 输出警告信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void w(String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(WARN, tag, message);
		}

	}

	/**
	 * 输出警告信息
	 * 
	 * @param @param tag
	 * @param @param message
	 * @return void
	 */
	@SuppressWarnings("unused")
	public static void w(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			printLoger(WARN, tag, format, args);
		}

	}

	public static void println(int priority, String tag, String message) {
		printLoger(priority, tag, message);
	}

	private static void printLoger(int priority, Object object, String message) {
		Class<?> cls = object.getClass();
		String tag = cls.getName();
		String arrays[] = tag.split("\\.");
		tag = arrays[arrays.length - 1];
		printLoger(priority, tag, message);
	}

	@SuppressWarnings("unused")
	private static void printLoger(int priority, String tag, String message) {
		if (BuildConfig.DEBUG || isDebug) {
			// printLoger(defaultLogger, priority, tag, message);
			Iterator<Entry<String, ILogger>> iter = loggerHashMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, ILogger> entry = iter.next();
				ILogger logger = entry.getValue();
				if (logger != null) {
					printLoger(logger, priority, tag, message);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void printLoger(int priority, String tag, String format,
			Object... args) {
		if (BuildConfig.DEBUG || isDebug) {
			// printLoger(defaultLogger, priority, tag, message);
			Iterator<Entry<String, ILogger>> iter = loggerHashMap.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Entry<String, ILogger> entry = iter.next();
				ILogger logger = entry.getValue();
				if (logger != null) {
					printLoger(logger, priority, tag, format, args);
				}
			}
		}
	}

	private static void printLoger(ILogger logger, int priority, String tag,
			String message) {

		switch (priority) {
		case VERBOSE:
			logger.v(tag, message);
			break;
		case DEBUG:
			logger.d(tag, message);
			break;
		case INFO:
			logger.i(tag, message);
			break;
		case WARN:
			logger.w(tag, message);
			break;
		case ERROR:
			logger.e(tag, message);
			break;
		default:
			break;
		}
	}

	private static void printLoger(ILogger logger, int priority, String tag,
			String format, Object... args) {

		switch (priority) {
		case VERBOSE:
			logger.v(tag, format, args);
			break;
		case DEBUG:
			logger.d(tag, format, args);
			break;
		case INFO:
			logger.i(tag, format, args);
			break;
		case WARN:
			logger.w(tag, format, args);
			break;
		case ERROR:
			logger.e(tag, format, args);
			break;
		default:
			break;
		}
	}

	public static String buildMessage(String format, Object... args) {
		try {
			String msg = (args == null) ? format : String.format(Locale.US, format,
					args);
			StackTraceElement[] trace = new Throwable().fillInStackTrace()
					.getStackTrace();

			String caller = "<unknown>";
			// Walk up the stack looking for the first caller outside of Logger.
			// It will be at least two frames up, so start there.
			for (int i = 2; i < trace.length; i++) {
				Class<?> clazz = trace[i].getClass();
				if (!clazz.equals(Logger.class)) {
					String callingClass = trace[i].getClassName();
					callingClass = callingClass.substring(callingClass
							.lastIndexOf('.') + 1);
					callingClass = callingClass.substring(callingClass
							.lastIndexOf('$') + 1);

					caller = callingClass + "." + trace[i].getMethodName();
					break;
				}
			}
			return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread()
					.getId(), caller, msg);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

//	/**
//	 * 分享微信
//	 * 
//	 * @param filePath
//	 */
//	private static void shareWX(Context context, String filePath) {
//		String appId = BaseConfig.getInstance().getConfig()
//				.getProperty("wxappid");
//		IWXAPI api = WXAPIFactory.createWXAPI(context, appId, true);
//		api.registerApp(appId);
//		WXFileObject mediaObj = new WXFileObject(filePath);
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = mediaObj;
//		msg.description = "日志文件";
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = String.valueOf(System.currentTimeMillis());
//		req.message = msg;
//		api.sendReq(req);
//	}

	/**
	 * 分享QQ
	 * 
	 * @param @param filePath
	 * @return void
	 */
	private static void shareQQ(Context context,String filePath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setPackage("com.tencent.mobileqq");
		intent.setType("text/plain");
		Uri uri = Uri.fromFile(new File(filePath));
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		context.startActivity(intent);
	}

//	/**
//	 * 通过QQ或微信发送日志，优先QQ
//	 * 
//	 * @param @param context
//	 * @param @param appId
//	 * @return void
//	 */
//	public static void sendLog(Context context) {
//		if (SDCardUtil.detectAvailable()) {
//			String sdDir = Environment.getExternalStorageDirectory()
//					.getAbsolutePath();
//			String packageName = AppUtil.getPackageInfo(context).packageName;
//			String filePath = sdDir + File.separator + packageName + ".txt";
//			File file = new File(filePath);
//			if (file.exists()) {
//				// 优先QQ，先判断是否安装QQ，再判断是否安装微信
//				if (ApkUtil.isApkInstalled(context, "com.tencent.mobileqq")) {
//					shareQQ(context,filePath);
//					return;
//				}
//				if (ApkUtil.isApkInstalled(context, "com.tencent.mm")) {
//					shareWX(context, filePath);
//					return;
//				}
//				
//				Toast.makeText(context, "请安装QQ", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}

}
