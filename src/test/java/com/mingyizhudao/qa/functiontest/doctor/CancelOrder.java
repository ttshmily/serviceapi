package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CancelOrder extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/cancelOrder";

    public static String s_CancelOrder(String token, String orderId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        if (!(Integer.parseInt(status) < 3000)) {
            logger.error("订单不可取消");
            return status;
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        HttpRequest.s_SendPost(host_doc + uri, body.toString(), token, null);
        res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
        status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        return status;
    }

    @Test
    public void test_01_取消订单_1000状态() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        String orderId = CreateOrder.s_CreateOrder(userToken);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");

    }

    @Test
    public void test_02_取消订单_2000状态() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        String orderId = CreateOrder.s_CreateOrder(userToken);
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");
    }

    @Test
    public void test_03_取消订单_2020状态() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        String orderId = CreateOrder.s_CreateOrder(userToken);
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        if(!Order_RecommendDoctor.s_RecommendDoctor(orderId, "777").equals("2020")) {
            logger.error("未推荐成功，退出用例执行");
            Assert.fail("未推荐成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        res = HttpRequest.s_SendPost(host_doc+uri, body.toString(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");
    }

    @Test
    public void test_04_取消订单_无token() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        String orderId = CreateOrder.s_CreateOrder(userToken);
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetOrderDetail_V1.s_MyInitiateOrder(userToken, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order:status"), "2000");
    }

    @Test
    public void test_05_取消订单_3000状态() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        String orderId = CreateOrder.s_CreateOrder(userToken);
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("未领取成功，退出用例执行");
            Assert.fail("未领取成功，退出用例执行");
        }
        if(!Order_RecommendDoctor.s_RecommendDoctor(orderId, "777").equals("2020")) {
            logger.error("未推荐成功，退出用例执行");
            Assert.fail("未推荐成功，退出用例执行");
        }
        if(!Order_ThreewayCall_V2.s_CallV2(orderId, "success").equals("3000")) {
            logger.error("三方通话未成功，退出用例执行");
            Assert.fail("三方通话未成功，退出用例执行");
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.s_MyInitiateOrder(userToken, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order:status"), "9000");
    }

}
