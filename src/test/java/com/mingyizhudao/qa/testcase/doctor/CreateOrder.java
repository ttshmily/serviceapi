package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.OrderDetail;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

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
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
//        logger.debug(unicodeString(res));
        String tmpOrderId = parseJson(JSONObject.fromObject(res), "data:order_id");
        if (null != tmpOrderId && !tmpOrderId.isEmpty()) {
            logger.info("orderId是: " + tmpOrderId);
            return tmpOrderId;
        } else {
            logger.error("获取orderId失败");
            return "";
        }
    }

    @Test
    public void 创建订单_信息齐备_已认证用户() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = parseJson(data, "order_id");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail.getOrderDetail(mainToken, orderId);
        checkResponse(res);
//        Assert.assertEquals(parseJson(data,"order:id"), orderId, "订单ID字段缺失");
        Assert.assertEquals(parseJson(data,"order:patient_name"), "方超", "患者姓名存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_gender"), "1", "患者性别字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_age"), "31", "患者年龄字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_phone"), "13817634203", "患者手机号字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:major_disease_id"), "33", "主诉疾病ID字段存储不正确");

        Assert.assertEquals(parseJson(data,"order:major_disease_name"), "非典型纤维黄色瘤", "主诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:minor_disease_id"), "32", "次诉疾病ID字段存储不正确");

        Assert.assertEquals(parseJson(data,"order:minor_disease_name"), "肺癌皮肤转移", "次诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:diagnosis"), "工程师", "病例描述字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_start_date"), "2017-04-09", "期望手术最早开始时间字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_due_date"), "2017-05-09", "期望手术最晚开始时间字段存储不正确");

        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), "43", "期望医院ID存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), "首都医科大学附属北京口腔医院","期望医院名称字段不正确");
        Assert.assertEquals(parseJson(data,"order:status"), "1000", "新建订单状态应当为1000");
        Assert.assertEquals(parseJson(data,"order:OrderStatusText"), "待处理", "新建订单状态描述应当为'处理中'");
        Assert.assertNotEquals(parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(parseJson(data,"order:order_number"), orderId, "订单号字段不正确");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures()"), "病例图片字段缺失");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures(0):url"), "病例图片url字段缺失");
        Assert.assertNotNull(parseJson(data,"order:medical_record_pictures(1):url"), "病例图片url字段缺失");
        Assert.assertEquals(parseJson(data,"order:medical_record_pictures(0):key"), "123");
        Assert.assertEquals(parseJson(data,"order:medical_record_pictures(1):key"), "456");

    }

    @Test
    public void 创建订单_信息齐备_未登录用户() {

        String res = "";
        OrderDetail body = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void 创建订单_缺少患者姓名不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_name", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").replace("patient_name", "abcdefghijklmnopqrstuvwxyz一二三四五六七八九十甲乙丙地子卯寅丑");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_name");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 创建订单_缺少患者性别或性别不正确不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_gender", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").replace("patient_gender", "3");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_gender");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "性别为3不应该能创建病历");

    }

    @Test
    public void 创建订单_缺少患者年龄不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_age", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_age");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 创建订单_缺少患者手机不可以创建() {

        String res = "";
        OrderDetail body = new OrderDetail(true);

        body.body.getJSONObject("order").replace("patient_phone", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.body.getJSONObject("order").remove("patient_phone");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 创建订单_缺少主诉疾病不可以创建() {

        String res = "";
        OrderDetail order = new OrderDetail(true);

        order.body.getJSONObject("order").replace("major_disease_id", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        order.body.getJSONObject("order").remove("major_disease_id");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 创建订单_缺少次诉疾病可以创建() {

        String res = "";
        OrderDetail order = new OrderDetail(true);

        order.body.getJSONObject("order").replace("minor_disease_id", "");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        order.body.getJSONObject("order").remove("minor_disease_id");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void 创建订单_信息齐备_未认证医生不可以创建() {

        String res = "";
        logger.info("创建一个新的未认证医生");
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        logger.info("tmpDoctorId为"+JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id"));
        UpdateDoctorProfile.updateDoctorProfile(tmpToken, null);
        logger.info("创建未认证医生成功");
        OrderDetail order = new OrderDetail(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210409");
        Assert.assertEquals(message, "医生未认证");
    }

    @Test
    public void 创建订单_信息齐备_期望手术医院不传() {

        String res = "";

        OrderDetail order = new OrderDetail(true);
        logger.info("不传入期望手术医院的ID。。。");
        order.body.getJSONObject("order").replace("expected_surgery_hospital_id","");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = parseJson(data, "order_id");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail.getOrderDetail(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), mainDoctorHospitalId);
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), mainDoctorHospitalName);

        logger.info("传入期望手术医院的ID=0。。。");
        order.body.getJSONObject("order").replace("expected_surgery_hospital_id","0");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = parseJson(data, "order_id");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail.getOrderDetail(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), mainDoctorHospitalId);
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), mainDoctorHospitalName);

        logger.info("不传入期望手术医院的key。。。");
        order.body.getJSONObject("order").remove("expected_surgery_hospital_id");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = parseJson(data, "order_id");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail.getOrderDetail(mainToken, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), mainDoctorHospitalId);
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), mainDoctorHospitalName);
    }

}

