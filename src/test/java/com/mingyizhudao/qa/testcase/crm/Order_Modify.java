package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
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
        String name = UT.randomString(4);
        body.put("patient_name","修改的姓名"+name);
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
        Assert.assertEquals(parseJson(data, "patient_name"), "修改的姓名"+name);
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
        String diseaseId = UT.randomDiseaseId();
        body.put("major_disease_id", diseaseId);
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
        Assert.assertEquals(parseJson(data, "major_disease_id"), diseaseId);
        Assert.assertEquals(parseJson(data, "major_disease_name"), UT.diseaseName(diseaseId));
    }

    @Test
    public void test_04_修改订单_患者次诉疾病() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String diseaseId = UT.randomDiseaseId();
        body.put("minor_disease_id",diseaseId);
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
        Assert.assertEquals(parseJson(data, "minor_disease_id"), diseaseId);
        Assert.assertEquals(parseJson(data, "minor_disease_name"), UT.diseaseName(diseaseId));
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

    @Test(enabled = false)
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

    @Test
    public void test_09_修改订单_图片资料() {

        String res = "";
        String order_number = CreateOrder.CreateOrder(mainToken); // create an order
        Order_ReceiveTask.receiveTask(order_number);
        String expId = UT.randomExpertId();
        Order_RecommendDoctor.recommendDoctor(order_number, expId);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        JSONArray pics = JSONArray.fromObject("[{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'1'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg';'type':'1'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102739.jpg';'type':'1'}]");
        body.put("medical_record_pictures",pics);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(order_number);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertEquals(parseJson(data, "medical_record_pictures(0):type"), "1");
        Assert.assertNotNull(parseJson(data, "medical_record_pictures(0):thumbnailPicture"), "缺少缩略图");
        Assert.assertNotNull(parseJson(data, "medical_record_pictures(0):largePicture"), "缺少大图");
    }
}
