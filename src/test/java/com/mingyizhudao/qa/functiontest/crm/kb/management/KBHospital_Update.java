package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Hospital;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile_test;
import com.mingyizhudao.qa.common.TestLogger;
import static com.mingyizhudao.qa.utilities.Generator.*;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
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

    public static HashMap<String, String> s_Update(String hospitalId, HospitalProfile_test hp) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        res = HttpRequest.s_SendPut(host_crm+uri, hp.body.toString(), crm_token, pathValue);
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
    public static HashMap<String, String> s_Update(String hospitalId, Hospital hp) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hp).toString(), crm_token, pathValue);
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
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String name = "医院改名了";
        hpModified.setName(name);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不能操作");
    }

    //    body.put("name", "测试医库医院" + tmp);
    @Test
    public void test_02_更新name() {
        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String name = "医院改名了";
        hpModified.setName(name);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院name未更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_name = hospitalInfo.get("name");
        Assert.assertEquals(actual_name, hpModified.getName(), "医院name未更新成功");
    }

    //    body.put("short_name", "测试短名" + tmp.substring(8));
    @Test
    public void test_02_更新short_name() {
        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String short_name = "改短名了";
        hpModified.setShort_name(short_name);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院short_name未更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_short_name = hospitalInfo.get("short_name");
        Assert.assertEquals(actual_short_name, hpModified.getShort_name(), "医院short_name未更新成功");
    }
    //    body.put("hospital_class_list", UT.randomKey(KB.kb_hospital_class));
    @Test
    public void test_03_更新hospital_class_list() {
        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        hpModified.setHospital_class_list(randomHospitalClass());
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院class没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_hospital_class_list = hospitalInfo.get("hospital_class_list");
        Assert.assertEquals(actual_hospital_class_list, hpModified.getHospital_class_list(), "医院class没有更新成功");
    }

    //    body.put("type_list", UT.randomKey(KB.kb_hospital_type));
    @Test
    public void test_04_更新type_list() {
        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        hpModified.setType_list(randomHospitalType());
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院类型没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_type_list = hospitalInfo.get("type_list");
        Assert.assertEquals(actual_type_list, hpModified.getType_list(),"医院类型没有更新成功");
    }

    //    body.put("city_id", UT.randomCityId());
    @Test
    public void test_05_更新city_id() {
        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        hpModified.setCity_id(randomCityId());
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院城市没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_city_id = hospitalInfo.get("city_id");
        String actual_city_name = hospitalInfo.get("city_name");
        Assert.assertEquals(actual_city_id, hpModified.getCity_id(), "医院城市没有更新成功");
        Assert.assertEquals(actual_city_name, cityName(hpModified.getCity_id()), "医院城市没有更新成功");

    }
    //    body.put("county_id", UT.randomCountryId());
    @Test
    public void test_06_更新county_id() {
        String res = "";
        HashMap<String, String> info = KBHospital_Create.s_Create(new Hospital());
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String city_id = randomCityId();
        String county_id = randomCountyIdUnder(city_id);
        hpModified.setCity_id(city_id);
        hpModified.setCounty_id(county_id);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院城市没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_county_id = hospitalInfo.get("county_id");
        String actual_county_name = hospitalInfo.get("county_name");
        Assert.assertEquals(actual_county_id, county_id, "医院区县没有更新成功");
        Assert.assertEquals(actual_county_name, countyName(county_id), "医院区县没有更新成功");
        String actual_city_id = hospitalInfo.get("city_id");
        String actual_city_name = hospitalInfo.get("city_name");
        Assert.assertEquals(actual_city_id, hpModified.getCity_id(), "医院城市没有更新成功");
        Assert.assertEquals(actual_city_name, cityName(hpModified.getCity_id()), "医院城市没有更新成功");
    }
    //    body.put("phone", "" + UT.randomPhone());
    @Test
    public void test_07_更新phone() {
        String res = "";
        HashMap<String, String> info = KBHospital_Create.s_Create(new Hospital());
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String phone = randomPhone();
        hpModified.setPhone(phone);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院电话没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_phone = hospitalInfo.get("phone");
        Assert.assertEquals(actual_phone, hpModified.getPhone(), "医院电话没有更新成功");
    }

    //    body.put("description", "医库医院描述" + UT.randomString(30));
    @Test
    public void test_08_更新description() {
        String res = "";
        HashMap<String, String> info = KBHospital_Create.s_Create(new Hospital());
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        String description = "修改描述" + randomString(70);
        hpModified.setDescription(description);
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院描述没有更新成功");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_description = hospitalInfo.get("description");
        Assert.assertEquals(actual_description, hpModified.getDescription(), "医院描述没有更新成功");
    }

    //TODO
    @Test(enabled = true)
    public void test_09_更新photo_url() {
        String res = "";
        HashMap<String, String> info = KBHospital_Create.s_Create(new Hospital());
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        String hospitalId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);

        Hospital hpModified = new Hospital();
        hpModified.setPhoto_url(new ArrayList<Hospital.Picture>() {
            {
                add(hpModified.new Picture("1.jpg", "5", "1x1"));
                add(hpModified.new Picture("2.jpg", "5", "2x2"));
            }
        });
        res = HttpRequest.s_SendPut(host_crm+uri, JSONObject.fromObject(hpModified).toString(), "", pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "医院图片更新失败");

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String actual_photo_url = hospitalInfo.get("photo_url");
        JSONArray photos = JSONArray.fromObject(actual_photo_url);
        Assert.assertEquals(photos.size(), hpModified.getPhoto_url().size(),"医院图片更新失败");
        Assert.assertNotNull(photos.getJSONObject(0).getString("url"), "医院图片更新失败");
    }
}
