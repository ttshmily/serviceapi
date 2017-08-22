package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.SurgeryOrder;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_Detail;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_List;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CreateSurgeryBriefs extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/createsurgeryBriefs/{orderId}";

    public static String s_Brief(String orderId, String token) {

        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = Order_Detail.s_Detail(orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        if (!status.equals("4000")) {
            logger.error("订单未支付，无法上传手术小结");
            return status;
        }
        SurgeryOrder su = new SurgeryOrder("brief");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(su).toString(), token, pathValue);
        status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status;
    }

    @Test
    public void test_01_上传手术小结() {
        String res = "";
        String orderId = Order_List.s_SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有已支付的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.s_Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }
        SurgeryOrder su = new SurgeryOrder("brief");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(su).toString(), token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetOrderDetail_V1.s_MyInitiateOrder(token, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONObject order = data.getJSONObject("order");
        Assert.assertEquals(order.getString("status"), "4010", "上传完成后状态不为4010");
        Assert.assertEquals(order.getString("surgery_brief_date").substring(0, 10), su.getBrief().getSurgery_brief_date().replace('-', '/'));
        Assert.assertEquals(order.getString("surgery_brief_description"), su.getBrief().getSurgery_brief_description());
        Assert.assertEquals(order.getString("surgery_brief_surgery_id"), su.getBrief().getSurgery_brief_surgery_id());
        Assert.assertEquals(order.getString("surgery_brief_surgery_name"), Generator.surgeryName(su.getBrief().getSurgery_brief_surgery_id()));
        Assert.assertEquals(order.getString("surgery_brief_final_diagnosed_disease_id"), su.getBrief().getSurgery_brief_final_diagnosed_disease_id());
        Assert.assertEquals(order.getString("surgery_brief_final_diagnosed_disease_name"), Generator.diseaseName(su.getBrief().getSurgery_brief_final_diagnosed_disease_id()));
        Assert.assertEquals(order.getString("surgery_brief_hospital_id"), su.getBrief().getSurgery_brief_hospital_id());
        Assert.assertEquals(order.getString("surgery_brief_hospital_name"), Generator.hospitalName(su.getBrief().getSurgery_brief_hospital_id()));
    }

    @Test
    public void test_02_上传手术小结_缺少字段() {
        String res = "";
        String orderId = Order_List.s_SelectPaidOrder();
        if (orderId == null) {
            Assert.fail("没有已支付的订单");
        }
        String agentPhone = JSONObject.fromObject(Order_Detail.s_Detail(orderId)).getJSONObject("data").getString("agent_phone");
        SendVerifyCode.s_Send(agentPhone);
        String token = CheckVerifyCode.s_Check(agentPhone);
        if (token == null) {
            logger.error("没有获取到token");
            Assert.fail();
        }

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);

        SurgeryOrder su1 = new SurgeryOrder("Brief");
        JsonConfig jsonConfig1 = new JsonConfig();
        jsonConfig1.setExcludes(new String[]{"surgery_brief_surgery_id"});
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(JSONSerializer.toJSON(su1, jsonConfig1)).toString(), token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryOrder su2 = new SurgeryOrder("Brief");
        JsonConfig jsonConfig2 = new JsonConfig();
        jsonConfig2.setExcludes(new String[]{"surgery_brief_final_diagnosed_disease_id"});
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(JSONSerializer.toJSON(su2, jsonConfig2)).toString(), token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryOrder su3 = new SurgeryOrder("Brief");
        JsonConfig jsonConfig3 = new JsonConfig();
        jsonConfig3.setExcludes(new String[]{"surgery_brief_date"});
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(JSONSerializer.toJSON(su3, jsonConfig3)).toString(), token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        SurgeryOrder su4 = new SurgeryOrder("Brief");
        JsonConfig jsonConfig4 = new JsonConfig();
        jsonConfig4.setExcludes(new String[]{"surgery_brief_hospital_id"});
        res = HttpRequest.s_SendPut(host_doc + uri, JSONObject.fromObject(JSONSerializer.toJSON(su4, jsonConfig4)).toString(), token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
