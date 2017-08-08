package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_Create extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/hospitals";


    public static HashMap<String, String> s_Create(HospitalProfile hp) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm+uri, hp.body.toString(), crm_token);
        JSONObject node = JSONObject.fromObject(res);
        if(!node.getString("code").equals("1000000")) return null;
        if(!node.has("data")) return null;
        HashMap<String, String> result = new HashMap<>();
        JSONObject expert = node.getJSONObject("data");
        result.put("id", expert.getString("id"));
        result.put("name", expert.getString("name"));
        result.put("city_id", expert.getString("city_id"));
        result.put("county_id", expert.getString("county_id"));
        return result;
    }

    @Test
    public void test_01_创建医院() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "short_name"), hp.body.getString("short_name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), hp.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "city_name"), KnowledgeBase.kb_city.get(hp.body.getString("city_id")));
        Assert.assertEquals(Helper.s_ParseJson(data, "county_name"), KnowledgeBase.kb_county.get(hp.body.getString("county_id")));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), hp.body.getString("description"));
        Assert.assertEquals(Helper.s_ParseJson(data, "phone"), hp.body.getString("phone"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_visible"));
    }

    @Test
    public void test_02_创建医院_只有必填字段() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(false);
        hp.body.put("name","测试医院");
        hp.body.put("hospital_class_list", Generator.randomKey(KnowledgeBase.kb_hospital_class));
        hp.body.put("type_list", Generator.randomKey(KnowledgeBase.kb_hospital_type));
        hp.body.put("city_id", Generator.randomCityId());
//        hp.body.put("county_id", UT.randomCountryId());
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
    }

    @Test
    public void test_03_创建医院_缺少必填字段() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(false);
        String name = "测试医院"+ Generator.randomString(2);
        String hospital_class_list = Generator.randomKey(KnowledgeBase.kb_hospital_class);
        String type_list = Generator.randomKey(KnowledgeBase.kb_hospital_type);
        String city_id = Generator.randomCityId();
        String county_id = Generator.randomCountyId();
        hp.body.put("name", name);
        hp.body.put("hospital_class_list", hospital_class_list);
        hp.body.put("type_list", type_list);
        hp.body.put("city_id", city_id);
        hp.body.put("county_id", county_id);
        hp.body.remove("name", name); // name
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.body.put("name", name); // name
        hp.body.remove("hospital_class_list", hospital_class_list);
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.body.put("hospital_class_list", hospital_class_list);
        hp.body.remove("type_list", type_list);
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.body.put("type_list", type_list);
        hp.body.remove("city_id", city_id);
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.body.put("city_id", city_id);
        hp.body.remove("county_id", county_id);
        res = HttpRequest.s_SendPost(host_crm + uri, hp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

}
