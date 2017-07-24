package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/10.
 */
public class Order_ThreewayCall_V2 extends Order_ThreewayCall {
    public static final Logger logger= Logger.getLogger(Order_ThreewayCall_V2.class);
    public static final String version = "/api/v2";
    public static String uri = version+"/orders/{orderNumber}/threeWayCalling";
    public static String mock = false ? "/mockjs/1" : "";

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String ThreewayCallv2(String orderId, String result) {//success,undetermined,failed
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        res = Order_Detail.Detail(orderId);
        if (!UT.parseJson(JSONObject.fromObject(res), "data:status").equals("2020")) {
            logger.error("当前订单状态无法进行三方通话");
            return UT.parseJson(JSONObject.fromObject(res), "data:status");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", UT.randomInt(4)+1);
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "55");
        body.put("minor_disease_id","66");
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", result);
        body.put("reject_reason", "http://www.automation.com");
        body.put("platform_proportion", "5");
        body.put("agent_proportion", "15");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return UT.parseJson(JSONObject.fromObject(res), "data:status");
    }

    @Test
    public void test_01_成功_传入检查比例是否正确() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        String rcmdDoc = UT.randomExpertId();
        if (!Order_RecommendDoctor.recommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        long fee =UT.randomInt(4)*100;
        body.put("surgeryFee", fee); // 以分作为单位
        body.put("calling_time", df.format(new Date()));
        String majorDiseaseId = UT.randomDiseaseId();
        String minorDiseaseId = UT.randomDiseaseId();
        body.put("major_disease_id", majorDiseaseId);
        body.put("minor_disease_id",minorDiseaseId);
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "success");
        body.put("reject_reason", "http://www.automation.com");
        long platform_p = UT.randomInt(100);
        long agent_p = UT.randomInt(20);
        body.put("platform_proportion", String.valueOf(platform_p)); //百分比
        body.put("agent_proportion", String.valueOf(agent_p)); //百分比
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "status"), "3000");
        Assert.assertEquals(UT.parseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(UT.parseJson(data, "surgeon_name"), UT.expertName(rcmdDoc));
        Assert.assertEquals(UT.parseJson(data, "surgeon_fee"), String.valueOf(fee));
        Assert.assertEquals(UT.parseJson(data, "conference_call_major_disease_id"), majorDiseaseId);
        Assert.assertEquals(UT.parseJson(data, "conference_call_minor_disease_id"), minorDiseaseId);
//        Assert.assertEquals(UT.parseJson(data, "major_disease_id"), majorDiseaseId);
//        Assert.assertEquals(UT.parseJson(data, "minor_disease_id"), minorDiseaseId);
//        Assert.assertEquals(UT.parseJson(data, "major_disease_name"), UT.diseaseName(majorDiseaseId));
//        Assert.assertEquals(UT.parseJson(data, "minor_disease_name"), UT.diseaseName(minorDiseaseId));
        Assert.assertEquals(UT.parseJson(data, "agent_fee"), String.valueOf(fee*agent_p/100));
        Assert.assertEquals(UT.parseJson(data, "platform_fee"), String.valueOf(fee*platform_p/100));
        Assert.assertEquals(UT.parseJson(data, "pre_order_fee"), String.valueOf(fee*(agent_p+platform_p)/100));
    }

    @Test
    public void test_02_待定_传入检查比例是否正确() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        String rcmdDoc = UT.randomKey(KB.kb_doctor);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        String fee = String.valueOf(UT.randomInt(4)+1);
        body.put("surgeryFee", fee);
        body.put("calling_time", df.format(new Date()));
        String majorDiseaseId = UT.randomDiseaseId();
        String minorDiseaseId = UT.randomDiseaseId();
        body.put("major_disease_id", majorDiseaseId);
        body.put("minor_disease_id",minorDiseaseId);
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "undetermined");
        body.put("reject_reason", "http://www.automation.com");
        body.put("platform_proportion", "5");
        body.put("agent_proportion", "15");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "status"), "2020");
        Assert.assertEquals(UT.parseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(UT.parseJson(data, "surgeon_name"), UT.expertName(rcmdDoc));
        Assert.assertEquals(UT.parseJson(data, "conference_call_major_disease_id"), majorDiseaseId);
        Assert.assertEquals(UT.parseJson(data, "conference_call_minor_disease_id"), minorDiseaseId);
    }
}
