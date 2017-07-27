package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.doctor.CreateSurgeryBriefs;
import com.mingyizhudao.qa.testcase.doctor.GetOrderDetail_V1;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Review extends BaseTest {

    public static final Logger logger = Logger.getLogger(Order_Review.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/reviewResult";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_正常审核手术小结_通过() {

        String res = "";
        String orderId = Order_List.SelectPaidOrder();
        if (orderId==null) {
            Assert.fail("没有待提交小结的的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.send(agentPhone);
        String token = CheckVerifyCode.check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        String status = CreateSurgeryBriefs.Brief(orderId, token);
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
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "审核订单接口失败");
        Assert.assertEquals(Generator.parseJson(data, "status"), "4020");
    }

    @Test
    public void test_02_正常审核手术小结_不通过() {

        String res = "";
        String orderId = Order_List.SelectPaidOrder();
        if (orderId==null) {
            Assert.fail("没有待审核的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.send(agentPhone);
        String token = CheckVerifyCode.check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        String status = CreateSurgeryBriefs.Brief(orderId, token);
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
            res = HttpRequest.sendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "审核订单接口失败");
        Assert.assertEquals(Generator.parseJson(data, "status"), "4000");
        res = GetOrderDetail_V1.MyInitiateOrder(token, orderId);
        checkResponse(res);
        Assert.assertEquals(Generator.parseJson(data, "order:header_info"), "自动化推荐之前据拒订单的理由");
    }
}
