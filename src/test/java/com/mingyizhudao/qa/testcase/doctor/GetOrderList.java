package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.testcase.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.fail;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderList extends BaseTest{

    public static final Logger logger= Logger.getLogger(GetOrderList.class);
    public static String uri = "/api/getorderlist";
    public static String mock = false ? "/mockjs/1" : "";

    public static String List(String token) {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", token);
        } catch (IOException e) {
            logger.debug(HttpRequest.unicodeString(res));
            logger.error(e);
            return null;
        }
        JSONObject orderList = JSONObject.fromObject(res).getJSONObject("data");
        return String.valueOf(orderList.getJSONArray("order").size());

    }
    @Test
    public void test_01_获取订单列表_登录用户() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");

    }

    @Test
    public void test_02_获取订单列表_登录用户_订单列表会更新且按时间倒序() {
        String res = "";

        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();

        logger.info(tmpToken);
        DoctorProfile dp = new DoctorProfile(true);
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        String docId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        UpdateDoctorProfile.updateDoctorProfile(tmpToken, dp);

        if (!RegisteredDoctor_Certify.certify(docId, "1").equals("1")) {
            logger.error("认证医生失败，退出用例执行");
            Assert.fail("认证医生失败，退出用例执行");
        }

        logger.info("创建订单with tmpToken");
        String orderId1 = CreateOrder.CreateOrder(tmpToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId2 = CreateOrder.CreateOrder(tmpToken);
        if (orderId2.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId3 = CreateOrder.CreateOrder(tmpToken);
        if (orderId3.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId4 = CreateOrder.CreateOrder(tmpToken);
        if (orderId4.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId1);
    }

    @Test
    public void test_03_获取订单列表_未登录用户() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210304", "应当提示未登录");
    }

    @Test
    public void test_04_获取订单列表_推荐医生后病历不展示上级医生信息() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败，退出执行");
        }
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("领取任务失败");
            Assert.fail("领取任务失败，退出执行");
        }
        String expert_id = UT.randomExpertId();
        if (!Order_RecommendDoctor.recommendDoctor(orderId, expert_id).equals("2020")) {
            logger.error("推荐医生失败");
            Assert.fail("推荐医生失败，退出执行");
        }
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");
    }

    @Test
    public void test_05_获取订单列表_三方通话成功后病历上展示上级医生信息() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败，退出执行");
        }
        if (!Order_ReceiveTask.receiveTask(orderId).equals("2000")) {
            logger.error("领取任务失败");
            Assert.fail("领取任务失败，退出执行");
        }
        String expert_id = UT.randomExpertId();
        if (!Order_RecommendDoctor.recommendDoctor(orderId, expert_id).equals("2020")) {
            logger.error("推荐医生失败");
            Assert.fail("推荐医生失败，退出执行");
        }
        if (!Order_ThreewayCall.ThreewayCall(orderId, "success").equals("3000")) {
            logger.error("确定三方通话失败");
            Assert.fail("确定三方通话失败，退出执行");
        }
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(UT.parseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():surgeon_id"), "手术医生ID字段不能缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():surgeon_name"), "手术医生姓名字段不能缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():surgeon_hospital"), "手术医生所在医院字段不能缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():surgeon_medical_title"), "手术医生技术职称字段不能缺失");
        Assert.assertNotNull(UT.parseJson(data,"order():surgeon_academic_title"), "手术医生学术职称字段不能缺失");
    }
}
