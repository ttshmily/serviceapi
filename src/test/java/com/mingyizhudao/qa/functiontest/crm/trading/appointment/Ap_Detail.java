package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.Appointment;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class Ap_Detail extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/appointments/{orderNumber}";

    public static String s_Detail(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        return HttpRequest.s_SendGet(host_appointment + uri, "", crm_token, pathValue);
    }

    @Test
    public void test_01_获取详情_检查必要字段() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendGet(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "order_number"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "created_at"));
        Assert.assertEquals(Helper.s_ParseJson(data, "type"), "2");
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_appointment_start_date"), ap.getExpected_appointment_start_date());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_appointment_due_date"), ap.getExpected_appointment_due_date());
        Assert.assertEquals(Helper.s_ParseJson(data, "patient_name"), ap.getPatient_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "patient_phone"), ap.getPatient_phone());
        Assert.assertEquals(Helper.s_ParseJson(data, "patient_gender"), String.valueOf(ap.getPatient_gender()));
        Assert.assertEquals(Helper.s_ParseJson(data, "patient_age"), String.valueOf(ap.getPatient_age()));
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_appointment_hospital_id"), ap.getExpected_appointment_hospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_appointment_hospital_name"), ap.getExpected_appointment_hospital_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_city_id"), ap.getExpected_city_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_city_name"), ap.getExpected_city_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_doctor_id"), ap.getExpected_doctor_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_doctor_name"), ap.getExpected_doctor_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_doctor_id"), ap.getExpected_doctor_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_province_id"), ap.getExpected_province_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "expected_province_name"), ap.getExpected_province_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "major_disease_id"), ap.getMajor_disease_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "major_disease_name"), ap.getMajor_disease_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_record_pictures"), ap.printPictures());
    }

    @Test
    public void test_02_传入错误的订单ID() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber+1);
        res = HttpRequest.s_SendGet(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
