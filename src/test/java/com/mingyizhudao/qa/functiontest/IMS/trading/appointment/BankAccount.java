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

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class BankAccount extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/addAlipayAccount";

    public static boolean s_Bank(String orderNumber) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String res = HttpRequest.s_SendPut(host_ims+uri, new AppointmentTask("account").transform(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getString("code").equals("1000000");
    }

    @Test
    public void test_01_正常增加支付宝账号() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        AppointmentTask account = new AppointmentTask("account");
        res = HttpRequest.s_SendPut(host_ims+uri, account.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String tid = data.getString("id");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject account_res = data.getJSONObject("doctor_account");
        Assert.assertNotNull(account_res);
        Assert.assertEquals(account_res.getString("doctor_account_identity"), account.getDoctor_account_identity());
        Assert.assertEquals(account_res.getString("doctor_account_info"), account.getDoctor_account_info());
        Assert.assertEquals(account_res.getString("doctor_account_name"), account.getDoctor_account_name());
        Assert.assertEquals(account_res.getString("appointment_fee_remark"), account.getAppointment_fee_remark());
    }

    @Test
    public void test_02_添加账号_错误的身份证() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        AppointmentTask account = new AppointmentTask("account");
        account.setDoctor_account_identity("123456");
        res = HttpRequest.s_SendPut(host_ims+uri, account.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_添加账号_缺少姓名() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        AppointmentTask account = new AppointmentTask("account");
        account.setDoctor_account_name(null);
        res = HttpRequest.s_SendPut(host_ims+uri, account.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_04_添加账号_缺少身份证() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        AppointmentTask account = new AppointmentTask("account");
        account.setDoctor_account_identity(null);
        res = HttpRequest.s_SendPut(host_ims+uri, account.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_05_添加账号_工单记录() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        AppointmentTask account = new AppointmentTask("account");
        res = HttpRequest.s_SendPut(host_ims+uri, account.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String tid = data.getString("id");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray track_list = data.getJSONArray("track_list");
        int size = track_list.size();
        JSONObject track = track_list.getJSONObject(size-1);
        Assert.assertEquals(track.getString("track_type"), "CHANGE_TARGET_PAY_ACCOUNT_V1");

    }
}
