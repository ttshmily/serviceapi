package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class DisablePayLink extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/payments/{paymentNumber}/disable";

    public static Boolean s_DisablePayment(String paymentNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", paymentNumber);
        String res = HttpRequest.s_SendPost(host_ims + uri, "", crm_token, pathValue);
        return JSONObject.fromObject(res).getString("code").equals("1000000");
    }

    @Test
    public void test_01_禁用支付链接_查看详情() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        String tid = Create.s_CreateTid(new AppointmentTask());
        String orderNumber = getOrderNumberByTid(tid);
        if (!ConfirmExpert.s_ConfirmExpert(orderNumber)) logger.error("确认专家失败");
        logger.info("创建一个支付链接");
        String payment_number = CreatePayLink.s_CreatePayment(orderNumber, 1);
        pathValue.put("paymentNumber", payment_number);

        res = HttpRequest.s_SendPost(host_ims + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);

        JSONArray payment_list = data.getJSONArray("payment_list");
        Assert.assertEquals(payment_list.getJSONObject(0).getString("enabled"), "false");
        Assert.assertEquals(payment_list.getJSONObject(0).getString("status"), "1000");
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }
}
