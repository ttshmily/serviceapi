package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Cancel extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/cancel";

    public static boolean s_Cancel(String orderNumber, boolean refund, boolean transfer) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        body.put("cancel_reason", "自动化取消");
        body.put("content", "自动化取消");

        body.put("need_refunds", refund);
        if (refund) body.put("refunds_fee", 1);
        body.put("need_transfer", refund);
        if (transfer) body.put("transfer_fee", 1);

        String res = HttpRequest.s_SendPut(host_ims+uri, "", crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getString("code").equals("1000000");
    }

    @Test
    public void test_01_取消订单_受理中() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("cancel_reason", "自动化取消");
        body.put("content", "自动化取消");
        body.put("need_refunds", false);
        body.put("refunds_fee", 1);
        body.put("need_transfer", false);
        body.put("transfer_fee", 1);
        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "CANCEL");
    }

    @Test
    public void test_02_取消订单_服务中需要返款() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        JSONObject body = new JSONObject();
        body.put("cancel_reason", "自动化取消");
        body.put("content", "自动化取消");
        body.put("need_refunds", true);
        body.put("refunds_fee", 1);
        body.put("need_transfer", false);
        body.put("transfer_fee", 1);
        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "CANCEL");
    }

    @Test
    public void test_03_取消订单_支付链接后需要返款() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        CreatePayLink.s_CreatePayment(orderNumber, 1);
        JSONObject body = new JSONObject();
        body.put("cancel_reason", "自动化取消");
        body.put("content", "自动化取消");
        body.put("need_refunds", true);
        body.put("refunds_fee", 1);
        body.put("need_transfer", true);
        body.put("transfer_fee", 1);
        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "CANCEL");
        //TODO
//        Assert.assertEquals(data.getJSONObject("payment_list").getJSONArray("refund_list"));
    }

    @Test
    public void test_04_取消订单_工单记录() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("cancel_reason", "自动化取消");
        body.put("content", "自动化取消");
        body.put("need_refunds", false);
        body.put("refunds_fee", 1);
        body.put("need_transfer", false);
        body.put("transfer_fee", 1);

        s_CheckResponse(Detail.s_Detail(tid));
        int track_list_size_before = data.getJSONArray("track_list").size();

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        s_CheckResponse(Detail.s_Detail(tid));
        int track_list_size_after = data.getJSONArray("track_list").size();
        Assert.assertEquals(track_list_size_after-track_list_size_before, 1);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "CANCEL");
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }
}
