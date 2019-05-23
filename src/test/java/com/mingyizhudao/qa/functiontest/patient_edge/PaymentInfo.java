package com.mingyizhudao.qa.functiontest.patient_edge;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.ConfirmExpert;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.Create;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.CreatePayLink;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.DisablePayLink;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class PaymentInfo extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/payments/{paymentNumber}/info";

    public static String s_Detail(String paymentNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", paymentNumber);
        return HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
    }

    @Test
    public void test_01_正常读取页面信息() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        String pid = CreatePayLink.s_CreatePayment(orderNumber, 1);
        pathValue.put("paymentNumber", pid);
        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(data.getInt("amount"), 1);
        Assert.assertEquals(data.getString("enabled"), "true");
        Assert.assertEquals(data.getString("order_number"), orderNumber);
        Assert.assertEquals(data.getString("order_status"), "WAIT_PAY");
        Assert.assertEquals(data.getString("patient_name"), at.getPatient_name());
        Assert.assertEquals(data.getString("patient_phone"), at.getPatient_phone());
        Assert.assertNotNull(data.getString("doctor_name"));
        Assert.assertEquals(data.getString("disease_name"), at.getDisease_name());
        Assert.assertNotNull(data.getString("pay_url"));
        Assert.assertEquals(data.getString("payment_number"), pid);
        Assert.assertEquals(data.getString("service_type"), at.getService_type());
        Assert.assertEquals(data.getString("status"), "1000");
    }

    @Test
    public void test_02_正常读取禁用的页面信息() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        String pid = CreatePayLink.s_CreatePayment(orderNumber, 1);
        if (!DisablePayLink.s_DisablePayment(pid)) Assert.fail("禁用支付链接失败");
        pathValue.put("paymentNumber", pid);
        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(data.getInt("amount"), 1);
        Assert.assertEquals(data.getString("enabled"), "false");
        Assert.assertEquals(data.getString("order_number"), orderNumber);
        Assert.assertEquals(data.getString("order_status"), "WAIT_PAY");
        Assert.assertEquals(data.getString("patient_name"), at.getPatient_name());
        Assert.assertEquals(data.getString("patient_phone"), at.getPatient_phone());
        Assert.assertNotNull(data.getString("doctor_name"));
        Assert.assertEquals(data.getString("disease_name"), at.getDisease_name());
        Assert.assertNotNull(data.getString("pay_url"));
        Assert.assertEquals(data.getString("payment_number"), pid);
        Assert.assertEquals(data.getString("service_type"), at.getService_type());
        Assert.assertEquals(data.getString("status"), "1000");
    }

}
