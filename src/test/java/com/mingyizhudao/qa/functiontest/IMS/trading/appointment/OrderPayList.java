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

public class OrderPayList extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/payments/{orderNumber}/list";

    @Test
    public void test_01_根据订单号获取所有支付信息() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        pathValue.put("orderNumber", orderNumber);

        if (!ConfirmExpert.s_ConfirmExpert(orderNumber)) logger.error("确认专家失败");
        logger.info("创建一个支付链接");
        String payment_number = CreatePayLink.s_CreatePayment(orderNumber, 1);

        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertNotNull(data.getJSONObject("pay_list"));
        Assert.assertNotNull(data.getJSONObject("receive_list"));
        Assert.assertNotNull(data.getJSONObject("refund_list"));
//        JSONObject order_info = data.getJSONObject("order_info");
//        Assert.assertNotNull(order_info);
//        Assert.assertNotNull(order_info.getString("assignee_name"));
//        Assert.assertNotNull(order_info.getString("id"));
//        Assert.assertNotNull(order_info.getString("patient_name"));
//        Assert.assertNotNull(order_info.getString("status"));
//        Assert.assertNotNull(order_info.getString("creator_name"));
    }

    @Test
    public void test_02_检查支付链接的信息receive_list() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        pathValue.put("orderNumber", orderNumber);

        if (!ConfirmExpert.s_ConfirmExpert(orderNumber)) logger.error("确认专家失败");
        logger.info("创建一个支付链接");
        String payment_number = CreatePayLink.s_CreatePayment(orderNumber, 1);
        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

//        JSONObject order_info = data.getJSONObject("order_info");
//        Assert.assertNotNull(order_info);
//        Assert.assertNotNull(order_info.getString("assignee_name"));
//        Assert.assertNotNull(order_info.getString("id"));
//        Assert.assertNotNull(order_info.getString("patient_name"));
//        Assert.assertNotNull(order_info.getString("status"));
//        Assert.assertNotNull(order_info.getString("creator_name"));

        Assert.assertNotNull(data.getJSONObject("receive_list"));
        JSONArray receive_list = data.getJSONArray("receive_list");
        Assert.assertEquals(receive_list.size(), 1);
        JSONObject receivable = receive_list.getJSONObject(receive_list.size()-1);
        Assert.assertEquals(receivable.getString("payment_number"), payment_number);
        Assert.assertEquals(receivable.getString("order_number"), orderNumber);
        Assert.assertEquals(receivable.getString("enabled"), "true");
        Assert.assertEquals(receivable.getInt("receivable_fee"), 1);
        Assert.assertEquals(receivable.getString("payment_number"), payment_number);
        Assert.assertEquals(receivable.getString("type"), "RECEIVE");
        Assert.assertNotNull(receivable.getString("status"));
    }

    @Test
    public void test_03_检查被禁用的支付链接的信息receive_list() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        AppointmentTask at = new AppointmentTask();
        String orderNumber = Create.s_CreateOrderNumber(at);
        pathValue.put("orderNumber", orderNumber);

        if (!ConfirmExpert.s_ConfirmExpert(orderNumber)) logger.error("确认专家失败");
        logger.info("创建一个支付链接");
        if (CreatePayLink.s_CreatePayment(orderNumber, 0).equals(null)) {
            logger.error("创建支付链接失败");
        }
        logger.info("再创建一个禁用的支付链接");
        String payment_number = CreatePayLink.s_CreatePayment(orderNumber, 1);
        if (!DisablePayLink.s_DisablePayment(payment_number)) logger.error("禁用支付链接失败");
        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

//        JSONObject order_info = data.getJSONObject("order_info");
//        Assert.assertNotNull(order_info);
//        Assert.assertNotNull(order_info.getString("assignee_name"));
//        Assert.assertNotNull(order_info.getString("id"));
//        Assert.assertNotNull(order_info.getString("patient_name"));
//        Assert.assertNotNull(order_info.getString("status"));
//        Assert.assertNotNull(order_info.getString("creator_name"));

        Assert.assertNotNull(data.getJSONObject("receive_list"));
        JSONArray receive_list = data.getJSONArray("receive_list");
        Assert.assertEquals(receive_list.size(), 2);
        JSONObject receivable = receive_list.getJSONObject(receive_list.size()-1);
        Assert.assertEquals(receivable.getString("payment_number"), payment_number);
        Assert.assertEquals(receivable.getString("order_number"), orderNumber);
        Assert.assertEquals(receivable.getString("enabled"), "false");
        Assert.assertEquals(receivable.getInt("receivable_fee"), 1);
        Assert.assertEquals(receivable.getString("payment_number"), payment_number);
        Assert.assertEquals(receivable.getString("type"), "RECEIVE");
        Assert.assertNotNull(receivable.getString("status"));
    }
}
