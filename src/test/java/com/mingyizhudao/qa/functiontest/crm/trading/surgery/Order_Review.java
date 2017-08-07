package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.doctor.CreateSurgeryBriefs;
import com.mingyizhudao.qa.functiontest.doctor.GetOrderDetail_V1;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Review extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/reviewResult";

    @Test
    public void test_01_正常审核手术小结_通过() {

        String res = "";
        String orderId = Order_List.s_SelectPaidOrder();
        if (orderId==null) {
            Assert.fail("没有待提交小结的的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.s_Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        String status = CreateSurgeryBriefs.s_Brief(orderId, token);
        if (!status.equals("4010")) {
            logger.error("不是待审核状态");
            Assert.fail();
        }
        HashMap<String, String> pathValue=new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("reason", "固定原因");
        body.put("reps_content", "客服原因");
        body.put("result", "true");//TODO 审核结果
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "审核订单接口失败");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "4020");
    }

    @Test
    public void test_02_正常审核手术小结_不通过() {

        String res = "";
        String orderId = Order_List.s_SelectPaidOrder();
        if (orderId==null) {
            Assert.fail("没有待审核的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.s_Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        String status = CreateSurgeryBriefs.s_Brief(orderId, token);
        if (!status.equals("4010")) {
            logger.error("不是待审核状态");
            Assert.fail();
        }
        HashMap<String, String> pathValue=new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        body.put("reason", "小姐审核不通过的列表选择原因");
        body.put("reps_content", "客服原因");
        body.put("result", "false");
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "审核订单接口失败");
        Assert.assertEquals(Helper.s_ParseJson(data, "status"), "4000");
        res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order:header_info"), "自动化推荐之前据拒订单的理由");
    }
}
