package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class UploadToken extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/common/uploadToken";

    @Test
    public void test_01_获取type6的图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "6");
        query.put("fileName", "当朝.jpg");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_04_获取PNG文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "6");
        query.put("fileName", "abcd.png");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("fileName", "abcdefg.PNG");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_05_获取GIF文件名正常图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "6");
        query.put("fileName", "abcd.gif");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("fileName", "abcd.GIF");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_06_获取文件名有特殊字符图片token_成功() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "6");
        query.put("fileName", "abcd!-+ _.png");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

}
