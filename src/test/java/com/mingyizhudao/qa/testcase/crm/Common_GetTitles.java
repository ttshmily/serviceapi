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
public class Common_GetTitles extends BaseTest {

    public static final Logger logger= Logger.getLogger(Common_GetTitles.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/titles";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取所有职级列表() {

        String res = "";

        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list:academic()"), "4");
        Assert.assertEquals(parseJson(data, "list:medical()"), "12");

    }
}
