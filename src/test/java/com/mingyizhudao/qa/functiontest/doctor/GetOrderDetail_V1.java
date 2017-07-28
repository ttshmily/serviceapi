package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.*;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/20.
 */
public class GetOrderDetail_V1 extends BaseTest {
    public static final Logger logger= Logger.getLogger(GetOrderDetail_V1.class);
    public static String uri = "/api/v1/orders/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    public static String MyInitiateOrder(String token, String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        try {
            res = HttpRequest.s_SendGet(host_doc +uri, query, token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    public static String MyReceivedOrder(String token, String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "2");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取订单详情_提供正确的自己发起的订单ID() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        try {
            res = HttpRequest.s_SendGet(host_doc +uri,query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data,"order:patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:minor_disease_id"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:minor_disease_name"), "", "次诉疾病名称字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:diagnosis"), "病例描述字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:expected_surgery_start_date"), "期望手术最早开始时间字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:expected_surgery_due_date"), "期望手术最晚开始时间字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:expected_surgery_hospital_id"), "期望医院ID字段缺失");
        Assert.assertNotNull(Generator.parseJson(data,"order:expected_surgery_hospital_name"), "期望医院名称字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(Generator.parseJson(data,"order:order_number"), orderId, "订单号字段缺失");
        Assert.assertNotEquals(Generator.parseJson(data,"order:medical_record_pictures():type"), "");
        Assert.assertNotEquals(Generator.parseJson(data,"order:medical_record_pictures():key"), "");
        Assert.assertNotEquals(Generator.parseJson(data,"order:medical_record_pictures():url"), "");
        Assert.assertNotEquals(Generator.parseJson(data,"order:NodeList"), "", "时间字段缺失");
    }

    @Test
    public void test_02_获取订单详情_提供正确的错误的ID_ID为非法整数() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000000000");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210430");
    }

    @Test
    public void test_03_获取订单详情_提供正确的错误的ID_ID为英文() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000asdfa000");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210430");

    }

    @Test
    public void test_04_获取订单详情_提供不属于自己的订单ID() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
        }
        pathValue.put("orderId", orderId);
        HashMap<String, String> doctorInfo = CreateSyncedDoctor(new DoctorProfile(true));
        String tmpToken = doctorInfo.get("token");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_05_获取订单详情_我收到的订单_刚被推荐() {
        String res = "";
        String tmpToken = CreateSyncedDoctor(new DoctorProfile(true)).get("token");
        String orderId = CreateOrder.CreateOrder(tmpToken);
        Order_ReceiveTask.receiveTask(orderId);
        Order_RecommendDoctor.recommendDoctor(orderId, mainExpertId);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "2");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:recommend_at"));

        Order_ThreewayCall_V2.CallV2(orderId, "success");
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:recommend_at"));
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:called_at"));
        Assert.assertEquals(Generator.parseJson(data, "order:status"), "3000");

        Order_Rollback.Rollback(orderId);
        try {
            res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:recommend_at"));
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:called_at"));
        Assert.assertNotNull(Generator.parseJson(data, "order:NodeList:canceled_at"));
        Assert.assertEquals(Generator.parseJson(data, "order:status"), "9000");
    }
}
