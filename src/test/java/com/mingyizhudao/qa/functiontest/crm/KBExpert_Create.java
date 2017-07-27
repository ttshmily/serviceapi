package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_Create extends BaseTest {

    public static final Logger logger= Logger.getLogger(KBExpert_Create.class);
    public static String uri = "/api/v1/medicallibrary/doctors";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static HashMap<String, String> Create(ExpertProfile ep) {
        String res = "";
        try {
            res = HttpRequest.sendPost(host_crm+uri, ep.body.toString(), crm_token);
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
    public void test_01_创建医生() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);

        try {
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotNull(Generator.parseJson(data, "id"), "医库ID不能少");
            Assert.assertEquals(Generator.parseJson(data, "gender"), ep.body.getString("gender"));
            Assert.assertEquals(Generator.parseJson(data, "name"), ep.body.getString("name"));
            Assert.assertEquals(Generator.parseJson(data, "major_id"), ep.body.getString("major_id"));
            Assert.assertNotNull(Generator.parseJson(data, "city_id"));
            Assert.assertEquals(Generator.parseJson(data, "hospital_id"), ep.body.getString("hospital_id"));
            Assert.assertEquals(Generator.parseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
            Assert.assertEquals(Generator.parseJson(data, "academic_title_list"), ep.body.getString("academic_title_list"));
            Assert.assertEquals(Generator.parseJson(data, "description"), ep.body.getString("description"));
            Assert.assertEquals(Generator.parseJson(data, "specialty"), ep.body.getString("specialty"));
            Assert.assertEquals(Generator.parseJson(data, "honour"), ep.body.getString("honour"));
            Assert.assertEquals(Generator.parseJson(data, "start_year"), ep.body.getString("start_year"));
            Assert.assertEquals(Generator.parseJson(data, "birthday"), ep.body.getString("birthday").replace('/', '-'));
            Assert.assertNotNull(Generator.parseJson(data, "user_visible"));
            Assert.assertNotNull(Generator.parseJson(data, "doctor_visible"));
//            Assert.assertNotNull(parseJson(data, "avatar_url"));

        } catch (IOException e) {
            logger.error(e);
        }
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
        try {
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotNull(Generator.parseJson(data, "id"), "医库ID不能少");
            Assert.assertEquals(Generator.parseJson(data, "name"), name);
            Assert.assertEquals(Generator.parseJson(data, "major_id"), major_id);
            Assert.assertEquals(Generator.parseJson(data, "major"), KnowledgeBase.kb_major.get(major_id));
            Assert.assertEquals(Generator.parseJson(data, "hospital_id"), hospital_id);
            Assert.assertEquals(Generator.parseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(hospital_id));
            Assert.assertEquals(Generator.parseJson(data, "certified_status"), "NOT_CERTIFIED");
            Assert.assertEquals(Generator.parseJson(data, "signed_status"), "NOT_SIGNED");
            Assert.assertEquals(Generator.parseJson(data, "source_type"), "CRM_SYSTEM");

            Assert.assertNotNull(Generator.parseJson(data, "user_visible"));
            Assert.assertNotNull(Generator.parseJson(data, "doctor_visible"));
            Assert.assertNotNull(Generator.parseJson(data, "city_id"), "城市ID不能少");

        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    public void test_03_创建医生_缺少必填字段() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(false);
        String name = "医库医生"+ Generator.randomString(2);
        String hospital_id = Generator.randomKey(KnowledgeBase.kb_hospital);
        String major_id = Generator.randomKey(KnowledgeBase.kb_major);
        try {
            ep.body.put("name", name); // name
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");
            ep.body.put("hospital_id", hospital_id); // name + hospital
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");
            ep.body.remove("hospital_id");
            ep.body.put("major_id", major_id); // name + major
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");
            ep.body.remove("name"); // major
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");
            ep.body.put("hospital_id", hospital_id); // major + hospital
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");
            ep.body.remove("major_id"); // hospital
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000");

        } catch (IOException e) {
            logger.error(e);
        }
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

        try {
            res = HttpRequest.sendPost(host_crm + uri, ep.body.toString(), crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotNull(Generator.parseJson(data, "id"), "医库ID不能少");
            Assert.assertEquals(Generator.parseJson(data, "name"), name);
            Assert.assertEquals(Generator.parseJson(data, "major_id"), major_id);
            Assert.assertEquals(Generator.parseJson(data, "major"), KnowledgeBase.kb_major.get(major_id));
            Assert.assertEquals(Generator.parseJson(data, "hospital_id"), hospital_id);
            Assert.assertEquals(Generator.parseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(hospital_id));
            Assert.assertEquals(Generator.parseJson(data, "certified_status"), "NOT_CERTIFIED");
            Assert.assertEquals(Generator.parseJson(data, "signed_status"), "NOT_SIGNED");
            Assert.assertEquals(Generator.parseJson(data, "source_type"), "CRM_SYSTEM");

            Assert.assertNotNull(Generator.parseJson(data, "user_visible"));
            Assert.assertNotNull(Generator.parseJson(data, "doctor_visible"));
            Assert.assertNotNull(Generator.parseJson(data, "city_id"), "城市ID不能少");

        } catch (IOException e) {
            logger.error(e);
        }
    }

}
