package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Rollback extends BaseTest {
// 创建支付订单后的取消
    public static final Logger logger= Logger.getLogger(Order_Rollback.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/orderRollback";
    public static String mock = false ? "/mockjs/1" : "";

    public static String Rollback(String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return Generator.parseJson(JSONObject.fromObject(res), "data:status"); // 期望2000
    }

    @Test
    public void test_01_回退订单_三方通话确认成功之后() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        logger.debug(Order_ReceiveTask.receiveTask(order_number));
        logger.debug(Order_RecommendDoctor.recommendDoctor(order_number, "666"));
        String status = Order_ThreewayCall.ThreewayCall(order_number, "success");
        if (!status.equals("3000")) {
            logger.debug(status);
            Assert.fail("未进行到支付状态，无法继续执行该用例");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
        Assert.assertNull(Generator.parseJson(data, "surgeon_id"));
        Assert.assertNull(Generator.parseJson(data, "surgeon_name"));
    }

    @Test(enabled = false)
    public void test_02_回退订单_支付以后不可回退() {
        Assert.fail("not implemented");
    }

    @Test
    public void test_03_回退订单_三方通话确认以前不可回退() {
        // 刚创建的订单
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order

        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "1000");

        // 刚领取的订单
        Order_ReceiveTask.receiveTask(order_number);
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");

        // 刚推荐的订单
        Order_RecommendDoctor.recommendDoctor(order_number, "666");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2020");

        // 三方通话中的订单
        Order_ThreewayCall.ThreewayCall(order_number, "undetermined");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2020");

        // 已拒绝合作的订单
        Order_ThreewayCall.ThreewayCall(order_number, "failed");
        Order_Reject.rejectOrder(order_number);
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "9000");

    }

    @Test(enabled = false)
    public void test_04_回退订单_以前不可回退() {
        Assert.fail("not implemented");
    }
}
