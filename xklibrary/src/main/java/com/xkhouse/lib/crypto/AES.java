package com.xkhouse.lib.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.xkhouse.frame.log.Logger;
import com.xkhouse.lib.utils.StringUtil;
import com.xkhouse.lib.utils.XmlUtil;


/**
 * AES高级加密标准,又称Rijndael加密法，是一种区块加密标准。
 * AES类用于下载和导入模块中加密音视频的头部1024字节，加密后的长度为1040字节。
 * 
 * @author nieshuting
 * @version 0.2
 */
public class AES {
	private final static String HEX = "0123456789ABCDEF";

	private final static String TAG = "AES";

	/**
	 * 加密字符串；
	 * 
	 * @param seed
	 *            密钥
	 * @param str
	 *            需要加密的字符串
	 * @return 加密后的String类型数据
	 * @throws Exception
	 */
	public static String encrypt(String seed, String str)
			throws BadPaddingException, Exception {
		if (StringUtil.isEmpty(seed) || StringUtil.isEmpty(str)) {
			throw new Exception();
		}
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = encryptByte(rawKey, str.getBytes());
		return toHex(result);
	}

	/**
	 * 加密字符串；
	 * 
	 * @param seed
	 *            密钥
	 * @param str
	 *            需要加密的字符串
	 * @return 加密后的String类型数据
	 * @throws Exception
	 */
	public static byte[] encrypt(String seed, byte[] rawInput)
			throws BadPaddingException, Exception {
		if (StringUtil.isEmpty(seed)) {
			throw new Exception();
		}
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = encryptByte(rawKey, rawInput);
		return result;
	}

	/**
	 * 解密字符串；
	 * 
	 * @param seed
	 *            密钥
	 * @param str
	 *            需要解密的字符串
	 * @return 解密完成的字符串
	 * @throws Exception
	 */
	public static String decrypt(String seed, String str) throws Exception {
		if (StringUtil.isEmpty(seed) || StringUtil.isEmpty(str)) {
			throw new Exception();
		}
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] enc = toByte(str);
		byte[] result = decryptByte(rawKey, enc);
		return new String(result);
	}

	/**
	 * 解密字符串；
	 * 
	 * @param seed
	 *            密钥
	 * @param str
	 *            需要解密的字符串
	 * @return 解密完成的字符串
	 * @throws Exception
	 */
	public static byte[] decrypt(String seed, byte[] rawInput) throws Exception {
		if (StringUtil.isEmpty(seed)) {
			throw new Exception();
		}
		byte[] rawKey = getRawKey(seed.getBytes());
		byte[] result = decryptByte(rawKey, rawInput);
		return result;
	}

	/**
	 * 加密视频文件，用于下载模块；
	 * 
	 * @param is
	 *            从服务器获取的流；
	 * @param fileOut
	 *            加密后的文件；
	 * @param key
	 *            本地密钥，一般指android_id；
	 * @return 执行状态，true表示加密成功；
	 */
	public static boolean encryptFileWithDownload(InputStream is, File fileOut,
			String key) {
		if (StringUtil.isEmpty(key) || fileOut == null) {
			return false;
		}
		int numread = 0;
		try {
			fileOut.createNewFile();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RandomAccessFile encodeFile = new RandomAccessFile(fileOut, "rwd");
			byte[] buffer = new byte[1024];
			if ((numread = is.read(buffer)) > 0) {
				baos.write(buffer, 0, numread);
				byte[] bytIn = baos.toByteArray();
				byte[] bytOut = encrypt(key, bytIn);
				// 加密后得到的字节数， 把字节个数整形转换成4个字节数组
				encodeFile.seek(0);
				encodeFile.write(bytOut);
				encodeFile.close();
				baos.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, e.toString());
			return false;
		}
	}

	/**
	 * 加密视频文件，用key解密后，再用本地密钥加密，用于导入模块；
	 * 
	 * @param fileIn
	 *            待加密文件；
	 * @param fileOut
	 *            被加密文件；
	 * @param key_server
	 *            服务端密钥；
	 * @param key_android_id
	 *            本地密钥；
	 * @return 执行状态，true表示加密成功；
	 */
	public static boolean encryptFileWithImport(File fileIn, File fileOut,
			String key_server, String key_android_id) {
		if (StringUtil.isEmpty(key_server) || fileOut == null) {
			return false;
		}
		String encryptPHPString = null;
		String decryptString = null;
		byte[] bytIn = null;
		try {
			// 读取加密的视频文件；
			encryptPHPString = XmlUtil.parseInputStream(new FileInputStream(
					fileIn));
			// 使用key解密；
			decryptString = DES.decryptWithNo(encryptPHPString, key_server);
			byte[] b = Base64.decode(decryptString);
			ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
			baos0.write(b, 0, b.length);
			bytIn = baos0.toByteArray();
			// 使用设备id加密；
			fileOut.createNewFile();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RandomAccessFile encodeFile = new RandomAccessFile(fileOut, "rwd");
			byte[] bytOut = encrypt(key_android_id, bytIn);
			// 加密后得到的字节数，把字节个数整形转换成4个字节数组
			encodeFile.seek(0);
			encodeFile.write(bytOut);
			encodeFile.close();
			baos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.e(TAG, e.toString());
			return false;
		}
	}

	/**
	 * 解密视频文件，用于播放器模块；
	 * 
	 * @param fileIn
	 *            待加密文件；
	 * @param fileOut
	 *            被加密文件；
	 * @param key_android_id
	 *            android系统id；
	 * @return 执行状态，true表示解密成功；
	 */
	public static boolean decryptFile(File fileIn, RandomAccessFile fileOut,
			String key_android_id) throws BadPaddingException, Exception {
		if (StringUtil.isEmpty(key_android_id) || fileOut == null) {
			throw new Exception();
		}
		byte[] bytOut = null;
		byte[] bytIn = null;
		FileInputStream rais = null;
		// 解密的长度；
		int deLen = 0;
		// 加密的长度；
		int enLen = 0;
		// 解密状态；
		boolean isOk = false;
		rais = new FileInputStream(fileIn);
		// 读取加密文件的长度；
		enLen = rais.available();
		byte[] buffer = new byte[enLen];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int l = rais.read(buffer);
		baos.write(buffer, 0, l);
		bytIn = baos.toByteArray();
		bytOut = decrypt(key_android_id, bytIn);
		if (bytOut != null) {
			deLen = bytOut.length;
			if (deLen == 1024)
				isOk = true;
		}
		boolean isDecryptSuccess = false;
		// 解密完成后，将其写入文件；
		if (isOk && deLen == 1024) {
			// 长度等于1024才能写入，否则，导致文件损坏；
			fileOut.seek(0);
			fileOut.write(bytOut);
			isDecryptSuccess = true;
		}
		fileOut.close();
		rais.close();
		return isDecryptSuccess;
	}

	/**
	 * 获取密钥的字节；
	 * 
	 * @param seed
	 *            密钥
	 * @return 加密后的byte类型数据
	 * @throws Exception
	 */
	public static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = null;
		// add by huangyu ，2012-12-20，解决4.2设备加密解密失败问题
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		} else {
			sr = SecureRandom.getInstance("SHA1PRNG");
		}
		sr.setSeed(seed);
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	/**
	 * 加密字节
	 * 
	 * @param rawKey
	 *            密钥字节
	 * @param rawInput
	 *            输入字节
	 * @return
	 * @throws BadPaddingException
	 * @throws Exception
	 *             byte[]
	 */
	private static byte[] encryptByte(byte[] rawKey, byte[] rawInput)
			throws BadPaddingException, Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(rawInput);
		return encrypted;
	}

	/**
	 * 解密字节
	 * 
	 * @param rawKey
	 *            密钥字节
	 * @param rawInput
	 *            输入字节
	 * @return
	 * @throws Exception
	 *             byte[]
	 */
	private static byte[] decryptByte(byte[] rawKey, byte[] rawInput)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(rawInput);
		return decrypted;
	}

	/**
	 * 将hex转换为字节
	 * 
	 * @param hexString
	 * @return byte[]
	 */
	private static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	/**
	 * 转换为hex
	 * 
	 * @param buf
	 * @return String
	 */
	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	/**
	 * 添加hex
	 * 
	 * @param sb
	 * @param b
	 *            void
	 */
	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}

}
