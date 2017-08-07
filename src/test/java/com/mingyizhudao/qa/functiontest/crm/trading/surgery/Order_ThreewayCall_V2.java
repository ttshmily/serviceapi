package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/10.
 */
public class Order_ThreewayCall_V2 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v2";
    public static String uri = version+"/orders/{orderNumber}/threeWayCalling";

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String s_CallV2(String orderId, String result) {//success,undetermined,failed
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        res = Order_Detail.s_Detail(orderId);
        if (!Helper.s_ParseJson(JSONObject.fromObject(res), "data:status").equals("2020")) {
            logger.error("当前订单状态无法进行三方通话");
            return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", Generator.randomInt(4)+1);
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "55");
        body.put("minor_disease_id","66");
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", result);
        body.put("reject_reason", "http://www.automation.com");
        body.put("platform_proportion", "5");
        body.put("agent_proportion", "15");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        res = Order_Detail.s_Detail(orderId);
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:status");
    }

    @Test
    public void test_01_成功_传入检查比例是否正确() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        Order_ReceiveTask.s_ReceiveTask(orderId);
        String rcmdDoc = Generator.randomExpertId();
        if (!Order_RecommendDoctor.s_RecommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        long fee = Generator.randomInt(4)*100;
        body.put("surgeryFee", fee); // 以分作为单位
        body.put("calling_time", df.format(new Date()));
        String majorDiseaseId = Generator.randomDiseaseId();
        String minorDiseaseId = Generator.randomDiseaseId();
        body.put("major_disease_id", majorDiseaseId);
        body.put("minor_disease_id",minorDiseaseId);
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "success");
        body.put("reject_reason", "http://www.automation.com");
        long platform_p = Generator.randomInt(100);
        long agent_p = Generator.randomInt(20);
        body.put("platform_proportion", String.valueOf(platform_p)); //百分比
        body.put("agent_proportion", String.valueOf(agent_p)); //百分比
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), Generator.expertName(rcmdDoc));
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_fee"), String.valueOf(fee));
        Assert.assertEquals(Helper.s_ParseJson(data, "conference_call_major_disease_id"), majorDiseaseId);
        Assert.assertEquals(Helper.s_ParseJson(data, "conference_call_minor_disease_id"), minorDiseaseId);
//        Assert.assertEquals(UT.s_ParseJson(data, "major_disease_id"), majorDiseaseId);
//        Assert.assertEquals(UT.s_ParseJson(data, "minor_disease_id"), minorDiseaseId);
//        Assert.assertEquals(UT.s_ParseJson(data, "major_disease_name"), UT.diseaseName(majorDiseaseId));
//        Assert.assertEquals(UT.s_ParseJson(data, "minor_disease_name"), UT.diseaseName(minorDiseaseId));
        Assert.assertEquals(Helper.s_ParseJson(data, "agent_fee"), String.valueOf(fee*agent_p/100));
        Assert.assertEquals(Helper.s_ParseJson(data, "platform_fee"), String.valueOf(fee*platform_p/100));
        Assert.assertEquals(Helper.s_ParseJson(data, "pre_order_fee"), String.valueOf(fee*(agent_p+platform_p)/100));
    }

    @Test
    public void test_02_待定_传入检查比例是否正确() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        Order_ReceiveTask.s_ReceiveTask(orderId);
        String rcmdDoc = Generator.randomKey(KnowledgeBase.kb_doctor);
        if (!Order_RecommendDoctor.s_RecommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        String fee = String.valueOf(Generator.randomInt(4)+1);
        body.put("surgeryFee", fee);
        body.put("calling_time", df.format(new Date()));
        String majorDiseaseId = Generator.randomDiseaseId();
        String minorDiseaseId = Generator.randomDiseaseId();
        body.put("major_disease_id", majorDiseaseId);
        body.put("minor_disease_id",minorDiseaseId);
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "undetermined");
        body.put("reject_reason", "http://www.automation.com");
        body.put("platform_proportion", "5");
        body.put("agent_proportion", "15");
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "2020");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeon_name"), Generator.expertName(rcmdDoc));
        Assert.assertEquals(Helper.s_ParseJson(data, "conference_call_major_disease_id"), majorDiseaseId);
        Assert.assertEquals(Helper.s_ParseJson(data, "conference_call_minor_disease_id"), minorDiseaseId);
    }
}
