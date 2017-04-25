package com.mingyizhudao.qa.testcase.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.login.MobileProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class SendVerifyCode extends BaseTest{

    public static final Logger logger= Logger.getLogger(SendVerifyCode.class);
    public static String uri = "/api/login/sendVerifyCode";
    public static String mock = false ? "/mockjs/1" : "";
    public static String mobile;
    public static String token;

    public static String send() {
        String res = "";
        MobileProfile body = new MobileProfile(true);
        CheckVerifyCode.mobile = mobile;
        Refresh.mobile = mobile;
        logger.info("请求验证码到手机号" + mobile + " ...") ;
        try {
            res = HttpRequest.sendPost(host_login +uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("mobile是: " + mobile + "...请发送验证码到服务器进行验证");
        return mobile;
    }

    @Test
    public void 填写错误手机号发送验证码成功失败() {
        String res = "";
        MobileProfile body = new MobileProfile(false);
        body.body.replace("mobile", "");
        try {
            res = HttpRequest.sendPost(host_login +mock+uri,body.body.toString(), "");
            checkResponse(res);
            Assert.fail("res should fail"); // 如果没有exception，就是fail
        } catch (IOException e) {
            logger.info("res returns error because of malformed input");
        } catch (JSONException e) {
        }
    }

    @Test
    public void 填写正确手机号发送验证码成功() {
        String res = "";
        MobileProfile body = new MobileProfile(true);
        try {
            res = HttpRequest.sendPost(host_login +mock+uri,body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code,"1000000","消息码不正确");
    }

    @Test
    public void 填写错误少于11位的手机号发送验证码失败() {
        String res = "";
        MobileProfile body = new MobileProfile(false);
        body.body.replace("mobile", "1330000000");
        try {
            res = HttpRequest.sendPost(host_login +mock+uri,body.body.toString(), "");
            checkResponse(res);
            Assert.fail("res should fail"); // 如果没有exception，就是fail
        } catch (IOException e) {
            logger.info("res returns error because of malformed input: mobile number less than 11");
        } catch (JSONException e) {

        }
    }

}

