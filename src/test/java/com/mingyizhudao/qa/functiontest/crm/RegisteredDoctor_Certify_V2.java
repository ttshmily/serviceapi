package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Certify_V2 extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_Certify_V2.class);
    public static final String version = "/api/v2";
    public static String uri = version+"/doctors/{id}/verifications";
    public static String mock = false ? "/mockjs/1" : "";

    public static String CertifyOnly(String regId, String status) {
        String res = "";
        if ( regId == null || regId.isEmpty()) {
            logger.error("医生ID不存在");
            return null;
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        logger.info(HttpRequest.unicodeString(res));
        if (Generator.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("1")) return "1";
        if (Generator.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("-1")) {
            logger.error("认证失败状态不能直接进行验证");
            return "-1";
        }
        if (Generator.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("0")) {
            logger.error("信息不完整，不能验证");
            return "0";
        }
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();
        pathValue.put("id", regId);
        body.put("status", status);  // 认证
        body.put("reason", "原因");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        return Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
    }

    @Test
    public void test_01_认证医生_有效医生ID_失败() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "失败原因");  // 失败原因
        try {
            res = HttpRequest.s_SendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "-1");
    }


    @Test
    public void test_02_认证医生_有效医生ID_成功() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        body.put("reason", "成功原因");  // 成功原因
        try {
            res = HttpRequest.s_SendPut(host_crm + mock + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "1");
    }

    @Test
    public void test_03_认证医生_有效医生ID_不填写原因_成功() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        body.put("reason", "");  // 成功原因
        try {
            res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        is_verified = Generator.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "1");
    }

    @Test
    public void test_04_认证医生_无效医生ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");

        pathValue.put("id", "1"+doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "失败原因");  // 失败原因
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_05_认证医生_认证失败的不能直接认证成功() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");

        pathValue.put("id", doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "先让你认证不通过");  // 失败原因
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        body.replace("status", "1");
        body.replace("reason", "再试图让你认证通过");  // 成功原因
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "原先认证失败的医生不应该直接通过认证");
    }

    @Test
    public void test_06_认证医生_信息不全的不能直接认证成功() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String doctorId = CreateRegistered().get("id");
        pathValue.put("id", doctorId);
        if ( doctorId == null)
            Assert.fail("创建用户失败，认证用例无法执行");

        body.put("status", "1");
        body.put("reason", "试图让你认证通过");  // 成功原因
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "信息不全的医生不应该直接通过认证");
    }
}
