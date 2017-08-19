package com.xkhouse.frame.log;

/**
 * ILogger是一个日志的接口
 * 
 * @Title ILogger
 * @Package com.cdel.frame.log
 * @author nieshuting
 * @date 2013-1-16 14:20
 * @version V1.0
 */
public interface ILogger {
	void v(String tag, String message);

	void d(String tag, String message);

	void i(String tag, String message);

	void w(String tag, String message);

	void e(String tag, String message);

	void v(String tag, String format, Object... args);

	void d(String tag, String format, Object... args);

	void i(String tag, String format, Object... args);

	void w(String tag, String format, Object... args);

	void e(String tag, String format, Object... args);

	void open();

	void close();

	void println(int priority, String tag, String message);
}
