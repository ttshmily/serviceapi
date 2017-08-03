package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Create;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/20.
 */
public class RegisteredDoctor_SyncToKB extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/synchronization";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_已认证医生_同步到医库_新增() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = s_CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res_Doctor);
        String expert_id = Generator.s_ParseJson(data, "register_id");
        String hospitalId = Generator.s_ParseJson(data, "hospital_id");
        String name = Generator.s_ParseJson(data, "name");
        String mobile = Generator.s_ParseJson(data, "mobile");
        String academic_title_list = Generator.s_ParseJson(data, "academic_title_list");
        String medical_title_list = Generator.s_ParseJson(data, "medical_title_list");

        res = KBExpert_Detail.s_Detail(expert_id);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Generator.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Generator.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Generator.s_ParseJson(data, "mobile"), mobile);
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list);
    }

    @Test
    public void test_02_未认证医生_不能同步到医库() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = s_CreateRegisteredDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_已认证医生_同步到医库_关联医库ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String expertId = KBExpert_Create.s_Create(new ExpertProfile(true)).get("id");
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = s_CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
//        body.put("reason", "单独同步");
        body.put("expert_id", expertId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res_Doctor);
        Assert.assertEquals(Generator.s_ParseJson(data, "register_id"), expertId);
        Assert.assertEquals(Generator.s_ParseJson(data, "is_verified"), "1");
        Assert.assertEquals(Generator.s_ParseJson(data, "audit_state"), "AUDIT_PASS");
        String hospitalId = Generator.s_ParseJson(data, "hospital_id");
        String name = Generator.s_ParseJson(data, "name");
        String mobile = Generator.s_ParseJson(data, "mobile");
        String academic_title_list = Generator.s_ParseJson(data, "academic_title_list");
        String medical_title_list = Generator.s_ParseJson(data, "medical_title_list");

        String res_Expert = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res_Expert);
        Assert.assertEquals(Generator.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Generator.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Generator.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Generator.s_ParseJson(data, "mobile"), mobile);
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list);
    }
}
