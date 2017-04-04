package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class GetEmployeeProfile extends BaseTest {

    public static String uri = "/api/getemployeeprofile";
    public static String mock = false ? "/mockjs/1" : "";

    public static String getEmployeeProfile() {
        return "";
    }

    @Test
    public void 有token的用户请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"number=SH0001", mainToken);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail();
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "错误码应该是1000000");
        Assert.assertNotNull(parseJson(data, "employee:id"), "id must not be null");
        Assert.assertNotNull(parseJson(data, "employee:name"), "name must not be null");
        Assert.assertNotNull(parseJson(data, "employee:number"), "number must not be null");
    }

    @Test
    public void 没有token的用户请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"number=SH0001", "");
        } catch (IOException e) {
            logger.error(e);
            Assert.fail();
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(parseJson(data, "employee:id"), "id must not be null");
        Assert.assertNotNull(parseJson(data, "employee:name"), "name must not be null");
        Assert.assertNotNull(parseJson(data, "employee:number"), "number must not be null");
    }

    @Test
    public void 员工ID无效时返回空的Employee() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"number=SH0444", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(parseJson(data, "employee:id"));
    }
}
