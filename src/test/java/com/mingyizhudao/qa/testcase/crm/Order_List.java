package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
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

    public static String SelectPaidOrder() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("status", "4000");
        query.put("searchKey", "agent_name");
        query.put("searchValue", "庄恕");
        query.put("page", "1");
        query.put("pageSize", "1");
        //query.put("hideTest", "true");

        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
            logger.debug(HttpRequest.unicodeString(res));
        } catch (IOException e) {
            logger.error(e);
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return UT.parseJson(JSONObject.fromObject(res), "data:list(0):order_number");
    }

    public static String SelectBrievedOrder() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("status", "4010");
        query.put("page", "1");
        query.put("pageSize", "1");
//        query.put("hideTest", "true");

        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return UT.parseJson(JSONObject.fromObject(res), "data:list(0):order_number");
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
        query.put("pageSize", "500");
        query.put("isRecommended", "true");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertTrue(Integer.parseInt(UT.parseJson(order, "status"))>2000); // 状态码2000以上的推荐过
        }
    }

    @Test
    public void test_04_获取订单列表_状态筛选() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "500");

        query.put("status", "4000");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(UT.parseJson(order, "status"), "4000");
        }

        query.replace("status", "5000");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(UT.parseJson(order, "status"), "5000");
        }

        query.replace("status", "4020");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(UT.parseJson(order, "status"), "4020");
        }
    }

    @Test
    public void test_05_获取订单列表_下级医生名称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "500");

        query.put("searchKey", "agent_name");
        query.put("searchValue", "庄恕");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertTrue(UT.parseJson(order, "agent_name").contains("庄恕"));
        }
    }
}
