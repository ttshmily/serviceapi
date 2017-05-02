package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
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
public class Order_ReceiveTask extends BaseTest {

    public static final Logger logger= Logger.getLogger(Order_ReceiveTask.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/receiveTask";
    public static String mock = false ? "/mockjs/1" : "";


    public static String receiveTask(String orderId) {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        res = Order_Detail.Detail(orderId);

        if (!parseJson(JSONObject.fromObject(res), "data:status").equals("1000")) {
            logger.error("订单处于不可领取状态");
            return parseJson(JSONObject.fromObject(res), "data:status");
        }
        try {
            res = HttpRequest.sendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return parseJson(JSONObject.fromObject(res), "data:status"); // 期望2000

    }


    @Test
    public void test_01_客服领取订单_1000状态的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.sendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(parseJson(data, "status"), "2000");
        Assert.assertEquals(parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_02_客服领取订单_无token() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.sendPost(host_crm + uri, "", "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertNull(parseJson(data, "major_reps_id"));
        Assert.assertEquals(parseJson(data, "status"), "1000");
        Assert.assertEquals(parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_03_客服领取订单_已被领取过的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.sendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        String status = parseJson(data, "status");
        logger.debug(status);
        Assert.assertEquals(parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(status, "2000");
        Assert.assertEquals(parseJson(data, "order_number"), order_number);

        // do it again
        try {
            res = HttpRequest.sendPost(host_crm + uri, "", crm_token, pathValue);
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
        }
        Assert.assertNotEquals(code, "1000000", "领取了已经领取过的订单");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

        // do it again
        try {
            res = HttpRequest.sendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取已经领取过的订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

    }
}
