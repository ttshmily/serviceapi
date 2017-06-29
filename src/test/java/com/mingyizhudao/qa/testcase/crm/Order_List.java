package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_List extends BaseTest {

    public static final Logger logger = Logger.getLogger(Order_List.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/orderList";
    public static String mock = false ? "/mockjs/1" : "";

    public static int orderList() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        return Integer.parseInt(UT.parseJson(JSONObject.fromObject(res), "data:size"));
    }

    @Test
    public void test_01_获取订单列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "10");
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "list()"), "10");
        Assert.assertEquals(UT.parseJson(data, "page"), "1");
    }

    @Test
    public void test_02_获取订单列表_分页() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "100");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "list()"), "100");
        Assert.assertEquals(UT.parseJson(data, "page"), "1");
        int size = Integer.parseInt(UT.parseJson(data, "size"));
        int total = size/100;
        for (int i=1; i<=total; i++) {
            query.replace("page", String.valueOf(i));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(UT.parseJson(data, "list()"), "100");
        }
        query.replace("page", String.valueOf(total+1));
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "list()"), String.valueOf(size-100*total));
    }

    @Test
    public void test_03_获取订单列表_已推荐() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "10");
        query.put("status", "2000,2020");
        query.put("isRecommended", "true");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "list()"), "10");
        Assert.assertEquals(UT.parseJson(data, "page"), "1");
        Assert.assertEquals(UT.parseJson(data, "list(1):status"), "2020");
    }
}
