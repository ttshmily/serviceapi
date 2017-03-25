package com.mingyizhudao.qa.tc.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class CheckVerifyCode extends BaseTest{

    public static String uri = "/api/login/checkVerifyCode";
    public static String mock = false ? "/mockjs/1" : "";
    public static String host = "http://login.dev.mingyizhudao.com";
    public static String mobile;
    public static String token;

    public static String check() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("mobile", mobile);
        body.body.replace("code", "123456");
        logger.info("发送短信验证码到服务器进行验证...");
        try {
            res = HttpRequest.sendPost(host+uri,body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("返回数据: " + JSONObject.fromObject(res).toString());
        String token = parseJson(JSONObject.fromObject(res), "data:token");
        if (!token.isEmpty() && null != token) {
            logger.info("token是: " + token);
            return token;
        } else {
            logger.error("获取token失败");
            return "";
        }
    }


    @Test
    public void 同一手机号先请求验证码再验证应该返回token() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        SendVerifyCode.send();
        body.body.replace("code", "123456");
        body.body.replace("mobile", mobile);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        Assert.assertNotNull(parseJson(data, "token"), "token不应该为空");
        Assert.assertNotEquals(parseJson(data, "token"), "", "code不应该为1000000");
        Assert.assertEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 手机号未发送过请求但直接进行验证应该返回错误() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        Assert.assertNotEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 手机号非法或不符时发送验证码返回正确的错误提示() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("code", "123456");
        body.body.replace("mobile", "13800000001");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        Assert.assertNotEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 有手机号无验证码返回正确的错误提示() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("mobile", "1380000"+"9999");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
            checkResponse(res);
            Assert.fail();
        } catch (IOException e) {
            logger.info("res returns error because of malformed input");
        }
    }

    @Test
    public void 无手机号有验证码返回正确的错误提示() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("code", "123456");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
            checkResponse(res);
            Assert.fail();
        } catch (IOException e) {
            logger.info("res returns error because of malformed input");
        }
    }

}

class CheckMobileProfile {

    public JSONObject body = new JSONObject();
    public CheckMobileProfile(boolean init) {
        if (init) {
            body.accumulate("mobile", "13" + phone() + "9999");
            body.accumulate("code", "123456");
            body.accumulate("state", "niyaowoa");
        } else {
            body.accumulate("mobile", "");
            body.accumulate("code", "");
            body.accumulate("state", "test");
        }
    }

    public String phone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        return String.format("%05d",m);

    }

}
