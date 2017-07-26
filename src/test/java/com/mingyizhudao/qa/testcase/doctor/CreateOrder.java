package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.dataprofile.doctor.OrderDetail;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static com.mingyizhudao.qa.util.UT.parseJson;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class CreateOrder extends BaseTest {

    public static final Logger logger= Logger.getLogger(CreateOrder.class);
    public static String uri = "/api/createorder";
    public static String mock = false ? "/mockjs/1" : "";

    public static String CreateOrder(String token) {
        String res = "";
        OrderDetail body = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(unicodeString(res));
        String tmpOrderId = parseJson(JSONObject.fromObject(res), "data:order_number");
        if (null != tmpOrderId && !tmpOrderId.isEmpty()) {
            logger.info("orderid是: " + tmpOrderId);
            return tmpOrderId;
        } else {
            logger.debug(HttpRequest.unicodeString(res));
            logger.error("获取orderId失败");
            return "";
        }
    }

    public static String CreateOrder(String token, OrderDetail mr) {
        String res = "";
        mr.body.getJSONObject("order").remove("medical_record_pictures");
        try {
            res = HttpRequest.sendPost(host_doc+uri, mr.body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(unicodeString(res));
        String tmpOrderId = parseJson(JSONObject.fromObject(res), "data:order_number");
        if (null != tmpOrderId && !tmpOrderId.isEmpty()) {
            logger.info("orderid是: " + tmpOrderId);
            return tmpOrderId;
        } else {
            logger.debug(HttpRequest.unicodeString(res));
            logger.error("获取orderId失败");
            return "";
        }
    }

    @Test
    public void test_01_创建订单_信息齐备_已认证用户() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        try {
            res = HttpRequest.sendPost(host_doc+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = parseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        Assert.assertEquals(parseJson(data,"order:patient_name"), body.body.getJSONObject("order").getString("patient_name"), "患者姓名存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_gender"), body.body.getJSONObject("order").getString("patient_gender"), "患者性别字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_age"), body.body.getJSONObject("order").getString("patient_age"), "患者年龄字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_phone"), body.body.getJSONObject("order").getString("patient_phone"), "患者手机号字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:major_disease_id"), body.body.getJSONObject("order").getString("major_disease_id"), "主诉疾病ID字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:major_disease_name"), UT.diseaseName(body.body.getJSONObject("order").getString("major_disease_id")), "主诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:minor_disease_id"), body.body.getJSONObject("order").getString("minor_disease_id"), "次诉疾病ID字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:minor_disease_name"), UT.diseaseName(body.body.getJSONObject("order").getString("minor_disease_id")), "次诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:diagnosis"), body.body.getJSONObject("order").getString("diagnosis"), "病例描述字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_start_date"), body.body.getJSONObject("order").getString("expected_surgery_start_date"), "期望手术最早开始时间字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_due_date"), body.body.getJSONObject("order").getString("expected_surgery_due_date"), "期望手术最晚开始时间字段存储不正确");

        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), body.body.getJSONObject("order").getString("expected_surgery_hospital_id"), "期望医院ID存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), UT.hospitalName(body.body.getJSONObject("order").getString("expected_surgery_hospital_id")),"期望医院名称字段不正确");
        Assert.assertEquals(parseJson(data,"order:status"), "1000", "新建订单状态应当为1000");
        Assert.assertEquals(parseJson(data,"order:OrderStatusText"), "待处理", "新建订单状态描述应当为'处理中'");
        Assert.assertNotEquals(parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(parseJson(data,"order:order_number"), orderId, "订单号字段不正确");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures()"), "病例图片字段缺失");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures(0):url"), "病例图片url字段缺失");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures(1):url"), "病例图片url字段缺失");
        Assert.assertEquals(parseJson(data,"order:medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertEquals(parseJson(data,"order:medical_record_pictures(1):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(parseJson(data,"order:agent_contact_id"));
        Assert.assertNotNull(parseJson(data,"order:agent_referrer_id"));
        Assert.assertNotNull(parseJson(data,"order:agent_referrer_name"));
        Assert.assertNotNull(parseJson(data,"order:agent_city_id"));
        Assert.assertNotNull(parseJson(data,"order:agent_city_name"));
        Assert.assertNotNull(parseJson(data,"order:agent_hospital"));
        Assert.assertNotNull(parseJson(data,"order:agent_referrer_group_id"));

    }

    @Test
    public void test_02_创建订单_信息齐备_未登录用户() {

        String res = "";
        OrderDetail body = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void test_03_创建订单_缺少患者姓名不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_name", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").replace("patient_name", "abcdefghijklmnopqrstuvwxyz一二三四五六七八九十甲乙丙地子卯寅丑");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_name");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_04_创建订单_缺少患者性别或性别不正确不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_gender", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").replace("patient_gender", "3");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_gender");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "性别为3不应该能创建病历");

    }

    @Test
    public void test_05_创建订单_缺少患者年龄不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_age", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_age");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_06_创建订单_缺少患者手机不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_phone", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
//        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "1000000"); // PD要求可以创建。。。

        body.body.getJSONObject("order").remove("patient_phone");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
//        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "1000000"); // PD要求可以创建。。。
    }

    @Test
    public void test_07_创建订单_缺少主诉疾病不可以创建() {

        String res = "";
        OrderDetail order = new OrderDetail(true);

        order.body.getJSONObject("order").replace("major_disease_id", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        order.body.getJSONObject("order").remove("major_disease_id");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_08_创建订单_缺少次诉疾病可以创建() {

        String res = "";
        OrderDetail order = new OrderDetail(true);

        order.body.getJSONObject("order").replace("minor_disease_id", "");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        order.body.getJSONObject("order").remove("minor_disease_id");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_09_创建订单_信息齐备_认证中的医生没有邀请者不可以创建() {

        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        dp.body.getJSONObject("doctor").remove("inviter_no");
        HashMap<String, String> doc = CreateRegisteredDoctor(dp);
        if (doc == null) {
            Assert.fail("创建医生失败");
        }
        String tmpToken = doc.get("token");
        OrderDetail order = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host_doc + uri, order.body.toString(), tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
//        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "1000000"); // PD要求认证中的医生也可以创建了。。。

    }

    @Test
    public void test_10_创建订单_信息齐备_认证中的医生有邀请者可以创建() {

        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doc = CreateRegisteredDoctor(dp);
        if (doc == null) {
            Assert.fail("创建医生失败");
        }
        String tmpToken = doc.get("token");
        OrderDetail order = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host_doc + uri, order.body.toString(), tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_11_创建订单_信息齐备_期望手术医院不传() {

        String res = "";

        OrderDetail order = new OrderDetail(true);
        logger.info("不传入期望手术医院的ID。。。");
        order.body.getJSONObject("order").replace("expected_surgery_hospital_id","");
        try {
            res = HttpRequest.sendPost(host_doc+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = parseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), mainDoctorHospitalId);
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), mainDoctorHospitalName);

        logger.info("传入期望手术医院的ID=0。。。");
        order.body.getJSONObject("order").replace("expected_surgery_hospital_id","0");
        try {
            res = HttpRequest.sendPost(host_doc+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = parseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), "0");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), "待回访");

        logger.info("不传入期望手术医院的key。。。");
        order.body.getJSONObject("order").remove("expected_surgery_hospital_id");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = parseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), mainDoctorHospitalId);
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), mainDoctorHospitalName);
    }

    @Test
    public void test_12_创建订单_病例图片作为非必填字段() {
        String res = "";

        OrderDetail order = new OrderDetail(true);
        order.body.getJSONObject("order").remove("medical_record_pictures");
        try {
            res = HttpRequest.sendPost(host_doc + uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

}

