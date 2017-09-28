package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.functiontest.doctor.GetOrderDetail_V1;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Reject extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/rejectOrder";

    public static String s_RejectOrder(String orderId) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("content", "选择内容");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        res = Order_Detail.s_Detail(orderId);
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status"); // 期望9000
    }

    @Test
    public void test_01_客服拒绝订单_推荐之前() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.s_ReceiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");
        Assert.assertEquals(Helper.s_ParseJson(data, "order_number"), order_number);
        res = GetOrderDetail_V1.s_MyInitiateOrder(mainToken, order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order:header_info"), "自动化推荐之前据拒订单的理由");
//        Assert.assertEquals(UT.s_ParseJson(data, "content"), "自动化推荐之前据拒订单的理由");

    }

    @Test
    public void test_02_客服拒绝订单_推荐之后_不能拒绝() {
//        String userToken = "";
//        String userExpertId = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");
//        userExpertId = mainDoctorInfo.get("expert_id");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.s_ReceiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        if (!Order_RecommendDoctor.s_RecommendDoctor(order_number, mainExpertId).equals("2020")) {
            Assert.fail("推荐专家失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        // 没有确定三方通话失败，不能拒绝已有推荐医生的订单
        Assert.assertNotEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_03_客服拒绝订单_三方通话待定之后_不能拒绝() {
//        String userToken = "";
//        String userExpertId = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");
//        userExpertId = mainDoctorInfo.get("expert_id");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.s_ReceiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        if (!Order_RecommendDoctor.s_RecommendDoctor(order_number, mainExpertId).equals("2020")) {
            Assert.fail("推荐专家失败，无法进行后续操作");
        }
        if (!Order_ThreewayCall_V2.s_CallV2(order_number, "undetermined").equals("2020")) {
            Assert.fail("三方通话待定失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化三方通话后拒绝订单");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        // 没有确定三方通话失败，不能拒绝已有推荐医生的订单
        Assert.assertNotEquals(code, "1000000", "拒绝订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_04_客服拒绝订单_无证操作() {
//        String userToken = "";
//        String userExpertId = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");
//        userExpertId = mainDoctorInfo.get("expert_id");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        if (!Order_ReceiveTask.s_ReceiveTask(order_number).equals("2000")) {
            Assert.fail("领取失败，无法进行后续操作");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化推荐之前据拒订单的理由");
        body.put("reps_content", "自动化推荐之前据拒订单的客服理由");
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "无证");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");
        Assert.assertEquals(Helper.s_ParseJson(data, "order_number"), order_number);
    }
}
