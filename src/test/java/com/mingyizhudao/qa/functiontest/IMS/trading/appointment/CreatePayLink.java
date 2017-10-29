package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreatePayLink extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/payments";

    public static String s_CreatePayment(String orderNumber, int fee) {
        JSONObject body = new JSONObject();
        body.put("order_number", orderNumber);
        body.put("fee", fee);
        String res = HttpRequest.s_SendPost(host_ims + uri, body.toString(), crm_token);
//        logger.info(res);
        String payId = JSONObject.fromObject(res).getJSONObject("data").getString("payment_number");
        return payId;
    }

    @Test
    public void test_01_支付链接带金额() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_CreateTid(at);
        String order_number = getOrderNumberByTid(tid);
        if (!ConfirmExpert.s_ConfirmExpert(order_number)) logger.error("确认预约信息失败");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject appointment_info = data.getJSONObject("appointment_info");
        int appointment_fee = appointment_info.getInt("appointment_fee");

        JSONObject body = new JSONObject();
        body.put("order_number", order_number);
        body.put("fee", Generator.randomInt(appointment_fee));

        res = HttpRequest.s_SendPost(host_ims + uri, body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("type"), "RECEIVE");
        String paymentId = data.getString("payment_number");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray payment_list = data.getJSONArray("payment_list");
        Assert.assertEquals(payment_list.size(), 1);
        Assert.assertEquals(payment_list.getJSONObject(payment_list.size()-1).getString("payment_number"), paymentId);
    }

    @Test
    public void test_02_支付链接金额不能大于总金额() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_CreateTid(at);
        String order_number = getOrderNumberByTid(tid);
        if (!ConfirmExpert.s_ConfirmExpert(order_number)) logger.error("确认预约信息失败");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject appointment_info = data.getJSONObject("appointment_info");
        int appointment_fee = appointment_info.getInt("appointment_fee");

        JSONObject body = new JSONObject();
        body.put("order_number", order_number);
        body.put("fee", Generator.randomInt(10) + appointment_fee);

        res = HttpRequest.s_SendPost(host_ims + uri, body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_支付链接金额不能为非正整数() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_CreateTid(at);
        String order_number = getOrderNumberByTid(tid);
        if (!ConfirmExpert.s_ConfirmExpert(order_number)) logger.error("确认预约信息失败");

        JSONObject body = new JSONObject();
        body.put("order_number", order_number);
        body.put("fee", -1);

        res = HttpRequest.s_SendPost(host_ims + uri, body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_04_支付链接没有数量限制() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_CreateTid(at);
        String order_number = getOrderNumberByTid(tid);
        if (!ConfirmExpert.s_ConfirmExpert(order_number)) logger.error("确认预约信息失败");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject appointment_info = data.getJSONObject("appointment_info");
        int appointment_fee = appointment_info.getInt("appointment_fee");

        JSONObject body = new JSONObject();
        body.put("order_number", order_number);
        for (int i = 0; i < appointment_fee; i++) {
            body.put("fee", 1);
            res = HttpRequest.s_SendPost(host_ims + uri, body.toString(), crm_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
        }

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray payment_list = data.getJSONArray("payment_list");
        Assert.assertEquals(payment_list.size(), appointment_fee);
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }
}
