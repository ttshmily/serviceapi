package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.testcase.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CancelOrder extends BaseTest {
    public static final Logger logger= Logger.getLogger(CancelOrder.class);
    public static String uri = "/api/cancelOrder";
    public static String mock = false ? "/mockjs/1" : "";

    public static String CancelOrder(String token, String orderId) {
        String res = "";
        res = GetOrderDetail_V1.MyInitiateOrder(token, orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        if (!(Integer.parseInt(status) < 3000)) {
            logger.error("订单不可取消");
            return status;
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);

        try {
            res = HttpRequest.sendPost(host_doc + uri, body.toString(), token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        res = GetOrderDetail_V1.MyInitiateOrder(token, orderId);
        status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        return status;
    }

    @Test
    public void test_01_取消订单_1000状态() {

        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "status"), "9000");
        Assert.assertEquals(Generator.parseJson(data, "status"), "9000");

    }

    @Test
    public void test_02_取消订单_2000状态() {

        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "status"), "9000");
    }

    @Test
    public void test_03_取消订单_2020状态() {

        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        if(!Order_RecommendDoctor.recommendDoctor(orderId, "777").equals("2020")) {
            logger.error("未推荐成功，退出用例执行");
            Assert.fail("未推荐成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "status"), "9000");
    }

    @Test
    public void test_04_取消订单_无token() {

        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "order:status"), "2000");
    }

    @Test
    public void test_05_取消订单_3000状态() {

        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        if(!Order_RecommendDoctor.recommendDoctor(orderId, "777").equals("2020")) {
            logger.error("未推荐成功，退出用例执行");
            Assert.fail("未推荐成功，退出用例执行");
        }
        if(!Order_ThreewayCall.ThreewayCall(orderId, "success").equals("3000")) {
            logger.error("三方通话未成功，退出用例执行");
            Assert.fail("三方通话未成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "order:status"), "9000");
    }

}
