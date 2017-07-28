package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
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

        if (!Generator.parseJson(JSONObject.fromObject(res), "data:status").equals("1000")) {
            logger.error("订单处于不可领取状态");
            return Generator.parseJson(JSONObject.fromObject(res), "data:status");
        }
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return Generator.parseJson(JSONObject.fromObject(res), "data:status"); // 期望2000
    }


    @Test
    public void test_01_客服领取订单_1000状态的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_02_客服领取订单_无token() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertNull(Generator.parseJson(data, "major_reps_id"));
        Assert.assertEquals(Generator.parseJson(data, "status"), "1000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);
    }

    @Test
    public void test_03_客服领取订单_已被领取过的订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "领取订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        String status = Generator.parseJson(data, "status");
        logger.debug(status);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com");
        Assert.assertEquals(status, "2000");
        Assert.assertEquals(Generator.parseJson(data, "order_number"), order_number);

        // do it again
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
            checkResponse(res);
        } catch (IOException e) {
            logger.error(e);
        }
        Assert.assertNotEquals(code, "1000000", "领取了已经领取过的订单");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

        // do it again
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "领取已经领取过的订单失败");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "major_reps_id"), "chao.fang@mingyizhudao.com", "");

    }

    @Test
    public void test_04_客服领取订单_没有认证通过的医生订单() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        HashMap<String, String> doctorInfo = CreateRegisteredDoctor(new DoctorProfile(true));// 创建一个未认证的医生
        String tmpToken = doctorInfo.get("token");
        String order_number = CreateOrder.CreateOrder(tmpToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(res);
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        doctorInfo = CreateVerifiedDoctor(new DoctorProfile(true));// 创建一个已认证未同步的医生
        tmpToken = doctorInfo.get("token");
        order_number = CreateOrder.CreateOrder(tmpToken); // create an order
        pathValue.put("orderNumber", order_number);

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(res);
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }
}
