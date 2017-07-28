package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class PaymentResult extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/paymentResult";
    public static String mock = false ? "/mockjs/1" : "";

    public static String result(String orderId, String token) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("payment", order);
        try {
            res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), token, null);
//            logger.debug(res);
        } catch (IOException e) {
            logger.error(e);
        }
        String status = "";
        String ispaid = "";
        String code = JSONObject.fromObject(res).getString("code");
        if (!code.equals("1000000")) {
            res = GetOrderDetail_V1.MyInitiateOrder(token, orderId);
            status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        } else {
            ispaid = JSONObject.fromObject(res).getJSONObject("data").getString("paid");
        }
        return ispaid.equals("true") ? "4000" : status;
    }

    @Test
    public void test_01_接口联通性() {

        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) Assert.fail("创建订单失败，退出用例执行");

        result(orderId, mainToken);
        Assert.assertEquals(result(orderId, mainToken),"1000");

        Order_ReceiveTask.receiveTask(orderId);
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"2000");

        Order_RecommendDoctor.recommendDoctor(orderId, "23");
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"2020");

        Order_RecommendDoctor.recommendDoctor(orderId, "24");
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"2020");

        Order_ThreewayCall.ThreewayCall(orderId, "failed");
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"2000");

        Order_RecommendDoctor.recommendDoctor(orderId, "24");
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"2020");

        Order_ThreewayCall.ThreewayCall(orderId, "success");
        result(orderId,mainToken);
        Assert.assertEquals(result(orderId, mainToken),"3000");
    }
}

