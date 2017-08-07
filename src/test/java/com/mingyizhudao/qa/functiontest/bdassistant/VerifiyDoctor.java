package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/17.
 */
public class VerifiyDoctor extends BaseTest {

    public static final Logger logger= Logger.getLogger(VerifiyDoctor.class);
    public static String uri = "/api/v1/user/undistributedList"; //TODO
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_地推认证医生_认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        String is_verified = Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        res = HttpRequest.s_SendPut(host_crm + mock + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        is_verified = Helper.s_ParseJson(data, "is_verified");
        String is_signed = Helper.s_ParseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(Helper.s_ParseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "SIGNED");
        Assert.assertNotNull(Helper.s_ParseJson(data, "signed_time"));
    }

    @Test
    public void test_02_地推认证医生_不认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        String is_verified = Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        is_verified = Helper.s_ParseJson(data, "is_verified");
        String is_signed = Helper.s_ParseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(Helper.s_ParseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "NOT SIGNED");
    }

}
