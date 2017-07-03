package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.doctor.GetDoctorProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class RegisteredDoctor_Certify_V2 extends BaseTest {
    public static final Logger logger= Logger.getLogger(RegisteredDoctor_Certify_V2.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/verificationssynchronization";
    public static String mock = false ? "/mockjs/1" : "";

    public static HashMap<String, String> certify(String regId, String verified_status) {
        String res = "";
        HashMap<String, String> result = new HashMap<>();
        if ( regId == null || regId.isEmpty()) {
            logger.error("医生ID不存在");
            return null;
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        logger.debug(HttpRequest.unicodeString(res));
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("1")) {
            logger.info("已认证医生");
            result.put("is_verified", "1");
            return result;
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("-1")) {
            logger.error("认证失败状态不能直接进行验证");
            result.put("is_verified", "-1");
            return result;
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("0")) {
            logger.error("信息不完整，不能验证");
            result.put("is_verified", "0");
            return result;
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", regId);

        JSONObject body = new JSONObject();
        body.put("status", verified_status);  // 认证
        body.put("reason", "程序认证注册医生并关联到医库");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
            logger.debug(HttpRequest.unicodeString(res));
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        logger.debug(HttpRequest.unicodeString(res));
        result.put("is_verified", UT.parseJson(JSONObject.fromObject(res), "data:is_verified"));
        result.put("register_id", UT.parseJson(JSONObject.fromObject(res), "data:register_id"));
        return result;
    }

    public static HashMap<String, String> certify(String regId, String status, String expertId) {
        String res = "";
        HashMap<String, String> result = new HashMap<>();
        if ( regId == null || regId.isEmpty()) {
            logger.error("医生ID不存在");
            return null;
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        logger.info(HttpRequest.unicodeString(res));
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("1")) {
            logger.info("已认证医生");
            result.put("is_verified", "1");
            return result;
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("-1")) {
            logger.error("认证失败状态不能直接进行验证");
            result.put("is_verified", "-1");
            return result;
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:is_verified").equals("0")) {
            logger.error("信息不完整，不能验证");
            result.put("is_verified", "0");
            return result;
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", regId);

        JSONObject body = new JSONObject();
        body.put("status", status);  // 认证
        body.put("reason", "程序认证注册医生并关联到医库");
        body.put("register_id", expertId);
        try {
            HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(regId);
        result.put("is_verified", UT.parseJson(JSONObject.fromObject(res), "data:is_verified"));
        result.put("register_id", UT.parseJson(JSONObject.fromObject(res), "data:register_id"));
        return result;
    }

    @Test
    public void test_01_认证医生_有效医生ID_失败() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = CreateRegisteredDoctor(dp);
        String doctorId = doctorInfo.get("id");
        String tmpTokenn = doctorInfo.get("token");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "程序自动测试失败原因");  // 失败原因
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "-1");
        res = GetDoctorProfile.getDoctorProfile(tmpTokenn);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "doctor:reject_reason"), "程序自动测试失败原因");
    }

    @Test
    public void test_02_认证医生_有效医生ID无专家ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res1);
        Assert.assertEquals(UT.parseJson(data, "is_verified"), "1");
        String expertId = UT.parseJson(data, "register_id");
        Assert.assertNotNull(expertId);
        String mobile = UT.parseJson(data, "mobile");
        String cityId = UT.parseJson(data, "city_id");

        String res2 = KBExpert_Detail.Detail(expertId);
        checkResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
        Assert.assertEquals(UT.parseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(UT.parseJson(data, "source_type"), "DOCTOR_SERVICE");

        // TODO: 专家信息和医生信息同步
        Assert.assertEquals(UT.parseJson(data, "name"), dp.body.getJSONObject("doctor").getString("name"));
        Assert.assertEquals(UT.parseJson(data, "mobile"), mobile);
        Assert.assertEquals(UT.parseJson(data, "major_id"), dp.body.getJSONObject("doctor").getString("major_id"));
        Assert.assertEquals(UT.parseJson(data, "hospital_id"), dp.body.getJSONObject("doctor").getString("hospital_id"));
        Assert.assertEquals(UT.parseJson(data, "academic_title_list"), dp.body.getJSONObject("doctor").getString("academic_title_list"));
        Assert.assertEquals(UT.parseJson(data, "medical_title_list"), dp.body.getJSONObject("doctor").getString("medical_title_list"));
        Assert.assertEquals(UT.parseJson(data, "city_id"), cityId);

    }

    @Test
    public void test_03_认证医生_有效医生ID有专家ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);

        ExpertProfile ep = new ExpertProfile(true);
        String expertId = KBExpert_Create.Create(ep).get("id");
        if (expertId == null) Assert.fail("创建专家失败，认证用例无法执行");

        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        body.put("register_id", expertId);
        body.put("is_signed", "0");
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res1);
        Assert.assertEquals(UT.parseJson(data, "is_verified"), "1");
        Assert.assertEquals(UT.parseJson(data, "register_id"), expertId);
        String mobile = UT.parseJson(data, "mobile");
        String cityId = UT.parseJson(data, "city_id");


        String res2 = KBExpert_Detail.Detail(expertId);
        checkResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
        Assert.assertEquals(UT.parseJson(data, "certified_status"), "CERTIFIED");

        Assert.assertEquals(UT.parseJson(data, "name"), dp.body.getJSONObject("doctor").getString("name"));
        Assert.assertEquals(UT.parseJson(data, "mobile"), mobile);
        Assert.assertEquals(UT.parseJson(data, "major_id"), dp.body.getJSONObject("doctor").getString("major_id"));
        Assert.assertEquals(UT.parseJson(data, "hospital_id"), dp.body.getJSONObject("doctor").getString("hospital_id"));
        Assert.assertEquals(UT.parseJson(data, "academic_title_list"), dp.body.getJSONObject("doctor").getString("academic_title_list"));
        Assert.assertEquals(UT.parseJson(data, "medical_title_list"), dp.body.getJSONObject("doctor").getString("medical_title_list"));
        Assert.assertEquals(UT.parseJson(data, "city_id"), cityId);
    }

    @Test
    public void test_04_认证医生_同时新增为主刀() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);

        ExpertProfile ep = new ExpertProfile(true);
        String expertId = KBExpert_Create.Create(ep).get("id");
        if (expertId == null) Assert.fail("创建专家失败，认证用例无法执行");

        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        body.put("register_id", expertId);
        body.put("is_signed", "1");
        try {
            res = HttpRequest.sendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res1);
        Assert.assertEquals(UT.parseJson(data, "is_verified"), "1");
        Assert.assertEquals(UT.parseJson(data, "register_id"), expertId);
        Assert.assertEquals(UT.parseJson(data, "is_verified"), "1");
        Assert.assertEquals(UT.parseJson(data, "signed_status"), "SIGNED");
        String mobile = UT.parseJson(data, "mobile");
        String cityId = UT.parseJson(data, "city_id");

        String res2 = KBExpert_Detail.Detail(expertId);
        checkResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(UT.parseJson(data, "register_id"), doctorId);
        Assert.assertEquals(UT.parseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(UT.parseJson(data, "signed_status"), "SIGNED");

        Assert.assertEquals(UT.parseJson(data, "name"), dp.body.getJSONObject("doctor").getString("name"));
        Assert.assertEquals(UT.parseJson(data, "mobile"), mobile);
        Assert.assertEquals(UT.parseJson(data, "major_id"), dp.body.getJSONObject("doctor").getString("major_id"));
        Assert.assertEquals(UT.parseJson(data, "hospital_id"), dp.body.getJSONObject("doctor").getString("hospital_id"));
        Assert.assertEquals(UT.parseJson(data, "academic_title_list"), dp.body.getJSONObject("doctor").getString("academic_title_list"));
        Assert.assertEquals(UT.parseJson(data, "medical_title_list"), dp.body.getJSONObject("doctor").getString("medical_title_list"));
        Assert.assertEquals(UT.parseJson(data, "city_id"), cityId);
    }

    @Test
    public void test_05_认证医生_认真失败原因返回() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctorInfo = CreateRegisteredDoctor(dp);
        String doctorId = doctorInfo.get("id");
        String tmpTokenn = doctorInfo.get("token");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        String is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "程序自动测试失败原因");  // 失败原因
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(doctorId);
        is_verified = UT.parseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "-1");
        res = GetDoctorProfile.getDoctorProfile(tmpTokenn);
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "doctor:reject_reason"), "程序自动测试失败原因");
    }
}
