package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_CertifySync_V2;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
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

    public static HashMap<String, String> s_Update(String expertId, Doctor ep) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
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
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String name = "改名了";
        ep.setName(name);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不能操作");
    }

    @Test
    public void test_02_更新个人信息_name() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String name = "改名了";
        ep.setName(name);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "name"), ep.getName(), "姓名未更新");
    }

    @Test
    public void test_03_更新个人信息_hospital_id() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        Doctor epModified = new Doctor();
        String hospitalId = randomHospitalId();
        ep.setHospital_id(hospitalId);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");

        HashMap<String, String> hosInfo = KBHospital_Detail.s_Detail(hospitalId);
        String cityId = hosInfo.get("city_id");
        String countryId =  hosInfo.get("county_id");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "hospital_id"), hospitalId, "医生的医院ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "hospital_name"), hospitalName(ep.getHospital_id()), "医生的医院名称没有更新");
        Assert.assertEquals(s_ParseJson(data, "city_id"), cityId, "医生的城市没有更新");
        Assert.assertEquals(s_ParseJson(data, "city_name"), cityName(cityId), "医生的城市没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_id"), countryId, "医生的地区名称没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_name"), countyName(countryId), "医生的地区名称没有更新");

    }

    @Test(enabled = false)
    public void test_04_更新个人信息_禁止更新city_id() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String cityId = randomCityId();
        ep.setCity_id(cityId);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "禁止更新city_id");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertNotEquals(s_ParseJson(data, "city_id"), cityId);
    }

    @Test(enabled = false)
    public void test_05_更新个人信息_禁止更新county_id() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String countryId = randomCountyId();
        ep.setCounty_id(countryId);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "禁止更新country_id");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertNotEquals(s_ParseJson(data, "county_id"), countryId);
    }

    @Test
    public void test_06_更新个人信息_更新major_id() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String majorId = randomMajorId();
        ep.setMajor_id(majorId);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新major_id失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "major_id"), ep.getMajor_id(), "专业ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "major"), majorName(ep.getMajor_id()), "专业名称没有更新");

        String tmp = ep.getMajor_id();
        ep.setMajor_id("WRONG_MAJOR_ID");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "错误major_id");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "major_id"), tmp);
        Assert.assertEquals(s_ParseJson(data, "major"), majorName(tmp));
    }

    @Test
    public void test_07_更新个人信息_更新medical_title_list和academic_title_list() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        ep.setMedical_title_list(randomMedicalId());
        ep.setAcademic_title_list(randomAcademicId());
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新medical_title_list失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), ep.getMedical_title_list(), "技术职称没有更新");
        Assert.assertEquals(s_ParseJson(data, "academic_title_list"), ep.getAcademic_title_list(), "学术职称没有更新");

        String medical_title_list = ep.getMedical_title_list();
        String academic_title_list = ep.getAcademic_title_list();
        ep.setMedical_title_list("WRONG_MEDICAL_ID");
        ep.setAcademic_title_list("WRONG_ACADEMIC_ID");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), medical_title_list);
        Assert.assertEquals(s_ParseJson(data, "academic_title_list"), academic_title_list);
    }

    @Test
    public void test_08_更新个人信息_更新specialty和honour和description() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String specialty = "特长"+ randomString(100);
        String honour = "荣誉"+ randomString(50);
        String description = "简介"+ randomString(70);
        ep.setSpecialty(specialty);
        ep.setHonour(honour);
        ep.setDescription(description);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新三个说明字段失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "specialty"), specialty, "特长没有更新");
        Assert.assertEquals(s_ParseJson(data, "honour"), honour, "荣誉没有更新");
        Assert.assertEquals(s_ParseJson(data, "description"), description, "简介没有更新");
    }

    @Test(enabled = true)
    public void test_09_更新avatar_url() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        ep.setAvatar_url(new ArrayList<Doctor.Picture>(){
            {
                add(ep.new Picture("1.jpg", "5"));
                add(ep.new Picture("2.jgp", "5"));
                add(ep.new Picture("3.jgp", "5"));
            }
        });
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院图片更新失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Integer.parseInt(s_ParseJson(data, "avatar_url()")), ep.getAvatar_url().size(), "医院图片更新失败");
        Assert.assertNotNull(s_ParseJson(data, "avatar_url():url"), "医院图片更新失败");
    }

    @Test
    public void test_10_更新个人信息_已关联医生会同步给注册用户() {
        String res = "";
        String expertId = KBExpert_Create.s_Create(new Doctor());
        if (expertId == null) {
            Assert.fail("创建医库医生失败，退出用例执行");
        }

        String doctorId = s_CreateRegisteredDoctor(new User()).get("id");
        if ( doctorId == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(doctorId, "1", expertId);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        Doctor epModified = new Doctor();
        epModified.setAvatar_url(new ArrayList<Doctor.Picture>(){
            {
                add(epModified.new Picture("1.jpg", "5"));
                add(epModified.new Picture("2.jpg", "5"));
                add(epModified.new Picture("3.jpg", "5"));
            }
        });
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(epModified).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新已关联的医库医生信息失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), epModified.getMedical_title_list(), "技术职称没有更新");
        Assert.assertEquals(s_ParseJson(data, "academic_title_list"), epModified.getAcademic_title_list(), "学术职称没有更新");
        Assert.assertEquals(s_ParseJson(data, "name"), epModified.getName(), "姓名没有更新");
        Assert.assertEquals(s_ParseJson(data, "major_id"), epModified.getMajor_id(), "专业没有更新");
        Assert.assertEquals(s_ParseJson(data, "major"), majorName(epModified.getMajor_id()), "专业没有更新");
        Assert.assertEquals(s_ParseJson(data, "hospital_id"), epModified.getHospital_id(), "医院ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "hospital_name"), hospitalName(epModified.getHospital_id()), "医院名称没有更新");
        HashMap<String, String> hospital = KBHospital_Detail.s_Detail(epModified.getHospital_id());
        Assert.assertEquals(s_ParseJson(data, "city_id"), hospital.get("city_id"), "城市ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "city_name"), cityName(hospital.get("city_id")), "城市名称没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_id"), hospital.get("county_id"), "区县ID没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_name"), countyName(hospital.get("county_id")), "区县名称没有更新");
        Assert.assertEquals(s_ParseJson(data, "signed_status"), "SIGNED", "主刀专家状态没有更新");
        Assert.assertEquals(s_ParseJson(data, "start_year"), epModified.getStart_year(), "专家从业时间没有更新");
        Assert.assertEquals(Integer.parseInt(s_ParseJson(data, "avatar_url()")), epModified.getAvatar_url().size(), "医院图片更新失败");
        Assert.assertNotNull(s_ParseJson(data, "avatar_url():url"), "医院图片更新失败");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), epModified.getMedical_title_list(), "技术职称没有更新");
        Assert.assertEquals(s_ParseJson(data, "academic_title_list"), epModified.getAcademic_title_list(), "学术职称没有更新");
        Assert.assertEquals(s_ParseJson(data, "name"), epModified.getName(), "姓名没有更新");
        Assert.assertEquals(s_ParseJson(data, "major_id"), epModified.getMajor_id(), "专业没有更新");
        Assert.assertEquals(s_ParseJson(data, "major_name"), majorName(epModified.getMajor_id()), "专业没有更新");
        Assert.assertEquals(s_ParseJson(data, "hospital_id"), epModified.getHospital_id(), "医院ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "hospital_name"), hospitalName(epModified.getHospital_id()), "医院名称没有更新");
        Assert.assertEquals(s_ParseJson(data, "city_id"), hospital.get("city_id"), "城市ID没有更新");
        Assert.assertEquals(s_ParseJson(data, "city"), cityName(hospital.get("city_id")), "城市名称没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_id"), hospital.get("county_id"), "区县ID没有更新");
//        Assert.assertEquals(s_ParseJson(data, "county_name"), countyName(hospital.get("county_id")), "区县名称没有更新");
        Assert.assertEquals(Integer.parseInt(s_ParseJson(data, "icon()")), epModified.getAvatar_url().size(), "医院图片更新失败");
        Assert.assertNotNull(s_ParseJson(data, "icon():largePicture"), "医院图片更新失败");
    }

    @Test
    public void test_11_更新个人信息_未关联医生修改signed_status() {
        String res = "";
        String expertId = KBExpert_Create.s_Create(new Doctor());
        if (expertId == null) {
            Assert.fail("创建医库医生失败，退出用例执行");
        }
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        Doctor epModified = new Doctor();
        epModified.setSigned_status("0");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(epModified).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "signed_status"), "NOT_SIGNED");

        epModified.setSigned_status("1");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(epModified).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "signed_status"), "SIGNED");
    }

    @Test
    public void test_12_更新个人信息_已关联医生修改signed_status() {
        String res = "";
        String expertId = KBExpert_Create.s_Create(new Doctor());
        if (expertId == null) {
            Assert.fail("创建医库医生失败，退出用例执行");
        }
        String doctorId = s_CreateRegisteredDoctor(new User()).get("id");
        if (doctorId == null) {
            Assert.fail("创建医生失败，认证用例无法执行");
        }
        RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(doctorId, "1", expertId);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        Doctor epModified = new Doctor();
        epModified.setSigned_status("0");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(epModified).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "signed_status"), "NOT_SIGNED");

        epModified.setSigned_status("1");
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(epModified).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新主刀专家状态字段失败");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(s_ParseJson(data, "signed_status"), "SIGNED");
    }

    @Test
    public void test_13_更新个人信息_更新Department_name() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        String department_name = "科室"+ randomString(2);
        ep.setDepartment_name(department_name);
        res = s_SendPut(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "更新三个说明字段失败");
        Assert.assertEquals(s_ParseJson(data, "department_name"), department_name, "特长没有更新");
    }

}
