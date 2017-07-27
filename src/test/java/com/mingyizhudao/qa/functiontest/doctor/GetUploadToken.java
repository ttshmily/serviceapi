package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 9/4/2017.
 */
public class GetUploadToken extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetUploadToken.class);
    public static String uri = "/api/getuploadtoken";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取type1的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "当朝.jpg");
        try {
            res = HttpRequest.sendGet(host_doc+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_获取type2的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "2");
        query.put("filename", "abcd!@#$%^&*().jpg");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "token"));
        Assert.assertEquals(code, "1000000");

    }

    @Test
    public void test_03_获取type3的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "3");
        query.put("filename", "abcd!@#$%^&*().jpg");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_04_获取PNG文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.png");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcdefg.PNG");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_05_获取GIF文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.gif");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcd.GIF");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_06_获取文件名有特殊字符图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~.png");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "+_)(*&^%$#@!.PNG");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Ignore
    public void 获取文件名无后缀名图片token_失败() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~aa");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
