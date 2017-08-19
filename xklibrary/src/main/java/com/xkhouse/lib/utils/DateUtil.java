package com.xkhouse.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DateUtil类用于转换日期；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class DateUtil {

	// 时分秒
	private final static SimpleDateFormat dateFormater_hms = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	// 没日期
	private final static SimpleDateFormat dateFormater_nohms = new SimpleDateFormat(
			"yyyy-MM-dd");
	// 美式格式
	private final static SimpleDateFormat dateFormater_us = new SimpleDateFormat(
			"EEE MMM dd HH:mm:ss z yyyy", Locale.US);
	
	/**
	 * 判断是否在指定时间段<br>
	 * 如：<br>
	 * 19：00 至 09：00  start=19,end=9<br>
	 * 09：00 至 19：00  start=9,end=19<br>
	 * 
	 * @param start 指定时间段开始时间
	 * @param end 指定时间段结束时间
	 * @return true/false
	 */
	public static boolean isOnSetTime(int start, int end) {
		boolean isOntime;
		Calendar cal = Calendar.getInstance(); // 当前日期
		int hour = cal.get(Calendar.HOUR_OF_DAY); // 获取小时 
		int minute = cal.get(Calendar.MINUTE); // 获取分钟 
		int minuteOfDay = hour * 60 + minute; // 从0:00分开是到目前为止的分钟数 
		
		int minuteStart = start * 60; // 指定开始的时间的分钟数 
		int minuteEnd = end * 60; // 指定结束的时间的分钟数 
		
		if(minuteEnd < minuteStart ) { // 如果开始时间和结束时间不同天
			isOntime = (minuteOfDay>0 && minuteOfDay<= minuteEnd) || (minuteOfDay>minuteStart && minuteOfDay<1440);
			
		}else if(minuteEnd > minuteStart) {
			isOntime = minuteOfDay >= minuteStart && minuteOfDay <= minuteEnd;
			
		}else {
			isOntime = minuteOfDay == minuteStart;
		}
		return isOntime;
	}

	/**
	 * 转换Date为相对时间，如“几天前，几年前”
	 * 
	 * @param date
	 * @return 如返回1分钟前
	 */
	public static String getCreateAt(Date date) {
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.YEAR) - (date.getYear() + 1900) > 0) {
			int i = c.get(Calendar.YEAR) - (date.getYear() + 1900);
			return i + "年前";
		} else if (c.get(Calendar.MONTH) - date.getMonth() > 0) {
			int i = c.get(Calendar.MONTH) - date.getMonth();
			return i + "月前";
		} else if (c.get(Calendar.DAY_OF_MONTH) - date.getDate() > 0) {
			int i = c.get(Calendar.DAY_OF_MONTH) - date.getDate();
			return i + "天前";
		} else if (c.get(Calendar.HOUR_OF_DAY) - date.getHours() > 0) {
			int i = c.get(Calendar.HOUR_OF_DAY) - date.getHours();
			return i + "小时前";
		} else if (c.get(Calendar.MINUTE) - date.getMinutes() > 0) {
			int i = c.get(Calendar.MINUTE) - date.getMinutes();
			return i + "分钟前";
		} else {
			return "1分钟前";
		}
	}

	/**
	 * 功能：截取两个日期之间的f分钟间隔 　　
	 * 
	 * @param String
	 *            beginDate,String endDate 　　
	 * @return int 　
	 * @throws ParseException
	 */
	public static int getMinute(String beginDate, String endDate)
			throws ParseException {
		Date d1 = dateFormater_hms.parse(beginDate);
		Date d2 = dateFormater_hms.parse(endDate);
		return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60));
	}

	/**
	 * 转换字符串为Date格式，也支持转换Locale.US格式
	 * 
	 * @param str
	 *            格式为"yyyy-MM-dd HH:mm:ss"的日期
	 * @return 转换后的日期，null为转换错误
	 */
	public static Date getDate(String date) {
		Date d = null;
		if (StringUtil.isNotNull(date)) {
			try {
				d = dateFormater_hms.parse(date);
			} catch (ParseException e) {
				try {
					d = dateFormater_us.parse(date);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
		return d;
	}

	/**
	 * 转换字符串为Date格式，也支持转换Locale.US格式
	 * 
	 * @param str
	 *            格式为"yyyy-MM-dd"的日期
	 * @return 转换后的日期，null为转换错误
	 */
	public static String getDateNoTime(Date date) {
		if (null != date) {
			return dateFormater_nohms.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 转换Date格式为字符串
	 * 
	 * @param date
	 * @return "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getString(Date date) {
		if (null != date) {
			return dateFormater_hms.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 转换Date格式为字符串
	 * 
	 * @param date
	 * @return "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getDateString(Date date) {
		if (null != date) {
			return dateFormater_nohms.format(date);
		} else {
			return "";
		}
	}

	/**
	 * 转换字符串类型的日期为标准的字符串类型日期；
	 * 
	 * @param date
	 * @return "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getStandardString(String date) {
		Date d = getDate(date);
		return getString(d);
	}

	/**
	 * 与当前时间比较，用于判断是否关课
	 * 
	 * @param dateString
	 *            截止时间
	 * @return true 在截止时间以内，false，在截止时间以外
	 */
	public static boolean checkTime(String dateString) {
		Date beforeDate = null;
		Date nowDate = null;
		boolean status = false;
		if (StringUtil.isNotNull(dateString)) {
			nowDate = new Date();
			try {
				beforeDate = dateFormater_hms.parse(dateString);
				status = nowDate.before(beforeDate);
			} catch (ParseException e) {
				try {
					beforeDate = dateFormater_us.parse(dateString);
					status = nowDate.before(beforeDate);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
		return status;
	}

	/**
	 * Sat Sep 11 21:50:00 CST 2010 to 2010-09-11 21:50:00
	 * 格式化关课日期，转换日期格式为正常年月日时分秒，如解析错误返回；
	 * 
	 * @param endDate
	 * @return
	 */
	public static String formatEndDate(String _endDate) {
		String endDate = "";
		try {
			Date d = dateFormater_us.parse(_endDate);
			endDate = dateFormater_hms.format(d);
		} catch (ParseException e) {
			// e.printStackTrace();
			return _endDate;
		}
		return endDate;
	}

	/**
	 * 获取今年
	 * 
	 * @param
	 * @return String
	 */
	public static String getThisYear() {
		return String.valueOf(new Date().getYear() + 1900);
	}

	/**
	 * 判断是否为当天
	 * 
	 * @param @return
	 * @return boolean
	 * 
	 */
	public static boolean checkThisDay(String day) {
		Date date = new Date();
		String now = dateFormater_nohms.format(date);
		if (now.equals(day)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater_hms.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater_nohms.format(today);
			String timeDate = dateFormater_nohms.format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 获取当前日期
	 * 
	 * @param @return
	 * @return String
	 */
	public static String getCurrentDate() {
		Date now = new Date();
		return dateFormater_hms.format(now);
	}

	/**
	 * 判断是否为同一天
	 * 
	 * @param @param time
	 * @param @return
	 * @return boolean
	 */
	public static boolean isTheSameDay(String time) {
		if (time == null || "".equals(time)) {
			return false;
		}
		Date curDate = new Date();
		Date lastDate;
		try {
			lastDate = dateFormater_hms.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(lastDate);
		c2.setTime(curDate);
		return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2
						.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 根据日期得到相隔天数，用于接口缓存
	 * 
	 * @param dateStr
	 * @return 如返回1天，０为当天
	 */
	public static int getDayNums(String dateStr) {
		int day = 0;
		if (!StringUtil.isNotNull(dateStr)) {
			// 默认第１次为-1
			return -1;
		}
		Date date = toDate(dateStr);
		if (date == null) {
			return -1;
		}
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.YEAR) - (date.getYear() + 1900) > 0
				|| c.get(Calendar.MONTH) - date.getMonth() > 0) {
			day = 31;
		} else if (c.get(Calendar.DAY_OF_MONTH) - date.getDate() > 0) {
			day = c.get(Calendar.DAY_OF_MONTH) - date.getDate();
		}
		return day;
	}

	/**
	 * 
	* @Description: 计算两个日期之间间隔多少天
	* @param @param startDate  yyyy-MM-dd  形式
	* @param @param endDate    yyyy-MM-dd
	* @param @return    设定文件 
	* @return int    返回类型
	 */
	public static int getDayNums(String startDate, String endDate){
		int day = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try{
		    Date d1 = df.parse(startDate);
		    Date d2 = df.parse(endDate);
		    long diff = d2.getTime() - d1.getTime();
		    day = (int) (diff / (1000 * 60 * 60 * 24));
		    
		}catch (Exception e){
		}
		return day;		
	}
	
	/**
	* @Description: 将秒值转成日期
	* @param @param time
	* @param @return    设定文件 
	* @return String    返回类型
	 */
	public static String getDateFromLong(long time, String format){
		String str = "";
		try {
			Date date = new Date(time*1000L);
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			str = sdf.format(date);
		} catch (Exception e) {
		}
		
		return str;
	}
	
	
    

}
