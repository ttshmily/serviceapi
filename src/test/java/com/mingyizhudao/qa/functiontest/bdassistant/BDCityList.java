package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;

import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
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

    public void test_01_获取个人负责区域列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");
        Assert.assertNotNull(Helper.s_ParseJson(data, ""));
    }

    public void test_02_获取多人负责区域列表() {

    }
}
