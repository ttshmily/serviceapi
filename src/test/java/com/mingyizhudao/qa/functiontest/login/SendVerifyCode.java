package com.mingyizhudao.qa.functiontest.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.login.MobileProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class SendVerifyCode extends BaseTest{

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/login/sendVerifyCode";
    public static String mobile;
    public static String token;

    public static String s_Send() {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        MobileProfile body = new MobileProfile(true);
        CheckVerifyCode.mobile = mobile;
        Refresh.mobile = mobile;
        logger.info("请求验证码到手机号" + mobile + " ...") ;
        try {
            res = HttpRequest.s_SendPost(host_login +uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("mobile是: " + mobile + "...请发送验证码到服务器进行验证");
        return mobile;
    }

    public static void s_Send(String phone) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject mobile = new JSONObject();
        mobile.put("mobile", phone);
        logger.info("请求验证码到手机号" + phone + " ...") ;
        try {
            res = HttpRequest.s_SendPost(host_login +uri, mobile.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("mobile是: " + phone + "...请发送验证码到服务器进行验证");
    }

    @Test
    public void 填写错误手机号发送验证码成功失败() {
        String res = "";
        MobileProfile body = new MobileProfile(false);
        body.body.replace("mobile", "");
        try {
            res = HttpRequest.s_SendPost(host_login + uri,body.body.toString(), "");
            s_CheckResponse(res);
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
            res = HttpRequest.s_SendPost(host_login + uri,body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code,"1000000","消息码不正确");
    }

    @Test
    public void 填写错误少于11位的手机号发送验证码失败() {
        String res = "";
        MobileProfile body = new MobileProfile(false);
        body.body.replace("mobile", "1330000000");
        try {
            res = HttpRequest.s_SendPost(host_login + uri,body.body.toString(), "");
            s_CheckResponse(res);
            Assert.fail("res should fail"); // 如果没有exception，就是fail
        } catch (IOException e) {
            logger.info("res returns error because of malformed input: mobile number less than 11");
        } catch (JSONException e) {

        }
    }

}

