package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class Detail extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/orders/{id}";

    public static String s_Detail(String id) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", id);
        return HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
    }

    @Test
    public void test_01_获取详情_检查必要字段() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        AppointmentTask at = new AppointmentTask();
        String id = Create.s_Create(at);
        pathValue.put("id", id);

        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
        Assert.assertNotNull(data.getString("order_number"));
        Assert.assertNotNull(data.getString("created_at"));
        Assert.assertNotNull(data.getString("modified_at"));
        Assert.assertEquals(data.getString("source_type"), at.getSource_type());
        Assert.assertEquals(data.getString("service_type"), at.getService_type());
        Assert.assertNotNull(data.getString("source_channel"));
        Assert.assertNotNull(data.getString("status"));
        Assert.assertNotNull(data.getString("track_list"));
        Assert.assertEquals(data.getString("patient_name"), at.getPatient_name());
        Assert.assertEquals(data.getString("patient_phone"), at.getPatient_phone());
        Assert.assertEquals(data.getString("patient_gender"), String.valueOf(at.getPatient_gender()));
        Assert.assertEquals(data.getString("patient_age"), String.valueOf(at.getPatient_age()));
        Assert.assertEquals(data.getString("patient_id_card"), at.getPatient_id_card());
        Assert.assertEquals(data.getString("patient_city_id"), at.getPatient_city_id());
        Assert.assertEquals(data.getString("patient_city_name"), Generator.cityName(at.getPatient_city_id()));
        Assert.assertNotNull(data.getString("patient_province_id"));
        Assert.assertNotNull(data.getString("patient_province_name"));
        Assert.assertEquals(data.getString("disease_id"), at.getDisease_id());
        Assert.assertEquals(data.getString("disease_name"), at.getDisease_name());
        Assert.assertEquals(data.getString("disease_description"), at.getDisease_description());
        Assert.assertEquals(data.getString("indications"), at.getIndications().toString());
        Assert.assertEquals(data.getString("previous_appointment_date"), at.getPrevious_appointment_date());
        Assert.assertEquals(data.getString("previous_doctor_suggest"), at.getPrevious_doctor_suggest());
        Assert.assertEquals(data.getString("previous_hospital_id"), at.getPrevious_hospital_id());
        Assert.assertEquals(data.getString("previous_hospital_name"), at.getPrevious_hospital_name());
        Assert.assertEquals(data.getString("expected_appointment_hospital_id"), at.getExpected_appointment_hospital_id());
        Assert.assertEquals(data.getString("expected_appointment_hospital_name"), at.getExpected_appointment_hospital_name());
        Assert.assertEquals(data.getString("expected_appointment_hospital_alternative"), at.getExpected_appointment_hospital_alternative().toString());
        Assert.assertEquals(data.getString("expected_city_id"), at.getExpected_city_id());
        Assert.assertEquals(data.getString("expected_city_name"), Generator.cityName(at.getExpected_city_id()));
        Assert.assertNotNull(data.getString("expected_province_id"));
        Assert.assertNotNull(data.getString("expected_province_name"));
        Assert.assertEquals(data.getString("expected_doctor_id"), at.getExpected_doctor_id());
        Assert.assertEquals(data.getString("expected_doctor_name"), at.getExpected_doctor_name());
        Assert.assertEquals(data.getString("expected_doctor_alternative"), at.getExpected_doctor_alternative().toString());
        Assert.assertEquals(data.getString("expected_appointment_start_date"), at.getExpected_appointment_start_date());
        Assert.assertEquals(data.getString("expected_appointment_due_date"), at.getExpected_appointment_due_date());
    }
}
