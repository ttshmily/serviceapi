package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
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
 * Created by ttshmily on 2/6/2017.
 */
public class RegisteredDoctor_Sign extends BaseTest {
    public static final Logger logger= Logger.getLogger(RegisteredDoctor_Sign.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/expert_verifications";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_通过专家认证_已认证的医生() {

        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> info = CreateVerifiedDoctor(dp);
        String doctorId = info.get("id");
        String expertId = info.get("expert_id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        JSONObject body = new JSONObject();
        body.put("status", "SIGNED");
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "signed_status"), "SIGNED");



        res = KBExpert_Detail.Detail(expertId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "signed_status"), "SIGNED");
    }

    @Test
    public void test_02_通过专家认证_未认证的医生() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> info = CreateRegisteredDoctor(dp);
        String doctorId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        JSONObject body = new JSONObject();
        body.put("status", "SIGNED");
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "signed_status"), "NOT_SIGNED");
    }
}
