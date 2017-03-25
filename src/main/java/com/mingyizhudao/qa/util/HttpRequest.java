package com.mingyizhudao.qa.util;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    public static Logger logger = Logger.getLogger(HttpRequest.class);

    public static void main(String[] args) {

        String url = "http://rap.mingyizhudao.com/mockjs/1/hospital/search";
        String para = "nameStr=ddd";
        try {
            String res = sendGet(url, para, "xxx");
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL所代表远程资源的响应结果，类型为String
     */
	public static String sendGet(String url, String param, String authCode) throws IOException {
	    String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (!param.isEmpty()) urlNameString = urlNameString.concat("?").concat(URLEncoder.encode(param, "utf-8"));
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "close");
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
			// 建立实际的连接
            long start,end;
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
			httpURLConnection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}

//			logger.debug(httpURLConnection.getRequestProperties().keySet());
			in.close();
		} catch (IOException e) {
			logger.error("发送请求异常");
            throw e;
		}
		return result;
	}

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数是JsonString的形式。
     * @return 所代表远程资源的响应结果，JSON转换的String
     */
    public static String sendPost(String url, String param, String authCode) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("connection", "close");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
            // 发送POST请求必须设置如下两行
            long start,end;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.debug("发送请求异常");
            throw e;
        }
        return result;
	}

    /**
     * 向指定 URL 发送PUT方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数是JsonString的形式。
     * @return 所代表远程资源的响应结果，JSON转换的String
     */
    public static String sendPut(String url, String param, String authCode) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
            // 发送POST请求必须设置如下两行
            long start,end;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error("发送请求异常");
            throw e;
//            e.printStackTrace();
        }
        return result;
	}


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数是FORM表单的形式。
     * @return 所代表远程资源的响应结果，JSON转换的String
     */
    public static String sendPostForm(String url, String param, String authCode) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
            // 发送POST请求必须设置如下两行
            long start,end;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error("发送请求异常");
//            e.printStackTrace();
            throw e;
        }
        return result;
	}

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数是Text文本的形式。
     * @return 所代表远程资源的响应结果，JSON转换的String
     */
    public static String sendPostText(String url, String param, String authCode) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "text/plain");//文本形式
            httpURLConnection.setRequestProperty("connection", "close");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
            // 发送POST请求必须设置如下两行
            long start,end;
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
//            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error("发送请求出现异常！");
            throw e;
        }
        return result;
	}
}
