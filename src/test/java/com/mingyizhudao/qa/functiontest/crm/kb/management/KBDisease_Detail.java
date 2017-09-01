package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Disease;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

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
        Disease d = new Disease();
        String id = KBDisease_Create.s_Create(d);
        if (id == null)
            Assert.fail("创建疾病失败，退出用例执行");

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",id);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "name"), d.getName());
        Assert.assertEquals(s_ParseJson(data, "description"), d.getDescription());
        Assert.assertEquals(s_ParseJson(data, "user_visible"), "true");
        Assert.assertEquals(s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(s_ParseJson(data, "is_common"), d.getIs_common().toString());
        Assert.assertEquals(s_ParseJson(data, "category_list(0):disease_category_id"), d.getCategory_list().get(0).getDisease_category_id());
    }

    @Test
    public void test_02_获取疾病详情_无效ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","abc");
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_03_获取疾病详情_检查关联医生数量() {
        String res = "";
        Disease d = new Disease();
        String diseaseId = KBDisease_Create.s_Create(d);
        if (diseaseId == null)
            Assert.fail("创建疾病失败，退出用例执行");

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);

        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "related_to_doctors"), "0");
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
