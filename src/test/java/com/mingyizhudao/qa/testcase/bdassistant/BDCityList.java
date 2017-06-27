package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;

import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/6/26.
 */
public class BDCityList extends BaseTest{
    public static final Logger logger= Logger.getLogger(BDCityList.class);
    public static String uri = "/api/v1/user/bdCityList";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public void test_01_获取个人负责区域列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, bda_token_staff);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "");
        Assert.assertNotNull(parseJson(data, ""));
    }

    public void test_02_获取多人负责区域列表() {

    }
}
