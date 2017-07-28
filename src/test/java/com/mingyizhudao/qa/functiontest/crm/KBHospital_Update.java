package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_Update extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/hospitals/{hospital_id}";

    public static HashMap<String, String> s_Update(String hospitalId, HospitalProfile hp) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hp.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
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
    public void test_01_没有token不能操作() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String name = "医院改名了";
        hpModified.body.put("name", name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), "", pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不能操作");
    }

    //    body.put("name", "测试医库医院" + tmp);
    @Test
    public void test_02_更新name() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String name = "医院改名了";
        hpModified.body.put("name", name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院名称未更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_name = hospitalInfo.get("name");
        Assert.assertEquals(actual_name, name, "医院名称未更新成功");
    }

    //    body.put("short_name", "测试短名" + tmp.substring(8));
    @Test
    public void test_02_更新short_name() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String short_name = "改短名了";
        hpModified.body.put("short_name", short_name);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "短名没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_short_name = hospitalInfo.get("short_name");
        Assert.assertEquals(actual_short_name, short_name, "短名没有更新成功");
    }
    //    body.put("hospital_class_list", UT.randomKey(KB.kb_hospital_class));
    @Test
    public void test_03_更新hospital_class_list() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String hospital_class_list = Generator.randomKey(KnowledgeBase.kb_hospital_class);
        hpModified.body.put("hospital_class_list", hospital_class_list);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院等级没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_hospital_class_list = hospitalInfo.get("hospital_class_list");
        Assert.assertEquals(actual_hospital_class_list, hospital_class_list, "医院等级没有更新成功");
    }
    //    body.put("type_list", UT.randomKey(KB.kb_hospital_type));
    @Test
    public void test_04_更新type_list() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String type_list = Generator.randomKey(KnowledgeBase.kb_hospital_type);
        hpModified.body.put("type_list", type_list);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院类型没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_type_list = hospitalInfo.get("type_list");
        Assert.assertEquals(actual_type_list, type_list, "医院类型没有更新成功");
    }
    //    body.put("city_id", UT.randomCityId());
    @Test
    public void test_05_更新city_id() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String city_id = Generator.randomCityId();
        hpModified.body.put("city_id", city_id);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertEquals(code, "1000000", "医院城市没有更新成功");
        } catch (IOException e) {
            logger.error(e);
        }
        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_city_id = hospitalInfo.get("city_id");
        String actual_city_name = hospitalInfo.get("city_name");
        Assert.assertEquals(actual_city_id, city_id, "医院城市没有更新成功");
        Assert.assertEquals(actual_city_name, Generator.cityName(city_id), "医院城市没有更新成功");

        hpModified.body.replace("city_id", "11"+city_id);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000", "错误的城市ID不能更新成功");
        } catch (IOException e) {
            logger.error(e);
        }
        hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        Assert.assertEquals(hospitalInfo.get("city_id"), city_id, "医院城市没有更新成功");
        Assert.assertEquals(hospitalInfo.get("city_name"), Generator.cityName(city_id), "医院城市没有更新成功");
    }
    //    body.put("county_id", UT.randomCountryId());
    @Test
    public void test_06_更新county_id() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String county_id = Generator.randomCountyId();
        hpModified.body.put("county_id", county_id);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院城市没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_county_id = hospitalInfo.get("county_id");
        String actual_county_name = hospitalInfo.get("county_name");
        Assert.assertEquals(actual_county_id, county_id, "医院区县没有更新成功");
        Assert.assertEquals(actual_county_name, Generator.countyName(county_id), "医院区县没有更新成功");
    }
    //    body.put("phone", "" + UT.randomPhone());
    @Test
    public void test_07_更新phone() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String phone = Generator.randomPhone();
        hpModified.body.put("phone", phone);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院电话没有更新成功");
        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_phone = hospitalInfo.get("phone");
        Assert.assertEquals(actual_phone, phone, "医院电话没有更新成功");
    }
    //    body.put("description", "医库医院描述" + UT.randomString(30));
    @Test
    public void test_08_更新description() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String description = "修改描述" + Generator.randomString(70);
        hpModified.body.put("description", description);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院描述没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_description = hospitalInfo.get("description");
        Assert.assertEquals(actual_description, description, "医院描述没有更新成功");
    }

    @Test(enabled = true)
    public void test_09_更新photo_url() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        JSONObject large = new JSONObject();
        large.put("key", "test1.jpg");
        large.put("type", "5");
        large.put("tag", "");
        JSONObject medium = new JSONObject();
        medium.put("key", "test1.jpg");
        medium.put("type", "5");
        medium.put("tag", "");
        JSONObject small = new JSONObject();
        small.put("key", "test1.jpg");
        small.put("type", "5");
        small.put("tag", "");
        hpModified.body.accumulate("photo_url", large);
        hpModified.body.accumulate("photo_url", medium);
        hpModified.body.accumulate("photo_url", small);
        try {
            res = HttpRequest.s_SendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院图片更新失败");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_photo_url = hospitalInfo.get("photo_url");
        JSONArray photos = JSONArray.fromObject(actual_photo_url);
        int size = photos.size();
        Assert.assertEquals(size, 3, "医院图片更新失败");
        Assert.assertNotNull(photos.getJSONObject(0).getString("url"), "医院图片更新失败");
    }
}
