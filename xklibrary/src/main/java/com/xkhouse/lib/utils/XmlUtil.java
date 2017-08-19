/*
 *
 *@作者 nieshuting
 *@创建日期 2011-7-20上午09:33:45
 *@所有人 CDEL
 *@文件名 DomUtil.java
 *@包名 org.cdel.chinaacc.phone.util
 */

package com.xkhouse.lib.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * XmlUtil类用于字符串与Document的转换；
 * 
 * @author nieshuting
 * @version 0.1
 */
public class XmlUtil {

	/**
	 * 将字符串转换为xml的Document对象
	 * 
	 * @param str
	 *            指定要转换的字符串
	 * @return Document Document对象 or null
	 */
	public static Document parseString(String str) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			return builder.parse(new ByteArrayInputStream(str.getBytes()));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将输入流转换为字符串
	 * 
	 * @param is
	 *            指定要转换的输入流
	 * @return String 字符串
	 */
	public static String parseInputStream(InputStream is) {
		String content = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			content = buffer.toString();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 将输入流转换为字符串
	 * 
	 * @param is
	 *            指定要转换的输入流
	 * @return String 字符串
	 */
	public static String parseInputStream1(InputStream is) {
		String content = "";
		byte[] b = new byte[1024];
		int len = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			while ((len = is.read(b)) != -1) {
				out.write(b, 0, len);
			}
			content = new String(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 将流转换为字节
	 * 
	 * @param inStream
	 *            输入流
	 * @throws Exception
	 * @return 转换后的字节
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 转换字符串为Document对象
	 * 
	 * @param str
	 *            字符串
	 * @return Document Document对象 or null
	 */
	public static Document getDocument(String str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(str
					.getBytes()));
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: getDocument
	 * @Description:转换流为Document对象
	 * @param: @param in
	 * @param: @return
	 * @return: Document
	 * @throws
	 */
	public static Document getDocument(InputStream in) {
		Document dom = null;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			dom = db.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dom;
	}
}
