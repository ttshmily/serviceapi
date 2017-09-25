package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Create;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPut;

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

    @Test
    public void test_01_已认证医生_同步到医库_新增() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        User dp = new User();
        HashMap<String, String> doctorInfo = s_CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res_Doctor);
        String expert_id = Helper.s_ParseJson(data, "register_id");
        String hospitalId = Helper.s_ParseJson(data, "hospital_id");
        String name = Helper.s_ParseJson(data, "name");
        String mobile = Helper.s_ParseJson(data, "mobile");
        String academic_title_list = Helper.s_ParseJson(data, "academic_title_list");
        String medical_title_list = Helper.s_ParseJson(data, "medical_title_list");

        res = KBExpert_Detail.s_Detail(expert_id);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), mobile);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), medical_title_list);
    }

    @Test
    public void test_02_未认证医生_不能同步到医库() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        User dp = new User();
        HashMap<String, String> doctorInfo = s_CreateRegisteredDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_已认证医生_同步到医库_关联医库ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String expertId = KBExpert_Create.s_Create(new Doctor(""));
        User dp = new User();
        HashMap<String, String> doctorInfo = s_CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        body.put("expert_id", expertId);
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res_Doctor);
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), expertId);
        Assert.assertEquals(Helper.s_ParseJson(data, "is_verified"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "audit_state"), "AUDIT_PASS");
        String hospitalId = Helper.s_ParseJson(data, "hospital_id");
        String name = Helper.s_ParseJson(data, "name");
        String mobile = Helper.s_ParseJson(data, "mobile");
        String academic_title_list = Helper.s_ParseJson(data, "academic_title_list");
        String medical_title_list = Helper.s_ParseJson(data, "medical_title_list");

        String res_Expert = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res_Expert);
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), mobile);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), medical_title_list);
    }
}
