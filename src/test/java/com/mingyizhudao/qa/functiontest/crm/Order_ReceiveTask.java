package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_ReceiveTask extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/receiveTask";

    public static String s_ReceiveTask(String orderId) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        res = Order_Detail.s_Detail(orderId);

        if (!Generator.parseJson(JSONObject.fromObject(res), "data:status").equals("1000")) {
            logger.error("订单处于不可领取状态");
            return Generator.parseJson(JSONObject.fromObject(res), "data:status");
        }
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.s_Detail(orderId);
        return Generator.parseJson(JSONObject.fromObject(res), "data:status"); // 期望2000
    }


    @Test
    public void test_01_客服领取订单_1000状态的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_02_客服领取订单_无token() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertNull(Generator.parseJson(data, "major_reps_id"));
        Assert.assertEquals(Generator.parseJson(data, "status"), "1000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_03_客服领取订单_已被领取过的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        String status = Generator.parseJson(data, "status");
        logger.debug(status);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(status, "2000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);

        // do it again
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
            s_CheckResponse(res);
        } catch (IOException e) {
            logger.error(e);
        }
        Assert.assertNotEquals(code, "1000000", "领取了已经领取过的订单");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

        // do it again
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取已经领取过的订单失败");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

    }

    @Test
    public void test_04_客服领取订单_没有认证通过的医生订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        HashMap<String, String> doctorInfo = s_CreateRegisteredDoctor(new DoctorProfile(true));// 创建一个未认证的医生
        String tmpToken = doctorInfo.get("token");
        String order_number = CreateOrder.s_CreateOrder(tmpToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        doctorInfo = s_CreateVerifiedDoctor(new DoctorProfile(true));// 创建一个已认证未同步的医生
        tmpToken = doctorInfo.get("token");
        order_number = CreateOrder.s_CreateOrder(tmpToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }
}
