package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.CrmCertifiedDoctor;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.testcase.doctor.GetDoctorProfile;
import com.mingyizhudao.qa.testcase.doctor.UpdateDoctorProfile;
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
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Detail extends BaseTest {

    public static final Logger logger= Logger.getLogger(Order_Detail.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/orders/{orderNumber}/orderDetail";
    public static String mock = false ? "/mockjs/1" : "";

    public static String Detail(String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取订单详情() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        pathValue.put("orderNumber", orderId);
        try {
            res = HttpRequest.sendGet(host_crm+uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data,"patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(parseJson(data,"patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(parseJson(data,"patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(parseJson(data,"major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"minor_disease_id"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"minor_disease_name"), "", "次诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"diagnosis"), "病例描述字段缺失");
        Assert.assertNotNull(parseJson(data,"expected_surgery_start_date"), "期望手术最早开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"expected_surgery_due_date"), "期望手术最晚开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"expected_surgery_hospital_id"), "期望医院ID字段缺失");
        Assert.assertNotNull(parseJson(data,"expected_surgery_hospital_name"), "期望医院名称字段缺失");
        Assert.assertNotEquals(parseJson(data,"status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(parseJson(data,"OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotEquals(parseJson(data,"created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(parseJson(data,"order_number"), orderId, "订单号字段缺失");

        Assert.assertEquals(parseJson(data, "medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertNotNull(parseJson(data, "medical_record_pictures(0):largePicture"), "没有大图URL");
        Assert.assertEquals(parseJson(data, "medical_record_pictures(1):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(parseJson(data, "medical_record_pictures(1):thumbnailPicture"), "没有缩略图URL");

        Assert.assertNotEquals(parseJson(data,"agent_id"), "", "下级医生id字段缺失");
        Assert.assertNotEquals(parseJson(data,"agent_name"), "", "下级医生姓名字段缺失");
        Assert.assertNotEquals(parseJson(data,"agent_medical_title"), "", "下级医生学术职称字段缺失");

    }

    @Test
    public void test_02_获取订单详情_错误的ID_ID为非法整数() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", "2000000");
        try {
            res = HttpRequest.sendGet(host_crm+uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(parseJson(data, "orderNumber"), "订单ID错误，不应该有数据返回");
    }

    @Test
    public void test_03_获取订单详情_错误的ID_ID为英文() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", "20000asdfa000");
        try {
            res = HttpRequest.sendGet(host_crm + uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
            return;
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210430");

    }

}
