package com.mingyizhudao.qa.utilities;

import com.mingyizhudao.qa.common.TestLogger;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

    public static TestLogger logger = new TestLogger();

    public static void main(String[] args) {

//        String url = "http://rap.mingyizhudao.com/mockjs/1/hospital/{search}";
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("search", "3");
//        map.put("search2", "4");
//        System.out.println(restUrl(url, map));
//        System.out.println(queryBuilder(map));
//        try {
//            String res = sendGet(url, para, "xxx");
//            System.out.println(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

        String jobName = "";
	    StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if ("sendGet".equals(s.getMethodName()) || "init".equals(s.getMethodName())) {
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
		    HttpsURLConnection httpsURLConnection;
			String urlNameString = url;
            if (param != null && !param.isEmpty()) urlNameString = urlNameString.concat("?").concat(param);
            URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
			// 设置通用的请求属性
			httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "close");
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
            if (status == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())); //connection
                end = System.currentTimeMillis();
                logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
                logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            }
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) { // now only available for redirect on qiye wechat
                // get redirect url from "location" header field
                String newUrl = httpURLConnection.getHeaderField("Location");
                // get the cookie if need, for login
                String cookies = httpURLConnection.getHeaderField("Set-Cookie");

                X509TrustManager trustManager = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // Don't do anything.
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // Don't do anything.
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        // Don't do anything.
                        return null;
                    }
                };
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, new TrustManager[]{trustManager}, null);
                // 从上述SSLContext对象中得到SSLSocketFactory对象
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                // open the new https connection
                httpsURLConnection = (HttpsURLConnection) new URL(newUrl).openConnection();
                httpsURLConnection.setSSLSocketFactory(ssf);
                httpsURLConnection.setRequestProperty("Accept", "*/*");
                httpsURLConnection.setRequestProperty("Connection", "keep-alive");
                httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                httpsURLConnection.setRequestProperty("Cookie", cookies);
                logger.info("Redirect to URL : " + newUrl);
                httpsURLConnection.connect();
                in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); //connection
                end = System.currentTimeMillis();
                logger.info("等待回应: <<<<<  " + httpsURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
                logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            }
//            in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); //connection
//			end = System.currentTimeMillis();
//            logger.info("等待回应: <<<<<  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");

			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
		} catch (IOException e) {
		    e.printStackTrace();
			logger.error("发送请求异常");
		} catch (NoSuchAlgorithmException e) {
            logger.error("发送HTTPS请求异常");
        } catch (KeyManagementException e) {
            logger.error("发送HTTPS请求异常");
        }
		return result;
	}

    public static String sendGet(String url, HashMap<String,String> query, String authCode) throws IOException {
        String result = "";
        String param = "";
        try {
            if (query != null ) {
                param = queryBuilder(query);
            }
            result = sendGet(url, param, authCode);
        } catch (IOException e) {
            logger.error("发送请求异常");
            throw e;
        }
        return result;
    }

    public static String sendGet(String url, String param, String authCode, HashMap<String,String> pathValue) throws IOException {
        String result = "";
        try {
            String urlNameString;
            if (pathValue != null ) {
                urlNameString = restUrl(url, pathValue);
            } else {
                urlNameString = url;
            }
            result = sendGet(urlNameString, param, authCode);

        } catch (IOException e) {
            logger.error("发送请求异常");
            throw e;
        }
        return result;
    }

    public static String sendGet(String url, HashMap<String,String> query, String authCode, HashMap<String,String> pathValue) throws IOException {
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
            result = sendGet(urlNameString, param, authCode);

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

        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if ("sendPost".equals(s.getMethodName()) || "init".equals(s.getMethodName())) {
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
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("connection", "close");
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

    public static String sendPost(String url, String param, String authCode, HashMap<String,String> pathValue) throws IOException {

        String result = "";
        try {
            String urlString = restUrl(url, pathValue);
            result = sendPost(urlString, param, authCode);
        } catch (IOException e) {
            logger.error("发送请求异常");
            throw e;
        }
        return result;
    }

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

        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if ("sendPut".equals(s.getMethodName()) || "init".equals(s.getMethodName())) {
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
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
            // 设置通用的请求属性
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
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
        }
        return result;
	}

    public static String sendPut(String url, String param, String authCode, HashMap<String,String> pathValue) throws IOException {

        String result = "";
        try {
            String urlString;
            if (pathValue != null ) {
                urlString = restUrl(url, pathValue);
            } else {
                urlString = url;
            }
            result = sendPut(urlString, param, authCode);
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

    public static String sendDelete(String url, String param, String authCode, HashMap<String,String> pathValue) throws IOException {
        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if ("sendDelete".equals(s.getMethodName()) || "init".equals(s.getMethodName())) {
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
        for (String key:query.keySet()
             ) {
            try {
                sb.append(key).append("=").append(URLEncoder.encode(query.get(key), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
        return new String(sb);
    }

    /**
     * 对于非unicode的字符串，转换为unicode，用于打印显示
     *
     * @param strArr
     *            非unicode String
     *
     * @return unicode String
     */

    public static String unicodeString( String strArr ) {
        List<String> list	= new ArrayList<String>();
        String		zz	= "\\\\u[0-9,a-z,A-Z]{4}";
		/* 正则表达式用法参考API */
        Pattern pattern = Pattern.compile( zz );
        Matcher m = pattern.matcher( strArr );
        while ( m.find() )
        {
            list.add( m.group() );
        }
        for ( int i = 0, j = 2; i < list.size(); i++ )
        {
            String st = list.get( i ).substring( j, j + 4 );
			/* 将得到的数据按16进制解析为十进制整数，再強转为字符*/
            char ch = (char) Integer.parseInt( st, 16 );
			/* 用得到的字符替换编码表达式 */
            strArr = strArr.replace( list.get( i ), String.valueOf( ch ) );
        }
        return(strArr);
    }
}
