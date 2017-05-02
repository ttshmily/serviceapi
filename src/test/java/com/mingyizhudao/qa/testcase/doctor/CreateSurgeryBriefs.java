package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.crm.Order_Detail;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.testcase.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CreateSurgeryBriefs extends BaseTest {
    public static final Logger logger= Logger.getLogger(CreateSurgeryBriefs.class);
    public static String uri = "/api/createsurgeryBriefs/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    public static String brief(String orderId, String token) {

        String res = "";
        res = Order_Detail.Detail(orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        if (!status.equals("4000")) {
            logger.error("订单未支付，无法上传手术小结");
            return status;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("surgery_brief_surgeon_name", "自动化直刀医生");
        order.put("surgery_brief_date", df.format(new Date()));
        order.put("surgery_brief_description", "自动化手术小结描述");
        order.put("surgery_brief_final_diagnosed_disease_id", "90");
        order.put("surgery_brief_surgery_id", "45");
        body.put("order", order);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendPut(host_doc + uri, body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        return status;
    }

    @Test
    public void test_01_上传手术小结() {
        String res = "";
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        Order_RecommendDoctor.recommendDoctor(orderId, "666");
        Order_ThreewayCall.ThreewayCall(orderId, "success");
        CreatePayment.pay(orderId, mainToken); // 期望 3000 -> 4000
        // TODO: Can't proceed since payment incompleted.
        if (!PaymentResult.result(orderId, mainToken).equals("4000")) {
            logger.error("订单未支付，无法上传手术小结");
            Assert.fail("前置条件不符，退出用例");
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("surgery_brief_surgeon_name", "自动化直刀医生");
        order.put("surgery_brief_date", df.format(new Date()));
        order.put("surgery_brief_description", "自动化手术小结描述");
        order.put("surgery_brief_final_diagnosed_disease_id", "90");
        order.put("surgery_brief_surgery_id", "45");
        body.put("order", order);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendPut(host_doc + uri, body.toString(), mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(status, "4010", "上传完成后状态不为4010");

    }

    @Test
    public void test_02_上传手术小结_缺少字段() {

        Assert.fail("Can't proceed since payment incompleted.");

    }
}
