package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class ConfirmExpert extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/confirmSurgeon";

    public static Boolean s_ConfirmExpert(String orderNumber, String expert_id) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        body.put("doctor_fee", Generator.randomInt(10));
        body.put("platform_fee", Generator.randomInt(10));
        body.put("appointment_date", Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("appointment_doctor_id", expert_id);

        String res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getString("code").equals("1000000");
    }

    public static Boolean s_ConfirmExpert(String orderNumber) {
        return s_ConfirmExpert(orderNumber, Generator.randomExpertId());
    }

    @Test
    public void test_01_确认医生() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!Recommend.s_Recommend(orderNumber)) logger.error("推荐专家失败");
        JSONObject body = new JSONObject();
        body.put("doctor_fee", Generator.randomInt(10));
        body.put("platform_fee", Generator.randomInt(100));
        body.put("appointment_date", Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        String expertId = Generator.randomExpertId();
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONObject expertInfo = data;
        body.put("appointment_doctor_id", expertId);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String tid = data.getString("id");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject appointment_info = data.getJSONObject("appointment_info");
        Assert.assertEquals(appointment_info.getString("appointment_date"), body.getString("reserved_date"));
        Assert.assertEquals(appointment_info.getString("doctor_fee"), body.getString("doctor_fee"));
        Assert.assertEquals(appointment_info.getString("platform_fee"), body.getString("platform_fee"));
        Assert.assertEquals(Integer.parseInt(appointment_info.getString("appointment_fee")), (int)body.get("doctor_fee")+(int)body.get("platform_fee"));
        JSONObject appointment_doctor = appointment_info.getJSONObject("appointment_doctor");
        Assert.assertEquals(appointment_doctor.getString("id"), expertId);
        Assert.assertEquals(appointment_doctor.getString("name"), Generator.expertName(expertId));
        Assert.assertEquals(appointment_doctor.getString("medical_title_list"), expertInfo.getString("medical_title_list"));
        Assert.assertEquals(appointment_doctor.getString("hospital_name"), expertInfo.getString("hospital_name"));
        Assert.assertEquals(appointment_doctor.getString("department"), expertInfo.getString("department"));
        Assert.assertEquals(appointment_doctor.getString("referrer_name"), expertInfo.getString("referrer_name"));
        Assert.assertEquals(appointment_doctor.getString("referrer_tel"), expertInfo.getString("referrer_tel"));
        Assert.assertEquals(appointment_doctor.getString("hospital_id"), expertInfo.getString("hospital_id"));
    }


    @Test
    public void test_02_确认医生_金额为0() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!Recommend.s_Recommend(orderNumber)) logger.error("推荐专家失败");
        JSONObject body = new JSONObject();
        body.put("doctor_fee", 0);
        body.put("platform_fee", 0);
        body.put("appointment_date", Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        String expertId = Generator.randomExpertId();
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONObject expertInfo = data;
        body.put("appointment_doctor_id", expertId);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String tid = data.getString("id");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONObject appointment_info = data.getJSONObject("appointment_info");
        Assert.assertEquals(appointment_info.getString("appointment_date"), body.getString("reserved_date"));
        Assert.assertEquals(appointment_info.getString("doctor_fee"), body.getString("doctor_fee"));
        Assert.assertEquals(appointment_info.getString("platform_fee"), body.getString("platform_fee"));
        Assert.assertEquals(Integer.parseInt(appointment_info.getString("appointment_fee")), (int)body.get("doctor_fee")+(int)body.get("platform_fee"));
        JSONObject appointment_doctor = appointment_info.getJSONObject("appointment_doctor");
        Assert.assertEquals(appointment_doctor.getString("id"), expertId);
        Assert.assertEquals(appointment_doctor.getString("name"), Generator.expertName(expertId));
        Assert.assertEquals(appointment_doctor.getString("medical_title_list"), expertInfo.getString("medical_title_list"));
        Assert.assertEquals(appointment_doctor.getString("hospital_name"), expertInfo.getString("hospital_name"));
        Assert.assertEquals(appointment_doctor.getString("department"), expertInfo.getString("department"));
        Assert.assertEquals(appointment_doctor.getString("referrer_name"), expertInfo.getString("referrer_name"));
        Assert.assertEquals(appointment_doctor.getString("referrer_tel"), expertInfo.getString("referrer_tel"));
        Assert.assertEquals(appointment_doctor.getString("hospital_id"), expertInfo.getString("hospital_id"));
    }

    @Test
    public void test_03_确认医生_缺少日期() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!Recommend.s_Recommend(orderNumber)) logger.error("推荐专家失败");
        JSONObject body = new JSONObject();
        body.put("doctor_fee", 0);
        body.put("platform_fee", 0);
        String expertId = Generator.randomExpertId();
        body.put("appointment_doctor_id", expertId);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_04_确认医生_医生ID错误() {
        String res = "";
        String orderNumber = Create.s_CreateOrderNumber(new AppointmentTask());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if (!Recommend.s_Recommend(orderNumber)) logger.error("推荐专家失败");
        JSONObject body = new JSONObject();
        body.put("doctor_fee", 0);
        body.put("platform_fee", 0);
        body.put("appointment_date", Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        String expertId = Generator.randomExpertId();
        body.put("appointment_doctor_id", expertId+11);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }
}
