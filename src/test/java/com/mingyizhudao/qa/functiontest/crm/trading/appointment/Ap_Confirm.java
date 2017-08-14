package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Appointment;
import com.mingyizhudao.qa.dataprofile.User;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

public class Ap_Confirm extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/confirmSurgeon";

    public static boolean s_Confirm(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        String expertId = randomExpertId();
        body.put("doctor_id", expertId);
//        body.put("doctor_name", expertName(expertId));
        body.put("appointment_date", randomDateFromNow(2, 7, new SimpleDateFormat("yyyy-MM-dd")));
        long platform_fee = randomInt(100);
        body.put("platform_fee", platform_fee);
        long doctor_fee = randomInt(10);
        body.put("doctor_fee", doctor_fee);
        String res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("3000") ? true : false;
    }

    @Test
    public void test_01_确认面诊医生() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        if (!s_Take(orderNumber)) {
            Assert.fail("创建状态2000订单失败");
        }
        JSONObject body = new JSONObject();
        User ep = new User();

        HashMap<String, String> expert = s_CreateSyncedDoctor(ep);
        body.put("doctor_id", expert.get("expert_id"));
//        body.put("doctor_name", ep.getDoctor().getName());
//        body.put("doctor_phone", expert.get("mobile"));
//        body.put("doctor_user_id", expert.get("id"));
//        body.put("doctor_medical_title", ep.getDoctor().getMedical_title_list());
//        body.put("doctor_academic_title", ep.getDoctor().getAcademic_title_list());
//        body.put("doctor_department", ep.getDoctor().getDepartment());
        body.put("appointment_date", randomDateFromNow(2, 3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        long platform_fee = randomInt(100);
        body.put("platform_fee", platform_fee);
        long doctor_fee = randomInt(10);
        body.put("doctor_fee", doctor_fee);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "doctor_id"), expert.get("expert_id"));
        Assert.assertEquals(s_ParseJson(data, "doctor_name"), ep.getDoctor().getName());
        Assert.assertEquals(s_ParseJson(data, "doctor_phone"), expert.get("mobile"));
        Assert.assertEquals(s_ParseJson(data, "doctor_user_id"), expert.get("id"));
        Assert.assertEquals(s_ParseJson(data, "doctor_medical_title"), ep.getDoctor().getMedical_title_list());
        Assert.assertEquals(s_ParseJson(data, "doctor_academic_title"), ep.getDoctor().getAcademic_title_list());
        Assert.assertEquals(s_ParseJson(data, "doctor_department"), ep.getDoctor().getDepartment());
        Assert.assertEquals(s_ParseJson(data, "platform_fee"), Long.toString(platform_fee));
        Assert.assertEquals(s_ParseJson(data, "doctor_fee"), Long.toString(doctor_fee));
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(s_ParseJson(data, "appointment_fee"), Long.toString(platform_fee+doctor_fee));
    }

    @Test
    public void test_02_面诊医生信息不完整() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        if (!s_Take(orderNumber)) {
            Assert.fail("创建状态2000订单失败");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        User ep = new User();
        String expert_id = s_CreateSyncedDoctor(ep).get("expert_id");
        body.put("doctor_id", expert_id);
//        body.put("doctor_name", ep.getDoctor().getName());

        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        long platform_fee = randomInt(100);
        body.put("platform_fee", platform_fee);
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        long doctor_fee = randomInt(10);
        body.put("doctor_fee", doctor_fee);
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.put("appointment_date", randomDateFromNow(2, 3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");

        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(s_ParseJson(data, "doctor_id"), expert_id);
        Assert.assertEquals(s_ParseJson(data, "doctor_name"), ep.getDoctor().getName());
        Assert.assertEquals(s_ParseJson(data, "doctor_medical_title"), ep.getDoctor().getMedical_title_list());
        Assert.assertEquals(s_ParseJson(data, "doctor_academic_title"), ep.getDoctor().getAcademic_title_list());
        Assert.assertEquals(s_ParseJson(data, "doctor_department"), ep.getDoctor().getDepartment());
        Assert.assertEquals(s_ParseJson(data, "platform_fee"), Long.toString(platform_fee));
        Assert.assertEquals(s_ParseJson(data, "doctor_fee"), Long.toString(doctor_fee));
        Assert.assertEquals(s_ParseJson(data, "status"), "3000");
        Assert.assertEquals(s_ParseJson(data, "appointment_fee"), Long.toString(platform_fee+doctor_fee));
    }
}
