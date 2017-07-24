package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
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
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        try {
            res = HttpRequest.sendPut(host_crm + mock + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res);
        is_verified = UT.parseJson(data, "is_verified");
        String is_signed = UT.parseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(UT.parseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "SIGNED");
        Assert.assertNotNull(UT.parseJson(data, "signed_time"));
    }

    @Test
    public void test_02_地推认证医生_不认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res);
        is_verified = UT.parseJson(data, "is_verified");
        String is_signed = UT.parseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(UT.parseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "NOT SIGNED");
    }

}
