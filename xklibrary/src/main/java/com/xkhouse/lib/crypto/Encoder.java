package com.xkhouse.lib.crypto;

/**
 * Encoder类实现编解码字符串，用于解决因编码导致的乱码问题；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class Encoder {

	/**
	 * 编码；
	 * 
	 * @param src
	 *            原始字符串
	 * @return　编码后字符串
	 */
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		try {
			int size = src.length();
			tmp.ensureCapacity(size * 6);
			for (i = 0; i < size; i++) {
				j = src.charAt(i);
				if (Character.isDigit(j) || Character.isLowerCase(j)
						|| Character.isUpperCase(j))
					tmp.append(j);
				else if (j < 256) {
					tmp.append("%");
					if (j < 16)
						tmp.append("0");
					tmp.append(Integer.toString(j, 16));
				} else {
					tmp.append("%u");
					tmp.append(Integer.toString(j, 16));
				}
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return tmp.toString();
	}

	/**
	 * 解码；
	 * 
	 * @param src
	 *            被编码字符串
	 * @return　原始字符串
	 */
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		try {
			while (lastPos < src.length()) {
				pos = src.indexOf("%", lastPos);
				if (pos == lastPos) {
					if (src.charAt(pos + 1) == 'u') {
						try {
							String s = src.substring(pos + 2, pos + 6);
							lastPos = pos + 6;
							// 针对特殊字符转义失败的bug修改，2012-10-30，add by tanhangchang
							if (s.contains("%")) {
								s = src.substring(pos + 2, pos + 5);
								lastPos = pos + 5;
							}
							ch = (char) Integer.parseInt(s, 16);
							tmp.append(ch);
						} catch (Exception e) {
							lastPos = pos + 6;
						}
					} else {
						ch = (char) Integer.parseInt(
								src.substring(pos + 1, pos + 3), 16);
						tmp.append(ch);
						lastPos = pos + 3;
					}
				} else {
					if (pos == -1) {
						tmp.append(src.substring(lastPos));
						lastPos = src.length();
					} else {
						tmp.append(src.substring(lastPos, pos));
						lastPos = pos;
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp.toString();
	}
}
