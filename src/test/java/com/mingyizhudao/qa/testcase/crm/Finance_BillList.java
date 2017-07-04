package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

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

    @Test
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
        Assert.assertNotNull(UT.parseJson(data, "size"));
        Assert.assertNotNull(UT.parseJson(data, "page_size"));
    }

    @Test
    public void test_02_获取支付订单_分页逻辑() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "0");
        for (int i = 1; i < 10; i++) {
            query.replace("pageSize", String.valueOf(i));
            try {
                res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(UT.parseJson(data, "list()"), String.valueOf(i));
        }
    }
}
