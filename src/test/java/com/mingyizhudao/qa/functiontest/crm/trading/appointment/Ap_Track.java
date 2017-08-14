package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Appointment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Close.s_Close;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Update.s_Update;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;

public class Ap_Track extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/orderTracks";


    @Test
    public void test_01_创建订单记录CREATE_ORDER() {
        String res = "";
        String orderNumber = s_Create(new Appointment());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
    }

    @Test
    public void test_02_推荐医生记录RECOMMEND_DOCTOR() {
        String res = "";
        String orderNumber = s_Create(new Appointment());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        if(!s_Take(orderNumber)) {
            Assert.fail("推荐医生失败，退出执行");
        }
        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "RECOMMEND_DOCTOR");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(1).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(1).getString("tracker_id"), mainOperatorId);
    }

    @Test
    public void test_03_修改订单记录UPDATE_ORDER() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setExpected_city_id(randomCityId());
        if(!s_Update(orderNumber, ap)) {
            Assert.fail("更新面诊单失败，退出执行");
        }
        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "UPDATE_ORDER");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(1).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(1).getString("tracker_id"), mainOperatorId);
    }

    @Test
    public void test_04_确认医生记录CONFIRM_SURGEON() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setExpected_city_id(randomCityId());
        if(!s_Take(orderNumber) ||!s_Confirm(orderNumber)) {
            Assert.fail("确认面诊单失败，退出执行");
        }
        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "CONFIRM_SURGEON");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(1).getString("type"), "RECOMMEND_DOCTOR");
        Assert.assertEquals(list.getJSONObject(1).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(2).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(2).getString("tracker_id"), mainOperatorId);
    }

    @Test
    public void test_05_关闭订单记录CANCEL_BEFORE_PAY() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setExpected_city_id(randomCityId());
        if(!s_Update(orderNumber, ap) || !s_Close(orderNumber)) {
            Assert.fail("更新面诊单失败，退出执行");
        }
        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "CANCEL_BEFORE_PAY");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(1).getString("type"), "UPDATE_ORDER");
        Assert.assertEquals(list.getJSONObject(1).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(2).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(2).getString("tracker_id"), mainOperatorId);
    }

    @Test
    public void test_06_添加支付账号ADD_ALIPAY_ACCOUNT() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setExpected_city_id(randomCityId());
        if(!s_Take(orderNumber) ||!s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("添加账户失败，退出执行");
        }
        res = s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray list = data.getJSONArray("list");
        Assert.assertEquals(list.getJSONObject(0).getString("type"), "ADD_ALIPAY_ACCOUNT");
        Assert.assertEquals(list.getJSONObject(0).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(1).getString("type"), "CONFIRM_SURGEON");
        Assert.assertEquals(list.getJSONObject(1).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(2).getString("type"), "RECOMMEND_DOCTOR");
        Assert.assertEquals(list.getJSONObject(2).getString("tracker_id"), mainOperatorId);
        Assert.assertEquals(list.getJSONObject(3).getString("type"), "CREATE_ORDER");
        Assert.assertEquals(list.getJSONObject(3).getString("tracker_id"), mainOperatorId);
    }

}
