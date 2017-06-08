package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderDetail extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetOrderDetail.class);
    public static String uri = "/api/getorderdetail/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";


    public static String getOrderDetail(String token, String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, "", token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取订单详情_提供正确的属于自己的订单ID() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<String, String>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
//        Assert.assertEquals(parseJson(data,"order:id"), orderId, "订单ID字段不正确");
        Assert.assertNotEquals(parseJson(data,"order:patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(parseJson(data,"order:major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"order:minor_disease_id"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:minor_disease_name"), "", "次诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"order:diagnosis"), "病例描述字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_start_date"), "期望手术最早开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_due_date"), "期望手术最晚开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_hospital_id"), "期望医院ID字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_hospital_name"), "期望医院名称字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(parseJson(data,"order:order_number"), orderId, "订单号字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:medical_record_pictures():type"), "", "订单号时间字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:medical_record_pictures():key"), "", "订单号时间字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:medical_record_pictures():url"), "", "订单号时间字段缺失");


    }

    @Test
    public void test_02_获取订单详情_提供正确的错误的ID_ID为非法整数() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000000000");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken, pathValue);
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
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken, pathValue);
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
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        DoctorProfile dp = new DoctorProfile(true);
        UpdateDoctorProfile.updateDoctorProfile(tmpToken, dp);
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        String docId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        RegisteredDoctor_Certify.certify(docId, "1");
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", tmpToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
