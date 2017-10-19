package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.Generator;
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

public class Ap_CreatePayment_V2 extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v2";
    public static String uri = version + "/appointments/createPayment";

    public static boolean s_CreatePayment(String orderNumber, String type) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", type);//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        String res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        return JSONObject.fromObject(res).getString("code").equals("1000000");
    }

    @Test
    public void test_01_支付链接带金额() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
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
        int total = Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        body.put("fee", Generator.randomInt(total));
        body.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
//        Assert.assertEquals(data.getString("pay_type"), "APPOINTMENT");
//        Assert.assertEquals(data.getString("order_appointment_fee"), String.valueOf(total));
//        Assert.assertEquals(data.getString("receivable_fee"), String.valueOf(total));
        String paymentId = data.getString("payment_number");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("last_income_payment_id"), paymentId);
    }

    @Test
    public void test_02_支付链接带金额大于总金额() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
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
        int total = Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        body.put("fee", total+1);
        body.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "金额超出不应该创建成功");
    }

    @Test
    public void test_03_支付链接没有支付链接类型也可以创建() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
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
        int total = Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee);
        JSONObject body = new JSONObject();
//        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        body.put("fee", Generator.randomInt(total));
        body.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
//        Assert.assertEquals(data.getString("pay_type"), "APPOINTMENT");
//        Assert.assertEquals(data.getString("order_appointment_fee"), String.valueOf(total));
//        Assert.assertEquals(data.getString("receivable_fee"), String.valueOf(total));
        String paymentId = data.getString("payment_number");

        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("last_income_payment_id"), paymentId);
    }

    @Test
    public void test_04_支付链接金额不能为非正整数() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
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
        int total = Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        body.put("orderNumber", orderNumber);
        body.put("fee", 0);
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        body.put("fee", -1);
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_05_支付链接没有数量限制() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
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
        int total = Integer.parseInt(platform_fee) + Integer.parseInt(doctor_fee);
        JSONObject body = new JSONObject();
        body.put("paymentCreateType", "APPOINTMENT");//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        body.put("orderNumber", orderNumber);
        String paymentId=null;
        for (int i = 0; i < 10; i++) {
            body.put("fee", total/10);
            res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(data.getString("type"), "RECEIVE");
            paymentId = data.getString("payment_number");
        }
        res = s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("last_income_payment_id"), paymentId);
    }
}
