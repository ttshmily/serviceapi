package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.DiseaseProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
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
public class KBDisease_Detail extends BaseTest {

    public static final Logger logger= Logger.getLogger(KBDisease_Detail.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases/{id}";
    public static String mock = false ? "/mockjs/1" : "";

    public static String Detail(String diseaseId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",diseaseId);
        try {
            res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取疾病详情_有效ID() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",info.get("id"));
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Generator.parseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Generator.parseJson(data, "user_visible"), "true");
        Assert.assertEquals(Generator.parseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Generator.parseJson(data, "is_common"), "1");
        Assert.assertEquals(Generator.parseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_02_获取疾病详情_无效ID() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","111"+info.get("id"));
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_获取疾病详情_检查关联医生数量() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        String diseaseId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "related_to_doctors"), "0");
        List<String> ids = new ArrayList<>();
        ids.add(diseaseId);

        if (!KBExpert_Diseases.Connect(Generator.randomExpertId(), ids)) Assert.fail("关联疾病失败，退出用例执行");
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "related_to_doctors"), "1");

        KBExpert_Diseases.Connect(Generator.randomExpertId(), ids);
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "related_to_doctors"), "2");

        KBExpert_Diseases.Connect(Generator.randomExpertId(), ids);
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "related_to_doctors"), "3");

    }
}
