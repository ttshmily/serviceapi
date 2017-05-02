package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
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
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        return Integer.parseInt(parseJson(JSONObject.fromObject(res), "data:size"));
    }

    @Test
    public void test_01_获取订单列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
    }
}
