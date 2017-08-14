package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Appointment;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Rollback.s_Rollback;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
public class Ap_AddAccount extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/addAlipayAccount";

    public static boolean s_AddPayAccount(String orderNumber) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("doctor_account_info", randomPhone());
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        String code = JSONObject.fromObject(res).getString("code");
        return code.equals("1000000");
    }
    @Test
    public void test_01_添加账号_手机() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("待支付订单生成失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("doctor_account_info", randomPhone());
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(s_ParseJson(data, "doctor_account_name"), body.getString("doctor_account_name"));
        Assert.assertEquals(s_ParseJson(data, "doctor_account_info"), body.getString("doctor_account_info"));
        Assert.assertEquals(s_ParseJson(data, "appointment_fee_remark"), body.getString("appointment_fee_remark"));
    }

    @Test
    public void test_02_添加账号_邮箱() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("待支付订单生成失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("doctor_account_info", mainOperatorId);
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(s_ParseJson(data, "doctor_account_name"), body.getString("doctor_account_name"));
        Assert.assertEquals(s_ParseJson(data, "doctor_account_info"), body.getString("doctor_account_info"));
        Assert.assertEquals(s_ParseJson(data, "appointment_fee_remark"), body.getString("appointment_fee_remark"));
    }

    @Test
    public void test_03_添加账号_缺少姓名() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("待支付订单生成失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_info", mainOperatorId);
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_04_添加账号_缺少账户() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("待支付订单生成失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_05_添加账号_非待支付状态() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("doctor_account_info", randomPhone());
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        if(!s_Take(orderNumber)) {
            Assert.fail("处理中订单生成失败");
        }
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_06_修改账号() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber)) {
            Assert.fail("待支付订单生成失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("doctor_account_name", "收款姓名"+randomString(2));
        body.put("doctor_account_info", randomPhone());
        body.put("appointment_fee_remark", "添加说明");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        body.put("doctor_account_info", mainOperatorId);
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "doctor_account_name"), body.getString("doctor_account_name"));
        Assert.assertEquals(s_ParseJson(data, "doctor_account_info"), body.getString("doctor_account_info"));
        Assert.assertEquals(s_ParseJson(data, "appointment_fee_remark"), body.getString("appointment_fee_remark"));
    }
}
