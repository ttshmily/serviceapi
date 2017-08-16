package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Appointment;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_CreatePayment.s_CreatePayment;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;

public class Ap_PayUrl extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/appointments/{orderNumber}/payments";


    public void test_01_显示支付链接_APPOINTMENT() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_CreatePayment(orderNumber, "APPOINTMENT")) {
            Assert.fail("订单未生成支付链接");
        }

        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(data.getJSONArray("list").getJSONObject(0).getString("id"));
    }


    public void test_02_显示支付链接_PLATFORM() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_CreatePayment(orderNumber, "PLATFORM")) {
            Assert.fail("订单未生成支付链接");
        }

        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(data.getJSONArray("list").getJSONObject(0).getString("id"));
    }


    public void test_03_显示支付链接_DOCTOR() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_CreatePayment(orderNumber, "DOCTOR")) {
            Assert.fail("订单未生成支付链接");
        }

        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(data.getJSONArray("list").getJSONObject(0).getString("id"));
    }
}
