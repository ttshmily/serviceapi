package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPut;

public class UserVisibleSetting extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/doctors/{id}";

    @Test
    public void test_01_未同步医生只更新医生端的控制开关() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        HashMap<String, String> doctorInfo = s_CreateVerifiedDoctor(new User());
        String doctorId = doctorInfo.get("id");
        String doctorToken = doctorInfo.get("token");

        pathValue.put("id", doctorId);

        JSONObject body = new JSONObject();
        logger.info("sub_test 1: user_visible = 0");
        body.put("user_visible", 0);

        res = s_SendPut(host_doc+uri, body.toString(), doctorToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(doctorToken);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "doctor:user_visible"), "false");

        //
        logger.info("sub_test 2: user_visible = 1");
        body.put("user_visible", 1);

        res = s_SendPut(host_doc+uri, body.toString(), doctorToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(doctorToken);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "doctor:user_visible"), "true");

    }

    @Test
    public void test_02_已同步医生更新医生端和医生库的控制开关() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();

        HashMap<String, String> doctorInfo = s_CreateSyncedDoctor(new User());
        String doctorId = doctorInfo.get("id");
        String doctorToken = doctorInfo.get("token");
        String expertId = doctorInfo.get("expert_id");

        pathValue.put("id", doctorId);

        JSONObject body = new JSONObject();
        logger.info("sub_test 1: user_visible = 0");
        body.put("user_visible", 0);

        res = s_SendPut(host_doc+uri, body.toString(), doctorToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(doctorToken);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "doctor:user_visible"), "false");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("doctor_visible"), "false");

        //
        logger.info("sub_test 2: user_visible = 1");
        body.put("user_visible", 1);

        res = s_SendPut(host_doc+uri, body.toString(), doctorToken, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(doctorToken);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "doctor:user_visible"), "true");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("doctor_visible"), "true");
    }
}
