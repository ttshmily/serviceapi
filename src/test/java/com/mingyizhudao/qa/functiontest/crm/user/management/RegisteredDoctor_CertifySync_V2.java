package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.functiontest.doctor.GetDoctorProfile_V1;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Create;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPut;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class RegisteredDoctor_CertifySync_V2 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v2";
    public static String uri = version+"/doctors/{id}/verificationssynchronization";

    public static HashMap<String, String> s_CertifyAndSync(String regId, String verified_status) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> result = new HashMap<>();
        if ( regId == null || regId.isEmpty()) {
            logger.error("医生ID不存在");
            return null;
        }
//        res = RegisteredDoctor_Detail.s_Detail(regId);
//        logger.debug(Helper.unicodeString(res));
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("1")) {
//            logger.info("已认证医生");
//            result.put("is_verified", "1");
//            return result;
//        }
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("-1")) {
//            logger.error("认证失败状态不能直接进行验证");
//            result.put("is_verified", "-1");
//            return result;
//        }
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("0")) {
//            logger.error("信息不完整，不能验证");
//            result.put("is_verified", "0");
//            return result;
//        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", regId);

        JSONObject body = new JSONObject();
        body.put("status", verified_status);  // 认证
        body.put("reason", "程序认证注册医生并关联到医库");
        body.put("is_signed", "1");
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        logger.debug(Helper.unicodeString(res));
        res = RegisteredDoctor_Detail.s_Detail(regId);
        logger.debug(Helper.unicodeString(res));
        result.put("is_verified", Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified"));
        result.put("kb_id", Helper.s_ParseJson(JSONObject.fromObject(res), "data:register_id"));
        return result;
    }

    public static HashMap<String, String> s_CertifyAndSync(String regId, String verified_status, String expertId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> result = new HashMap<>();
        if ( regId == null || regId.isEmpty()) {
            logger.error("医生ID不存在");
            return null;
        }
//        res = RegisteredDoctor_Detail.s_Detail(regId);
//        logger.debug(Helper.unicodeString(res));
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("1")) {
//            logger.info("已认证医生");
//            result.put("is_verified", "1");
//            return result;
//        }
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("-1")) {
//            logger.error("认证失败状态不能直接进行验证");
//            result.put("is_verified", "-1");
//            return result;
//        }
//        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified").equals("0")) {
//            logger.error("信息不完整，不能验证");
//            result.put("is_verified", "0");
//            return result;
//        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", regId);

        JSONObject body = new JSONObject();
        body.put("status", verified_status);  // 认证
        body.put("reason", "程序认证注册医生并关联到医库");
        body.put("kb_id", expertId);
        body.put("is_signed", "1");
        s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        res = RegisteredDoctor_Detail.s_Detail(regId);
        result.put("is_verified", Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified"));
        result.put("register_id", Helper.s_ParseJson(JSONObject.fromObject(res), "data:register_id"));
        return result;
    }

    @Test
    public void test_01_认证医生_有效医生ID_不通过() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();
        User dp = new User();
        HashMap<String, String> doctorInfo = s_CreateRegisteredDoctor(dp);
        String doctorId = doctorInfo.get("id");
        String tmpToken = doctorInfo.get("token");
        if ( doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        String is_verified = Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "2");

        pathValue.put("id", doctorId);
        body.put("status", "-1");  // 认证失败
        body.put("reason", "程序自动测试失败原因");  // 失败原因
        res = s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        logger.debug(Helper.unicodeString(res));
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        is_verified = Helper.s_ParseJson(JSONObject.fromObject(res), "data:is_verified");
        Assert.assertEquals(is_verified, "-1");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:reject_reason"), body.getString("reason"));
    }

    @Test
    public void test_02_认证医生_有效医生ID_通过_无专家ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        User dp = new User();
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);
        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res1);
        Assert.assertEquals(Helper.s_ParseJson(data, "is_verified"), "1");
        String expertId = Helper.s_ParseJson(data, "register_id");
        Assert.assertNotNull(expertId);
        String mobile = Helper.s_ParseJson(data, "mobile");
        String cityId = Helper.s_ParseJson(data, "city_id");

        String res2 = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "source_type"), "DOCTOR_SERVICE");

        // TODO: 专家信息和医生信息同步
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.getDoctor().getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), mobile);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), dp.getDoctor().getHospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), dp.getDoctor().getAcademic_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), dp.getDoctor().getMedical_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), cityId);

    }

    @Test
    public void test_03_认证医生_有效医生ID_通过_有专家ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        User dp = new User();
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);

        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建专家失败，认证用例无法执行");

        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        body.put("kb_id", expertId);
        body.put("is_signed", "0");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res1);
        Assert.assertEquals(Helper.s_ParseJson(data, "is_verified"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), expertId);
        String mobile = Helper.s_ParseJson(data, "mobile");
        String cityId = Helper.s_ParseJson(data, "city_id");


        String res2 = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "CERTIFIED");

        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.getDoctor().getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), mobile);
//        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), dp.getDoctor());
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), dp.getDoctor().getHospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), dp.getDoctor().getAcademic_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), dp.getDoctor().getMedical_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), cityId);
    }

    @Test
    public void test_04_认证医生_同时认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        User dp = new User();
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        pathValue.put("id", doctorId);

        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建专家失败，认证用例无法执行");

        body.put("status", "1");  // 认证成功
        body.put("reason", "程序测试认真成功原因");  // 成功原因
        body.put("kb_id", expertId);
        body.put("is_signed", "1");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String res1 = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res1);
        Assert.assertEquals(Helper.s_ParseJson(data, "is_verified"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), expertId);
        String mobile = Helper.s_ParseJson(data, "mobile");
        String cityId = Helper.s_ParseJson(data, "city_id");


        String res2 = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res2);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "register_id"), doctorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "SIGNED");

        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.getDoctor().getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), mobile);
//        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), dp.getDoctor());
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), dp.getDoctor().getHospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), dp.getDoctor().getAcademic_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), dp.getDoctor().getMedical_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), cityId);
    }

}
