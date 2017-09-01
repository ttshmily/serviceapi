package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_ReminderList.s_ReminderList;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.utilities.Generator.randomDateFromNow;

public class Ap_Reminder extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/appointments/{orderNumber}/reminders";


    @Test
    public void test_01_创建备忘录_reminder() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未生成支付链接");
        }
        JSONObject body = new JSONObject();
        body.put("type", "reminder");
        body.put("content", "脚本reminder");
        body.put("remind_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = s_ReminderList(orderNumber);
        s_CheckResponse(res);
        JSONObject reminder = data.getJSONArray("list").getJSONObject(0);
        Assert.assertEquals(reminder.getString("type"), "reminder");
        Assert.assertEquals(reminder.getString("order_number"), orderNumber);

    }

    @Test
    public void test_02_创建备忘录_note() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未生成支付链接");
        }
        JSONObject body = new JSONObject();
        body.put("type", "note");
        body.put("content", "脚本note");
        body.put("remind_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        res = HttpRequest.s_SendPost(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = s_ReminderList(orderNumber);
        s_CheckResponse(res);
        JSONObject reminder = data.getJSONArray("list").getJSONObject(0);
        Assert.assertEquals(reminder.getString("type"), "note");
        Assert.assertEquals(reminder.getString("order_number"), orderNumber);
    }
}
