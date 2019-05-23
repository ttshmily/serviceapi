package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Disease;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mingyizhudao.qa.functiontest.crm.kb.management.KBDisease_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.kb.management.KBDisease_Detail.s_Detail;
import static com.mingyizhudao.qa.utilities.Generator.randomString;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_Update extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases/{id}";

    public static boolean s_Update(String diseaseId, Disease d) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        return JSONObject.fromObject(res).getString("code").equals("1000000");
    }

    @Test
    public void test_01_更新疾病详情_基本信息() {

        String res = "";
        Disease d = new Disease();
        String diseaseId = s_Create(d);
        if (diseaseId == null)
            Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);

        d.setName("疾病"+randomString(3));
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), d.getName());

        d.setDescription(randomString(100));
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), d.getDescription());

        d.setUser_visible(0);
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "user_visible"), "false");

        d.setIs_common(0);
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "is_common"), d.getIs_common().toString());

    }

    @Test
    public void test_02_更新疾病详情_关联专业() {

        String res = "";
        Disease d = new Disease();
        String diseaseId = s_Create(d);
        if (diseaseId == null)
            Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);

        List<String> input_ids = new ArrayList<>();
        List<String> output_ids = new ArrayList<>();
        JSONObject categoryId = new JSONObject();

        List<Disease.Category> tmp = d.getCategory_list();
        tmp.add(d.new Category());
        tmp.add(d.new Category());
        d.setCategory_list(tmp);
        res = HttpRequest.s_SendPut(host_crm + uri, d.transform(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        /*JSONArray category_list = data.getJSONArray("category_list");
        for (int i=0; i<category_list.size(); i++) {
            JSONObject category = category_list.getJSONObject(i);
            output_ids.add(category.getString("disease_category_id"));
        }
        logger.info(input_ids.toString());
        logger.info(output_ids.toString());
        for (int i=0; i<output_ids.size(); i++) {
            Assert.assertTrue(input_ids.contains(output_ids.get(i)));
        }
        for (int i=0; i<input_ids.size(); i++) {
            Assert.assertTrue(output_ids.contains(input_ids.get(i)));
        }*/
    }

/*
    @Test
    public void test_03_更新疾病详情_关联专业_去重() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = s_CreateTid(dp);
        String diseaseId = info.get("id");
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);
        DiseaseProfile dpModified = new DiseaseProfile(false);

        List<String> input_ids = new ArrayList<>();
        List<String> output_ids = new ArrayList<>();
        JSONObject categoryId = new JSONObject();
        String id = Generator.randomMajorId();
        categoryId.put("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        id = Generator.randomMajorId();
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        id = Generator.randomMajorId();
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        //以下为重复
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
//        input_ids.add(id);
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
//        input_ids.add(id);
        res = HttpRequest.s_SendPut(host_crm + uri, dpModified.body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = s_Detail(diseaseId);
        s_CheckResponse(res);
        JSONArray category_list = data.getJSONArray("category_list");
        for (int i=0; i<category_list.size(); i++) {
            JSONObject category = category_list.getJSONObject(i);
            output_ids.add(category.getString("disease_category_id"));
        }
        logger.info(input_ids.toString());
        logger.info(output_ids.toString());
        Assert.assertTrue(input_ids.size()==output_ids.size());
        for (int i=0; i<output_ids.size(); i++) {
            Assert.assertTrue(input_ids.contains(output_ids.get(i)));
        }
        for (int i=0; i<input_ids.size(); i++) {
            Assert.assertTrue(output_ids.contains(input_ids.get(i)));
        }
    }

    @Test
    public void test_04_更新疾病详情_关联专业_自动去除错误ID() {

        String res = "";
        DiseaseProfile dp = new DiseaseProfile(true);
        HashMap<String, String> info = s_CreateTid(dp);
        String diseaseId = info.get("id");
        if (info == null) Assert.fail("创建疾病失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", diseaseId);
        DiseaseProfile dpModified = new DiseaseProfile(false);

        List<String> input_ids = new ArrayList<>();
        List<String> output_ids = new ArrayList<>();
        JSONObject categoryId = new JSONObject();
        String id = Generator.randomMajorId();
        categoryId.put("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        id = Generator.randomMajorId();
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        id = Generator.randomMajorId();
        categoryId.replace("disease_category_id", id);
        dpModified.body.accumulate("category_list", categoryId);
        input_ids.add(id);
        //以下为错误
        categoryId.replace("disease_category_id", "11"+id);
        dpModified.body.accumulate("category_list", categoryId);
//        input_ids.add(id);
        categoryId.replace("disease_category_id", "22"+id);
        dpModified.body.accumulate("category_list", categoryId);
//        input_ids.add(id);
        res = HttpRequest.s_SendPut(host_crm + uri, dpModified.body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000"); //抱专业错误

        res = KBDisease_Detail.s_Detail(diseaseId);
        s_CheckResponse(res);
        JSONArray category_list = data.getJSONArray("category_list");
        for (int i=0; i<category_list.size(); i++) {
            JSONObject category = category_list.getJSONObject(i);
            output_ids.add(category.getString("disease_category_id"));
        }
        logger.info(input_ids.toString());
        logger.info(output_ids.toString());
        Assert.assertTrue(input_ids.size()==output_ids.size());
        for (int i=0; i<output_ids.size(); i++) {
            Assert.assertTrue(input_ids.contains(output_ids.get(i)));
        }
        for (int i=0; i<input_ids.size(); i++) {
            Assert.assertTrue(output_ids.contains(input_ids.get(i)));
        }
    }
*/
}
