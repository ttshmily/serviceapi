package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.SurgeryBrief;
import com.mingyizhudao.qa.functiontest.crm.*;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CreateSurgeryBriefs extends BaseTest {
    public static final Logger logger= Logger.getLogger(CreateSurgeryBriefs.class);
    public static String uri = "/api/createsurgeryBriefs/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    public static String Brief(String orderId, String token) {

        String res = "";
        res = Order_Detail.Detail(orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        if (!status.equals("4000")) {
            logger.error("订单未支付，无法上传手术小结");
            return status;
        }
        SurgeryBrief sb = new SurgeryBrief(true);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status;
    }

    @Test
    public void test_01_上传手术小结() {
        String res = "";
        String orderId = Order_List.SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有已支付的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        SurgeryBrief sb = new SurgeryBrief(true);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.MyInitiateOrder(token, orderId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONObject order = data.getJSONObject("order");
        Assert.assertEquals(order.getString("status"), "4010", "上传完成后状态不为4010");
        Assert.assertEquals(order.getString("surgery_brief_date").substring(0, 10), sb.body.getJSONObject("order").getString("surgery_brief_date").replace('-', '/'));
        Assert.assertEquals(order.getString("surgery_brief_description"), sb.body.getJSONObject("order").getString("surgery_brief_description"));
        Assert.assertEquals(order.getString("surgery_brief_surgery_id"), sb.body.getJSONObject("order").getString("surgery_brief_surgery_id"));
        Assert.assertEquals(order.getString("surgery_brief_surgery_name"), Generator.surgeryName(sb.body.getJSONObject("order").getString("surgery_brief_surgery_id")));
        Assert.assertEquals(order.getString("surgery_brief_final_diagnosed_disease_id"), sb.body.getJSONObject("order").getString("surgery_brief_final_diagnosed_disease_id"));
        Assert.assertEquals(order.getString("surgery_brief_final_diagnosed_disease_name"), Generator.diseaseName(sb.body.getJSONObject("order").getString("surgery_brief_final_diagnosed_disease_id")));
        Assert.assertEquals(order.getString("surgery_brief_hospital_id"), sb.body.getJSONObject("order").getString("surgery_brief_hospital_id"));
        Assert.assertEquals(order.getString("surgery_brief_hospital_name"), Generator.hospitalName(sb.body.getJSONObject("order").getString("surgery_brief_hospital_id")));
    }

    @Test
    public void test_02_上传手术小结_缺少字段() {
        String res = "";
        String orderId = Order_List.SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有已支付的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        SurgeryBrief sb1 = new SurgeryBrief(true);
        sb1.body.getJSONObject("order").remove("surgery_brief_surgery_id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb1.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryBrief sb2 = new SurgeryBrief(true);
        sb2.body.getJSONObject("order").remove("surgery_brief_final_diagnosed_disease_id");
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb2.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryBrief sb3 = new SurgeryBrief(true);
        sb3.body.getJSONObject("order").remove("surgery_brief_date");
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb3.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryBrief sb4 = new SurgeryBrief(true);
        sb4.body.getJSONObject("order").remove("surgery_brief_hospital_id");
        try {
            res = HttpRequest.s_SendPut(host_doc + uri, sb4.body.toString(), token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }
}
