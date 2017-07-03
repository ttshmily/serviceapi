package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class Finance_BillList extends BaseTest{

    public static final Logger logger= Logger.getLogger(Finance_BillList.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/finances/reconciliations";
    public static String mock = false ? "/mockjs/1" : "";

    public void test_01_获取支付订单列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        Assert.assertNotNull(UT.parseJson(data, "list()"));
    }
}
