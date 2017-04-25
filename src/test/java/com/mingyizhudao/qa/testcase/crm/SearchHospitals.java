package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class SearchHospitals extends BaseTest {

    public static final Logger logger= Logger.getLogger(SearchHospitals.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/hospitals";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_查询默认医院列表_默认返回热门医院() {

        String res = "";

        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_查询医院列表_根据查询条件() {

        String res = "";

        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
