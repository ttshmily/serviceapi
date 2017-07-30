package com.mingyizhudao.qa.utilities;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsRequest {

    public static void main(String[] args) {
        try {
//            System.setProperty("javax.net.debug","ssl");
            System.out.print(s_DoGet("https://work.myzd.info/wx/internal/api/dev-tokens"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String s_DoPost(String url,String jsonstr,String charset) throws Exception {
        HttpClient httpClient;
        HttpPost httpPost;
        String result = null;
        BufferedReader in;
        try {
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("application/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null && response.getEntity() != null) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                in.close();
            }
        } catch(Exception ex){
            throw ex;
        }
        return result;
    }

    public static String s_DoGet(String url) throws Exception {
        SSLClient httpClient;
        HttpGet httpGet;
        StringBuilder result = new StringBuilder();
        BufferedReader in;
        try {
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null && response.getEntity() != null) {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
            }
        } catch(Exception ex){
            throw ex;
        }
        return result.toString();
    }
}


/**
 * 用于进行Https请求的HttpClient
 * @author Dayi <xxx>
 *
 */
class SSLClient extends DefaultHttpClient {

    SSLClient() throws Exception{
        super();
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        X509HostnameVerifier hv = new X509HostnameVerifier(){
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }

            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {

            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {

            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

            }
        };
        SSLSocketFactory ssf = new SSLSocketFactory(ctx, hv);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }

}
