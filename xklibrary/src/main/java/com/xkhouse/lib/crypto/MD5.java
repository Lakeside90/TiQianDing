/*
 *
 *@作者 nieshuting
 *@创建日期 2011-6-22下午06:03:30
 *@所有人 CDEL
 *@文件名 MD5.java
 *@包名 org.cdel.chinaacc.phone.help
 */

package com.xkhouse.lib.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.xkhouse.frame.log.Logger;

/**
 * MD5用以提供消息的完整性保护，用于向服务器发送数据时验证；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class MD5 {

	private static final String TAG = "MD5";

	/**
	 * 生成MD5字符，适用于java和php；
	 * 
	 * @param s
	 * @return 加密后的字符
	 */
	public static String getMD5(String s) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(s.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();
			StringBuffer buf = new StringBuffer();
			int len = byteArray.length;
			for (int i = 0; i < len; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
					buf.append("0").append(
							Integer.toHexString(0xFF & byteArray[i]));
				} else {
					buf.append(Integer.toHexString(0xFF & byteArray[i]));
				}
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, e.toString());
		}
		return null;
	}

	/**
	 * 生成MD5字符，适用于asp；
	 * 
	 * @param s
	 *            　
	 * @return 生成的字符
	 */
	public static String getMD5Asp(String s) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(s.getBytes());
			byte b[] = messageDigest.digest();
			int i;
			int len = b.length;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < len; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, e.toString());
		}
		return null;
	}

	/**
	 * 用于图片md5 AskActivity 类中
	 * 
	 * @param plainText
	 * @param num
	 * @return
	 */
	public static String md54Picture(String plainText, int num) {
		StringBuffer buf = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
			e.printStackTrace();
		}
		if (num == 16) {
			return buf.toString().substring(8, 24);
		} else {
			return buf.toString();
		}

	}
}
