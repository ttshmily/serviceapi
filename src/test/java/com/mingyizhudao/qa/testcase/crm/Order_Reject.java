package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.testcase.doctor.GetOrderDetail;
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
public class Order_Reject extends BaseTest {
// 创建支付订单前的取消
    public static final Logger logger= Logger.getLogger(Order_Reject.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/rejectOrder";
    public static String mock = false ? "/mockjs/1" : "";

    public static String rejectOrder(String orderId) {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("content", "选择内容");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return UT.parseJson(JSONObject.fromObject(res), "data:status"); // 期望9000
    }


    @Test
    public void test_01_客服拒绝订单_推荐之前() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.receiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(UT.parseJson(data, "status"), "9000");
        Assert.assertEquals(UT.parseJson(data, "order_number"), order_number);
        res = GetOrderDetail.getOrderDetail(mainToken, order_number);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order:header_info"), "自动化推荐之前据拒订单的理由");
//        Assert.assertEquals(UT.parseJson(data, "content"), "自动化推荐之前据拒订单的理由");

    }

    @Test
    public void test_02_客服拒绝订单_推荐之后_不能拒绝() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.receiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        if (!Order_RecommendDoctor.recommendDoctor(order_number, mainDoctorId).equals("2020")) {
            Assert.fail("推荐专家失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        // 没有确定三方通话失败，不能拒绝已有推荐医生的订单
        Assert.assertNotEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(UT.parseJson(data, "status"), "2020");
        Assert.assertEquals(UT.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_03_客服拒绝订单_三方通话待定之后_不能拒绝() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.receiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        if (!Order_RecommendDoctor.recommendDoctor(order_number, mainDoctorId).equals("2020")) {
            Assert.fail("推荐专家失败，无法进行后续操作");
        }
        if (!Order_ThreewayCall.ThreewayCall(order_number, "undetermined").equals("2020")) {
            Assert.fail("三方通话待定失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化三方通话后拒绝订单");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        // 没有确定三方通话失败，不能拒绝已有推荐医生的订单
        Assert.assertNotEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(UT.parseJson(data, "status"), "2020");
        Assert.assertEquals(UT.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_04_客服拒绝订单_无证操作() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.receiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "无证");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "status"), "2000");
        Assert.assertEquals(UT.parseJson(data, "order_number"), order_number);
    }
}
