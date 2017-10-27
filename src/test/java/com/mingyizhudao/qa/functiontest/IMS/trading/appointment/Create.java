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

import java.text.SimpleDateFormat;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Create extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders";

    public static String s_CreateTid(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);
        s_CheckResponse(res);
//        JSONObject r = JSONObject.fromObject(res);
        if (!code.equals("1000000")) logger.error(unicodeString(res));
        return data.getString("id");
    }

    public static String s_CreateOrderNumber(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("data").getString("order_number");
    }

    @Test
    public void test_01_创建工单() {
        String res = "";
        AppointmentTask at = new AppointmentTask();

        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_02_创建工单_疾病无ID() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        at.setDisease_id(null);
        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_02_创建工单_期望专家无ID() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        at.setExpected_doctor_id(null);
        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_03_创建工单_期望医院无ID() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        at.setExpected_appointment_hospital_id(null);
        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_04_创建工单_备选选项为false() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        at.setIndications(false);
        at.setExpected_appointment_hospital_alternative(false);
        at.setExpected_doctor_alternative(false);

        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_05_创建工单_患者性别错误值() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        at.setPatient_gender(0);

        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_06_创建工单_只有基本的必填信息() {
        String res = "";
        AppointmentTask at = new AppointmentTask("empty");
        at.setPatient_name("孤独患者");
        at.setAssignee_id(Generator.randomEmployeeId());
        at.setSource_type("PC_WEB");
        at.setPatient_gender((int)Generator.randomInt(2));
        at.setPatient_city_id(Generator.randomCityId());
        at.setPatient_age((int)Generator.randomInt(100));
        at.setDisease_name(Generator.diseaseName(Generator.randomDiseaseId()));
        at.setDisease_description("");
        at.setExpected_city_id(Generator.randomCityId());
        at.setExpected_appointment_start_date(Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        at.setExpected_appointment_due_date(Generator.randomDateFromNow(1,3, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }

    @Test
    public void test_07_创建工单_日期格式错误() {
        String res = "";
        AppointmentTask at = new AppointmentTask();

        at.setExpected_appointment_start_date(Generator.randomDateFromNow(1,3));
        at.setExpected_appointment_due_date(Generator.randomDateFromNow(1,3));
        at.setPrevious_appointment_date(Generator.randomDateTillNow());
        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_08_创建工单_新增一条工单记录() {
        String res = "";
        AppointmentTask at = new AppointmentTask();

        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String tid = data.getString("id");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray track_list = data.getJSONArray("track_list");
        Assert.assertEquals(track_list.size(), 1);
        JSONObject track = data.getJSONArray("track_list").getJSONObject(track_list.size()-1);
        Assert.assertEquals(track.getString("track_type"), "CREATE_ORDER_V1");
        Assert.assertEquals(track.getString("poster_name"), mainOperatorName);
    }
}
