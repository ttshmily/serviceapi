package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/20.
 */
public class RegisteredDoctor_SyncToKB extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_SyncToKB.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/synchronization";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_已认证医生_同步到医库_新增() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res_Doctor);
        String expert_id = UT.parseJson(data, "register_id");
        String hospitalId = UT.parseJson(data, "hospital_id");
        String name = UT.parseJson(data, "name");
        String mobile = UT.parseJson(data, "mobile");
        String academic_title_list = UT.parseJson(data, "academic_title_list");
        String medical_title_list = UT.parseJson(data, "medical_title_list");

        res = KBExpert_Detail.Detail(expert_id);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
        Assert.assertEquals(UT.parseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(UT.parseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(UT.parseJson(data, "name"), name);
        Assert.assertEquals(UT.parseJson(data, "mobile"), mobile);
        Assert.assertEquals(UT.parseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(UT.parseJson(data, "medical_title_list"), medical_title_list);
    }

    @Test
    public void test_02_未认证医生_不能同步到医库() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = CreateRegisteredDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
        body.put("reason", "单独同步");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_已认证医生_同步到医库_关联医库ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String expertId = KBExpert_Create.Create(new ExpertProfile(true)).get("id");
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = CreateVerifiedDoctor(dp);
        if (doctorInfo == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        String doctorId = doctorInfo.get("id");
        pathValue.put("id", doctorId);

        body.put("status", "1");
//        body.put("reason", "单独同步");
        body.put("expert_id", expertId);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        String res_Doctor = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res_Doctor);
        Assert.assertEquals(UT.parseJson(data, "register_id"), expertId);
        Assert.assertEquals(UT.parseJson(data, "is_verified"), "1");
        Assert.assertEquals(UT.parseJson(data, "audit_state"), "AUDIT_PASS");
        String hospitalId = UT.parseJson(data, "hospital_id");
        String name = UT.parseJson(data, "name");
        String mobile = UT.parseJson(data, "mobile");
        String academic_title_list = UT.parseJson(data, "academic_title_list");
        String medical_title_list = UT.parseJson(data, "medical_title_list");

        String res_Expert = KBExpert_Detail.Detail(expertId);
        checkResponse(res_Expert);
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
        Assert.assertEquals(UT.parseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(UT.parseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(UT.parseJson(data, "name"), name);
        Assert.assertEquals(UT.parseJson(data, "mobile"), mobile);
        Assert.assertEquals(UT.parseJson(data, "academic_title_list"), academic_title_list);
        Assert.assertEquals(UT.parseJson(data, "medical_title_list"), medical_title_list);
    }
}
