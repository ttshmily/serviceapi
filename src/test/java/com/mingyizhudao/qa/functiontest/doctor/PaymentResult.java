package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
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

    public static String s_Result(String orderId, String token) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("payment", order);
        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), token, null);
        String status = "";
        String ispaid = "";
        String code = JSONObject.fromObject(res).getString("code");
        if (!code.equals("1000000")) {
            res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
            status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        } else {
            ispaid = JSONObject.fromObject(res).getJSONObject("data").getString("paid");
        }
        return ispaid.equals("true") ? "4000" : status;
    }

    @Test
    public void test_01_接口联通性() {

        String orderId = CreateOrder.s_CreateOrder(mainToken);
        if (orderId.isEmpty()) Assert.fail("创建订单失败，退出用例执行");

        s_Result(orderId, mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"1000");

        Order_ReceiveTask.s_ReceiveTask(orderId);
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"2000");

        Order_RecommendDoctor.s_RecommendDoctor(orderId, "23");
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"2020");

        Order_RecommendDoctor.s_RecommendDoctor(orderId, "24");
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"2020");

        Order_ThreewayCall_V2.s_CallV2(orderId, "failed");
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"2000");

        Order_RecommendDoctor.s_RecommendDoctor(orderId, "24");
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"2020");

        Order_ThreewayCall.s_Call(orderId, "success");
        s_Result(orderId,mainToken);
        Assert.assertEquals(s_Result(orderId, mainToken),"3000");
    }
}

