package com.mingyizhudao.qa.functiontest.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.login.CheckMobileProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
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
    public static String mobile;
    public static String token;

    public static String s_Check() {
        String res = "";
        CheckMobileProfile cmp = new CheckMobileProfile(false);
        cmp.body.replace("mobile", mobile);
        cmp.body.replace("code", "123456");
        logger.info("发送短信验证码到服务器进行验证...");
        String tmpToken = "";
        try {
            res = HttpRequest.s_SendPost(host_login +uri,cmp.body.toString(), "");
            tmpToken = Generator.parseJson(JSONObject.fromObject(res), "data:token");
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.info("返回数据: " + JSONObject.fromObject(res).toString());

        if (null != tmpToken  && !tmpToken.isEmpty()) {
            logger.info("token是: " + tmpToken);
            token = tmpToken;
            Refresh.token = tmpToken;
        } else {
            logger.error("获取token失败");
        }
        return tmpToken;
    }

    public static String s_Check(String phone) {
        String res = "";
        JSONObject check = new JSONObject();
        check.put("mobile", phone);
        check.put("code", "123456");
        logger.info("发送短信验证码到服务器进行验证...");
        String tmpToken = "";
        try {
            res = HttpRequest.s_SendPost(host_login +uri, check.toString(), "");
            tmpToken = Generator.parseJson(JSONObject.fromObject(res), "data:token");
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.info("返回数据: " + JSONObject.fromObject(res).toString());

        if (null != tmpToken  && !tmpToken.isEmpty()) {
            logger.info("token是: " + tmpToken);
            token = tmpToken;
            Refresh.token = tmpToken;
        } else {
            logger.error("获取token失败");
        }
        return tmpToken;
    }


    @Test
    public void 同一手机号先请求验证码再验证应该返回token() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        SendVerifyCode.s_Send();
        body.body.replace("code", "123456");
        body.body.replace("mobile", mobile);
        try {
            res = HttpRequest.s_SendPost(host_login +mock+uri, body.body.toString(), "");
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        Assert.assertNotNull(Generator.parseJson(data, "token"), "token不应该为空");
        Assert.assertNotEquals(Generator.parseJson(data, "token"), "", "code不应该为1000000");
        Assert.assertEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 手机号未发送过请求但直接进行验证应该返回错误() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(true);
        try {
            res = HttpRequest.s_SendPost(host_login +uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 手机号非法或不符时发送验证码返回正确的错误提示() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("code", "123456");
        body.body.replace("mobile", "13800000001");
        try {
            res = HttpRequest.s_SendPost(host_login + uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("http request returns an error");
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "code不应该为1000000");
    }

    @Test
    public void 有手机号无验证码返回正确的错误提示() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("mobile", "1380000"+"9999");
        try {
            res = HttpRequest.s_SendPost(host_login +mock+uri, body.body.toString(), "");
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
            res = HttpRequest.s_SendPost(host_login +mock+uri, body.body.toString(), "");
            checkResponse(res);
            Assert.fail();
        } catch (IOException e) {
            logger.info("res returns error because of malformed input");
        }
    }

}

