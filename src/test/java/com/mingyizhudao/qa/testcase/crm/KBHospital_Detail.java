package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
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
public class KBHospital_Detail extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBHospital_Detail.class);
    public static String uri = "/api/v1/medicallibrary/hospitals/{hospital_id}";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static HashMap<String, String> Detail(String hospitalId) {
        String res = "";
        JSONObject hospital = null;
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.sendGet(host_crm+uri,"", crm_token, pathValue);
            hospital = JSONObject.fromObject(res).getJSONObject("data");
        } catch (IOException e) {
            logger.error(e);
        }
        if (null == hospital) return null;
        HashMap<String, String> result = new HashMap<>();
        String cityId = hospital.containsKey("city_id") ? hospital.getString("city_id") : null;
        String countyId = hospital.containsKey("county_id") ? hospital.getString("county_id") : null;
        String name = hospital.containsKey("name") ? hospital.getString("name") : null;
        String short_name = hospital.containsKey("short_name") ? hospital.getString("short_name") : null;
        String type_list = hospital.containsKey("type_list") ? hospital.getString("type_list") : "";
        String hospital_class_list = hospital.containsKey("hospital_class_list") ? hospital.getString("hospital_class_list") : null;
        String city_name = hospital.containsKey("city_name") ? hospital.getString("city_name") : null;
        String county_name = hospital.containsKey("county_name") ? hospital.getString("county_name") : null;
        String phone = hospital.containsKey("phone") ? hospital.getString("phone") : null;
        String description = hospital.containsKey("description") ? hospital.getString("description") : null;
        String photo_url = hospital.containsKey("photo_url") ? hospital.getString("photo_url") : null;
        result.put("city_id", cityId);
        result.put("city_name", city_name);
        result.put("county_id", countyId);
        result.put("county_name", county_name);
        result.put("id", hospitalId);
        result.put("name", name);
        result.put("short_name", short_name);
        result.put("type_list", type_list);
        result.put("hospital_class_list", hospital_class_list);
        result.put("phone", phone);
        result.put("description", description);
        result.put("photo_url", photo_url);
        return result;
    }

    @Test
    public void test_01_获取医院详情_有效ID() {

        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id",info.get("id"));
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(parseJson(data, "name"), hp.body.getString("name"));
        Assert.assertEquals(parseJson(data, "short_name"), hp.body.getString("short_name"));
        Assert.assertEquals(parseJson(data, "hospital_class_list"), hp.body.getString("hospital_class_list"));
        Assert.assertEquals(parseJson(data, "type_list"), hp.body.getString("type_list"));
        Assert.assertEquals(parseJson(data, "city_id"), hp.body.getString("city_id"));
        Assert.assertEquals(parseJson(data, "city_name"), UT.cityName(hp.body.getString("city_id")));
        Assert.assertEquals(parseJson(data, "county_id"), hp.body.getString("county_id"));
        Assert.assertEquals(parseJson(data, "county_name"), UT.countyName(hp.body.getString("county_id")));
        Assert.assertEquals(parseJson(data, "phone"), hp.body.getString("phone"));
        Assert.assertEquals(parseJson(data, "description"), hp.body.getString("description"));
    }

    @Test
    public void test_02_获取医院详情_无效ID() {

        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", "111" + info.get("id"));
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }
}
