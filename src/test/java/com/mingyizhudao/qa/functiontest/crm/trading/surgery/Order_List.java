package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_List extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/orderList";
    public static String mock = false ? "/mockjs/1" : "";

    public static int s_OrderList() {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token);
        return Integer.parseInt(Helper.s_ParseJson(JSONObject.fromObject(res), "data:size"));
    }

    public static String s_SelectPaidOrder() {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> query = new HashMap<>();
        query.put("status", "4000");
//        query.put("searchValue", "测试的医生");
//        query.put("searchKey", "agent_name");
        query.put("page", "1");
        query.put("pageSize", "1");
        //query.put("hideTest", "true");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:list(0):order_number");
    }

    public static String s_SelectBriefedOrder() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("status", "4010");
        query.put("page", "1");
        query.put("pageSize", "1");
//        query.put("hideTest", "true");

        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:list(0):order_number");
    }

    @Test
    public void test_01_获取订单列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "10");
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "10");
        Assert.assertEquals(Helper.s_ParseJson(data, "page"), "1");
    }

    @Test
    public void test_02_获取订单列表_分页() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "100");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "100");
        Assert.assertEquals(Helper.s_ParseJson(data, "page"), "1");
        int size = Integer.parseInt(Helper.s_ParseJson(data, "size"));
        int total = size/100;
        for (int i=1; i<=total; i++) {
            query.replace("page", String.valueOf(i));
            res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "100");
        }
        query.replace("page", String.valueOf(total+1));
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), String.valueOf(size-100*total));
    }

    @Test
    public void test_03_获取订单列表_已推荐() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "500");
        query.put("isRecommended", "true");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertTrue(Integer.parseInt(Helper.s_ParseJson(order, "status"))>2000); // 状态码2000以上的推荐过
        }
    }

    @Test
    public void test_04_获取订单列表_状态筛选() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "500");

        query.put("status", "4000");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(Helper.s_ParseJson(order, "status"), "4000");
        }

        query.replace("status", "5000");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(Helper.s_ParseJson(order, "status"), "5000");
        }

        query.replace("status", "4020");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertEquals(Helper.s_ParseJson(order, "status"), "4020");
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
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray orderList = data.getJSONArray("list");
        for (int i=0; i<orderList.size(); i++) {
            JSONObject order = orderList.getJSONObject(i);
            Assert.assertTrue(Helper.s_ParseJson(order, "agent_name").contains("庄恕"));
        }
    }
}
