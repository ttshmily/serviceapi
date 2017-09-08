package com.mingyizhudao.qa.utilities;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

    public static TestLogger logger = new TestLogger();

    public static void main(String[] args) {

/*        String url = "http://rap.mingyizhudao.com/mockjs/1/hospital/{search}";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("search", "3");
        map.put("search2", "4");
        System.out.println(restUrl(url, map));
        System.out.println(queryBuilder(map));
        try {
            String res = s_SendGet(url, para, "xxx");
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
	public static String s_SendGet(String url, String param, String authCode) {

        String jobName = "";
	    StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if (s.getMethodName().startsWith("s_")) {
                continue;
            } else {
                jobName = s.getClassName();
                break;
            }
        }
	    logger.setJobName(jobName);
	    String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
            if (param != null && !param.isEmpty()) urlNameString = urlNameString.concat("?").concat(param);
            URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "close");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
			httpURLConnection.setInstanceFollowRedirects(true);
			HttpURLConnection.setFollowRedirects(true);
            if (!authCode.isEmpty()) httpURLConnection.setRequestProperty("Authorization", "Bearer " + authCode);
			// 建立实际的连接
            long start,end;
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            start = System.currentTimeMillis();
			httpURLConnection.connect();
            int status = httpURLConnection.getResponseCode();
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())); //connection
            end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            if (status == HttpURLConnection.HTTP_OK) {
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } else {
                String line;
                while ((line = in.readLine()) != null) {
                    logger.info(line);
                }
            }
            in.close();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) { // now only available for redirect on qiye wechat
                // get redirect url from "location" header field
                String newUrl = httpURLConnection.getHeaderField("Location");
                // open the new https connection
                logger.info("Redirect to URL : " + newUrl);
                result = HttpsRequest.s_DoGet(newUrl);
//                logger.info("等待回应: <<<<<  " + httpsURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
//                logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            }
		} catch (IOException e) {
			logger.error("发送请求异常");
		} catch (NoSuchAlgorithmException e) {
            logger.error("发送HTTPS请求异常");
        } catch (KeyManagementException e) {
            logger.error("发送HTTPS请求异常");
        } catch (Exception e) {
		    logger.error("发送请求异常");
        }
		return result;
	}

    public static String s_SendGet(String url, HashMap<String,String> query, String authCode) {
        String result = "";
        String param = "";
        try {
            if (query != null ) {
                param = queryBuilder(query);
            }
            result = s_SendGet(url, param, authCode);
        } catch (Exception e) {
            logger.error("发送请求异常");
        }
        return result;
    }

    public static String s_SendGet(String url, String param, String authCode, HashMap<String,String> pathValue) {
        String result = "";
        try {
            String urlNameString;
            if (pathValue != null ) {
                urlNameString = restUrl(url, pathValue);
            } else {
                urlNameString = url;
            }
            result = s_SendGet(urlNameString, param, authCode);

        } catch (Exception e) {
            logger.error("发送请求异常");
        }
        return result;
    }

    public static String s_SendGet(String url, HashMap<String,String> query, String authCode, HashMap<String,String> pathValue) {
        String result = "";
        String param = "";
        BufferedReader in = null;
        try {
            String urlNameString;
            if (pathValue != null ) {
                urlNameString = restUrl(url, pathValue);
            } else {
                urlNameString = url;
            }
            if (query != null ) {
                param = queryBuilder(query);
            }
            result = s_SendGet(urlNameString, param, authCode);

        } catch (Exception e) {
            logger.error("发送请求异常");
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
    public static String s_SendPost(String url, String param, String authCode) {

        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if (s.getMethodName().startsWith("s_")) {
                continue;
            } else {
                jobName = s.getClassName();
                break;
            }
        }
        logger.setJobName(jobName);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpURLConnection httpURLConnection;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("connection", "close");
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
            httpURLConnection.setRequestProperty("Referer", "http://www.mingyizhudao.com");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer " +authCode);
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
            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            end = System.currentTimeMillis();
            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error("发送请求异常");
        }
        return result;
	}

    public static String s_SendPost(String url, String param, String authCode, HashMap<String,String> pathValue) {

        String result = "";
        try {
            String urlString = restUrl(url, pathValue);
            result = s_SendPost(urlString, param, authCode);
        } catch (Exception e) {
            logger.error("发送请求异常");
        }
        return result;
    }

    public static String s_SendPostForm(String url, String param, String authCode) throws IOException {
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

    public static String s_SendPostText(String url, String param, String authCode) throws IOException {
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

    /**
     * 向指定 URL 发送PUT方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数是JsonString的形式。
     * @return 所代表远程资源的响应结果，JSON转换的String
     */

    public static String s_SendPut(String url, String param, String authCode) {

        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if (s.getMethodName().startsWith("s_")) {
                continue;
            } else {
                jobName = s.getClassName();
                break;
            }
        }
        logger.setJobName(jobName);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpURLConnection httpURLConnection;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+authCode);
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
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error("发送请求异常" + e);
        }
        return result;
	}

    public static String s_SendPut(String url, String param, String authCode, HashMap<String,String> pathValue) {

        String result = "";
        try {
            String urlString;
            if (pathValue != null ) {
                urlString = restUrl(url, pathValue);
            } else {
                urlString = url;
            }
            result = s_SendPut(urlString, param, authCode);
        } catch (Exception e) {
            logger.error("发送请求异常");
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

    public static String s_SendDelete(String url, String param, String authCode, HashMap<String,String> pathValue) {
        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if (s.getMethodName().startsWith("s_")) {
                continue;
            } else {
                jobName = s.getClassName();
                break;
            }
        }
        logger.setJobName(jobName);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            String urlNameString;
            if (pathValue != null ) {
                urlNameString = restUrl(url, pathValue);
            } else {
                urlNameString = url;
            }
            URL realUrl = new URL(urlNameString);
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+authCode);
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
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

            in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error("发送请求异常");
        }
        return result;
    }

    /**
     * 将restful URL中的资源位替换成资源名称
     *
     * @param url
     *            发送请求的 RESTFUL URL
     * @param pathValue
     *            资源map
     *
     * @return 替换资源名称后的URL
     */

	private static String restUrl(String url, HashMap<String, String> pathValue) {

        String regex = "^\\{([a-zA-Z_]+)}";
        Pattern p = Pattern.compile(regex);
        Matcher m;
        String[] s = url.split("/");
        for(int i=0; i < s.length; i++) {
            m = p.matcher(s[i]);
            if (m.find()) {
                s[i] = pathValue.get(m.group(1));
            }
        }

        StringBuffer sb=new StringBuffer();
        for(int i=0;i<s.length;i++){
            if(i==(s.length-1)){
                sb.append(s[i]);
            }else{
                sb.append(s[i]).append("/");
            }
        }
        return new String(sb);
    }

    /**
     * 将<k,v>对转换为HTTP的Query String
     *
     * @param query
     *          query parameter所对应的键值对
     *
     * @return 所代表远程资源的响应结果，JSON转换的String
     */

    private static String queryBuilder(HashMap<String,String> query) {
        StringBuffer sb=new StringBuffer();
        if (query == null) return "";
        for (String key:query.keySet()) {
            try {
                //if (!query.get(key).isEmpty())
                    sb.append(key).append("=").append(URLEncoder.encode(query.get(key), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
        return new String(sb);
    }

}
