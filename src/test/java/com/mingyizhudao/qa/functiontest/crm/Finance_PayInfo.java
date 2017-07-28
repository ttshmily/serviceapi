package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class Finance_PayInfo extends BaseTest {
    public static final Logger logger= Logger.getLogger(Finance_PayInfo.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/finances/pays/{paymentNumber}";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取支付对象的相关信息() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", "");
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

    }
}
