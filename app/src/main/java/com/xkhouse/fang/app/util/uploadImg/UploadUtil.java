package com.xkhouse.fang.app.util.uploadImg;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * 提交图片、语音的工具类
 * 
 */
public class UploadUtil {

	/**
	 * 提交图片；
	 * 
	 * @param uri 接口
	 * @param map  参数
	 * @return 返回的xml；
	 */
	public static String postImage(String uri, Map<String, String> map,
			String path) {

		String result = "";
		String MULTIPART_FORM_DATA = "multipart/form-data";
		String BOUNDARY = "---------------------------239738083042818571953359096"; // 数据分隔线
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setConnectTimeout(6000);// 6秒钟连接超时
			conn.setReadTimeout(25000);// 25秒钟读数据超时
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			// 上传的表单参数部分，格式请参考文章
			for (Entry<String, String> entry : map.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(sb.toString().getBytes());
			// =====================文件部分==========================
			dos.writeBytes("--"
					+ BOUNDARY
					+ "\r\n"
					+ "Content-Disposition: form-data; name=\"upfile\"; filename=\"a.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n");
			FileInputStream fStream = new FileInputStream(path);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				dos.write(buffer, 0, length);
			}
			dos.writeBytes("\r\n");
			// =================================================
			dos.writeBytes("--" + BOUNDARY + "--\r\n");
			dos.flush();
			fStream.close();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			result = br.readLine();
		} catch (Exception e) {
		    e.printStackTrace();
			result = "";
		}
		return result;
	}

	/**
	 * 提交语音
	 * 
	 * @param uri
	 * @param map
	 * @param path
	 * @return
	 */
	public static String postAmr(String uri, Map<String, String> map,
			String path) {

		String result = "";
		String MULTIPART_FORM_DATA = "multipart/form-data";
		String BOUNDARY = "---------------------------239738083042818571953359096"; // 数据分隔线
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setConnectTimeout(6000);// 6秒钟连接超时
			conn.setReadTimeout(25000);// 25秒钟读数据超时
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			// 上传的表单参数部分，格式请参考文章
			for (Entry<String, String> entry : map.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.write(sb.toString().getBytes());
			// =======================文件部分========================
			dos.writeBytes("--"
					+ BOUNDARY
					+ "\r\n"
					+ "Content-Disposition: form-data; name=\"media\"; filename=\"a.amr\"\r\nContent-Type: audio/amr\r\n\r\n");
			FileInputStream fStream = new FileInputStream(path);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				dos.write(buffer, 0, length);
			}
			dos.writeBytes("\r\n");
			// =================================================
			dos.writeBytes("--" + BOUNDARY + "--\r\n");
			dos.flush();
			fStream.close();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			result = br.readLine();
		} catch (Exception e) {
			result = "";
		}
		return result;
	}

	/**
	 * POST方式发送数据
	 *
	 * @method post
	 * @param api
	 *            　接口地址
	 * @param charast
	 *            　编码:默认为utf-8，
	 * @param m
	 *            参数 ： 按照map键值对方式存放。
	 * @return 字符串
	 */
	/*public static String postWithString(String api, Map<String, String> m,
			String charast) {
		if (!StringUtil.isNotNull(charast)) {
			charast = "UTF-8";
		}
		DefaultHttpClient httpclient = null;

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (m != null) {
			Iterator<Entry<String, String>> it = m.entrySet().iterator();
			Entry<String, String> map = null;
			while (it.hasNext()) {
				map = it.next();
				nvps.add(new BasicNameValuePair(map.getKey(), map.getValue()));
			}
		}
		try {

			HttpParams httpParams = new BasicHttpParams();
			// 请求超时
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			// 接收返回数据超时
			HttpConnectionParams.setSoTimeout(httpParams, 10000);

			httpclient = new DefaultHttpClient(httpParams);

			HttpPost httppost = new HttpPost(api);
			httppost.setEntity(new UrlEncodedFormEntity(nvps, charast));
			HttpResponse response = httpclient.execute(httppost);


			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				httppost.abort();
				return null;
			}
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity, HTTP.UTF_8);


			*//*InputStream is = response.getEntity().getContent();
            // 将流里面的内容转成一个文本，显示到TextView界面
			String text = readInputStream(is);
            return text;*//*

			return string;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}*/

	
	
	/**
	 * HttpURLConnection发送post请求
	 * @param api
	 * @param m
	 * @return
	 */
	public static String sendPostRequest(String api, Map<String, String> m) {

		try {
			StringBuilder params = new StringBuilder();
			for (Entry<String, String> entry : m.entrySet()) {
				params.append(entry.getKey());
				params.append("=");
				params.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
				params.append("&");
			}
			if (params.length() > 0)
				params.deleteCharAt(params.length() - 1);
			byte[] data = params.toString().getBytes();

			URL realUrl = new URL(api);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setDoOutput(true);// 发送POST请求必须设置允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Length",
					String.valueOf(data.length));
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(data);
			outStream.flush();
			if (conn.getResponseCode() == 200) {
				// 定义一个输入流，流里面就是服务器返回的信息（ username password）
				/*String result = readAsString(conn.getInputStream(), "UTF-8");
				outStream.close();
				return result;*/

				
				 InputStream is = conn.getInputStream(); 
				 //将流里面的内容转成一个文本，显示到TextView界面 
				 String text = readInputStream(is); 
				 outStream.close();
				 return text;
				 
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}
	
	
	/**
	 * 把输入流的内容 转化成 字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String readInputStream(InputStream is) {
		try {
			
		    //1，读取数据的过程
			//定义输出流
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//开始读取
			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);//写出去
			}
			is.close();
			baos.close();

			byte[] result = baos.toByteArray();// 字节转化成字符
			String temp = new String(result);// 将result转化成字符串
	
						
			//2，解决乱码的过程
			// 客户端针对解决乱码问题
			if (temp.contains("utf-8")) {
				return temp;// 如果字符串中包含utf-8就直接返回
			} else if (temp.contains("gb2312")) {
				return new String(result, "gb2312");//如果字符串中包含gb2312，就将result指定gb2312
			}
			return temp;

		} catch (Exception e) {
			e.printStackTrace();
			return "获取失败";
		}
	}
	
	
	private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    /**
     * 上传文件到服务器
     * @param path 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public static String uploadFile(String path, String RequestURL) {
    	File file = new File(path);
        int res=0;
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
 
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
 
            if (file != null) {
                /**
                 * 当文件不为空时执行上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名
                 */
 
                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                 res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    Log.e(TAG, "request success");
                    InputStream input = conn.getInputStream();
                    StringBuffer sb1 = new StringBuffer();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sb1.append((char) ss);
                    }
                    result = sb1.toString();
                    Log.e(TAG, "result : " + result);
                } else {
                    Log.e(TAG, "request error");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
