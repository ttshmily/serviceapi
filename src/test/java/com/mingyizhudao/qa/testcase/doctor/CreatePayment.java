package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.testcase.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 27/4/2017.
 */
public class CreatePayment extends BaseTest {

    public static final Logger logger= Logger.getLogger(CreatePayment.class);
    public static String uri = "/api/createPayment";
    public static String mock = false ? "/mockjs/1" : "";

    public static String pay(String orderId, String token) {

        String res = "";
        JSONObject body = new JSONObject();
        JSONObject payment = new JSONObject();
        payment.put("orderNumber", orderId);
        payment.put("returnUrl", "http://www.mingyizhudao.com");
        body.put("payment", payment);

        try {
            res = HttpRequest.sendPost(host_doc + uri, body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
        // TODO: JSONObject.fromObject(res).getJSONObject()
        return "TODO";
    }

    @Test
    public void test_01_创建支付订单() {

        String res = "";
        JSONObject body = new JSONObject();
        JSONObject payment = new JSONObject();

        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        Order_RecommendDoctor.recommendDoctor(orderId, mainDoctorId);
        if (!Order_ThreewayCall.ThreewayCall(orderId, "success").equals("3000")) {
            Assert.fail("未到支付状态，不能进行支付");
        }
        res = GetOrderDetail_V1.MyInitiateOrder(mainToken, orderId);
        checkResponse(res);
        Assert.assertNotNull(UT.parseJson(data, "order:pre_order_fee"));

        payment.put("orderNumber", orderId);
        payment.put("returnUrl", "http://www.mingyizhudao.com");
        body.put("payment", payment);

        try {
            res = HttpRequest.sendPost(host_doc + uri, body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000", "支付调用失败");
            Assert.assertNotNull(UT.parseJson(data, "payment:url"), "返回的订单ID格式有误");
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
