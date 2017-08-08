package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.DoctorProfile;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_RecommendDoctor extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/recommends";

    public static String s_RecommendDoctor(String orderId, String doctorId) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        res = Order_Detail.s_Detail(orderId);

        if (!Helper.s_ParseJson(JSONObject.fromObject(res), "data:status").equals("2000")) {
            logger.error("订单处于不可推荐状态");
            return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status");
        }
        JSONObject body = new JSONObject();
        body.put("surgeon_id",doctorId);
        body.put("content","自动化推荐的医生");
        HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        res = Order_Detail.s_Detail(orderId);
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status"); // 期望2020
    }

    @Test
    public void test_01_正常推荐() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        logger.debug(Order_ReceiveTask.s_ReceiveTask(order_number));

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String recommendedId = doc.get("expert_id");
        body.put("surgeon_id",recommendedId);
        body.put("content","自动化推荐的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), recommendedId);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), dp.body.getJSONObject("doctor").getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_user_id"), doc.get("id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_medical_title"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_academic_title"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_hospital"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_city_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_city_name"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_province_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_province_name"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_department"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_major"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_referrer_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_referrer_name"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_referrer_group_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_registration_time"));
    }

    @Test
    public void test_02_在三方通话成功前重新推荐() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String recommendedId = Generator.randomExpertId();
        body.put("surgeon_id",recommendedId);
        body.put("content","自动化推荐的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), recommendedId);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(recommendedId));

        // 重新推荐
        recommendedId = "666";
        body.replace("surgeon_id",recommendedId);
        body.replace("content","自动化重新推荐的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), recommendedId);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(recommendedId));

        // 重新推荐失败后，保留原先的上级医生信息
        String new_recommendedId = "666new_66666";
        body.replace("surgeon_id",new_recommendedId);
        body.replace("content","自动化重新推荐的不存在的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), recommendedId);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(recommendedId));
    }

    @Test(enabled = false)
    public void test_03_推荐和下级医生相同的用户() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        body.put("surgeon_id",mainDoctorId);
        body.put("content","和下级医生相同的上级医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "不应该推荐和发起医生相同的专家医生");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_id"));
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_name"));

    }

    @Test
    public void test_04_推荐不存在于用户表或医库中的医生() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String recommendedId = "444444444";
        body.put("surgeon_id",recommendedId);
        body.put("content","自动化推荐的不存在的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_id"), "不应该出现上级医生ID");
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_name"), "不应该出现上级医生姓名");


    }

    @Test
    public void test_06_推荐医生_无证操作() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String recommendedId = Generator.randomExpertId();
        body.put("surgeon_id",recommendedId);
        body.put("content","无证操作");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2000");
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_id"));
        Assert.assertNull(Helper.s_ParseJson(data, "surgeon_name"));

    }

    @Test
    public void test_07_在三方通话成功后不可以重新推荐() {

        String res = "";
        String order_number = CreateOrder.s_CreateOrder(mainToken); // create an order
        Order_ReceiveTask.s_ReceiveTask(order_number);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", order_number);
        JSONObject body = new JSONObject();
        String recommendedId = Generator.randomExpertId();
        body.put("surgeon_id",recommendedId);
        body.put("content","自动化推荐的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), recommendedId);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(recommendedId));

        if (!Order_ThreewayCall_V2.s_CallV2(order_number,"success").equals("3000")) {
            Assert.fail("三方确认失败，无法继续执行");
        }

        // 成功后重新推荐
        String new_recommendedId = "666";
        body.replace("surgeon_id",new_recommendedId);
        body.replace("content","自动化重新推荐的医生");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.s_Detail(order_number);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "3000");
    }
}
