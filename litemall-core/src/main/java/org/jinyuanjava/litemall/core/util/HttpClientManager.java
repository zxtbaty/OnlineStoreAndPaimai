package org.jinyuanjava.litemall.core.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpClientManager {
	/**
	 * 执行url请求数据
	 *
	 * @param urlStr
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String getUrlData(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		URLConnection connection = url.openConnection();
		// 一旦发送成功，用以下方法就可以得到服务器的回应：
		String sCurrentLine = "";
		StringBuffer sTotalString = new StringBuffer();
		InputStream l_urlStream = connection.getInputStream();
		// 传说中的三层包装阿！
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(
				l_urlStream, "UTF-8"));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString.append(sCurrentLine);
		}
		return sTotalString.toString();
	}

	/**
	 * 执行url请求数据
	 *
	 * @param urlStr
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public static String postUrlData(String urlStr, String data)
			throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("POST");
		http.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		http.setDoOutput(true);
		http.setDoInput(true);
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒28
		System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒29
																			// 30
		OutputStream os = http.getOutputStream();
		os.write(data.getBytes("UTF-8"));// 传入参数
		os.flush();
		os.close();
		// 一旦发送成功，用以下方法就可以得到服务器的回应：
		String sCurrentLine = "";
		String sTotalString = "";
		InputStream l_urlStream = http.getInputStream();
		// 传说中的三层包装阿！
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(
				l_urlStream, "UTF-8"));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			sTotalString += sCurrentLine;
		}
		return sTotalString;
	}
}
