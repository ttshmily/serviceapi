package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ttshmily on 28/4/2017.
 */
public class Order_ThreewayCall extends BaseTest {
    public static final Logger logger= Logger.getLogger(Order_ThreewayCall.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/threeWayCalling";
    public static String mock = false ? "/mockjs/1" : "";

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public static String ThreewayCall(String orderId, String result) {//success,undetermined,failed
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        res = Order_Detail.Detail(orderId);
        if (!Generator.parseJson(JSONObject.fromObject(res), "data:status").equals("2020")) {
            logger.error("当前订单状态无法进行三方通话");
            return Generator.parseJson(JSONObject.fromObject(res), "data:status");
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
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = Order_Detail.Detail(orderId);
        return Generator.parseJson(JSONObject.fromObject(res), "data:status");
    }

    @Test
    public void test_01_创建三方通话_结果为成功() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        String rcmdDoc = Generator.randomKey(KnowledgeBase.kb_doctor);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        String fee = String.valueOf(Generator.randomInt(4)+1);
        body.put("surgeryFee", fee);
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
        body.put("minor_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "success");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "3000");
        Assert.assertEquals(Generator.parseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(Generator.parseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(rcmdDoc));
        Assert.assertEquals(Generator.parseJson(data, "surgeon_fee"), fee);
    }

    @Test
    public void test_02_创建三方通话_结果为待定() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        String rcmdDoc = Generator.randomKey(KnowledgeBase.kb_doctor);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, rcmdDoc).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        String fee = String.valueOf(Generator.randomInt(4)+1);
        body.put("surgeryFee", fee);
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
        body.put("minor_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "undetermined");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2020");
        Assert.assertEquals(Generator.parseJson(data, "surgeon_id"), rcmdDoc);
        Assert.assertEquals(Generator.parseJson(data, "surgeon_name"), KnowledgeBase.kb_doctor.get(rcmdDoc));
//        Assert.assertEquals(parseJson(data, "surgeon_fee"), fee);
    }

    @Test
    public void test_03_创建三方通话_结果为不合作() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, "555").equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", "10000");
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "55");
        body.put("minor_disease_id","66");
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "failed");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
        //不合作以后，清除上级医生信息
//        Assert.assertEquals(UT.parseJson(data, "surgeon_id"), "555");
//        Assert.assertEquals(UT.parseJson(data, "surgeon_name"), KB.kb_doctor.get("555"));
//        Assert.assertEquals(parseJson(data, "surgeon_fee"), "10000");
    }

    @Test
    public void test_04_创建三方通话_不合作原因为空() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, "555").equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", "10000");
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "55");
        body.put("minor_disease_id","66");
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "failed");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2020");

        body.put("reject_reason", "");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2020");
    }

    @Test
    public void test_05_创建三方通话_成功后不能再次通话() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, "555").equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", Generator.randomInt(4)+1);
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "55");
        body.put("minor_disease_id","66");
        body.put("content", "自动创建的通话记录");
        body.put("audio_file", "http://www.automation.com");
        body.put("record_type", "success");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "3000");

        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "三方通话确认后，不能再次三方通话");
    }

    @Test
    public void test_06_创建三方通话_结果成功但缺少信息() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, "555").equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();

        body.put("surgeryFee", Generator.randomInt(4)+1);
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("calling_time", df.format(new Date()));
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("major_disease_id", "55");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("minor_disease_id", "66");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("content", "自动创建的通话记录");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("audio_file", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "status"), "3000");

        body.put("record_type", "success");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "3000");

    }

    @Test
    public void test_07_创建三方通话_结果为不合作无其他信息() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        if (!Order_RecommendDoctor.recommendDoctor(orderId, "555").equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", "");
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "");
        body.put("minor_disease_id","");
        body.put("content", "");
        body.put("audio_file", "");
        body.put("record_type", "failed");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
//        Assert.assertEquals(parseJson(data, "surgeon_id"), "555");
//        Assert.assertEquals(parseJson(data, "surgeon_name"), KB.kb_doctor.get("555"));
//        Assert.assertEquals(parseJson(data, "surgeon_fee"), "10000");
    }

    @Test
    public void test_08_创建三方通话_不合作医生信息记录在案() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        String expertId = Generator.randomExpertId();
        if (!Order_RecommendDoctor.recommendDoctor(orderId, expertId).equals("2020")) {
            Assert.fail("订单没有到达已推荐状态，无法进行三方通话");
        }
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("surgeryFee", "");
        body.put("calling_time", df.format(new Date()));
        body.put("major_disease_id", "");
        body.put("minor_disease_id","");
        body.put("content", "");
        body.put("audio_file", "");
        body.put("record_type", "failed");
        body.put("reject_reason", "http://www.automation.com");
        try {
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.Detail(orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "status"), "2000");
        Assert.assertNotNull(Generator.parseJson(data, "doctorFiledList"), expertId);
        Assert.assertEquals(Generator.parseJson(data, "doctorFiledList():surgeon_id"), expertId);
    }
}
