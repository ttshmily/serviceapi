package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_TrackList extends BaseTest {

    public static final Logger logger= Logger.getLogger(Order_TrackList.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/orderTracks";
    public static String mock = false ? "/mockjs/1" : "";

    public static String trackList(String orderId, String type) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        HashMap<String, String> query = new HashMap<>();
        if (!type.isEmpty()) {
            query.put("type", type);
        } else {
            query = null;
        }
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取订单操作记录_所有() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        res = trackList(order_number, "");
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "1");

        logger.debug(Order_ReceiveTask.receiveTask(order_number));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "2");

        logger.debug(Order_RecommendDoctor.recommendDoctor(order_number, "666"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "3");

        logger.debug(Order_ThreewayCall.ThreewayCall(order_number, "success"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "4");

        int id1 = Integer.parseInt(parseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(parseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(parseJson(data, "list(2):id"));
        int id4 = Integer.parseInt(parseJson(data, "list(3):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
        Assert.assertTrue(id3 > id4, "没有倒序排列");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "1");

    }


    @Test
    public void test_02_获取订单操作记录_三方通话记录() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        res = trackList(order_number, "");
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "1");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "0");

        logger.debug(Order_ReceiveTask.receiveTask(order_number));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "2");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "0");

        logger.debug(Order_RecommendDoctor.recommendDoctor(order_number, "666"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "3");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "0");

        logger.debug(Order_ThreewayCall.ThreewayCall(order_number, "undetermined"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "4");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "1");

        logger.debug(Order_ThreewayCall.ThreewayCall(order_number, "undetermined"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "5");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "2");

        logger.debug(Order_ThreewayCall.ThreewayCall(order_number, "failed"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "6");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "3");

        logger.debug(Order_RecommendDoctor.recommendDoctor(order_number, "777"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "7");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "3");

        logger.debug(Order_ThreewayCall.ThreewayCall(order_number, "success"));
        res = trackList(order_number, "");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "8");

        res = trackList(order_number, "THREE_CALL");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "list()"), "4");

        int id1 = Integer.parseInt(parseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(parseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(parseJson(data, "list(2):id"));
        int id4 = Integer.parseInt(parseJson(data, "list(3):id"));
        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
        Assert.assertTrue(id3 > id4, "没有倒序排列");

    }
}
