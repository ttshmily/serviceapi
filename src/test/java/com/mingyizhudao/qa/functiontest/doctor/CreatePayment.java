package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 27/4/2017.
 */
public class CreatePayment extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/createPayment";

    public static String s_Pay(String orderId, String token) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject body = new JSONObject();
        JSONObject payment = new JSONObject();
        payment.put("orderNumber", orderId);
        payment.put("returnUrl", "http://www.mingyizhudao.com");
        body.put("payment", payment);

        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), token);
        // TODO: JSONObject.fromObject(res).getJSONObject()
        return "TODO";
    }

    @Test
    public void test_01_创建支付订单() {

//        String userToken = "";
//        String userExpertId = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");
//        userExpertId = mainDoctorInfo.get("expert_id");

        String res = "";
        JSONObject body = new JSONObject();
        JSONObject payment = new JSONObject();

        String orderId = CreateOrder.s_CreateOrder(mainToken);
        Order_ReceiveTask.s_ReceiveTask(orderId);
        Order_RecommendDoctor.s_RecommendDoctor(orderId, mainExpertId);
        if (!Order_ThreewayCall_V2.s_CallV2(orderId, "success").equals("3000")) {
            Assert.fail("未到支付状态，不能进行支付");
        }
        res = GetOrderDetail_V1.s_MyInitiateOrder(mainToken, orderId);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "order:pre_order_fee"));

        payment.put("orderNumber", orderId);
        payment.put("returnUrl", "http://www.mingyizhudao.com");
        body.put("payment", payment);

        res = HttpRequest.s_SendPost(host_doc + uri, body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "支付调用失败");
        Assert.assertNotNull(Helper.s_ParseJson(data, "payment:url"), "返回的订单ID格式有误");
    }
}
