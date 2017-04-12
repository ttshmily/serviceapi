package com.mingyizhudao.qa.tc.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.login.CheckMobileProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class CheckVerifyCode extends BaseTest{

    public static final Logger logger= Logger.getLogger(CheckVerifyCode.class);
    public static String uri = "/api/login/checkVerifyCode";
    public static String mock = false ? "/mockjs/1" : "";
    public static String host = "http://login.dev.mingyizhudao.com";
    public static String mobile;
    public static String token;

    public static String check() {
        String res = "";
        CheckMobileProfile cmp = new CheckMobileProfile(false);
        cmp.body.replace("mobile", mobile);
        cmp.body.replace("code", "123456");
        logger.info("发送短信验证码到服务器进行验证...");
        try {
            res = HttpRequest.sendPost(host+uri,cmp.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("返回数据: " + JSONObject.fromObject(res).toString());
        String tmpToken = parseJson(JSONObject.fromObject(res), "data:token");
        if (!tmpToken.isEmpty() && null != tmpToken) {
            logger.info("token是: " + tmpToken);
            token = tmpToken;
            Refresh.token = tmpToken;
            return tmpToken;
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

