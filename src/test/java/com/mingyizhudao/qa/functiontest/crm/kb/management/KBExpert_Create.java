package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

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
    public static String uri = "/api/v1/medicallibrary/doctors";

    public static HashMap<String, String> s_Create(ExpertProfile ep) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm+uri, ep.body.toString(), crm_token);
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
    public void test_01_创建医生() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);

        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "gender"), ep.body.getString("gender"));
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), ep.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), ep.body.getString("major_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "city_id"));
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), ep.body.getString("hospital_id"));
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), ep.body.getString("academic_title_list"));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), ep.body.getString("description"));
        Assert.assertEquals(Helper.s_ParseJson(data, "specialty"), ep.body.getString("specialty"));
        Assert.assertEquals(Helper.s_ParseJson(data, "honour"), ep.body.getString("honour"));
        Assert.assertEquals(Helper.s_ParseJson(data, "start_year"), ep.body.getString("start_year"));
        Assert.assertEquals(Helper.s_ParseJson(data, "birthday"), ep.body.getString("birthday").replace('/', '-'));
        Assert.assertNotNull(Helper.s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_visible"));

    }

    @Test
    public void test_02_创建医生_只有必填字段() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(false);
        String name = "医库医生"+ Generator.randomString(2);
        String hospital_id = Generator.randomKey(KnowledgeBase.kb_hospital);
        String major_id = Generator.randomKey(KnowledgeBase.kb_major);
        ep.body.put("name", name);
        ep.body.put("hospital_id", hospital_id);
        ep.body.put("major_id", major_id);
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), major_id);
        Assert.assertEquals(Helper.s_ParseJson(data, "major"), KnowledgeBase.kb_major.get(major_id));
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospital_id);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(hospital_id));
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "NOT_CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "NOT_SIGNED");
        Assert.assertEquals(Helper.s_ParseJson(data, "source_type"), "CRM_SYSTEM");

        Assert.assertNotNull(Helper.s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "city_id"), "城市ID不能少");

    }

    @Test
    public void test_03_创建医生_缺少必填字段() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(false);
        String name = "医库医生"+ Generator.randomString(2);
        String hospital_id = Generator.randomKey(KnowledgeBase.kb_hospital);
        String major_id = Generator.randomKey(KnowledgeBase.kb_major);
        ep.body.put("name", name); // name
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.body.put("hospital_id", hospital_id); // name + hospital
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.body.remove("hospital_id");
        ep.body.put("major_id", major_id); // name + major
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.body.remove("name"); // major
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.body.put("hospital_id", hospital_id); // major + hospital
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        ep.body.remove("major_id"); // hospital
        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_04_创建医生_枚举字段为空值() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(false);
        String name = "医库医生"+ Generator.randomString(2);
        String hospital_id = Generator.randomKey(KnowledgeBase.kb_hospital);
        String major_id = Generator.randomKey(KnowledgeBase.kb_major);
        ep.body.put("name", name);
        ep.body.put("hospital_id", hospital_id);
        ep.body.put("major_id", major_id);
        ep.body.put("medical_title_list", "");
        ep.body.put("academic_title_list", "");
        ep.body.put("birthday", "");
        ep.body.put("start_year", "");

        res = HttpRequest.s_SendPost(host_crm + uri, ep.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), name);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), major_id);
        Assert.assertEquals(Helper.s_ParseJson(data, "major"), KnowledgeBase.kb_major.get(major_id));
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospital_id);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(hospital_id));
        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "NOT_CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "NOT_SIGNED");
        Assert.assertEquals(Helper.s_ParseJson(data, "source_type"), "CRM_SYSTEM");

        Assert.assertNotNull(Helper.s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "city_id"), "城市ID不能少");

    }

}
