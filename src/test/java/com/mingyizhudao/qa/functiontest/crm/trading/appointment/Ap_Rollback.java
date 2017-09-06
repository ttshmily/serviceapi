package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

public class Ap_Rollback extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/cleanSurgeons";

    public static boolean s_Rollback(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("content", "客服记录内容");
        body.put("reason", "官方选择理由");
        String res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("1010");
    }

    @Test
    public void test_01_回退面诊订单() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        if (!s_Take(orderNumber)) {
            Assert.fail("创建状态2000订单失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("content", "重新风控content");
        body.put("reason", "重新风控reason");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "status"), "1010");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_id"), "");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_name"), "");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_hospital"), "");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_department"), "");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_medical_title"), "");
        Assert.assertNull(s_ParseJson(data, "minor_recommended_doctor_id"), "");
        Assert.assertNull(s_ParseJson(data, "minor_recommended_doctor_name"), "");
        Assert.assertNull(s_ParseJson(data, "minor_recommended_doctor_hospital"), "");
        Assert.assertNull(s_ParseJson(data, "minor_recommended_doctor_department"), "");
        Assert.assertNull(s_ParseJson(data, "minor_recommended_doctor_medical_title"), "");
    }

    @Test
    public void test_02_回退面诊订单理由不完整() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        if (!s_Take(orderNumber)) {
            Assert.fail("创建状态2000订单失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("content", "重新风控content");
//        body.put("reason", "重新风控reason");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "reason必选");
    }

}
