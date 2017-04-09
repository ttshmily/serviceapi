package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.OrderProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class CreateOrder extends BaseTest {
    // TODO

    public static String uri = "/api/createorder";
    public static String mock = false ? "/mockjs/1" : "";

    public static String CreateOrder(String token) {
        String res = "";
        OrderProfile body = new OrderProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.debug(unicodeString(res));
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
        OrderProfile body = new OrderProfile(true);
//        body.body.getJSONObject("doctor").replace("department","尿不出来科");
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
        Assert.assertEquals(parseJson(data,"order:id"), orderId, "订单ID字段缺失");
        Assert.assertEquals(parseJson(data,"order:patient_name"), "方超", "患者姓名存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_gender"), "1", "患者性别字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_age"), "31", "患者年龄字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:patient_phone"), "13817634203", "患者手机号字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:major_disease_id"), "33", "主诉疾病ID字段存储不正确");
        // TODO
        Assert.assertEquals(parseJson(data,"order:major_disease_name"), "", "主诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:minor_disease_id"), "32", "次诉疾病ID字段存储不正确");
        // TODO
        Assert.assertEquals(parseJson(data,"order:minor_disease_name"), "", "次诉疾病名称不正确");
        Assert.assertEquals(parseJson(data,"order:diagnosis"), "工程师", "病例描述字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_start_date"), "2017-04-09", "期望手术最早开始时间字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_due_date"), "2017-05-09", "期望手术最晚开始时间字段存储不正确");
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_id"), "43", "期望医院ID存储不正确");
        // TODO
        Assert.assertEquals(parseJson(data,"order:expected_surgery_hospital_name"), "","期望医院名称字段不正确");
        Assert.assertEquals(parseJson(data,"order:status"), "1000", "新建订单状态应当为1000");
        Assert.assertEquals(parseJson(data,"order:OrderStatusText"), "处理中", "新建订单状态描述应当为'处理中'");
        Assert.assertNotEquals(parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:order_number"), "", "订单号时间字段不正确");
        Assert.assertEquals(parseJson(data,"order:pics"), "", "订单号时间字段缺失");

    }

    @Test
    public void 创建订单_信息齐备_未登录用户() {

        String res = "";
        OrderProfile body = new OrderProfile(true);
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
    public void 创建订单_缺少患者姓名() {

        String res = "";
        OrderProfile body = new OrderProfile(true);
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
    public void 创建订单_缺少患者性别() {

        String res = "";
        OrderProfile body = new OrderProfile(true);
        body.body.getJSONObject("order").remove("patient_gender");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 创建订单_缺少患者年龄() {

        String res = "";
        OrderProfile body = new OrderProfile(true);
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
    public void 创建订单_缺少患者手机() {

        String res = "";
        OrderProfile body = new OrderProfile(true);
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
    public void 创建订单_缺少主诉疾病() {

        String res = "";
        OrderProfile order = new OrderProfile(true);

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
    public void 创建订单_缺少次诉疾病() {

        String res = "";
        OrderProfile order = new OrderProfile(true);
        order.body.getJSONObject("order").remove("minor_disease_id");
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void 创建订单_未认证医生不能创建订单() {

        String res = "";
        logger.info("创建一个新的未认证医生");
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        logger.info("创建成功");
        OrderProfile order = new OrderProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, order.body.toString(), tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210409");
    }


}

