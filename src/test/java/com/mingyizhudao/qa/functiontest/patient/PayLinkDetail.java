package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.ConfirmExpert;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.Create;
import com.mingyizhudao.qa.functiontest.IMS.trading.appointment.CreatePayLink;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class PayLinkDetail extends BaseTest {
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
    public void test_01_支付链接的详情() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        String paymentNumber = CreatePayLink.s_CreatePayment(orderNumber, 1);
        pathValue.put("paymentNumber", paymentNumber);

        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
