/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-29下午03:19:14
 *@所有人 CDEL
 *@文件名 SDCardUtil.java
 *@包名 org.cdel.chinaacc.phone.util
 */

package com.xkhouse.lib.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.xkhouse.frame.log.Logger;

/**
 * SDCardUtil类用于检测sd卡的状态和大小；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class SDCardUtil {

	// 外置sd卡
	public static String[] EXTERNAL_SD = { "removable_sdcard", "extr_sd",
			"external_sd", "extsdcard", "extsd", "extern_sd", "sdcard2",
			"sdcard-ext", "ext_sdcard", "sd_card", "extra_sd", "extrasd_bind",
			"ext_sd", "sdcard1", "sdcard0" };

	// 所有sdcard
	public static String[] allSDCard;

	private static final int MINSIZE = 50;

	/**
	 * 获取设备所有内置外置卡的绝对路径
	 * 
	 * @param @return
	 * @return String[]
	 */
	public static String[] getAllSD() {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			String mount = new String();
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				Logger.i("mount", line);
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				if (line.contains("vendor"))
					continue;
				if (line.contains("htcfs"))
					continue;
				if (line.contains("system"))
					continue;
				if (line.contains("cache"))
					continue;
				if (line.contains("efs"))
					continue;
				if (line.contains("obb"))
					continue;
				// if (line.contains("legacy"))
				// continue;
				// VFAT filesystem vfat Windows95/98采用的文件系统
				// rw 可读写
				// fuse 指完全在用户态实现的文件系统
				if (line.contains("vfat") && line.contains("rw")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						if (detectAvailableDirectory(columns[1])) {
							mount = mount.concat(columns[1] + "&");
						}
					}
				} else if (line.contains("vfat") && line.contains("on")
						&& line.contains("rw")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						if (detectAvailableDirectory(columns[2])) {
							mount = mount.concat(columns[2] + "&");
						}
					}
				} else if (line.contains("exfat") && line.contains("rw")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						if (detectAvailableDirectory(columns[2])) {
							mount = mount.concat(columns[2] + "&");
						}
					}
				} else if (line.contains("fuse") && line.contains("rw")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						if (detectAvailableDirectory(columns[1])) {
							mount = mount.concat(columns[1] + "&");
						}
					}
				} else if (line.contains("sdcardfs") && line.contains("rw")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						if (detectAvailableDirectory(columns[1])) {
							mount = mount.concat(columns[1] + "&");
						}
					}
				}
			}
			allSDCard = mount.split("&");
			Logger.i("SDCard", "设备sd卡%s", mount);
			return allSDCard;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 判断文件夹是否有可用空间
	 * 
	 * @param @param path
	 * @param @return
	 * @return boolean
	 */
	public static boolean detectAvailableDirectory(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			long size = getDirectorySize(path);
			if (size > MINSIZE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断文件夹是否有可用空间
	 * 
	 * @param path
	 * @param minSize
	 *            自定义大小
	 * @return
	 */
	public static boolean detectAvailableDirectory(String path, int minSize) {
		File file = new File(path);
		if (file.isDirectory()) {
			long size = getDirectorySize(path);
			if (size > minSize) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取手机上有足够空间的sdcard路径，优先：外置卡》内置卡
	 * 
	 * @param @return
	 * @return String
	 */
	public static String getAvailableSD() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 优化获取android api指向的外置存储卡路径；
			File file = Environment.getExternalStorageDirectory();
			if (getEXTSDSize(file.getAbsolutePath()) > MINSIZE) {
				return file.getAbsolutePath();
			}
		}
		if (allSDCard == null) {
			// 再获取所有sdcard
			getAllSD();
		}
		for (String sd : allSDCard) {
			if (getDirectorySize(sd) > MINSIZE) {
				return sd;
			}
		}
		return null;
	}

	/**
	 * 获取目录的分区大小
	 * 
	 * @param @param path
	 * @param @return
	 * @return long
	 */
	public static long getDirectorySize(String path) {
		if (StringUtil.isEmpty(path)) {
			return 0;
		}
		long freeSize = 0;
		try {
			// 取得sdcard文件路径
			StatFs statfs = new StatFs(path);
			// 获取SDCard上每个block的SIZE
			long blocSize = statfs.getBlockSize();
			// 获取可供程序使用的Block的数量
			long availaBlock = statfs.getAvailableBlocks();
			// 计算 SDCard 剩余大小MB
			freeSize = availaBlock * blocSize / 1024 / 1024;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freeSize;
	}

	/**
	 * 获取目录的分区总量
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getDirectoryTotalSize(Context context, String path) {
		if (StringUtil.isEmpty(path)) {
			return "";
		}
		String freeSize = "";
		try {
			// 取得sdcard文件路径
			StatFs statfs = new StatFs(path);
			// 获取SDCard上每个block的SIZE
			long blocSize = statfs.getBlockSize();
			// 获取可供程序使用的Block的数量
			long availaBlock = statfs.getBlockCount();
			// 总的块数*块数的尺寸=总空间
			freeSize = Formatter
					.formatFileSize(context, blocSize * availaBlock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freeSize;
	}

	/**
	 * 获取目录的分区可用量
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getDirectoryAvailSize(Context context, String path) {
		if (StringUtil.isEmpty(path)) {
			return "";
		}
		String freeSize = "";
		try {
			// 取得sdcard文件路径
			StatFs statfs = new StatFs(path);
			// 获取SDCard上每个block的SIZE
			long blocSize = statfs.getBlockSize();
			// 获取可供程序使用的Block的数量
			long availaBlock = statfs.getAvailableBlocks();
			// 可用块数*块数的尺寸=可用空间
			freeSize = Formatter
					.formatFileSize(context, blocSize * availaBlock);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return freeSize;
	}

	/**
	 * 判断是否为外置sdcard的目录
	 * 
	 * @param @param name
	 * @param @return
	 * @return boolean
	 */
	public static boolean detectISEXTSD(String name) {
		for (String s : EXTERNAL_SD) {
			if (name.toLowerCase().contains(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测外置sd卡可用且有足够空间，默认大小为100M
	 * 
	 * @param @return
	 * @return boolean
	 */
	public static boolean detectAvailableEXTSD(String _path) {
		String path = _path;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File file = Environment.getExternalStorageDirectory();
			if (file.canWrite()) {
				// sdcard 已插入并可读写；
				if (StringUtil.isEmpty(path)) {
					path = file.getAbsolutePath();
					if (getEXTSDSize(path) > MINSIZE) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检测SDCard是否挂载；
	 * 
	 * @return true 可用，false：不可用
	 */
	public static boolean detectEXTSDMounted() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// sdcard 已插入并可读写；
			if (Environment.getExternalStorageDirectory().canWrite()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测是否有足够的容量；
	 * 
	 * @param minSize
	 *            最小大小；MB
	 * @return 空间不足为false，否则为true;
	 */
	public static boolean detectAvailableEXTSD(int minSize, String path) {
		if (minSize > 0 && StringUtil.isNotNull(path)) {
			// 取得SDCard当前的状态
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				// 取得sdcard文件路径
				StatFs statfs = new StatFs(path);
				// 获取SDCard上每个block的SIZE
				long blocSize = statfs.getBlockSize();
				// 获取可供程序使用的Block的数量
				long availaBlock = statfs.getAvailableBlocks();
				// 计算 SDCard 剩余大小MB
				long freeSize = availaBlock * blocSize / 1024 / 1024;
				if (freeSize > minSize) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取外置sd卡下指定目录的空间大小
	 * 
	 * @param path
	 *            路径
	 * @return　返回大小MB
	 */
	public static long getEXTSDSize(String path) {
		if (StringUtil.isNotNull(path)) {
			// 取得SDCard当前的状态
			String sDcString = Environment.getExternalStorageState();
			if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
				// 取得sdcard文件路径
				StatFs statfs = new StatFs(path);
				// 获取SDCard上每个block的SIZE
				long nBlocSize = statfs.getBlockSize();
				// 获取可供程序使用的Block的数量
				long nAvailaBlock = statfs.getAvailableBlocks();
				// 计算 SDCard 剩余大小MB
				long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
				return nSDFreeSize;
			}
		}
		return 0;
	}

	/**
	 * 获取外置sd卡的路径
	 * 
	 * @param @return
	 * @return String
	 */
	public static String getEXTSDDirctory() {
		String sDcString = Environment.getExternalStorageState();
		if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return "";
	}

	/**
	 * 检测SDCard是否可用；
	 * 
	 * @return true 可用，false：不可用
	 */
	public static boolean detectAvailable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// sdcard 已插入并可读写；
			if (Environment.getExternalStorageDirectory().canWrite()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测是否有足够的容量；
	 * 
	 * @param minSize
	 *            最小大小；MB
	 * @return 空间不足为false，否则为true;
	 */
	public static boolean detectStorage(String path) {
		if (StringUtil.isNotNull(path)) {
			// 取得SDCard当前的状态
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				// 取得sdcard文件路径
				StatFs statfs = new StatFs(path);
				// 获取SDCard上每个block的SIZE
				long blocSize = statfs.getBlockSize();
				// 获取可供程序使用的Block的数量
				long availaBlock = statfs.getAvailableBlocks();
				// 计算 SDCard 剩余大小MB
				long freeSize = availaBlock * blocSize / 1024 / 1024;
				if (freeSize > MINSIZE) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取指定目录下的空间大小
	 * 
	 * @param path
	 *            路径
	 * @return　返回大小MB
	 */
	public static long getStorage(String path) {
		if (StringUtil.isNotNull(path)) {
			// 取得SDCard当前的状态
			String sDcString = Environment.getExternalStorageState();
			if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
				// 取得sdcard文件路径
				StatFs statfs = new StatFs(path);
				// 获取SDCard上每个block的SIZE
				long nBlocSize = statfs.getBlockSize();
				// 获取可供程序使用的Block的数量
				long nAvailaBlock = statfs.getAvailableBlocks();
				// 计算 SDCard 剩余大小MB
				long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
				return nSDFreeSize;
			}
		}
		return 0;
	}
}
