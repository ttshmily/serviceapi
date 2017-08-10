package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.DiseaseProfile;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_Detail extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases/{id}";

    public static String s_Detail(String diseaseId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",diseaseId);
        res = HttpRequest.s_SendGet(host_crm + uri,"", crm_token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取疾病详情_有效ID() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.s_Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), dp.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), dp.body.getString("description"));
        Assert.assertEquals(Helper.s_ParseJson(data, "user_visible"), "true");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(Helper.s_ParseJson(data, "is_common"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "category_list(0):disease_category_id"), dp.body.getJSONArray("category_list").getJSONObject(0).getString("disease_category_id"));
    }

    @Test
    public void test_02_获取疾病详情_无效ID() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.s_Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","111"+info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_获取疾病详情_检查关联医生数量() {
//TODO
        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = KBDisease_Create.s_Create(dp);
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        String diseaseId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "related_to_doctors"), "0");
        List<String> ids = new ArrayList<>();
        ids.add(diseaseId);

        if (!KBExpert_Diseases.s_Connect(Generator.randomExpertId(), ids)) Assert.fail("关联疾病失败，退出用例执行");
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "related_to_doctors"), "1");

        KBExpert_Diseases.s_Connect(Generator.randomExpertId(), ids);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "related_to_doctors"), "2");

        KBExpert_Diseases.s_Connect(Generator.randomExpertId(), ids);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "related_to_doctors"), "3");

    }
}
