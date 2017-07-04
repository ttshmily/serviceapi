package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.GetOrderDetail;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Cancel extends BaseTest {
// 待上传小结时，可以取消订单，此时已定金支付完成。
    public static final Logger logger= Logger.getLogger(Order_Cancel.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/cancelOrder";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_支付后取消订单() {

        String res = "";
        String orderId = Order_List.SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有待审核的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.send(agentPhone);
        String token = CheckVerifyCode.check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        HashMap<String, String> body = new HashMap<>();
        body.put("reason", "小姐审核不通过的列表选择原因");
        body.put("responsible", "选择责任方 - 下级医生原因");

        try {
            res = HttpRequest.sendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "取消订单接口失败");
        Assert.assertEquals(UT.parseJson(data, "order:status"), "4030");
        res = GetOrderDetail.getOrderDetail(token, orderId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order:header_info"), "选择责任方 - 下级医生原因");
    }
}
