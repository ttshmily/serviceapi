package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import static com.mingyizhudao.qa.utilities.Generator.*;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.mingyizhudao.qa.utilities.Helper.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class Ap_Update extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}";

    public static boolean s_Update(String orderNumber, AppointmentOrder ap) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        String code = JSONObject.fromObject(res).getString("code");
        return code.equals("1000000");
    }

    @Test
    public void test_01_更新会诊单图片() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setMedical_record_pictures(new ArrayList<AppointmentOrder.Picture>() {
            {
                add(ap.new Picture("234.jpg", "7"));
            }
        });
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "medical_record_pictures"), ap.printPictures());
    }

    @Test
    public void test_02_更新期望手术医院() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String hospitalId = randomHospitalId();
        ap.setExpected_appointment_hospital_id(hospitalId);
        ap.setExpected_appointment_hospital_name(hospitalName(hospitalId));
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_hospital_id"), ap.getExpected_appointment_hospital_id());
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_hospital_name"), ap.getExpected_appointment_hospital_name());
    }

    @Test
    public void test_03_更新期望手术省市() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String provinceId = randomProvinceId();
        String cityId = randomCityIdUnder(provinceId);
        ap.setExpected_city_id(cityId);
        ap.setExpected_province_id(provinceId);
        ap.setExpected_city_name(cityName(cityId));
        ap.setExpected_province_name(provinceName(provinceId));
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "expected_city_id"), ap.getExpected_city_id());
        Assert.assertEquals(s_ParseJson(data, "expected_city_name"), ap.getExpected_city_name());
        Assert.assertEquals(s_ParseJson(data, "expected_province_id"), ap.getExpected_province_id());
        Assert.assertEquals(s_ParseJson(data, "expected_province_name"), ap.getExpected_province_name());
    }

    @Test
    public void test_04_更新期望手术医生() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String expertId = randomExpertId();
        ap.setExpected_doctor_id(expertId);
        ap.setExpected_doctor_name(expertName(expertId));
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "expected_doctor_id"), ap.getExpected_doctor_id());
        Assert.assertEquals(s_ParseJson(data, "expected_doctor_name"), ap.getExpected_doctor_name());
    }

    @Test
    public void test_05_更新疾病名称() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String diseaseId = randomDiseaseId();
        ap.setMajor_disease_id(diseaseId);
        ap.setMajor_disease_name(diseaseName(diseaseId));
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "major_disease_id"), ap.getMajor_disease_id());
        Assert.assertEquals(s_ParseJson(data, "major_disease_name"), ap.getMajor_disease_name());
    }

    @Test
    public void test_06_更新患者姓名() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String name = "新面诊病人"+ randomString(5);
        ap.setPatient_name(name);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_name"), ap.getPatient_name());
    }

    @Test
    public void test_07_更新患者年龄() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        int age = (int) randomInt(100);
        ap.setPatient_age(age);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_age"), String.valueOf(ap.getPatient_age()));
    }

    @Test
    public void test_08_更新患者性别() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        int gender = (int) randomInt(2);
        ap.setPatient_gender(gender);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_gender"), String.valueOf(ap.getPatient_gender()));
    }

    @Test
    public void test_09_更新患者手机() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String phone = randomPhone();
        ap.setPatient_phone(phone);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_phone"), ap.getPatient_phone());
    }

    @Test
    public void test_10_更新疾病描述() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String desp = randomString(100);
        ap.setDisease_description(desp);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "disease_description"), String.valueOf(ap.getDisease_description()));
    }

    @Test
    public void test_11_更新期望的起止时间() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String start_date = randomDateFromNow(3,10, df);
        String due_date = randomDateFromNow(10,20, df);
        ap.setExpected_appointment_start_date(start_date);
        ap.setExpected_appointment_due_date(due_date);
        res = HttpRequest.s_SendPut(host_crm + uri, JSONObject.fromObject(ap).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_start_date"), ap.getExpected_appointment_start_date());
        Assert.assertEquals(s_ParseJson(data, "expected_appointment_due_date"), ap.getExpected_appointment_due_date());
    }

}
