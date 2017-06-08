package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_Update extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBHospital_Update.class);
    public static String uri = "/api/v1/medicallibrary/hospitals/{hospital_id}";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static HashMap<String, String> Update(String hospitalId, HospitalProfile hp) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hp.body.toString(), crm_token, pathValue);
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
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String name = "医院改名了";
        hpModified.body.put("name", name);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), "", pathValue);
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
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String name = "医院改名了";
        hpModified.body.put("name", name);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院名称未更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "name"), name, "医院名称未更新成功");
    }

    //    body.put("short_name", "测试短名" + tmp.substring(8));
    @Test
    public void test_02_更新short_name() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String short_name = "改短名了";
        hpModified.body.put("short_name", short_name);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "短名没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "short_name"), short_name, "短名没有更新成功");
    }
    //    body.put("hospital_class_list", UT.randomKey(KB.kb_hospital_class));
    @Test
    public void test_03_更新hospital_class_list() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String hospital_class_list = UT.randomKey(KB.kb_hospital_class);
        hpModified.body.put("hospital_class_list", hospital_class_list);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院等级没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_class_list"), hospital_class_list, "医院等级没有更新成功");
    }
    //    body.put("type_list", UT.randomKey(KB.kb_hospital_type));
    @Test
    public void test_04_更新type_list() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String type_list = UT.randomKey(KB.kb_hospital_type);
        hpModified.body.put("type_list", type_list);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院类型没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "type_list"), type_list, "医院类型没有更新成功");
    }
    //    body.put("city_id", UT.randomCityId());
    @Test
    public void test_05_更新city_id() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String city_id = UT.randomCityId();
        hpModified.body.put("city_id", city_id);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertEquals(code, "1000000", "医院城市没有更新成功");
        } catch (IOException e) {
            logger.error(e);
        }
        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "city_id"), city_id, "医院城市没有更新成功");
        Assert.assertEquals(parseJson(data, "city_name"), UT.cityName(city_id), "医院城市没有更新成功");

        hpModified.body.replace("city_id", "11"+city_id);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000", "错误的城市ID不能更新成功");
        } catch (IOException e) {
            logger.error(e);
        }
        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "city_id"), city_id, "医院城市没有更新成功");
        Assert.assertEquals(parseJson(data, "city_name"), UT.cityName(city_id), "医院城市没有更新成功");
    }
    //    body.put("county_id", UT.randomCountryId());
    @Test
    public void test_06_更新county_id() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String county_id = UT.randomCountyId();
        hpModified.body.put("county_id", county_id);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院城市没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "county_id"), county_id, "医院地区没有更新成功");
        Assert.assertEquals(parseJson(data, "county_name"), UT.countyName(county_id), "医院地区没有更新成功");
    }
    //    body.put("phone", "" + UT.randomPhone());
    @Test
    public void test_07_更新phone() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String phone = UT.randomPhone();
        hpModified.body.put("phone", phone);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院电话没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "phone"), phone, "医院电话没有更新成功");
    }
    //    body.put("description", "医库医院描述" + UT.randomString(30));
    @Test
    public void test_08_更新description() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        HospitalProfile hpModified = new HospitalProfile(false);
        String description = "修改描述" + UT.randomString(70);
        hpModified.body.put("description", description);
        try {
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院电话没有更新成功");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "description"), description, "医院电话没有更新成功");
    }

    @Test(enabled = true)
    public void test_09_更新photo_url() {
        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
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
            res = HttpRequest.sendPut(host_crm+uri, hpModified.body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "医院图片更新失败");

        res = KBHospital_Detail.Detail(hospitalId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "photo_url()"), "3", "医院图片更新失败");
        Assert.assertNotNull(parseJson(data, "photo_url():url"), "医院图片更新失败");
    }
}
