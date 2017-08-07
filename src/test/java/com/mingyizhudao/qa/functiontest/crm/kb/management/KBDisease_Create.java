package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.DiseaseProfile;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_Create extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases";

    public static HashMap<String, String> s_Create(DiseaseProfile dp) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm+uri, dp.body.toString(), crm_token);
        JSONObject node = JSONObject.fromObject(res);
        HashMap<String, String> result = new HashMap<>();
        if(!node.getString("code").equals("1000000")) return null;
        if(!node.has("data")) return null;
        JSONObject disease = node.getJSONObject("data");
        result.put("id", disease.getString("id"));
        result.put("name", disease.getString("name"));
        List<String> category_list = new ArrayList<>();
        JSONArray categoryList = node.getJSONObject("data").getJSONArray("category_list");
        for (int i=0; i<categoryList.size(); i++) {
            JSONObject category = categoryList.getJSONObject(i);
            category_list.add(category.getString("disease_category_id"));
        }
        result.put("category_list", category_list.toString());
        return result;
    }

    @Test
    public void test_01_创建疾病() {
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);

        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Helper.s_ParseJson(data, "user_visible"), "true");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Helper.s_ParseJson(data, "is_common"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_02_创建疾病_boolean取反() {
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        dp.body.replace("is_common", 0);
        dp.body.replace("user_visible", 0);
        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Helper.s_ParseJson(data, "user_visible"), "false");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Helper.s_ParseJson(data, "is_common"), "0");
        Assert.assertEquals(Helper.s_ParseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_03_创建疾病_缺少字段() {
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);

        dp.body.remove("name");
        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("name", "疾病"+ Generator.randomString(2));
        dp.body.remove("user_visible");
        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("user_visible", 1);
        dp.body.remove("is_common");
        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("is_common", 1);
        dp.body.remove("category_list");
        res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
