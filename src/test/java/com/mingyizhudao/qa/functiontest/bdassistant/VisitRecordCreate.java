package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.bdassistant.VisitRecord.s_Detail;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPost;

public class VisitRecordCreate extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/visitRecord";

    public static String s_Create(String token) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject body = new JSONObject();

        body.put("doctor_attitude", randomInt(3));
        body.put("doctor_id", randomInt(1000));
        body.put("doctor_user_id", randomInt(5000));
        body.put("doctor_name", randomString(4));
        body.put("doctor_hospital_id", randomHospitalId());
        body.put("doctor_hospital_name", "xx医院");
        body.put("doctor_department_name", "xx部门");
        body.put("doctor_medical_title", randomMedicalId());
        body.put("interview_start_time", randomDateFromNow(0,1, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        body.put("interview_end_time", randomDateFromNow(0,1, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")));
        body.put("location", "xx location");
        body.put("visit_content", randomString(100));

        res = s_SendPost(host_bda + uri, body.toString(), token);
        return JSONObject.fromObject(res).getJSONObject("data").getString("id");
    }

    @Test
    public void test_01_创建拜访记录() {
        String res = "";
        JSONObject body = new JSONObject();

        body.put("doctor_attitude", randomInt(3));
        body.put("doctor_id", randomInt(1000));
        body.put("doctor_user_id", randomInt(5000));
        body.put("doctor_name", randomString(4));
        body.put("doctor_hospital_id", randomHospitalId());
        body.put("doctor_hospital_name", "xx医院");
        body.put("doctor_department_name", "xx部门");
        body.put("doctor_medical_title", randomMedicalId());
        body.put("interview_start_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("interview_end_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("location", "xx location");
        body.put("visit_content", randomString(100));

        for (String token:new String[] {bda_session_staff, bda_session}) {
            res = s_SendPost(host_bda + uri, body.toString(), token);

            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

            String recordId = data.getString("id");
            res = s_Detail(recordId, token);
            s_CheckResponse(res);
            Assert.assertEquals(data.getString("doctor_id"), body.getString("doctor_id"));
            Assert.assertEquals(data.getString("doctor_user_id"), body.getString("doctor_user_id"));
            Assert.assertEquals(data.getString("doctor_name"), body.getString("doctor_name"));
            Assert.assertEquals(data.getString("doctor_hospital_id"), body.getString("doctor_hospital_id"));
            Assert.assertEquals(data.getString("doctor_hospital_name"), body.getString("doctor_hospital_name"));
            Assert.assertEquals(data.getString("doctor_department_name"), body.getString("doctor_department_name"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));
            Assert.assertEquals(data.getString("location"), body.getString("location"));
            Assert.assertEquals(data.getString("visit_content"), body.getString("visit_content"));
            if (token.equals(bda_session)) {
                Assert.assertEquals(data.getString("staff_id"), "SH0133");
            } else {
                Assert.assertEquals(data.getString("staff_id"), "SH0143");
            }
        }

    }

    @Test
    public void test_02_创建拜访记录_临时医生() {
        String res = "";
        JSONObject body = new JSONObject();

        body.put("doctor_attitude", randomInt(3));
        body.put("doctor_name", randomString(4));
        body.put("doctor_hospital_id", randomHospitalId());
        body.put("doctor_hospital_name", "xx医院");
        body.put("doctor_department_name", "xx部门");
        body.put("doctor_medical_title", randomMedicalId());
        body.put("interview_start_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("interview_end_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("location", "xx location");
        body.put("visit_content", randomString(100));

        for (String token:new String[] {bda_session_staff, bda_session}) {
            res = s_SendPost(host_bda + uri, body.toString(), token);

            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

            String recordId = data.getString("id");
            res = s_Detail(recordId, token);
            s_CheckResponse(res);
            Assert.assertEquals(data.getString("doctor_name"), body.getString("doctor_name"));
            Assert.assertEquals(data.getString("doctor_hospital_id"), body.getString("doctor_hospital_id"));
            Assert.assertEquals(data.getString("doctor_hospital_name"), body.getString("doctor_hospital_name"));
            Assert.assertEquals(data.getString("doctor_department_name"), body.getString("doctor_department_name"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));            Assert.assertEquals(data.getString("location"), body.getString("location"));
            Assert.assertEquals(data.getString("visit_content"), body.getString("visit_content"));
            if (token.equals(bda_session)) {
                Assert.assertEquals(data.getString("staff_id"), "SH0133");
            } else {
                Assert.assertEquals(data.getString("staff_id"), "SH0143");
            }
        }
    }

    @Test
    public void test_03_创建拜访记录_医库非注册医生() {
        String res = "";
        JSONObject body = new JSONObject();

        body.put("doctor_attitude", randomInt(3));
        body.put("doctor_id", randomInt(1000));
        body.put("doctor_name", randomString(4));
        body.put("doctor_hospital_id", randomHospitalId());
        body.put("doctor_hospital_name", "xx医院");
        body.put("doctor_department_name", "xx部门");
        body.put("doctor_medical_title", randomMedicalId());
        body.put("interview_start_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("interview_end_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("location", "xx location");
        body.put("visit_content", randomString(100));

        for (String token:new String[] {bda_session_staff, bda_session}) {
            res = s_SendPost(host_bda + uri, body.toString(), token);

            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

            String recordId = data.getString("id");
            res = s_Detail(recordId, token);
            s_CheckResponse(res);
            Assert.assertEquals(data.getString("doctor_id"), body.getString("doctor_id"));
            Assert.assertEquals(data.getString("doctor_name"), body.getString("doctor_name"));
            Assert.assertEquals(data.getString("doctor_hospital_id"), body.getString("doctor_hospital_id"));
            Assert.assertEquals(data.getString("doctor_hospital_name"), body.getString("doctor_hospital_name"));
            Assert.assertEquals(data.getString("doctor_department_name"), body.getString("doctor_department_name"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));            Assert.assertEquals(data.getString("location"), body.getString("location"));
            Assert.assertEquals(data.getString("visit_content"), body.getString("visit_content"));
            if (token.equals(bda_session)) {
                Assert.assertEquals(data.getString("staff_id"), "SH0133");
            } else {
                Assert.assertEquals(data.getString("staff_id"), "SH0143");
            }
        }
    }

    @Test
    public void test_04_创建拜访记录_注册未同步医生() {
        String res = "";
        JSONObject body = new JSONObject();

        body.put("doctor_attitude", randomInt(3));
        body.put("doctor_user_id", randomInt(1000));
        body.put("doctor_name", randomString(4));
        body.put("doctor_hospital_id", randomHospitalId());
        body.put("doctor_hospital_name", "xx医院");
        body.put("doctor_department_name", "xx部门");
        body.put("doctor_medical_title", randomMedicalId());
        body.put("interview_start_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("interview_end_time", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("location", "xx location");
        body.put("visit_content", randomString(100));

        for (String token:new String[] {bda_session_staff, bda_session}) {
            res = s_SendPost(host_bda + uri, body.toString(), token);

            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

            String recordId = data.getString("id");
            res = s_Detail(recordId, token);
            s_CheckResponse(res);
            Assert.assertEquals(data.getString("doctor_user_id"), body.getString("doctor_user_id"));
            Assert.assertEquals(data.getString("doctor_name"), body.getString("doctor_name"));
            Assert.assertEquals(data.getString("doctor_hospital_id"), body.getString("doctor_hospital_id"));
            Assert.assertEquals(data.getString("doctor_hospital_name"), body.getString("doctor_hospital_name"));
            Assert.assertEquals(data.getString("doctor_department_name"), body.getString("doctor_department_name"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));
            Assert.assertTrue(almostEqual(data.getString("interview_start_time"), body.getString("interview_start_time"), "yyyy-MM-dd'T'HH:mm:ss"));            Assert.assertEquals(data.getString("location"), body.getString("location"));
            Assert.assertEquals(data.getString("visit_content"), body.getString("visit_content"));
            if (token.equals(bda_session)) {
                Assert.assertEquals(data.getString("staff_id"), "SH0133");
            } else {
                Assert.assertEquals(data.getString("staff_id"), "SH0143");
            }
        }
    }
}
