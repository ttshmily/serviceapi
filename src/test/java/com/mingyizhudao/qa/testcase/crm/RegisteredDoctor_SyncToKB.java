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
 * Created by dayi on 2017/7/20.
 */
public class RegisteredDoctor_SyncToKB extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_SyncToKB.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/synchronization";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_已认证医生_同步到医库() {

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
        String expert_id = UT.parseJson(data, "register_id");
        Assert.assertNotNull(expert_id);

        res = KBExpert_Detail.Detail(expert_id);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
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
}
