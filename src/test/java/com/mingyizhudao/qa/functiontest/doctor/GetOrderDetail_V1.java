package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_Rollback;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by dayi on 2017/7/20.
 */
public class GetOrderDetail_V1 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/orders/{orderId}";

    public static String s_MyInitiateOrder(String token, String orderId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        res = HttpRequest.s_SendGet(host_doc +uri, query, token, pathValue);
        return res;
    }

    public static String s_MyReceivedOrder(String token, String orderId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "2");
        res = HttpRequest.s_SendGet(host_doc + uri, query, token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取订单详情_提供正确的自己发起的订单ID() {
        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.s_CreateOrder(userToken);
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        res = HttpRequest.s_SendGet(host_doc +uri,query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:minor_disease_id"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:minor_disease_name"), "", "次诉疾病名称字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:diagnosis"), "病例描述字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:expected_surgery_start_date"), "期望手术最早开始时间字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:expected_surgery_due_date"), "期望手术最晚开始时间字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:expected_surgery_hospital_id"), "期望医院ID字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order:expected_surgery_hospital_name"), "期望医院名称字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(Helper.s_ParseJson(data,"order:order_number"), orderId, "订单号字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():type"), "");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():key"), "");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():url"), "");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:NodeList"), "", "时间字段缺失");
    }

    @Test
    public void test_02_获取订单详情_提供正确的错误的ID_ID为非法整数() {
        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000000000");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        res = HttpRequest.s_SendGet(host_doc + uri,query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210430");
    }

    @Test
    public void test_03_获取订单详情_提供正确的错误的ID_ID为英文() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000asdfa000");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        res = HttpRequest.s_SendGet(host_doc + uri,query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210430");

    }

    @Test
    public void test_04_获取订单详情_提供不属于自己的订单ID() {
        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.s_CreateOrder(userToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
        }
        pathValue.put("orderId", orderId);
        HashMap<String, String> doctorInfo = s_CreateSyncedDoctor(new User());
        String tmpToken = doctorInfo.get("token");
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "1");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_05_获取订单详情_我收到的订单_刚被推荐() {

        String userToken = "";
        String userExpertId = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");
        userExpertId = mainDoctorInfo.get("expert_id");

        String res = "";
        String tmpToken = s_CreateSyncedDoctor(new User()).get("token");
        String orderId = CreateOrder.s_CreateOrder(tmpToken);
        Order_ReceiveTask.s_ReceiveTask(orderId);
        Order_RecommendDoctor.s_RecommendDoctor(orderId, userExpertId);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", "2");
        res = HttpRequest.s_SendGet(host_doc + uri, query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:recommend_at"));

        Order_ThreewayCall_V2.s_CallV2(orderId, "success");
        res = HttpRequest.s_SendGet(host_doc + uri, query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:recommend_at"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:called_at"));
        Assert.assertEquals(Helper.s_ParseJson(data, "order:status"), "3000");

        Order_Rollback.s_Rollback(orderId);
        res = HttpRequest.s_SendGet(host_doc + uri, query, userToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "order:order_number"), orderId);
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:recommend_at"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:called_at"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:NodeList:canceled_at"));
        Assert.assertEquals(Helper.s_ParseJson(data, "order:status"), "9000");
    }
}
