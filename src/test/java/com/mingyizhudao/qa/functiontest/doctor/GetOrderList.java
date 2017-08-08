package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_CertifySync_V2;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getorderlist";

    public static String s_List(String token) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendGet(host_doc+uri,"", token);
        JSONObject orderList = JSONObject.fromObject(res).getJSONObject("data");
        return String.valueOf(orderList.getJSONArray("order").size());
    }

    @Test
    public void test_01_获取订单列表_登录用户() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");

    }

    @Test
    public void test_02_获取订单列表_登录用户_订单列表会更新且按时间倒序() {
        String res = "";

        SendVerifyCode.s_Send();
        String tmpToken = CheckVerifyCode.s_Check();

        logger.info(tmpToken);
        DoctorProfile dp = new DoctorProfile(true);
        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        String docId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        UpdateDoctorProfile_V1.s_Update(tmpToken, dp);

        if (!RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(docId, "1").get("is_verified").equals("1")) {
            logger.error("认证医生失败，退出用例执行");
            Assert.fail("认证医生失败，退出用例执行");
        }

        logger.info("创建订单with tmpToken");
        String orderId1 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId2 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId2.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId3 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId3.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId4 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId4.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);
    }

    @Test
    public void test_03_获取订单列表_未登录用户() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210304", "应当提示未登录");
    }

    @Test
    public void test_04_获取订单列表_推荐医生后病历不展示上级医生信息() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败，退出执行");
        }
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("领取任务失败");
            Assert.fail("领取任务失败，退出执行");
        }
        String expert_id = Generator.randomExpertId();
        if (!Order_RecommendDoctor.s_RecommendDoctor(orderId, expert_id).equals("2020")) {
            logger.error("推荐医生失败");
            Assert.fail("推荐医生失败，退出执行");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");
    }

    @Test
    public void test_05_获取订单列表_三方通话成功后病历上展示上级医生信息() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败，退出执行");
        }
        if (!Order_ReceiveTask.s_ReceiveTask(orderId).equals("2000")) {
            logger.error("领取任务失败");
            Assert.fail("领取任务失败，退出执行");
        }
        String expert_id = Generator.randomExpertId();
        if (!Order_RecommendDoctor.s_RecommendDoctor(orderId, expert_id).equals("2020")) {
            logger.error("推荐医生失败");
            Assert.fail("推荐医生失败，退出执行");
        }
        if (!Order_ThreewayCall_V2.s_CallV2(orderId, "success").equals("3000")) {
            logger.error("确定三方通话失败");
            Assert.fail("确定三方通话失败，退出执行");
        }
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotEquals(Helper.s_ParseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():surgeon_id"), "手术医生ID字段不能缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():surgeon_name"), "手术医生姓名字段不能缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():surgeon_hospital"), "手术医生所在医院字段不能缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():surgeon_medical_title"), "手术医生技术职称字段不能缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data,"order():surgeon_academic_title"), "手术医生学术职称字段不能缺失");
    }
}
