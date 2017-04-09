package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 9/4/2017.
 */
public class GetUploadToken extends BaseTest {
    public static String uri = "/api/getuploadtoken";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void 获取type为1的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "当朝.jpg");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void 获取type为2的图片token_失败() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "2");
        query.put("filename", "abcd!@#$%^&*().jpg");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 获取PNG文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.png");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcdefg.PNG");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void 获取GIF文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.gif");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcd.GIF");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void 获取文件名有特殊字符图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~.png");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "+_)(*&^%$#@!.PNG");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void 获取文件名无后缀名图片token_失败() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~aa");
        try {
            res = HttpRequest.sendGet(host+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}