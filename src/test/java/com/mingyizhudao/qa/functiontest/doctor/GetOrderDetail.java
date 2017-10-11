package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Certify_V2;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.doctor.CreateOrder.mainToken;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderDetail extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getorderdetail/{orderId}";

    public static String s_Detail(String token, String orderId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        res = HttpRequest.s_SendGet(host_doc + uri, "", token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取订单详情_提供正确的属于自己的订单ID() {

//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> pathValue = new HashMap<String, String>();
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        pathValue.put("orderId", orderId);
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken, pathValue);
        s_CheckResponse(res);
//        Assert.assertEquals(s_ParseJson(data,"order:id"), orderId, "订单ID字段不正确");
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
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():type"), "", "订单号时间字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():key"), "", "订单号时间字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order:medical_record_pictures():url"), "", "订单号时间字段缺失");


    }

    @Test
    public void test_02_获取订单详情_提供正确的错误的ID_ID为非法整数() {

//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000000000");
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210430");
    }

    @Test
    public void test_03_获取订单详情_提供正确的错误的ID_ID为英文() {

//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000asdfa000");
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210430");

    }

    @Test
    public void test_04_获取订单详情_提供不属于自己的订单ID() {

//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        logger.info("创建订单with userToken");
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with userToken失败");
        }
        pathValue.put("orderId", orderId);
        SendVerifyCode.s_Send();
        String tmpToken = CheckVerifyCode.s_Check();
        User dp = new User();
        UpdateDoctorProfile_V1.s_Update(tmpToken, dp);
        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        String docId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        RegisteredDoctor_Certify_V2.s_CertifyOnly(docId, "1");

        res = HttpRequest.s_SendGet(host_doc + uri,"", tmpToken, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
