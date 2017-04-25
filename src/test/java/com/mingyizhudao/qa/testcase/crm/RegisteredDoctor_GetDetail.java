package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_GetDetail extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_GetDetail.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/profiles";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取医生详情_有效ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","12");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_获取医生详情_无效ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","122222");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}