package com.mingyizhudao.qa.utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpsRequest {

    public static void main(String[] args) {
        try {
            System.out.print(doGet("https://work.myzd.info/wx/internal/api/dev-tokens"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String doPost(String url,String jsonstr,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("application/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static String doGet(String url) throws Exception {
        HttpClient httpClient;
        HttpGet httpGet;
        String result = null;
        BufferedReader in;
        try{
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null && response.getEntity() != null){
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;
                while ((line = in.readLine()) != null) {
                    result += line;
                }
                in.close();
            }
        }catch(Exception ex){
            throw ex;
        }
        return result;
    }
}


/**
 * 用于进行Https请求的HttpClient
 * @ClassName: SSLClient
 * @Description: TODO
 * @author Devin <xxx>
 * @date 2017年2月7日 下午1:42:07
 *
 */
class SSLClient extends DefaultHttpClient {
    public SSLClient() throws Exception{
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
        SSLSocketFactory ssf = new SSLSocketFactory(ctx,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager ccm = this.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", 443, ssf));
    }
}
