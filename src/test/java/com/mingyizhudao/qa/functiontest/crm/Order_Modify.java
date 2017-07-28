package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class Order_Modify extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/orders/{orderNumber}/orderDetail";

    @Test
    public void test_01_修改订单_患者姓名() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String name = Generator.randomString(4);
        body.put("patient_name","修改的姓名"+name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "patient_name"), "修改的姓名"+name);
    }

    @Test
    public void test_02_修改订单_患者年龄() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_age","44");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "patient_age"), "44");
    }

    @Test
    public void test_03_修改订单_患者主诉疾病() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String diseaseId = Generator.randomDiseaseId();
        body.put("major_disease_id", diseaseId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "major_disease_id"), diseaseId);
        Assert.assertEquals(Generator.s_ParseJson(data, "major_disease_name"), Generator.diseaseName(diseaseId));
    }

    @Test
    public void test_04_修改订单_患者次诉疾病() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String diseaseId = Generator.randomDiseaseId();
        body.put("minor_disease_id",diseaseId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "minor_disease_id"), diseaseId);
        Assert.assertEquals(Generator.s_ParseJson(data, "minor_disease_name"), Generator.diseaseName(diseaseId));
    }

    @Test
    public void test_05_修改订单_患者性别() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_gender","2");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "patient_gender"), "2");
    }

    @Test
    public void test_06_修改订单_患者手机() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        Order_RecommendDoctor.s_RecommendDoctor(order_number, "666");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data, "list");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "patient_phone"), "13799990123");
    }

    @Test(enabled = false) // 可修改，由前端控制
    public void test_07_修改订单_待支付后不可修改() {
        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        logger.debug(Order_ReceiveTask.s_ReceiveTask(order_number));
        logger.debug(Order_RecommendDoctor.s_RecommendDoctor(order_number, "666"));
        if (!Order_ThreewayCall_V2.s_CallV2(order_number, "success").equals("3000")) {
            Assert.fail("未进行到支付状态，无法继续执行该用例");
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertNotEquals(Generator.s_ParseJson(data, "patient_mobile"), "13799990123");
    }

    @Test(enabled = false)
    public void test_08_修改订单_未领之前不可修改() {
        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("patient_phone","13799990123");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertNotEquals(Generator.s_ParseJson(data, "patient_mobile"), "13799990123");
    }

    @Test
    public void test_09_修改订单_图片资料() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        String expId = Generator.randomExpertId();
        Order_RecommendDoctor.s_RecommendDoctor(order_number, expId);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        JSONArray pics = JSONArray.fromObject("[{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'1'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg';'type':'1'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102739.jpg';'type':'1'}]");
        body.put("medical_record_pictures",pics);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_record_pictures(0):type"), "1");
        Assert.assertNotNull(Generator.s_ParseJson(data, "medical_record_pictures(0):thumbnailPicture"), "缺少缩略图");
        Assert.assertNotNull(Generator.s_ParseJson(data, "medical_record_pictures(0):largePicture"), "缺少大图");

        pics = JSONArray.fromObject("[]");
        body.replace("medical_record_pictures",pics);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_record_pictures()"), "0");
    }
}
