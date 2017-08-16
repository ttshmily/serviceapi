package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Hospital;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import static com.mingyizhudao.qa.utilities.Generator.*;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.*;

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


//    public static HashMap<String, String> s_Create(HospitalProfile_test hp) {
//        String res = "";
//        TestLogger logger = new TestLogger(s_JobName());
//        res = HttpRequest.s_SendPost(host_crm+uri, hp.body.toString(), crm_token);
//        JSONObject node = JSONObject.fromObject(res);
//        if(!node.getString("code").equals("1000000")) return null;
//        if(!node.has("data")) return null;
//        HashMap<String, String> result = new HashMap<>();
//        JSONObject expert = node.getJSONObject("data");
//        result.put("id", expert.getString("id"));
//        result.put("name", expert.getString("name"));
//        result.put("city_id", expert.getString("city_id"));
//        result.put("county_id", expert.getString("county_id"));
//        return result;
//    }
    public static HashMap<String, String> s_Create(Hospital hp) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(hp).toString(), crm_token);
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

//    public static String s_Create(Hospital hp) {
//        String res = "";
//        TestLogger logger = new TestLogger(s_JobName());
//        res = HttpRequest.s_SendPost(host_crm+uri, JSONObject.fromObject(hp).toString(), crm_token);
//        JSONObject node = JSONObject.fromObject(res);
//        return node.getString("code").equals("1000000") ? node.getJSONObject("data").getString("id") : null;
//    }

    @Test
    public void test_01_创建医院() {
        String res = "";
        Hospital hp = new Hospital();
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "short_name"), hp.getShort_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), hp.getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_name"), cityName(hp.getCity_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "county_name"), countyName(hp.getCounty_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), hp.getDescription());
        Assert.assertEquals(Helper.s_ParseJson(data, "phone"), hp.getPhone());
        Assert.assertNotNull(Helper.s_ParseJson(data, "user_visible"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_visible"));
    }

    @Test
    public void test_02_创建医院_只有必填字段() {
        String res = "";
        Hospital hp = new Hospital();
        hp.setCounty_id(null);
        hp.setDescription(null);
        hp.setShort_name(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
    }

    @Test
    public void test_03_创建医院_缺少必填字段() {
        String res = "";
        Hospital hp = new Hospital();

        String tmp = hp.getName(); // name
        hp.setName(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.setName(tmp);

        tmp = hp.getHospital_class_list();
        hp.setHospital_class_list(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.setHospital_class_list(tmp);

        tmp = hp.getType_list();
        hp.setType_list(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.setType_list(tmp);

        tmp = hp.getCity_id();
        hp.setCity_id(null);
        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        hp.setCity_id(tmp);

        res = HttpRequest.s_SendPost(host_crm + uri, JSONObject.fromObject(hp).toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

    }

}
