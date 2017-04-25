package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Certify extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_Certify.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/verifications";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_认证医生_无效医生ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","12");
        JSONObject body = new JSONObject();
        body.put("status", "-1");  // 认证失败
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO

        body.replace("status", "1");  // 认证成功
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
    }


    @Test
    public void test_02_认证医生_无效医生ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","12");
        JSONObject body = new JSONObject();
        body.put("status", "-1");  // 认证失败
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
