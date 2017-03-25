package com.mingyizhudao.qa.tc.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import static com.mingyizhudao.qa.tc.login.Refresh.token;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class Refresh extends BaseTest{

    public static String uri = "/api/login/refresh";
    public static String mock = false ? "/mockjs/1" : "";
    public static String host = "http://login.dev.mingyizhudao.com";
    public static String mobile;
    public static String token;

    public static String refresh() {
        String res = "";
        CheckMobileProfile body = new CheckMobileProfile(false);
        body.body.replace("mobile", BaseTest.mainMobile);
        body.body.replace("code", "123456");
        logger.info("发送短信验证码到服务器进行登录校验");
        try {
            res = HttpRequest.sendPost(host+mock+uri,body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info("发送成功，等待解析token");
        String token = parseJson(JSONObject.fromObject(res), "data:token");
        if (token.isEmpty() && null == token) {
            logger.info("token是: " + token);
            return token;
        } else {
            logger.error("获取token失败");
            return "";
        }
    }
}


class RefreshProfile {

    public JSONObject body = new JSONObject();

    public RefreshProfile(boolean init) {
        if (init) {
            body.accumulate("token", token);
        } else {
            body.accumulate("token", "");
        }
    }

    public String phone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        SendVerifyCode.mobile = "13" + String.format("%05d",m) + "9999";
        return String.format("%05d",m);

    }
}