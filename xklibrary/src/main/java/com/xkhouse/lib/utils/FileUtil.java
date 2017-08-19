/*
 *
 *@作者 nieshuting
 *@创建日期 2011-7-18下午04:48:00
 *@所有人 CDEL
 *@文件名 FileUtil.java
 *@包名 org.cdel.chinaacc.phone.util
 */

package com.xkhouse.lib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xkhouse.lib.crypto.AES;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * FileUtil类，用于操作文件，如创建、删除等；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class FileUtil {

	/**
	 * 判断路径是否存在，如果不存在在，则创建；
	 * 
	 * @param path
	 *            路径名称
	 * @return boolean 是否存在，或无法创建
	 */
	public static boolean hasFolder(String path) {
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (!file.exists()) {
				// 创建多级目录；
				return file.mkdirs();
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 创建文件夹，支持创建多级目录；
	 * 
	 * @param path
	 *            路径名称
	 * @return boolean 是否创建成功
	 */
	public static boolean createFolder(String path) {
		if (StringUtil.isNotNull(path)
				&& !"null".equals(path.substring(path.lastIndexOf("/") + 1))) {
			File file = new File(path);
			if (!file.exists()) {
				// 创建多级目录；
				return file.mkdirs();
			}
		}
		return false;
	}

	/**
	 * 创建文件；
	 * 
	 * @param path
	 *            路径名称
	 * @return boolean 是否创建成功
	 * @throws IOException
	 */
	public static boolean createFile(String path) throws IOException {
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (!file.exists()) {
				// 如果不存在，创建新文件；
				return file.createNewFile();
			}
		}
		return false;
	}

	/**
	 * 删除文件或文件夹；
	 * 
	 * @param path
	 *            路径名称
	 */
	public static void delete(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {// 删除文件前重命名。
				// 可能是系统bug，见http://stackoverflow.com/questions/11539657/open-failed-ebusy-device-or-resource-busy
				File to = new File(file.getAbsolutePath()
						+ System.currentTimeMillis());
				file.renameTo(to);
				to.delete();
			} else if (file.isDirectory()) {
				File[] files = file.listFiles();
                if(files != null){
                    int len = files.length;
                    for (int i = 0; i < len; i++) {
                        delete(files[i].getAbsolutePath());
                    }
                    file.delete();
                }
			}
		}
	}

	/**
	 * 过滤文件;
	 * 
	 * @param extension
	 *            后缀
	 * @return 根据后缀过滤文件
	 */
	public static FilenameFilter getFileExtensionFilter(String extension) {
		final String _extension = extension;
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				return name.endsWith(_extension);
			}
		};
	}

	/**
	 * 
	 * @Title: resetFile
	 * @Description: TODO(恢复音视频文件)
	 * @param: @param path
	 * @return: void
	 */
	public static boolean resetFile(String path) {
		// 将完整文件的开头1024清空；
		boolean isOK = false;
		if (path != null && !"".equals(path)) {
			RandomAccessFile raf = null;
			File file = null;
			try {
				file = new File(path);
				if (file.exists() && file.canWrite() && file.isFile()) {
					raf = new RandomAccessFile(file, "rw");
					raf.seek(0);
					raf.write(new byte[1024]);
					raf.close();
					path = null;
					isOK = true;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isOK;
	}

	/**
	 * 重命名，需要是文件的具体路径。比如/sdcard/cdel/target.txt
	 * 
	 * @Title: renameFile
	 * @param: @param path 需要命名的文件
	 * @param: @param filePath 修改后的文件名
	 * @return: void
	 */
	public static boolean renameFile(String source, String target) {
		if (StringUtil.isNotNull(source) && StringUtil.isNotNull(target)) {
			File oldF = new File(source);
			File newF = new File(target);
			return oldF.renameTo(newF);
		}
		return false;
	}

	/**
	 * 
	 * @Title: getFileByte
	 * @Description: 读取文件的字节
	 * @param: @param file
	 * @param: @return
	 * @return: byte[]
	 */
	public static byte[] getFileByte(File file) {
		byte[] bytIn = null;
		if (file.exists() && file.isFile()) {
			FileInputStream rais = null;
			try {
				rais = new FileInputStream(file);
				// 读取加密文件的长度；
				int enLen = rais.available();
				byte[] buffer = new byte[enLen];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int l = rais.read(buffer);
				baos.write(buffer, 0, l);
				bytIn = baos.toByteArray();
				rais.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytIn;
	}

	/**
	 * 将字符串写入文件，或加密
	 * 
	 * @Title: writeFile
	 * @param: @param path
	 * @param: @param value
	 * @param: @param key
	 * @param: @return
	 * @return: boolean
	 */
	public static boolean writeFile(String path, String value, String key) {
		boolean isok = false;
		if (StringUtil.isNotNull(path) && StringUtil.isNotNull(value)) {
			File file = new File(path);
			try {
                if (file.exists()){
                    file.delete();
                }else{
                    file.createNewFile();
                }
				String newResult;
				if (StringUtil.isNotNull(key)) {
					newResult = AES.encrypt(key, value);
				} else {
					newResult = value;
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(newResult.getBytes());
				fos.flush();
				fos.close();
				isok = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isok;
	}


    public static String readFile(String path, String key){
        String res = "";
        if (StringUtil.isNotNull(path)){
            File file = new File(path);
            byte[] srouce = getFileByte(file);
            if (srouce != null && srouce.length > 0){
                if (StringUtil.isNotNull(key)) {
                    try {
                        res = new String(AES.decrypt(key, new String(srouce)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    res = srouce.toString();
                }
            }
        }

        return res;
    }

	/**
	 * 
	 * @Title: chmod
	 * @Description: 修改文件权限
	 * @param: @param permission
	 * @param: @param path
	 * @return: void
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫描文件
	 * 
	 * @Title scanFile
	 * @param @param context
	 * @param @param file
	 * @return void
	 */
	public static void scanFile(Context context, String file) {
		if (context != null && StringUtil.isNotNull(file)) {
			try {
				Uri data = Uri.parse("file://" + file);
				context.sendBroadcast(new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 扫描文件夹
	 * 
	 * @Title scanFolder
	 * @param @param context
	 * @param @param path
	 * @return void
	 */
	public static void scanFolder(Context context, String path) {
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (file.isDirectory()) {
				File[] array = file.listFiles();
				for (int i = 0; i < array.length; i++) {
					File f = array[i];
					if (f.isFile()) {// FILE TYPE
						String name = f.getName();
						if (name.contains(".mp4") || name.contains(".jpg")
								|| name.contains(".png")) {
							scanFile(context, f.getAbsolutePath());
						}
					} else {// FOLDER TYPE
						scanFile(context, f.getAbsolutePath());
					}
				}
			}
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param @param fromFile
	 * @param @param toFile
	 * @param @return
	 * @return boolean
	 */
	public static boolean copyFile(File fromFile, File toFile) {
		try {
			InputStream is = new FileInputStream(fromFile);
			FileOutputStream fos = new FileOutputStream(toFile);
			byte[] buffer = new byte[7168];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
