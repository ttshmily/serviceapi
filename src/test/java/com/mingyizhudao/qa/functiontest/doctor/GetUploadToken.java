package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 9/4/2017.
 */
public class GetUploadToken extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getuploadtoken";

    @Test
    public void test_01_获取type1的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "当朝.jpg");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_获取type2的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "2");
        query.put("filename", "abcd!@#$%^&*().jpg");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");

    }

    @Test
    public void test_03_获取type3的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "3");
        query.put("filename", "abcd!@#$%^&*().jpg");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_04_获取PNG文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.png");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcdefg.PNG");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_05_获取GIF文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.gif");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcd.GIF");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_06_获取文件名有特殊字符图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~.png");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "+_)(*&^%$#@!.PNG");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Ignore
    public void 获取文件名无后缀名图片token_失败() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~aa");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
