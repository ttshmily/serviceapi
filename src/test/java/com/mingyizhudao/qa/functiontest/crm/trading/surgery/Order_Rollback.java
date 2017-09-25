package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

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
public class Order_Rollback extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/orderRollback";

    public static String s_Rollback(String orderId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        res = Order_Detail.s_Detail(orderId);
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status"); // 期望2000
    }

    @Test
    public void test_01_回退订单_三方通话确认成功之后() {
        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.s_CreateOrder(userToken); // create an order
        logger.debug(Order_ReceiveTask.s_ReceiveTask(order_number));
        logger.debug(Order_RecommendDoctor.s_RecommendDoctor(order_number, "666"));
        String status = Order_ThreewayCall_V2.s_CallV2(order_number, "success");
        if (!status.equals("3000")) {
            logger.debug(status);
            Assert.fail("未进行到支付状态，无法继续执行该用例");
        }
        pathValue.put("orderNumber", order_number);

        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_id"));
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_name"));
    }

    @Test(enabled = false)
    public void test_02_回退订单_支付以后不可回退() {
        Assert.fail("not implemented");
    }

    @Test
    public void test_03_回退订单_三方通话确认以前不可回退() {
        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        // 刚创建的订单
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.s_CreateOrder(userToken); // create an order

        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("content", "自动化测试的回退原因");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "1000");

        // 刚领取的订单
        Order_ReceiveTask.s_ReceiveTask(order_number);
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");

        // 刚推荐的订单
        Order_RecommendDoctor.s_RecommendDoctor(order_number, "666");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");

        // 三方通话中的订单
        Order_ThreewayCall_V2.s_CallV2(order_number, "undetermined");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");

        // 已拒绝合作的订单
        Order_ThreewayCall.s_Call(order_number, "failed");
        Order_Reject.s_RejectOrder(order_number);
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "9000");

    }

    @Test(enabled = false)
    public void test_04_回退订单_以前不可回退() {
        Assert.fail("not implemented");
    }
}
