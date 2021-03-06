package com.qa.utilities;

import com.qa.framework.TestLogger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

    private static String s_JobName() {
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
        return jobName;
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
	    logger.setJobName(s_JobName());
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
            HttpURLConnection.setFollowRedirects(true);
            httpURLConnection.setRequestProperty("accept", "*/*");
			httpURLConnection.setRequestProperty("connection", "close");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
			httpURLConnection.setInstanceFollowRedirects(true);
            if (!authCode.isEmpty()) httpURLConnection.setRequestProperty("Authorization", "Bearer " + authCode);
			// 建立实际的连接
            logger.info("发送请求: ===>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: ===>>  " + param);
            long start = System.currentTimeMillis();
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())); //connection
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            long end = System.currentTimeMillis();
            logger.info("等待回应: <<===  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) { // now only available for redirect on qiye wechat
                // get redirect url from "location" header field
                String newUrl = httpURLConnection.getHeaderField("Location");
                // open the new https connection
                logger.info("Redirect to URL : " + newUrl);
                if(newUrl.startsWith("https:"))
                    result = HttpsRequest.s_DoGet(newUrl);
                else
                    result = s_SendGet(newUrl, "", authCode);
            }
        } catch (Exception e) {
		    logger.error(e.getMessage());
        }
		return result;
	}

    public static String s_SendGet(String url, HashMap<String,String> query, String authCode) {
        String param = queryBuilder(query);
        return s_SendGet(url, param, authCode);
    }

    public static String s_SendGet(String url, String param, String authCode, HashMap<String,String> pathValue) {
        String urlNameString;
        if (pathValue != null )
            urlNameString = restUrl(url, pathValue);
        else
            urlNameString = url;
        return s_SendGet(urlNameString, param, authCode);
    }

    public static String s_SendGet(String url, HashMap<String,String> query, String authCode, HashMap<String,String> pathValue) {
        String param = queryBuilder(query);
        return s_SendGet(url, param, authCode, pathValue);
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
        logger.setJobName(s_JobName());
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
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
            httpURLConnection.setRequestProperty("Referrer", "http://www.mingyizhudao.com");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer " +authCode);
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: ===>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: ===>>  " + param);
            long start = System.currentTimeMillis();
            out.print(param);// 发送请求参数
            out.flush();// flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            long end = System.currentTimeMillis();
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            logger.info("等待回应: <<===  " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage());
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
	}

    public static String s_SendPost(String url, String param, String authCode, HashMap<String,String> pathValue) {

        String urlString = restUrl(url, pathValue);
        return s_SendPost(urlString, param, authCode);
    }

    public static String s_SendPostForm(String url, String param, String authCode) {
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
            httpURLConnection.setRequestProperty("connection", "close");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("authorization", authCode);
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            long start = System.currentTimeMillis();
            out.print(param);// 发送请求参数
            out.flush();// flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            long end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + status + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static String s_SendPostText(String url, String param, String authCode) {
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
                httpURLConnection.setRequestProperty("authorization", "Bearer " + authCode);
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            long start = System.currentTimeMillis();
            out.print(param);// 发送请求参数
            out.flush();// flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            long end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + status + " " + httpURLConnection.getResponseMessage());
            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
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
        logger.setJobName(s_JobName());
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
            httpURLConnection.setRequestProperty("Cookie", "myzd="+ authCode);
            httpURLConnection.setRequestProperty("connection", "close");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+authCode);
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: ===>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: ===>>  " + param);
            long start = System.currentTimeMillis();
            out.print(param);// 发送请求参数
            out.flush();// flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            long end = System.currentTimeMillis();
            logger.info("等待回应: <<===  " + status + " " + httpURLConnection.getResponseMessage());
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
	}

    public static String s_SendPut(String url, String param, String authCode, HashMap<String,String> pathValue) {
        String urlString = restUrl(url, pathValue);
        return s_SendPut(urlString, param, authCode);
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
        logger.setJobName(s_JobName());
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
            httpURLConnection.setRequestProperty("connection", "close");
            if (!authCode.isEmpty())
                httpURLConnection.setRequestProperty("Authorization", "Bearer "+authCode);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            logger.info("发送请求: >>>>>  " + httpURLConnection.getRequestMethod() + " " + httpURLConnection.getURL());
            logger.info("请求数据: >>>>>  " + param);
            long start = System.currentTimeMillis();
            out.print(param);// 发送请求参数
            out.flush();// flush输出流的缓冲
            // 定义BufferedReader输入流来读取URL的响应
            int status = httpURLConnection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK)
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
            long end = System.currentTimeMillis();
            logger.info("等待回应: <<<<<  " + status + " " + httpURLConnection.getResponseMessage());
//            logger.info("响应时间: <<<<<  " + Long.toString(end-start) + " ms");
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            in.close();
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
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
        if (query == null) return "";
        StringBuffer sb=new StringBuffer();
        for (String key:query.keySet()) {
            try {
                if (!query.get(key).isEmpty() && query.get(key) != null)
                    sb.append(key).append("=").append(URLEncoder.encode(query.get(key), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sb.length()>0) sb.deleteCharAt(sb.length()-1);
        return new String(sb);
    }

}
