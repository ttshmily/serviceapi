package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.DiseaseProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
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

    public static final Logger logger= Logger.getLogger(KBDisease_Create.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases";
    public static String mock = false ? "/mockjs/1" : "";

    public static HashMap<String, String> Create(DiseaseProfile dp) {
        String res = "";
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
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

        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Generator.parseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Generator.parseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Generator.parseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Generator.parseJson(data, "user_visible"), "true");
        Assert.assertEquals(Generator.parseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Generator.parseJson(data, "is_common"), "1");
        Assert.assertEquals(Generator.parseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_02_创建疾病_boolean取反() {
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        dp.body.replace("is_common", 0);
        dp.body.replace("user_visible", 0);
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Generator.parseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(Generator.parseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Generator.parseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Generator.parseJson(data, "user_visible"), "false");
        Assert.assertEquals(Generator.parseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Generator.parseJson(data, "is_common"), "0");
        Assert.assertEquals(Generator.parseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_03_创建疾病_缺少字段() {
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);

        dp.body.remove("name");
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("name", "疾病"+ Generator.randomString(2));
        dp.body.remove("user_visible");
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("user_visible", 1);
        dp.body.remove("is_common");
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

        dp.body.put("is_common", 1);
        dp.body.remove("category_list");
        try {
            res = HttpRequest.s_SendPost(host_crm + uri, dp.body.toString(), crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
