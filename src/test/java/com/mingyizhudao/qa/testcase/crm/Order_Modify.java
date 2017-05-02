package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.Enum;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class Order_Modify extends BaseTest {

    public static final Logger logger= Logger.getLogger(Order_Modify.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/orders/{orderNumber}/orderDetail";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_修改订单_患者姓名() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_name","自动生成的姓名");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "patient_name"), "自动生成的姓名");
    }

    @Test
    public void test_02_修改订单_患者年龄() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_age","44");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "patient_age"), "44");
    }

    @Test
    public void test_03_修改订单_患者主诉疾病() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("major_disease_id","44");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_disease_id"), "44");
        Assert.assertEquals(parseJson(data, "major_disease_name"), Enum.kb_disease.get("44"));
    }

    @Test
    public void test_04_修改订单_患者次诉疾病() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("minor_disease_id","44");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "minor_disease_id"), "44");
        Assert.assertEquals(parseJson(data, "minor_disease_name"), Enum.kb_disease.get("44"));
    }

    @Test
    public void test_05_修改订单_患者性别() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_gender","2");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "patient_gender"), "2");
    }

    @Test
    public void test_06_修改订单_患者手机() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        Order_RecommendDoctor.recommendDoctor(order_number, "666");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "patient_phone"), "13799990123");
    }

    @Test
    public void test_07_修改订单_待支付后不可修改() {
        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        logger.debug(Order_ReceiveTask.receiveTask(order_number));
        logger.debug(Order_RecommendDoctor.recommendDoctor(order_number, "666"));
        if (!Order_ThreewayCall.ThreewayCall(order_number, "success").equals("3000")) {
            Assert.fail("未进行到支付状态，无法继续执行该用例");
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "patient_mobile"), "13799990123");
    }

    @Test
    public void test_08_修改订单_未领之前不可修改() {
        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "patient_mobile"), "13799990123");
    }
}
