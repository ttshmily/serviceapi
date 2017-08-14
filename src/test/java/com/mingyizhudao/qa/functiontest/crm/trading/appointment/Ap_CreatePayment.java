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
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Detail.s_Detail;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;

public class Ap_CreatePayment extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/appointments/{orderNumber}/createPayment";

    public static boolean s_CreatePayment(String orderNumber, String type) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", type);//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        String res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        return JSONObject.fromObject(res).getString("code").equals("1000000");
    }

    @Test
    public void test_01_创建支付链接_APPOINTMENT() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        String platform_fee = data.getString("platform_fee");
        String doctor_fee = data.getString("doctor_fee");
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
        Assert.assertEquals(data.getString("pay_type"), "APPOINTMENT");
        Assert.assertEquals(data.getString("order_appointment_fee"), String.valueOf(Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee)));
        Assert.assertEquals(data.getString("receivable_fee"), String.valueOf(Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee)));
        String paymentId = data.getString("payment_number");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("first_income_payment_id"), paymentId);
    }

    @Test
    public void test_02_创建支付链接_DOCTOR() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        String platform_fee = data.getString("platform_fee");
        String doctor_fee = data.getString("doctor_fee");
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "DOCTOR");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
        Assert.assertEquals(data.getString("pay_type"), "DOCTOR");
        Assert.assertEquals(data.getString("order_appointment_fee"), String.valueOf(Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee)));
        Assert.assertEquals(data.getString("receivable_fee"), doctor_fee);
        String paymentId = data.getString("payment_number");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("first_income_payment_id"), paymentId);
    }

    @Test
    public void test_03_创建支付链接_PLATFORM() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        String platform_fee = data.getString("platform_fee");
        String doctor_fee = data.getString("doctor_fee");
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "PLATFORM");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
        Assert.assertEquals(data.getString("pay_type"), "PLATFORM");
        Assert.assertEquals(data.getString("order_appointment_fee"), String.valueOf(Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee)));
        Assert.assertEquals(data.getString("receivable_fee"), platform_fee);
        String paymentId = data.getString("payment_number");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("first_income_payment_id"), paymentId);
    }

    @Test
    public void test_04_创建支付链接_DOCTOR_PLATFORM() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }

        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "DOCTOR");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String paymentId1 = data.getString("payment_number");

        body.put("paymentCreateType", "PLATFORM");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String paymentId2 = data.getString("payment_number");
        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("first_income_payment_id"), paymentId1);
        Assert.assertEquals(data.getString("last_income_payment_id"), paymentId2);
    }

    @Test
    public void test_05_创建支付链接_APPOINTMENT_X() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }

        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String paymentId = data.getString("payment_number");

        body.put("paymentCreateType", "PLATFORM");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        body.put("paymentCreateType", "DOCTOR");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("first_income_payment_id"), paymentId);
    }

}
