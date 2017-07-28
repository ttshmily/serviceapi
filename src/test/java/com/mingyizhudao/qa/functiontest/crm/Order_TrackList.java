package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_TrackList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/orderTracks";

    public static String s_TrackList(String orderId, String type) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        HashMap<String, String> query = new HashMap<>();
        if (!type.isEmpty()) {
            query.put("type", type);
        } else {
            query = null;
        }
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取订单操作记录_所有() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        res = s_TrackList(order_number, "");
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "1");

        logger.debug(Order_ReceiveTask.s_ReceiveTask(order_number));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "2");

        logger.debug(Order_RecommendDoctor.s_RecommendDoctor(order_number, "666"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "3");

        logger.debug(Order_ThreewayCall_V2.s_CallV2(order_number, "success"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "4");

        int id1 = Integer.parseInt(Generator.s_ParseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Generator.s_ParseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Generator.s_ParseJson(data, "list(2):id"));
        int id4 = Integer.parseInt(Generator.s_ParseJson(data, "list(3):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
        Assert.assertTrue(id3 > id4, "没有倒序排列");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "1");

    }

    @Test
    public void test_02_获取订单操作记录_三方通话记录() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        res = s_TrackList(order_number, "");
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "1");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "0");

        logger.debug(Order_ReceiveTask.s_ReceiveTask(order_number));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "2");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "0");

        logger.debug(Order_RecommendDoctor.s_RecommendDoctor(order_number, "666"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "3");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "0");

        logger.debug(Order_ThreewayCall_V2.s_CallV2(order_number, "undetermined"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "4");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "1");

        logger.debug(Order_ThreewayCall.s_Call(order_number, "undetermined"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "5");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "2");

        logger.debug(Order_ThreewayCall.s_Call(order_number, "failed"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "6");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "3");

        logger.debug(Order_RecommendDoctor.s_RecommendDoctor(order_number, "777"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "7");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "3");

        logger.debug(Order_ThreewayCall.s_Call(order_number, "success"));
        res = s_TrackList(order_number, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "8");

        res = s_TrackList(order_number, "THREE_CALL");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.s_ParseJson(data, "list()"), "4");

        int id1 = Integer.parseInt(Generator.s_ParseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Generator.s_ParseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Generator.s_ParseJson(data, "list(2):id"));
        int id4 = Integer.parseInt(Generator.s_ParseJson(data, "list(3):id"));
        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
        Assert.assertTrue(id3 > id4, "没有倒序排列");

    }
}
