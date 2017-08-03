package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_CertifySync_V2;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_Update extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/doctors/{id}";

    public static HashMap<String, String> s_Update(String expertId, ExpertProfile ep) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, ep.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        JSONObject node = JSONObject.fromObject(res);
        HashMap<String, String> result = new HashMap<>();
        if(!node.getString("code").equals("1000000")) return null;
        if(!node.has("data")) return null;
        JSONObject expert = node.getJSONObject("data");
        result.put("id", expert.getString("id"));
        result.put("name", expert.getString("name"));
        result.put("major_id", expert.getString("major_id"));
        result.put("hospital_id", expert.getString("hospital_id"));
        return result;
    }

    @Test
    public void test_01_没有token不能操作() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String name = "改名了";
        epModified.body.put("name", name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不能操作");
    }

    @Test
    public void test_02_更新个人信息_name() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String name = "改名了";
        epModified.body.put("name", name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "name"), name, "姓名未更新");
    }

    @Test
    public void test_03_更新个人信息_hospital_id() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String hospitalId = Generator.randomHospitalId();
        epModified.body.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");

        HashMap<String, String> hosInfo = KBHospital_Detail.s_Detail(hospitalId);
        String cityId = hosInfo.get("city_id");
        String countryId =  hosInfo.get("county_id");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_id"), hospitalId, "医生的医院ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_name"), Generator.hospitalName(hospitalId), "医生的医院名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "city_id"), cityId, "医生的城市没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "county_id"), countryId, "医生的地区名称没有更新");

    }

    @Test
    public void test_04_更新个人信息_禁止更新city_id() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String cityId = Generator.randomCityId();
        epModified.body.put("city_id", cityId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "禁止更新city_id");

        res = KBExpert_Detail.s_Detail(expertId);
    }

    @Test
    public void test_05_更新个人信息_禁止更新county_id() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String countryId = Generator.randomCountyId();
        epModified.body.put("county_id", countryId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "禁止更新country_id");

        res = KBExpert_Detail.s_Detail(expertId);
    }

    @Test
    public void test_06_更新个人信息_更新major_id() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String majorId = Generator.randomMajorId();
        epModified.body.put("major_id", majorId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000", "更新major_id失败");
        } catch (IOException e) {
            logger.error(e);
        }
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "major_id"), majorId, "专业ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major"), Generator.majorName(majorId), "专业名称没有更新");


        epModified.body.replace("major_id", "11"+majorId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
            s_CheckResponse(res);
            Assert.assertNotEquals(code, "1000000", "错误major_id");
        } catch (IOException e) {
            logger.error(e);
        }
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "major_id"), majorId, "专业ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major"), Generator.majorName(majorId), "专业名称没有更新");
    }

    @Test
    public void test_07_更新个人信息_更新medical_title_list和academic_title_list() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String medical_title_list = Generator.randomMedicalId();
        String academic_title_list = Generator.randomAcademicId();
        epModified.body.put("medical_title_list", medical_title_list);
        epModified.body.put("academic_title_list", academic_title_list);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新medical_title_list失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list, "技术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list, "学术职称没有更新");

        medical_title_list = Generator.randomMedicalId();
        academic_title_list = Generator.randomAcademicId();
        epModified.body.replace("medical_title_list", medical_title_list);
        epModified.body.replace("academic_title_list", academic_title_list);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新medical_title_list失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list, "技术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list, "学术职称没有更新");
    }

    @Test
    public void test_08_更新个人信息_更新specialty和honour和description() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String specialty = "特长"+ Generator.randomString(100);
        String honour = "荣誉"+ Generator.randomString(50);
        String description = "简介"+ Generator.randomString(70);
        epModified.body.put("specialty", specialty);
        epModified.body.put("honour", honour);
        epModified.body.put("description", description);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新三个说明字段失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "specialty"), specialty, "特长没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "honour"), honour, "荣誉没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "description"), description, "简介没有更新");
    }

    @Test(enabled = true)
    public void test_09_更新avatar_url() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String doctorId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        ExpertProfile epModified = new ExpertProfile(false);
        JSONObject large = new JSONObject();
        large.put("key", "test1.jpg");
        large.put("type", "5");
        large.put("tag", "");
        JSONObject medium = new JSONObject();
        medium.put("key", "test1.jpg");
        medium.put("type", "5");
        medium.put("tag", "");
        JSONObject small = new JSONObject();
        small.put("key", "test1.jpg");
        small.put("type", "5");
        small.put("tag", "");
        epModified.body.accumulate("avatar_url", large);
        epModified.body.accumulate("avatar_url", medium);
        epModified.body.accumulate("avatar_url", small);
        logger.info(epModified.body.toString());
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院图片更新失败");

        res = KBExpert_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "avatar_url()"), "3", "医院图片更新失败");
        Assert.assertNotNull(Generator.s_ParseJson(data, "avatar_url():url"), "医院图片更新失败");
    }

    @Test
    public void test_10_更新个人信息_已关联医生会同步给注册用户() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) {
            Assert.fail("创建医库医生失败，退出用例执行");
        }
        String expertId = info.get("id");

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if ( doctorId == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(doctorId, "1", expertId);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        String medical_title_list = Generator.randomMedicalId();
        String academic_title_list = Generator.randomAcademicId();
        String majorId = Generator.randomMajorId();
        String hospitalId = Generator.randomHospitalId();
        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String cityId = hospitalInfo.get("city_id");
        String countyId = hospitalInfo.get("county_id");
        String name = "医库中改名" + Generator.randomString(8);
        epModified.body.put("name", name);
        epModified.body.put("major_id", majorId);
        epModified.body.put("hospital_id", hospitalId);
        epModified.body.put("medical_title_list", medical_title_list);
        epModified.body.put("academic_title_list", academic_title_list);
        epModified.body.put("signed_status", "1");
        epModified.body.put("start_year", "2007");
        JSONObject small = new JSONObject();
        small.put("key", "test1.jpg");
        small.put("type", "5");
        JSONObject large = new JSONObject();
        large.put("key", "test1.jpg");
        large.put("type", "5");
        epModified.body.accumulate("avatar_url", large);
        epModified.body.accumulate("avatar_url", small);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新已关联的医库医生信息失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list, "技术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list, "学术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "name"), name, "姓名没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major_id"), majorId, "专业没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major"), Generator.majorName(majorId), "专业没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_id"), hospitalId, "医院ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_name"), Generator.hospitalName(hospitalId), "医院名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "city_id"), cityId, "城市ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "city_name"), Generator.cityName(cityId), "城市名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "county_id"), countyId, "区县ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "county_name"), Generator.countyName(countyId), "区县名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "SIGNED", "主刀专家状态没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "start_year"), "2007", "专家从业时间没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "avatar_url()"), "2", "医院图片更新失败");
        Assert.assertNotNull(Generator.s_ParseJson(data, "avatar_url():url"), "医院图片更新失败");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "medical_title_list"), medical_title_list, "技术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "academic_title_list"), academic_title_list, "学术职称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "name"), name, "姓名没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major_id"), majorId, "专业没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "major_name"), Generator.majorName(majorId), "专业没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_id"), hospitalId, "医院ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "hospital_name"), Generator.hospitalName(hospitalId), "医院名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "city_id"), cityId, "城市ID没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "city"), Generator.cityName(cityId), "城市名称没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_id"), cityId, "区县ID没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_name"), UT.countyName(countyId), "区县名称没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "SIGNED", "主刀专家状态没有更新");
        Assert.assertEquals(Generator.s_ParseJson(data, "icon()"), "2", "医院图片更新失败");
        Assert.assertNotNull(Generator.s_ParseJson(data, "icon():largePicture"), "医院图片更新失败");
    }

    @Test
    public void test_11_更新个人信息_未关联专家更新signed_status() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);

        epModified.body.put("signed_status", "0");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "NOT_SIGNED");

        epModified.body.put("signed_status", "1");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "SIGNED");
    }

    @Test
    public void test_12_更新个人信息_已关联医生修改专家认证字段() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null)
            Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");

        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");

        RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(doctorId, "1", expertId);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        ExpertProfile epModified = new ExpertProfile(false);
        epModified.body.put("signed_status", "0");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "NOT_SIGNED");

        epModified.body.put("signed_status", "1");
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, epModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Generator.s_ParseJson(data, "signed_status"), "SIGNED");
    }

}
