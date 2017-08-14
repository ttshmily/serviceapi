package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Appointment;
import com.mingyizhudao.qa.common.TestLogger;

import static com.mingyizhudao.qa.utilities.Helper.*;

import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Ap_Create extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments";

    public static String s_Create(Appointment ap) {
        String res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(ap).toString(), crm_token);
        return JSONObject.fromObject(res).getJSONObject("data").getString("order_number");
    }

    @Test
    public void test_01_创建订单信息保存() {
        String res = "";
        Appointment ap = new Appointment();
        res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(ap).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(s_ParseJson(data, "order_number"));
        Assert.assertNotNull(s_ParseJson(data, "created_at"));
        Assert.assertEquals(s_ParseJson(data, "type"), "2");
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_start_date"), ap.getExpected_appointment_start_date());
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_due_date"), ap.getExpected_appointment_due_date());
        Assert.assertEquals(s_ParseJson(data, "patient_name"), ap.getPatient_name());
        Assert.assertEquals(s_ParseJson(data, "patient_phone"), ap.getPatient_phone());
        Assert.assertEquals(s_ParseJson(data, "patient_gender"), String.valueOf(ap.getPatient_gender()));
        Assert.assertEquals(s_ParseJson(data, "patient_age"), String.valueOf(ap.getPatient_age()));
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_hospital_id"), ap.getExpected_appointment_hospital_id());
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_hospital_name"), ap.getExpected_appointment_hospital_name());
        Assert.assertEquals(s_ParseJson(data, "expected_city_id"), ap.getExpected_city_id());
        Assert.assertEquals(s_ParseJson(data, "expected_city_name"), ap.getExpected_city_name());
        Assert.assertEquals(s_ParseJson(data, "expected_doctor_id"), ap.getExpected_doctor_id());
        Assert.assertEquals(s_ParseJson(data, "expected_doctor_name"), ap.getExpected_doctor_name());
        Assert.assertEquals(s_ParseJson(data, "expected_doctor_id"), ap.getExpected_doctor_id());
        Assert.assertEquals(s_ParseJson(data, "expected_province_id"), ap.getExpected_province_id());
        Assert.assertEquals(s_ParseJson(data, "expected_province_name"), ap.getExpected_province_name());
        Assert.assertEquals(s_ParseJson(data, "major_disease_id"), ap.getMajor_disease_id());
        Assert.assertEquals(s_ParseJson(data, "major_disease_name"), ap.getMajor_disease_name());
        Assert.assertEquals(s_ParseJson(data, "medical_record_pictures"), ap.printPictures());
        Assert.assertEquals(s_ParseJson(data, "major_reps_id"), mainOperatorId);
        Assert.assertEquals(s_ParseJson(data, "status"), "1000");
    }

    @Test
    public void test_02_创建订单检查创建时间() {
        String res = "";
        Appointment ap = new Appointment();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String now = null;
        try {
            now = df.format(new Date());
        } catch (Exception e) {
            logger.error(e);
        }
        res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(ap).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(s_ParseJson(data, "order_number"));
        Assert.assertNotNull(s_ParseJson(data, "created_at").substring(0, 20), now.substring(0,20));
    }

}

