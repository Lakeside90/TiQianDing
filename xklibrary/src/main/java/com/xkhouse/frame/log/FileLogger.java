package com.xkhouse.frame.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

import com.xkhouse.frame.app.AppUtil;
import com.xkhouse.lib.utils.PhoneUtil;
import com.xkhouse.lib.utils.SDCardUtil;
import com.xkhouse.lib.widget.MyToast;


/**
 * SDFileLogger是TA框架中打印到sdcard上面的日志类
 * 
 * @Title SDFileLogger
 * @Package com.cdel.frame.log
 * @author nieshuting
 * @date 2013-1-16 14:25
 * @version V1.0
 */
public class FileLogger implements ILogger {

	public static final int VERBOSE = 2;

	public static final int DEBUG = 3;

	public static final int INFO = 4;

	public static final int WARN = 5;

	public static final int ERROR = 6;

	public static final int ASSERT = 7;

	private String mPath;

	private FileWriter writer = null;

	private String packageName;

	private Context context;

	private static final SimpleDateFormat TIMESTAMP_FMT = new SimpleDateFormat(
			"[yyyy-MM-dd HH:mm:ss] ");

	// 日志文件最大值，10M
	private static final int LOG_FILE_MAX_SIZE = 10 * 1024 * 1024;

	public FileLogger(Context context) {
		this.context = context;
	}

	public void open() {
		if (PhoneUtil.hasFroyo() && SDCardUtil.detectAvailable()) {
			String sdDir = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			packageName = AppUtil.getPackageInfo(context).packageName;
			if (!SDCardUtil.detectStorage(sdDir)) {
				return;
			}
			try {
				String logPath = sdDir + File.separator + packageName;
				File file = new File(logPath + ".txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				if (file.length() < LOG_FILE_MAX_SIZE) {
					mPath = file.getAbsolutePath();
					writer = new FileWriter(mPath, true);
					System.out.println("已创建并打开日志文件");
				} else {
					String renamefile = logPath + getCurrentTimeString()
							+ ".txt";
					if (file.renameTo(new File(renamefile))) {
						System.out.println("日志已满，已重命名日志文件" + renamefile);
						MyToast.showAtCenter(context.getApplicationContext(),
								"日志已满，已重命名日志文件" + renamefile);
						open();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String getCurrentTimeString() {
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(now);
	}

	public String getPath() {
		return mPath;
	}

	@Override
	public void d(String tag, String message) {
		// TODO Auto-generated method stub
		println(DEBUG, tag, message);
	}

	@Override
	public void e(String tag, String message) {
		println(ERROR, tag, message);
	}

	@Override
	public void i(String tag, String message) {
		println(INFO, tag, message);
	}

	@Override
	public void v(String tag, String message) {
		println(VERBOSE, tag, message);
	}

	@Override
	public void w(String tag, String message) {
		println(WARN, tag, message);
	}

	@Override
	public void v(String tag, String format, Object... args) {
		println(VERBOSE, tag, Logger.buildMessage(format, args));
	}

	@Override
	public void w(String tag, String format, Object... args) {
		println(WARN, tag, Logger.buildMessage(format, args));
	}

	@Override
	public void d(String tag, String format, Object... args) {
		println(DEBUG, tag, Logger.buildMessage(format, args));
	}

	@Override
	public void e(String tag, String format, Object... args) {
		println(ERROR, tag, Logger.buildMessage(format, args));
	}

	@Override
	public void i(String tag, String format, Object... args) {
		println(INFO, tag, Logger.buildMessage(format, args));
	}

	@Override
	public void println(int priority, String tag, String message) {
		String printMessage = "";
		switch (priority) {
		case VERBOSE:
			printMessage = "[V]|" + tag + "|" + packageName + "|" + message;
			break;
		case DEBUG:
			printMessage = "[D]|" + tag + "|" + packageName + "|" + message;
			break;
		case INFO:
			printMessage = "[I]|" + tag + "|" + packageName + "|" + message;
			break;
		case WARN:
			printMessage = "[W]|" + tag + "|" + packageName + "|" + message;
			break;
		case ERROR:
			printMessage = "[E]|" + tag + "|" + packageName + "|" + message;
			break;
		default:

			break;
		}
		println(printMessage);

	}

	public void println(String message) {
		if (writer != null) {
			try {
				writer.write(TIMESTAMP_FMT.format(new Date()));
				writer.write(message);
				writer.write('\n');
				writer.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (writer != null) {
			try {
				writer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
