package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

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

    public static String s_Create(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("data").getString("id");
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
    public void test_06_创建工单_只有患者姓名() {
        String res = "";
        AppointmentTask at = new AppointmentTask("empty");
        at.setPatient_name("孤独患者");

        res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("id"));
    }
}
