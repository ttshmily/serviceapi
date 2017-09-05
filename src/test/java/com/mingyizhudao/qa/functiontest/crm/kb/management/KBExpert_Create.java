package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_Create extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v2/medicallibrary/doctors";

    public static String s_Create(Doctor ep) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(ep).toString(), crm_token);
        JSONObject node = JSONObject.fromObject(res);
        HashMap<String, String> result = new HashMap<>();
        if(!node.getString("code").equals("1000000")) return null;
        if(!node.has("data")) return null;
        JSONObject expert = node.getJSONObject("data");
        result.put("id", expert.getString("id"));
        result.put("name", expert.getString("name"));
        result.put("major_id", expert.getString("major_id"));
        result.put("hospital_id", expert.getString("hospital_id"));
        return expert.getString("id");
    }

    @Test
    public void test_01_创建医生() {
        String res = "";
        Doctor ep = new Doctor();
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        String expertId = data.getString("id");
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(s_ParseJson(data, "gender"), String.valueOf(ep.getGender()));
        Assert.assertEquals(s_ParseJson(data, "name"), ep.getName());
//        Assert.assertNotNull(s_ParseJson(data, "city_id"), KBHospital_Detail.s_Detail(ep.getHospital_id()).get("city_id")); //TODO
        Assert.assertEquals(s_ParseJson(data, "hospital_id"), ep.getHospital_id());
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), ep.getMedical_title_list());
        Assert.assertEquals(s_ParseJson(data, "academic_title_list"), ep.getAcademic_title_list());
        Assert.assertEquals(s_ParseJson(data, "description"), ep.getDescription());
        Assert.assertEquals(s_ParseJson(data, "specialty"), ep.getSpecialty());
        Assert.assertEquals(s_ParseJson(data, "honour"), ep.getHonour());
        Assert.assertEquals(s_ParseJson(data, "start_year"), ep.getStart_year());
        Assert.assertEquals(s_ParseJson(data, "birthday"), ep.getBirthday().replace('/', '-'));
        Assert.assertNotNull(s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(s_ParseJson(data, "doctor_visible"));
        Assert.assertEquals(s_ParseJson(data, "department_category_id"), ep.getDepartment_category_id(), "科室类别没有更新");
        Assert.assertEquals(s_ParseJson(data, "department_category_name"), departmentName(ep.getDepartment_category_id()), "科室类别没有更新");
    }

    @Test
    public void test_02_创建医生_只有必填字段() {
        String res = "";
        Doctor ep = new Doctor("basic");

        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(s_ParseJson(data, "name"), ep.getName());
        Assert.assertEquals(s_ParseJson(data, "hospital_id"), ep.getHospital_id());
        Assert.assertEquals(s_ParseJson(data, "hospital_name"), hospitalName(ep.getHospital_id()));
        Assert.assertEquals(s_ParseJson(data, "medical_title_list"), ep.getMedical_title_list());
//        Assert.assertEquals(s_ParseJson(data, "city_id"), KBHospital_Detail.s_Detail(ep.getHospital_id()).get("city_id"));
    }

    @Test
    public void test_03_创建医生_缺少必填字段() {
        String res = "";
        Doctor ep = new Doctor("basic");

        String tmp = ep.getName();
        ep.setName(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.setName(tmp);

        tmp = ep.getHospital_id();
        ep.setHospital_id(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.setHospital_id(tmp);

        tmp = ep.getMedical_title_list();
        ep.setMedical_title_list(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.setMedical_title_list(tmp);

        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_04_创建医生_枚举字段为空值() {
        String res = "";
        Doctor ep = new Doctor();

        String tmp = ep.getMedical_title_list();
        ep.setMedical_title_list("WRONG_ENUM");
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.setMedical_title_list(tmp);

        ep.setAcademic_title_list("WRONG_ENUM");
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(ep).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
