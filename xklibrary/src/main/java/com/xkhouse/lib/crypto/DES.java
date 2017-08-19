/*
 *
 *@作者 nieshuting
 *@创建日期 2011-11-3下午05:14:25
 *@所有人 CDEL
 *@文件名 DES.java
 *@包名 com.cdel.chinaacc.phone.help
 */

package com.xkhouse.lib.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.xkhouse.lib.utils.StringUtil;

/**
 * DES数据加密标准，是一种对称加密算法，用于导入模块中解密视频路径；
 * 
 * @author nieshuting
 * @version 0.2
 */
public class DES {

	private static byte[] iv = { 'f', 'Y', 'f', 'h', 'H', 'e', 'D', 'm' };

	/**
	 * 解密，模式为DES/CBC/NoPadding；
	 * 
	 * @param decryptString
	 *            加密的字符串；
	 * @param decryptKey
	 *            密钥；
	 * @return 解密的字符串；
	 * @throws Exception
	 */
	public static String decryptWithNo(String decryptString, String decryptKey)
			throws BadPaddingException, Exception {
		if (StringUtil.isEmpty(decryptKey) || StringUtil.isEmpty(decryptString)) {
			throw new Exception();
		}
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);
		return new String(decryptedData);
	}

	/**
	 * 解密，模式为DES/CBC/PKCS5Padding；
	 * 
	 * @param decryptString
	 *            加密的字符串；
	 * @param decryptKey
	 *            密钥；
	 * @return 解密的字符串；
	 * @throws Exception
	 */
	public static String decryptWithPKCS5(String decryptString,
			String decryptKey) throws Exception {
		if (StringUtil.isEmpty(decryptKey) || StringUtil.isEmpty(decryptString)) {
			throw new Exception();
		}
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);
		return new String(decryptedData);
	}
}
