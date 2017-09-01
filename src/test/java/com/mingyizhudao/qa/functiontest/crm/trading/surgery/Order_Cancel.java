package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.GetOrderDetail_V1;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Cancel extends BaseTest {
// 待上传小结时，可以取消订单，此时已定金支付完成。
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/cancelOrder";

    @Test
    public void test_01_支付后取消订单() {

        String res = "";
        String orderId = Order_List.s_SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有待提交小结的的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.s_Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("reason", "小节审核不通过的列表选择原因");
        body.put("responsible", "责任方：下级医生原因");

        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "取消订单接口失败");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "4030");
        res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order:header_info"), "责任方：下级医生原因");
    }
}
