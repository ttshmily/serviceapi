package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.Appointment;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Rollback.s_Rollback;
import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class Ap_Close extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/cancelBeforePayment";

    public static boolean s_Close(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String res = s_SendPost(host_appointment + uri, "", crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("9000") ? true : false;
    }

    @Test
    public void test_01_关闭1000的订单() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = s_SendPost(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    @Test
    public void test_02_关闭2000的订单() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if (!s_Take(orderNumber)) {
            Assert.fail("设定订单2000状态失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = s_SendPost(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    @Test
    public void test_03_关闭3000的订单() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("设定订单3000状态失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = s_SendPost(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    @Test
    public void test_04_关闭回退到1000的订单() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if (!s_Take(orderNumber) || !s_Rollback(orderNumber)) {
            Assert.fail("设定订单3000状态失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = s_SendPost(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

}
